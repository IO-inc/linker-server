package models

import Tables._
import data.ErrorMessage
import java.sql.Timestamp
import javax.inject.Inject

import common.Common
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await

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
          case None => Left(ErrorMessage.NO_ACCESS_TOKEN)
        }
      }, Common.COMMON_ASYNC_DURATION)
  }
}

