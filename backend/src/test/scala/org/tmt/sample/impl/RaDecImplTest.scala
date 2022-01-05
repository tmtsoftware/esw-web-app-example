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
      response.futureValue.formattedRa should ===("2h 7m 48s")
      response.futureValue.formattedDec should ===(s"2°10'48\"")
    }
  }
}
// #raDecToString-impl-test
