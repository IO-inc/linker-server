package models

import java.sql.Timestamp
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.JdbcProfile

import scala.concurrent.Future

/**
  * Created by mijeongpark on 2017. 7. 5..
  */

case class Customer (
                     id: Long,
                     name: Option[String],
                     authNumber: Option[String],
                     postNo: Option[String],
                     addr1: Option[String],
                     addr2: Option[String],
                     createdAt: Timestamp,
                     updatedAt: Timestamp,
                     deletedAt: Option[Timestamp] )

class CustomerRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private[models] val Customers = TableQuery[CustomersTable]

  private def _findById(id: Long): DBIO[Option[Customer]] =
    Customers.filter(_.id === id).result.headOption

  def findById(id: Long): Future[Option[Customer]] =
    db.run(_findById(id))

  private[models] class CustomersTable(tag: Tag) extends Table[Customer](tag, "Customers") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[Option[String]]("name")
    def authNumber = column[Option[String]]("authNumber")
    def postNo = column[Option[String]]("postNo")
    def addr1 = column[Option[String]]("addr1")
    def addr2 = column[Option[String]]("addr2")
    def createdAt = column[Timestamp]("createdAt")
    def updatedAt = column[Timestamp]("updatedAt")
    def deletedAt = column[Option[Timestamp]]("deletedAt")

    def * = (id, name, authNumber, postNo, addr1, addr2, createdAt, updatedAt, deletedAt) <> (Customer.tupled, Customer.unapply)
//    def ? = (id.?, createdAt.?, updatedAt.?).shaped.<>({ r => import r._; _1.map(_ => Customer.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

}

