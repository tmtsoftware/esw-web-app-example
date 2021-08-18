// #raDec-impl
package org.tmt.sample.impl

import csw.params.core.models.Angle
import org.tmt.sample.core.models.{RaDecRequest, RaDecResponse}
import org.tmt.sample.service.RaDecService

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.Future

class RaDecImpl extends RaDecService {

  private val raDecValues = mutable.ListBuffer[RaDecResponse]()

  override def raDecToString(raDecRequest: RaDecRequest): Future[RaDecResponse] = {
    val formattedRa   = Angle.raToString(Math.toRadians(raDecRequest.raInDecimals*15))
    val formattedDec  = Angle.deToString(Math.toRadians(raDecRequest.decInDecimals))
    val raDecResponse = RaDecResponse(UUID.randomUUID().toString, formattedRa, formattedDec)
    raDecValues.append(raDecResponse)
    Future.successful(raDecResponse)
  }
  override def getRaDecValues: Future[List[RaDecResponse]] = Future.successful(raDecValues.toList)
}
// #raDec-impl
