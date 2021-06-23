package org.tmt.library.service

import org.tmt.library.models.Book

import scala.concurrent.Future

trait LibraryService {
  def getBooks: Future[List[Book]]
  def insertBook(title: String, author: String): Future[String]
}
