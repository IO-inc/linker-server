package models

import Tables._
import javax.inject.Inject

import common.Common
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await}

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
      }, Common.COMMON_ASYNC_DURATION)
  }
}
