package org.tmt.sample.service

import org.tmt.sample.core.models.{RaDecRequest, RaDecResponse}

import scala.concurrent.Future

// #raDecToString-contract
trait RaDecService {
  def raDecToString(raDecRequest: RaDecRequest): Future[RaDecResponse]
  // #raDecToString-contract

  // #getRaDecValues-contract
  def getRaDecValues: Future[List[RaDecResponse]]
  // #getRaDecValues-contract

  // #raDecToString-contract
}
// #raDecToString-contract
