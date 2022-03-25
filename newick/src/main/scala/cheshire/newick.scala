/*
 * Copyright 2021 Arman Bilge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cheshire
package newick

import cats.Eval
import cats.parse.Parser
import cats.parse.Parser0
import cats.parse.Rfc5234
import cats.syntax.all.*

import scala.collection.mutable
import cats.parse.Numbers
import cats.data.NonEmptyList
import cats.Defer

final case class Tree(subtree: Subtree)
sealed abstract class Subtree
final case class Leaf(name: Option[String]) extends Subtree
final case class Internal(branchSet: NonEmptyList[Branch], name: Option[String]) extends Subtree
final case class Branch(subtree: Subtree, length: Option[BigDecimal])

private val subtree: Parser0[Subtree] = leaf | internal

private val internal =
  (branchSet.surroundedBy(skip).between(Parser.char('('), Parser.char(')')) ~ name.?)
    .map(Internal(_, _))

private val branchSet = Defer[Parser0].fix[NonEmptyList[Branch]] { recurse =>
  (branch ~ (skip *> Parser.char(',') *> skip *> recurse)).map { (branch, branchSet) =>
    branchSet.append(branch)
  }
}

private val branch = ((subtree <* skip) ~ length.?).map(Branch(_, _))

private val leaf = name.?.map(Leaf(_))

private val name =
  val unquoted =
    Parser.charIn(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ "#*%/.\\-+_&").repAs[String]
  val quoted = (Rfc5234.vchar | Parser.charIn(' ')).repAs[String]
  (unquoted | quoted.surroundedBy(Rfc5234.dquote) | quoted.surroundedBy(Parser.char('\'')))

private val length = Parser.char(':') *> Numbers.jsonNumber.map(BigDecimal(_))

private val skip = (comment.void | Parser.charIn(" \t\n").rep.void).rep0.void
private val comment = Parser.anyChar.between(Parser.char('['), Parser.char(']'))

def render(tree: Tree): String =
  val sb = new mutable.StringBuilder

  def go(subtree: Subtree): Eval[Unit] =
    subtree match
      case Leaf(name) =>
        Eval.now(name.foreach(sb ++= _))
      case Internal(branchSet, name) =>
        Eval.later(sb += '(') *> branchSet.traverse_ {
          case Branch(subtree, length) =>
            go(subtree) *> Eval.later(length.foreach(sb ++= _.toString))
        } *> Eval.later(sb += ')') *> Eval.later(name.foreach(sb ++= _))

  go(tree.subtree).value
  sb += ';'

  sb.result()
