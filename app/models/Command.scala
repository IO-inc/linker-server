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

  private def __findByCommand(thingId: Long, command: String): DBIO[Seq[Command]] =
    Commands.filter(_.thingId === thingId).filter(_.command === command).filter(_.deletedAt.isEmpty).result

  def findByCommand(thingId: Long, command: String): Seq[Command] = {
    Await.result(db.run(__findByCommand(thingId, command)), Common.COMMON_ASYNC_DURATION)
  }
}
