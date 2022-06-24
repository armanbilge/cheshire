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

package cheshire.nucleotide

import scala.compiletime.ops.int.BitwiseOr

opaque type Base <: Matchable = Int

object Base:

  opaque type A <: Base = 1
  final val A: A = 1

  opaque type C <: Base = 2
  final val C: C = 2

  opaque type G <: Base = 4
  final val G: G = 4

  opaque type T <: Base = 8
  final val T: T = 8

  opaque type U <: Base = T
  final val U: U = 8

  opaque type W <: Base = BitwiseOr[A, T]
  final val W: W = 9

  opaque type S <: Base = BitwiseOr[C, G]
  final val S: S = 6

  opaque type M <: Base = BitwiseOr[A, C]
  final val M: M = 3

  opaque type K <: Base = BitwiseOr[G, T]
  final val K: K = 12

  opaque type R <: Base = BitwiseOr[A, G]
  final val R: R = 5

  opaque type Y <: Base = BitwiseOr[C, T]
  final val Y: Y = 10

  opaque type B <: Base = BitwiseOr[BitwiseOr[C, G], T]
  final val B: B = 14

  opaque type D <: Base = BitwiseOr[BitwiseOr[A, G], T]
  final val D: D = 13

  opaque type H <: Base = BitwiseOr[BitwiseOr[A, C], T]
  final val H: H = 11

  opaque type V <: Base = BitwiseOr[BitwiseOr[A, C], G]
  final val V: V = 7

  opaque type N <: Base = BitwiseOr[BitwiseOr[A, C], BitwiseOr[G, T]]
  final val N = 15
