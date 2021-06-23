package org.tmt.library.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.tmt.library.TestHelper
import org.tmt.library.http.LibraryRequest.InsertBook
import org.tmt.library.models.Book
import org.tmt.library.service.LibraryService

import java.util.UUID
import scala.concurrent.Future

class LibraryRoutesTest
    extends AnyWordSpecLike
    with Matchers
    with ScalatestRouteTest
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterAll
    with HttpCodecs {

  private val libraryService: LibraryService = mock[LibraryService]
  private val routes                         = new LibraryRoutes(libraryService).routes

  "LibraryRoutes" must {
    "be able to insert a new book in the library service | ESW-196" in {
      val title                = "Harry Potter"
      val author               = "J.K. Rowling"
      val book: LibraryRequest = InsertBook(title, author)
      val id                   = TestHelper.randomString(10)

      when(libraryService.insertBook(title, author)).thenReturn(Future.successful(id))
      Post("/books", book) ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] should ===(id)
        verify(libraryService).insertBook(title, author)
      }
    }

    "be able to get all the books present in the library service | ESW-196" in {
      val book1 = Book(UUID.randomUUID().toString, "Harry Potter", "J.K. Rowling", available = true)
      val book2 = Book(UUID.randomUUID().toString, "Harry Potter: The Goblet of Fire", "J.K. Rowling", available = true)

      val books = List(book1, book2)
      when(libraryService.getBooks).thenReturn(Future.successful(books))

      Get("/books") ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[List[Book]] should ===(books)
        verify(libraryService).getBooks
      }
    }
  }
}
