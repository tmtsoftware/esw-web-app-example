package org.tmt.library.impl

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import csw.database.DBTestHelper
import csw.database.scaladsl.JooqExtentions.{RichQuery, RichResultQuery}
import org.jooq.DSLContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.tmt.library.models.Book
import org.tmt.library.service.LibraryService

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class LibraryServiceImplTest extends AnyWordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {

  private val system: ActorSystem[SpawnProtocol.Command] = ActorSystem(SpawnProtocol(), "test")

  override implicit val patienceConfig: PatienceConfig    = PatienceConfig(5.seconds)
  private implicit val executionContext: ExecutionContext = system.executionContext
  private var dsl: DSLContext                             = _
  private var postgres: EmbeddedPostgres                  = _
  private var libraryService: LibraryService              = _

  private val book1 = Book(UUID.randomUUID().toString, "Harry Potter", "J.K. Rowling", available = true)
  private val book2 = Book(UUID.randomUUID().toString, "Theory of Relativity", "Stephan Hawking", available = true)

  override def beforeAll(): Unit = {
    super.beforeAll()
    postgres = DBTestHelper.postgres(0) // 0 is random port
    dsl = DBTestHelper.dslContext(system, postgres.getPort)
    libraryService = new LibraryServiceImpl(dsl)

    dsl
      .query(
        "CREATE TABLE BOOKS(id TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, author TEXT NOT NULL, available BOOLEAN NOT NULL)"
      )
      .executeAsyncScala()
      .futureValue
  }

  override def afterAll(): Unit = {
    super.afterAll()
    postgres.close()
    system.terminate()
    system.whenTerminated.futureValue
  }

  "LibraryService" must {

    "get all the books present from the BOOKS table in the postgres database | ESW-196" in {
      val insertQueryStr =
        s"""
         |INSERT INTO books (id, title, author, available)
         |VALUES
         |	('${book1.id}','${book1.title}', '${book1.author}', ${book1.available}),
         |	('${book2.id}','${book2.title}', '${book2.author}', ${book2.available})
         |""".stripMargin

      dsl.query(insertQueryStr).executeAsyncScala().futureValue
      libraryService.getBooks.futureValue.toSet should ===(Set(book1, book2))
    }

    "insert the given book in the BOOKS table in the postgres database | ESW-196" in {
      val title  = "A brief history of time"
      val author = "Stephan Hawking"

      libraryService.insertBook(title, author).futureValue

      val books =
        dsl.resultQuery(s"SELECT * from BOOKS WHERE UPPER(title) LIKE UPPER('$title%')").fetchAsyncScala[Book].futureValue

      books.head.title should ===(title)
      books.head.author should ===(author)
    }
  }

}
