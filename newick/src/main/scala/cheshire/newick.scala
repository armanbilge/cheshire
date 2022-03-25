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

package cheshire.newick

import cats.Applicative
import cats.Defer
import cats.data.Chain
import cats.data.NonEmptyList
import cats.parse.Numbers
import cats.parse.Parser
import cats.parse.Parser0
import cats.parse.Rfc5234
import cats.syntax.all.*

final case class Tree(subtree: Subtree)
sealed abstract class Subtree
final case class Leaf(name: Option[String]) extends Subtree
final case class Internal(branchSet: NonEmptyList[Branch], name: Option[String]) extends Subtree
final case class Branch(subtree: Subtree, length: Option[BigDecimal])

def render[F[_]: Applicative: Defer](tree: Tree): F[String] =
  def go(subtree: Subtree): F[Chain[String]] =
    subtree match
      case Leaf(name) =>
        Chain.fromOption(name).pure
      case Internal(branchSet, name) =>
        Chain
          .fromSeq(branchSet.toList)
          .flatTraverse {
            case Branch(subtree, length) =>
              Defer[F].defer(go(subtree)).map(_ ++ Chain(":", length.toString))
          }
          .map("(" +: _ :+ ")")

  go(tree.subtree).map(t => (t :+ ";").iterator.mkString)

def parse(s: String): Either[Parser.Error, Tree] =
  tree.parse(s).map(_._2)

private val tree =
  Parser.start *> (subtree <* skip <* Parser.char(';')).map(Tree(_)) <* Parser.end

private def subtree: Parser0[Subtree] = leaf | internal

private def internal =
  (branchSet.surroundedBy(skip)
    .between(Parser.char('('), Parser.char(')')) ~ name.?).map(Internal(_, _))

private def branchSet = Defer[Parser0].fix[NonEmptyList[Branch]] { recurse =>
  (branch ~ (skip *> Parser.char(',') *> skip *> recurse)).map {
    (branch, branchSet) => branchSet.append(branch)
  }
}

private def branch = ((subtree <* skip) ~ length.?).map(Branch(_, _))

private def leaf = name.?.map(Leaf(_))

private def name =
  val unquoted =
    Parser.charIn(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ "#*%/.\\-+_&").repAs[String]
  val quoted = (Rfc5234.vchar | Parser.charIn(' ')).repAs[String]
  (unquoted | quoted.surroundedBy(Rfc5234.dquote) | quoted.surroundedBy(Parser.char('\'')))

private def length = Parser.char(':') *> Numbers.jsonNumber.map(BigDecimal(_))

private def skip = (comment.void | Parser.charIn(" \t\n").rep.void).rep0.void
private def comment = Parser.anyChar.between(Parser.char('['), Parser.char(']'))
