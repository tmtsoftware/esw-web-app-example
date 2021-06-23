package org.tmt.library.http

sealed trait LibraryRequest

object LibraryRequest {
  case class InsertBook(title: String, author: String) extends LibraryRequest
}
