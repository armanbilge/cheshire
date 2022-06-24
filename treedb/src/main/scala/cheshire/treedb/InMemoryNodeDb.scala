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

import org.typelevel.vault.Vault


override extension (node: NodeDb.this.N) def attributes: F[Vault] = ???

override extension (node: NodeDb.this.N) def rightChild: Any = ???

override extension (node: NodeDb.this.N) def leftChild: Any = ???

override def mkNode(parent: Option[Nothing], children: Option[(Nothing, Nothing)], attributes: Vault): Any = ???

override extension (node: NodeDb.this.N) def parent: Any = ???


private final class InMemoryNodeDb[F[_]] extends NodeDb[F]

object InMemoryNodeDb:

  def apply[F[_]]: F[NodeDb[F]] = ???
