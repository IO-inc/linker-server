package models

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.JdbcProfile

/**
  * Created by Rachel on 2017. 7. 7..
  */

case class DeviceToken (
                       id: Long,
                       customerDeviceId: Option[Long],
                       deviceToken: Option[String],
                       createdAt: Timestamp,
                       updatedAt: Timestamp,
                       deletedAt: Option[Timestamp] )

class DeviceTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val DeviceTokens = TableQuery[DeviceTokensTable]

  def insert(deviceToken: DeviceToken): DBIO[Long] =
    DeviceTokens returning DeviceTokens.map(_.id) += deviceToken

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
