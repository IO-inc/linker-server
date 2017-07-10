
package models

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Rachel on 2017. 7. 7..
  */

class DeviceTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import scala.concurrent.ExecutionContext.Implicits.global
  import dbConfig.profile.api._

  private[models] val DeviceTokens = TableQuery[DeviceTokensTable]
  private[models] val AccessTokens = TableQuery[AccessTokensTable]

  private def __findByAccessToken(accessToken: String): DBIO[Option[AccessToken]] =
    AccessTokens.filter(_.accessToken === accessToken).result.headOption

  private def __insert(customerDeviceId: Option[Long], token: Option[String], now: Timestamp): DBIO[Long] =
    DeviceTokens returning DeviceTokens.map(_.id) += DeviceToken(0, customerDeviceId, token, now, now, None)

  def createDeviceToken(token: String, accessToken: String): Either[String, Long] = {
    val now = new Timestamp(System.currentTimeMillis())

    val actions = for {
      accessTokenResult <- __findByAccessToken(accessToken)
      deviceTokenResult <- {
        if(accessTokenResult.isEmpty) DBIO.successful(None)
        else __insert(accessTokenResult.get.customerDeviceId, Option(token), now)
      }
    } yield deviceTokenResult

    // TODO: define error code
    Await.result(
      db.run(actions.transactionally).map { deviceTokenResult =>
        deviceTokenResult match {
          case n: Long => Right(n)
          case None => Left("There is no access token")
        }

      }, Duration(3000, "millis"))
  }

  // TODO: separate table definitions
  class DeviceTokensTable(tag: Tag) extends Table[DeviceToken](tag, "DeviceTokens") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def deviceToken = column[Option[String]]("deviceToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, deviceToken, createdAt, updatedAt, deletedAt) <> (DeviceToken.tupled, DeviceToken.unapply)
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

