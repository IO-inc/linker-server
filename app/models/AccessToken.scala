package models

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Rachel on 2017. 7. 13..
  */
class AccessTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import scala.concurrent.ExecutionContext.Implicits.global
  import dbConfig.profile.api._

  private[models] val AccessTokens = TableQuery[AccessTokensTable]

  private def __findByAccessToken(accessToken: String): DBIO[Option[AccessToken]] =
    AccessTokens.filter(_.accessToken === accessToken).result.headOption

  def findByAccessToken(accessToken: String): Option[AccessToken] = {
    Await.result(
      db.run(__findByAccessToken(accessToken)).map { accessTokenResult =>
        accessTokenResult match {
          case result => result
          case None => None
        }
      }, Duration(3000, "millis"))
  }


  private[models] class AccessTokensTable(tag: Tag) extends Table[AccessToken](tag, "AccessTokens") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def accessToken = column[Option[String]]("accessToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, accessToken, createdAt, updatedAt, deletedAt) <> (AccessToken.tupled, AccessToken.unapply)

  }

}
