package models

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.JdbcProfile

import scala.concurrent.Future

/**
  * Created by Rachel on 2017. 7. 7..
  */

case class AccessToken(
                      id: Long,
                      customerDeviceId: Option[Long],
                      accessToken: Option[String],
                      createdAt: Timestamp,
                      updatedAt: Timestamp,
                      deletedAt: Option[Timestamp])

case class DeviceToken (
                         id: Long,
                         customerDeviceId: Option[Long],
                         deviceToken: Option[String],
                         createdAt: Timestamp,
                         updatedAt: Timestamp,
                         deletedAt: Option[Timestamp] )

class AccessTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  import scala.concurrent.ExecutionContext.Implicits.global

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val AccessTokens = TableQuery[AccessTokensTable]
  private[models] val DeviceTokens = TableQuery[DeviceTokensTable]

  private def __findByAccessToken(accessToken: String): DBIO[Option[AccessToken]] =
    AccessTokens.filter(_.accessToken === accessToken).result.headOption

  def findByAccessToken(token: String, accessToken: String): Future[Long] = {
    val now = new Timestamp(System.currentTimeMillis())

    for {
      Some(accessToken) <- db.run(__findByAccessToken(accessToken))
      create <- db.run(DeviceTokens returning DeviceTokens.map(_.id) += DeviceToken(1, accessToken.customerDeviceId, Option(token), now, now, None))
    } yield create
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

  private[models] class DeviceTokensTable(tag: Tag) extends Table[DeviceToken](tag, "DeviceTokens") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def deviceToken = column[Option[String]]("deviceToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, deviceToken, createdAt, updatedAt, deletedAt) <> (DeviceToken.tupled, DeviceToken.unapply)
  }
}

/*class DeviceTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val DeviceTokens = TableQuery[DeviceTokensTable]

  def insert(deviceToken: DeviceToken): Future[Long] = {
    println("insert in")
    db.run(DeviceTokens returning DeviceTokens.map(_.id) += deviceToken)
  }

  private[models] class DeviceTokensTable(tag: Tag) extends Table[DeviceToken](tag, "DeviceTokens") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def customerDeviceId = column[Option[Long]]("customerDeviceId")
    def deviceToken = column[Option[String]]("deviceToken")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, customerDeviceId, deviceToken, createdAt, updatedAt, deletedAt) <> (DeviceToken.tupled, DeviceToken.unapply)
  }
}*/

