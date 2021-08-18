// #raDecService-contract
package org.tmt.sample.service

import org.tmt.sample.core.models.{RaDecRequest, RaDecResponse}

import scala.concurrent.Future

trait RaDecService {
  def raDecToString(raDecRequest: RaDecRequest): Future[RaDecResponse]
  def getRaDecValues: Future[List[RaDecResponse]]
}
// #raDecService-contract
