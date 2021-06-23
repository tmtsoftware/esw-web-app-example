package org.tmt.library.impl

import csw.database.scaladsl.JooqExtentions.{RichQuery, RichResultQuery}
import org.jooq.DSLContext
import org.tmt.library.models.Book
import org.tmt.library.service.LibraryService

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceImpl(dsl: DSLContext)(implicit ec: ExecutionContext) extends LibraryService {
  override def getBooks: Future[List[Book]] = dsl.resultQuery("SELECT * from BOOKS").fetchAsyncScala[Book]

  override def insertBook(title: String, author: String): Future[String] = {
    val id = UUID.randomUUID().toString
    dsl
      .query(s"INSERT INTO BOOKS (id, title, author, available) values (?, ?, ?, ?)", id, title, author, true)
      .executeAsyncScala()
      .map {
        case x if x < 0 => throw new RuntimeException(s"Failed to insert the book $title")
        case _          => id
      }
  }
}
