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

package cheshire.likelihood
package laws

import algebra.ring.Field
import cats.Monad
import cats.effect.laws.*
import cats.kernel.CommutativeSemigroup
import cats.kernel.laws.CommutativeSemigroupLaws
import cats.syntax.all.*

object PartitionLaws:
  def apply[F[_]: Monad, R: Field, Model, Matrix, Ppv, NodeClv, TipClv](
      partition: Partition.Aux[F, R, Model, Matrix, Ppv, NodeClv, TipClv])
      : PartitionLaws[F, R, Model, Matrix, Ppv, NodeClv, TipClv] =
    new PartitionLaws(partition) {}

trait PartitionLaws[F[_], R, Model, Matrix, Ppv, NodeClv, TipClv](
    val partition: Partition.Aux[F, R, Model, Matrix, Ppv, NodeClv, TipClv])(
    using val F: Monad[F],
    R: Field[R]):

  type Clv = NodeClv | TipClv

  extension (x: R)
    def +(y: R): R = R.plus(x, y)
    def -(y: R): R = R.minus(x, y)
    def unary_- : R = R.negate(x)
    def *(y: R): R = R.times(x, y)
    def /(y: R): R = R.div(x, y)
    def **(n: Int): R = R.pow(x, n)
  extension (n: Int) def *(y: R): R = R.sumN(y, n)

  def meanRate(
      freqs: IndexedSeq[R],
      params: IndexedSeq[R],
      rate: R,
      alpha: R
  ): IsEq[F[R]] =
    val left = for
      model <- partition.model(freqs, params, rate, alpha)
      rates <- partition.rates(model)
    yield R.sum(rates)
    val right = (partition.categoryCount * rate).pure
    left <-> right

  def forecastIdentity(
      model: F[Model],
      ppv: F[Ppv]
  ): IsEq[F[Ppv]] =
    val left = for
      model <- model
      matrix <- partition.matrix(model, R.zero)
      ppv <- ppv
      ppv <- partition.forecast(ppv, matrix)
    yield ppv
    val right = ppv
    left <-> right

  def equilibriumIdentity(
      model: F[Model],
      t: R
  ): IsEq[F[Ppv]] =
    val left = for
      model <- model
      seed <- partition.seed(model)
      matrix <- partition.matrix(model, t)
      ppv <- partition.forecast(seed, matrix)
    yield ppv
    val right = model.flatMap(partition.seed)
    left <-> right

  def forecastScaleInvariance(
      ppv: F[Ppv],
      freqs: IndexedSeq[R],
      params: IndexedSeq[R],
      alpha: R,
      x: R,
      y: R
  ): IsEq[F[Ppv]] =
    def expr(rate: R, t: R) = for
      ppv <- ppv
      model <- partition.model(freqs, params, rate, alpha)
      matrix <- partition.matrix(model, t)
      ppv <- partition.forecast(ppv, matrix)
    yield ppv
    val left = expr(x, y)
    val right = expr(y, x)
    left <-> right

  def forecastCompatibility(
      model: F[Model],
      ppv: F[Ppv],
      s: R,
      t: R
  ): IsEq[F[Ppv]] =
    val left = for
      model <- model
      ppv <- ppv
      matrix1 <- partition.matrix(model, s)
      matrix2 <- partition.matrix(model, t)
      ppv <- partition.forecast(ppv, matrix1)
      ppv <- partition.forecast(ppv, matrix2)
    yield ppv
    val right = for
      model <- model
      ppv <- ppv
      matrix <- partition.matrix(model, s + t)
      ppv <- partition.forecast(ppv, matrix)
    yield ppv
    left <-> right

  def forecastCommutativity(
      model: F[Model],
      ppv: F[Ppv],
      s: R,
      t: R
  ): IsEq[F[Ppv]] =
    val left = for
      model <- model
      ppv <- ppv
      matrix1 <- partition.matrix(model, s)
      matrix2 <- partition.matrix(model, t)
      ppv <- partition.forecast(ppv, matrix1)
      ppv <- partition.forecast(ppv, matrix2)
    yield ppv
    val right = for
      model <- model
      ppv <- ppv
      matrix1 <- partition.matrix(model, s)
      matrix2 <- partition.matrix(model, t)
      ppv <- partition.forecast(ppv, matrix2)
      ppv <- partition.forecast(ppv, matrix1)
    yield ppv
    left <-> right

  def backcastIdentity(
      model: F[Model],
      clv: F[Clv]
  ): IsEq[F[Clv]] =
    val left = for
      model <- model
      matrix <- partition.matrix(model, R.zero)
      clv <- clv
      clv <- partition.backcast(clv, matrix)
    yield clv
    val right = clv
    left.widen <-> right

  def backcastScaleInvariance(
      clv: F[Clv],
      freqs: IndexedSeq[R],
      params: IndexedSeq[R],
      alpha: R,
      x: R,
      y: R
  ): IsEq[F[Clv]] =
    def expr(rate: R, t: R) = for
      clv <- clv
      model <- partition.model(freqs, params, rate, alpha)
      matrix <- partition.matrix(model, t)
      clv <- partition.backcast(clv, matrix)
    yield clv
    val left = expr(x, y)
    val right = expr(y, x)
    left.widen <-> right.widen

  def backcastCompatibility(
      model: F[Model],
      clv: F[Clv],
      s: R,
      t: R
  ): IsEq[F[Clv]] =
    val left = for
      model <- model
      clv <- clv
      matrix1 <- partition.matrix(model, s)
      matrix2 <- partition.matrix(model, t)
      clv <- partition.backcast(clv, matrix1)
      clv <- partition.backcast(clv, matrix2)
    yield clv
    val right = for
      model <- model
      clv <- clv
      matrix <- partition.matrix(model, s + t)
      clv <- partition.backcast(clv, matrix)
    yield clv
    left.widen <-> right.widen

  def backcastCommutativity(
      model: F[Model],
      clv: F[Clv],
      s: R,
      t: R
  ): IsEq[F[Clv]] =
    val left = for
      model <- model
      clv <- clv
      matrix1 <- partition.matrix(model, s)
      matrix2 <- partition.matrix(model, t)
      clv <- partition.backcast(clv, matrix1)
      clv <- partition.backcast(clv, matrix2)
    yield clv
    val right = for
      model <- model
      clv <- clv
      matrix1 <- partition.matrix(model, s)
      matrix2 <- partition.matrix(model, t)
      clv <- partition.backcast(clv, matrix2)
      clv <- partition.backcast(clv, matrix1)
    yield clv
    left.widen <-> right.widen

  def ppvProductCompatibility(
      ppv: F[Ppv],
      clv0: F[Clv],
      clv1: F[Clv]
  ): IsEq[F[Ppv]] =
    def expr(clv0: Clv, clv1: Clv): F[Ppv] =
      ppv.flatMap(partition.product(_, clv0)).flatMap(partition.product(_, clv1))
    val left = (clv0, clv1).mapN(expr).flatten
    val right = (clv1, clv0).mapN(expr).flatten
    left <-> right

  def clvProductLaws: CommutativeSemigroupLaws[F[Clv]] =
    given CommutativeSemigroup[F[Clv]] with
      def combine(x: F[Clv], y: F[Clv]): F[Clv] =
        (x, y).mapN(partition.product).flatten.widen
    CommutativeSemigroupLaws[F[Clv]]

  def backcastProductConsistency(
      leftClv: F[Clv],
      leftMatrix: F[Matrix],
      rightClv: F[Clv],
      rightMatrix: F[Matrix]
  ): IsEq[F[Clv]] =
    val left =
      (leftClv, leftMatrix, rightClv, rightMatrix).mapN(partition.backcastProduct).flatten
    val right = for
      leftClv <- leftClv
      leftMatrix <- leftMatrix
      leftClv <- partition.backcast(leftClv, leftMatrix)
      rightClv <- rightClv
      rightMatrix <- rightMatrix
      rightClv <- partition.backcast(rightClv, rightMatrix)
      clv <- partition.product(leftClv, rightClv)
    yield clv
    left.widen <-> right.widen

  def backcastProductCommutativity(
      leftClv: F[Clv],
      leftMatrix: F[Matrix],
      rightClv: F[Clv],
      rightMatrix: F[Matrix]
  ): IsEq[F[Clv]] =
    val left =
      (leftClv, leftMatrix, rightClv, rightMatrix).mapN(partition.backcastProduct).flatten
    val right =
      (rightClv, rightMatrix, leftClv, leftMatrix).mapN(partition.backcastProduct).flatten
    left.widen <-> right.widen

  def seedAndIntegrateConsistency(
      model: F[Model],
      clv: F[Clv]
  ): IsEq[F[R]] =
    val left = for
      model <- model
      clv <- clv
      ppv <- partition.seed(model)
      l <- partition.integrateProduct(ppv, clv)
    yield l
    val right = for
      model <- model
      clv <- clv
      l <- partition.seedAndIntegrate(model, clv)
    yield l
    left <-> right

  def forecastBackcastConsistency(
      model: F[Model],
      ppv: F[Ppv],
      clv: F[Clv],
      t: R
  ): IsEq[F[R]] =
    val left = for
      model <- model
      matrix <- partition.matrix(model, t)
      ppv <- ppv
      ppv <- partition.forecast(ppv, matrix)
      clv <- clv
      l <- partition.integrateProduct(ppv, clv)
    yield l
    val right = for
      model <- model
      matrix <- partition.matrix(model, t)
      ppv <- ppv
      clv <- clv
      clv <- partition.backcast(clv, matrix)
      l <- partition.integrateProduct(ppv, clv)
    yield l
    left <-> right

  def edgeLikelihoodConsistency(
      model: F[Model],
      ppv: F[Ppv],
      clv: F[Clv],
      t: R
  ): IsEq[F[R]] =
    val left = for
      model <- model
      ppv <- ppv
      clv <- clv
      l <- partition.edgeLikelihood(model, ppv, clv)(t)
      ll <- l.logLikelihood
    yield ll
    val right = for
      model <- model
      ppv <- ppv
      clv <- clv
      matrix <- partition.matrix(model, t)
      clv <- partition.backcast(clv, matrix)
      l <- partition.integrateProduct(ppv, clv)
    yield l
    left <-> right

  def nodeLikelihoodConsistency(
      model: F[Model],
      ppv: F[Ppv],
      parentHeight: R,
      leftClv: F[Clv],
      leftHeight: R,
      rightClv: F[Clv],
      rightHeight: R,
      t: R
  ): IsEq[F[R]] =
    val left = for
      model <- model
      ppv <- ppv
      leftClv <- leftClv
      rightClv <- rightClv
      l <- partition.nodeLikelihood(
        model,
        ppv,
        parentHeight,
        leftClv,
        leftHeight,
        rightClv,
        rightHeight)(t)
      ll <- l.logLikelihood
    yield ll
    val right = for
      model <- model
      ppv <- ppv
      leftClv <- leftClv
      rightClv <- rightClv
      parentMatrix <- partition.matrix(model, parentHeight - t)
      leftMatrix <- partition.matrix(model, t - leftHeight)
      rightMatrix <- partition.matrix(model, t - rightHeight)
      clv <- partition.backcastProduct(leftClv, leftMatrix, rightClv, rightMatrix)
      clv <- partition.backcast(clv, parentMatrix)
      l <- partition.integrateProduct(ppv, clv)
    yield l
    left <-> right
