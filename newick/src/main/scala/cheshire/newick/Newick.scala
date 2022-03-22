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
import cats.syntax.all.*

import scala.collection.mutable

final case class Tree(subtree: Subtree)
sealed abstract class Subtree
final case class Leaf(name: Option[String]) extends Subtree
final case class Internal(branchSet: List[Branch], name: Option[String]) extends Subtree
final case class Branch(subtree: Subtree, length: Option[BigDecimal])

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

  go(tree.subtree)
  sb += ';'

  sb.result()
