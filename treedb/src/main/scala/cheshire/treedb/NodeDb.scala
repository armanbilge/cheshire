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

trait NodeDb[F[_]]:
  type N

  extension (node: N)
    def parent: F[Option[N]]
    def leftChild: F[Option[N]]
    def rightChild: F[Option[N]]
    def attributes: F[Vault]

  def mkNode(
      parent: Option[N],
      children: Option[(N, N)],
      attributes: Vault
  ): F[N]
