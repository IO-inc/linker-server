package models

import javax.inject.Inject

import common.Common
import models.Tables.CustomerDevicesTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Await

/**
  * Created by Rachel on 2017. 7. 22..
  */
class CustomerDevice @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  import dbConfig.profile.api._

  private[models] val CustomerDevices = TableQuery[CustomerDevicesTable]

  private def __findByCustomerId(customerId: Long): DBIO[Option[CustomerDevice]] =
    CustomerDevices.filter(_.customerId === customerId).result.headOption

  def findByCustomerId(customerId: Long): Option[CustomerDevice] = {
    Await.result(db.run(__findByCustomerId(customerId)), Common.COMMON_ASYNC_DURATION)
  }
}
