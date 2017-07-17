package models

import java.sql.Timestamp

/**
  * Created by mijeongpark on 2017. 7. 10..
  */

case class Customer(
                     id: Long,
                     phoneNumber: Option[String],
                     name: Option[String],
                     authNumber: Option[String],
                     postNo: Option[String],
                     addr1: Option[String],
                     addr2: Option[String],
                     email: Option[String],
                     createdAt: Timestamp,
                     updatedAt: Timestamp,
                     deletedAt: Option[Timestamp])

case class CustomerDevice(
                           id: Long,
                           customerId: Option[Long],
                           uuid: Option[String],
                           createdAt: Timestamp,
                           updatedAt: Timestamp,
                           deletedAt: Option[Timestamp]
                         )

case class AccessToken(
                        id: Long,
                        customerDeviceId: Option[Long],
                        accessToken: Option[String],
                        createdAt: Timestamp,
                        updatedAt: Timestamp,
                        deletedAt: Option[Timestamp])

case class DeviceToken(
                        id: Long,
                        customerDeviceId: Option[Long],
                        deviceToken: Option[String],
                        createdAt: Timestamp,
                        updatedAt: Timestamp,
                        deletedAt: Option[Timestamp])

case class Linker(
                   id: Long,
                   macAddress: Option[String],
                   createdAt: Timestamp,
                   updatedAt: Timestamp,
                   deletedAt: Option[Timestamp]
                 )

case class Purchase(
                     id: Long,
                     linkerId: Option[Long],
                     customerId: Option[Long],
                     price: Option[String],
                     warrantyDate: Option[Timestamp],
                     createdAt: Timestamp,
                     updatedAt: Timestamp,
                     deletedAt: Option[Timestamp]
                   )

case class PurchaseOwner(
                          id: Long,
                          purchaseId: Option[Long],
                          customerId: Option[Long],
                          createdAt: Timestamp,
                          updatedAt: Timestamp,
                          deletedAt: Option[Timestamp]
                        )
