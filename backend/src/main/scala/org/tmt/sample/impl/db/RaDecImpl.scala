package org.tmt.sample.impl.db

import csw.params.core.models.Angle
import org.tmt.sample.core.models.{RaDecRequest, RaDecResponse}
import org.tmt.sample.service.RaDecService

import scala.concurrent.{ExecutionContext, Future}

// #add-repository
class RaDecImpl(raDecRepository: RaDecRepository)(implicit ec: ExecutionContext) extends RaDecService {
  // #add-repository

  // #raDecToString-impl
  override def raDecToString(raDecRequest: RaDecRequest): Future[RaDecResponse] = {
    val formattedRa  = Angle.raToString(raDecRequest.raInDecimals)
    val formattedDec = Angle.deToString(raDecRequest.decInDecimals)
    raDecRepository.insertRaDec(formattedRa, formattedDec).map(id => RaDecResponse(id, formattedRa, formattedDec))
  }
  // #raDecToString-impl

  // #getRaDecValues-impl
  override def getRaDecValues: Future[scala.List[RaDecResponse]] =
    raDecRepository.getRaDecValues
  // #getRaDecValues-impl

}
