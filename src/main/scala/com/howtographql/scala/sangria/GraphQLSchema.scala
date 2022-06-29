package com.howtographql.scala.sangria
import sangria.schema.{Field, ListType, ObjectType}
import models._
import sangria.schema._
import sangria.macros.derive._

import sangria.execution.deferred.Fetcher
import sangria.execution.deferred.DeferredResolver
import sangria.execution.deferred.HasId

object GraphQLSchema {

  // 1
  val LinkType = ObjectType[Unit, Link](
    "Link",
    fields[Unit, Link](
      Field("id", IntType, resolve = _.value.id),
      Field("url", StringType, resolve = _.value.url),
      Field("description", StringType, resolve = _.value.description)
    )
  )

  implicit val linkHasId = HasId[Link, Int](_.id)

  val linksFetcher =
    Fetcher((ctx: MyContext, ids: Seq[Int]) => ctx.dao.getLinks(ids))

  val Resolver = DeferredResolver.fetchers(linksFetcher)
  // 2
  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))

  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field("allLinks", ListType(LinkType), resolve = c => c.ctx.dao.allLinks),
      Field(
        "link",
        OptionType(LinkType),
        arguments = Id :: Nil,
        resolve = c => linksFetcher.deferOpt(c.arg(Id))
      ),
      Field(
        "links",
        ListType(LinkType),
        arguments = Ids :: Nil,
        resolve = c => linksFetcher.deferSeq(c.arg(Ids))
      )
    )
  )

  // 3
  val SchemaDefinition = Schema(QueryType)

}
