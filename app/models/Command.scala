package models

import javax.inject.Inject

import common.Common
import models.Tables.{CommandsTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await

/**
  * Created by Rachel on 2017. 7. 30..
  */
class CommandRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val Commands = TableQuery[CommandsTable]

  private def __findByThingId(thingId: Long): DBIO[Seq[Command]] =
    Commands.filter(_.thingId === thingId).filter(_.deletedAt.isEmpty).result

  def findByThingId(thingId: Long): Seq[Command] = {
    Await.result(db.run(__findByThingId(thingId)), Common.COMMON_ASYNC_DURATION)
  }
}
