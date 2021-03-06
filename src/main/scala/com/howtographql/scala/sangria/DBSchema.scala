package com.howtographql.scala.sangria

import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import com.howtographql.scala.sangria.models._

object DBSchema {

  // 1 defines mapping to database table,
  class LinksTable(tag: Tag) extends Table[Link](tag, "LINKS") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def url = column[String]("URL")
    def description = column[String]("DESCRIPTION")

    def * = (id, url, description).mapTo[Link]

  }

  // 2 gives us a helper we will use to accessing data in this table.
  val Links = TableQuery[LinksTable]

  // 3 is responsible for creating schema and adding three entities to the database.
  val databaseSetup = DBIO.seq(
    Links.schema.create,
    Links forceInsertAll Seq(
      Link(
        1,
        "http://howtographql.com",
        "Awesome community driven GraphQL tutorial"
      ),
      Link(2, "http://graphql.org", "Official GraphQL web page"),
      Link(3, "https://graphql.org/", "GraphQL specification")
    )
  )

  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)

    new DAO(db)

  }

}
