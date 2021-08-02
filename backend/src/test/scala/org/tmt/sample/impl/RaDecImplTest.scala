package org.tmt.sample.impl

import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.tmt.sample.core.models.RaDecRequest
// #raDecToString-impl-test
class RaDecImplTest extends AnyWordSpec with Matchers {
  "RaDecImpl" must {
    "convert Ra and Dec to String" in {
      val raDecImpl     = new RaDecImpl()
      val response = raDecImpl.raDecToString(RaDecRequest(2.13, 2.18))
      response.futureValue.formattedRa should ===("8h 8m 9.602487087684134s")
      response.futureValue.formattedDec should ===(s"124°54'17.277618670114634\"")
    }
  }
}
// #raDecToString-impl-test
