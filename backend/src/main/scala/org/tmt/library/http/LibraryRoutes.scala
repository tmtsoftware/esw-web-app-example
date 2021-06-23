package org.tmt.library.http

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.util.ByteString
import csw.commons.http.codecs.ErrorCodecs._
import csw.commons.http.{ErrorMessage, ErrorResponse}
import io.bullet.borer.Json
import io.bullet.borer.compat.akka._
import org.tmt.library.http.LibraryRequest.InsertBook
import org.tmt.library.service.LibraryService

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class LibraryRoutes(libraryService: LibraryService)(implicit
    ec: ExecutionContext
) extends HttpCodecs {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case NonFatal(ex) =>
      val errorResponse = ErrorResponse(ErrorMessage(ex.getMessage, Some(ex.getClass.getSimpleName)))
      complete(
        HttpResponse(
          StatusCodes.InternalServerError,
          entity = HttpEntity(ContentTypes.`application/json`, Json.encode(errorResponse).to[ByteString].result)
        )
      )
  }

  def routes: Route =
    handleExceptions(exceptionHandler) {
      path("books") {
        post {
          entity(as[LibraryRequest]) {
            case InsertBook(title, authorName) =>
              complete(libraryService.insertBook(title, authorName))
          }
        } ~
        get {
          complete(libraryService.getBooks)
        }
      }
    }
}
