package models

import java.sql.Timestamp

/**
  * Created by mijeongpark on 2017. 7. 10..
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
