package org.tmt.sample.impl.db

// #repo-imports
import csw.database.scaladsl.JooqExtentions.{RichQuery, RichResultQuery}
import org.jooq.DSLContext
import org.tmt.sample.core.models.RaDecResponse

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
// #repo-imports

// #add-repository
class RaDecRepository(dsl: DSLContext)(implicit ec: ExecutionContext) {
  // #add-repository

  // #insert-raDecValue-in-db
  def insertRaDec(formattedRa: String, formattedDec: String): Future[String] = {
    val id = UUID.randomUUID().toString
    dsl
      .query(s"INSERT INTO RaDecValues (id,formattedRa,formattedDec) values (?,?,?)", id, formattedRa, formattedDec)
      .executeAsyncScala()
      .map {
        case x if x < 0 => throw new RuntimeException(s"Failed to insert the (ra ,dec) value ($formattedRa, $formattedDec )")
        case _          => id
      }
  }
  // #insert-raDecValue-in-db

  // #get-raDecValues-from-db
  def getRaDecValues: Future[scala.List[RaDecResponse]] =
    dsl.resultQuery("SELECT * from RaDecValues").fetchAsyncScala[RaDecResponse]
  // #get-raDecValues-from-db

  // #add-repository
}
// #add-repository
