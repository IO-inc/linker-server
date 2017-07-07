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

class AccessTokenRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val AccessTokens = TableQuery[AccessTokensTable]

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
