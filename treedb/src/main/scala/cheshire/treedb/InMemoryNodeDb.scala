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

package cheshire.treedb

import cats.effect.kernel.Sync
import cats.effect.kernel.Unique
import cats.syntax.all.*
import org.typelevel.vault.Vault
import cats.effect.std.Semaphore
import java.util

import InMemoryNodeDb.*

final private class InMemoryNodeDb[F[_]](
    semaphore: Semaphore[F],
    childMap: util.WeakHashMap[Node, util.Set[Node]]
)(using F: Sync[F])
    extends NodeDb[F]:

  type N = Node

  extension (node: N)
    def parent: F[Option[N]] = node.parent.pure

    def children: F[Option[(N, N)]] = ???

    def attributes: F[Vault] = node.attributes.pure

  def mkNode(
      parent: Option[N],
      children: Option[(N, N)],
      attributes: Vault
  ): F[N] = ???

object InMemoryNodeDb:

  def apply[F[_]]: F[NodeDb[F]] = ???

  final private[InMemoryNodeDb] case class Node(
      token: Unique.Token,
      parent: Option[Node],
      attributes: Vault
  )
