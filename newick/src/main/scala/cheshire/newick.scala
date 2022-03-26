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

final case class Tree(root: Node)
final case class Node(children: List[Node], label: Option[String], length: Option[BigDecimal])

def render[F[_]: Applicative: Defer](tree: Tree): F[String] =
  def go(node: Node): F[Chain[String]] =
    node match
      case Node(children, label, length) =>
        val renderedLabel = Chain.fromOption(label)
        val renderedLength = length.fold(Chain.empty)(l => Chain(":", l.toString))
        val renderedChildren = children match
          case Nil => Chain.empty.pure
          case head :: tail =>
            val renderedHead = Defer[F].defer(go(head))
            val renderedTail =
              Chain.fromSeq(tail).flatTraverse(c => Defer[F].defer(go(c)).map("," +: _))
            renderedHead.map2(renderedTail)(_ ++ _)

        renderedChildren.map(c => ("(" +: c :+ ")") ++ renderedLabel ++ renderedLength)

  go(tree.root).map(t => (t :+ ";").iterator.mkString)

// def parse(s: String): Either[Parser.Error, Tree] =
//   tree.parse(s).map(_._2)

// private val tree =
//   Parser.start *> (subtree <* skip <* Parser.char(';')).map(Tree(_)) <* Parser.end

// private def subtree: Parser0[Subtree] = internal | leaf

// private def internal =
//   (branchSet.surroundedBy(skip).between(Parser.char('('), Parser.char(')')) ~ name.?)
//     .map(Internal(_, _))

// private def branchSet = Defer[Parser0].fix[NonEmptyList[Branch]] { recurse =>
//   (branch ~ (skip *> Parser.char(',') *> skip *> recurse).?).map {
//     case (branch, Some(branchSet)) => branchSet.append(branch)
//     case (branch, None) => NonEmptyList.one(branch)
//   }
// }

// private def branch = ((subtree <* skip) ~ length.?).map(Branch(_, _))

// private def leaf = name.?.map(Leaf(_))

// private def name =
//   val unquoted =
//     Parser.charIn(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ "#*%/.\\-+_&").repAs[String]
//   val quoted = (Rfc5234.vchar | Parser.charIn(' ')).repAs[String]
//   (unquoted | quoted.surroundedBy(Rfc5234.dquote) | quoted.surroundedBy(Parser.char('\'')))

// private def length = Parser.char(':') *> Numbers.jsonNumber.map(BigDecimal(_))

// private def skip = (comment.void | Parser.charIn(" \t\n").rep.void).rep0.void
// private def comment = Parser.anyChar.between(Parser.char('['), Parser.char(']'))
