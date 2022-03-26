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

import cats.data.NonEmptyList
import org.specs2.mutable.Specification

class NewickSpec extends Specification:

  "Newick parser" should {
    "parse ;" in {
      parse(";") === Right(Tree(Node(Nil, None, None)))
    }
    "parse A;" in {
      parse("A;") === Right(Tree(Node(Nil, Some("A"), None)))
    }
    "parse (A);" in {
      parse("(A);") === Right(Tree(Node(List(Node(Nil, Some("A"), None)), None, None)))
    }
    "parse (,,(,));" in {
      parse("(,,(,));") should beRight
    }
    "parse (A,B,(C,D));" in {
      parse("(A,B,(C,D));") should beRight
    }
    "parse (A,B,(C,D)E)F;" in {
      parse("(A,B,(C,D)E)F;") should beRight
    }
    "parse (:0.1,:0.2,(:0.3,:0.4):0.5);" in {
      parse("(:0.1,:0.2,(:0.3,:0.4):0.5);") should beRight
    }
    "parse (:0.1,:0.2,(:0.3,:0.4):0.5):0.0;" in {
      parse("(:0.1,:0.2,(:0.3,:0.4):0.5):0.0;") should beRight
    }
    "parse (A:0.1,B:0.2,(C:0.3,D:0.4):0.5);" in {
      parse("(A:0.1,B:0.2,(C:0.3,D:0.4):0.5);") should beRight
    }
    "parse (A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;" in {
      parse("(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;") should beRight
    }
    "parse ((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;" in {
      parse("((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;") should beRight
    }
  }
