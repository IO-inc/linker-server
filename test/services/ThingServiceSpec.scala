package services

import java.sql.Timestamp

import data.ErrorMessage
import models.{Command, Thing, CommandRepo, ThingRepo}
import org.specs2.mock.Mockito
import play.api.test.{WithApplication, PlaySpecification}

/**
  * Created by Rachel on 2017. 7. 30..
  */
class ThingServiceSpec extends PlaySpecification with Mockito {

  val mockThingRepo = mock[ThingRepo]
  val mockCommandRepo = mock[CommandRepo]

  val service = new ThingService(mockThingRepo, mockCommandRepo)

  private val `TYPE` = "switcher"
  private val MAC_ADDRESS = "10:12:34:12:34:11"
  private val THING_ID = 1L
  private val TIMESTAMP = new Timestamp(System.currentTimeMillis())
  private val COMMAND = "RESERVATION"
  private val COMMAND_ID = 2L

  "getThingCommandListByType" should {

    "return Left(error) if there is no thing by mac address" in new WithApplication() {

      mockThingRepo.findByMacAddress(anyString) returns None

      // given
      val `type` = `TYPE`
      val macAddress = MAC_ADDRESS
      var command = COMMAND

      // when
      val result = service.getThingCommandListByType(`type`, macAddress, command)

      // then
      val expectedResult = Left(ErrorMessage.NO_THING)

      result mustEqual(expectedResult)
    }

    "return Left(error) if the type of thing is not matched" in new WithApplication() {

      val wrongType = "doorlock"

      val thing = Thing(
        id = THING_ID,
        `type` = Some(wrongType),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      mockThingRepo.findByMacAddress(anyString) returns Some(thing)

      // given
      val `type` = `TYPE`
      val macAddress = MAC_ADDRESS
      var command = COMMAND

      // when
      val result = service.getThingCommandListByType(`type`, macAddress, command)

      // then
      val expectedResult = Left(ErrorMessage.INVALID_THING_TYPE)

      result mustEqual(expectedResult)
    }

    "return Right(command list) if there is command list by type" in new WithApplication() {

      val thing = Thing(
        id = THING_ID,
        `type` = Some(`TYPE`),
        createdAt = TIMESTAMP,
        updatedAt = TIMESTAMP
      )

      val commandList = Seq(
        Command(
          id = COMMAND_ID,
          command = Some(COMMAND),
          createdAt = TIMESTAMP,
          updatedAt = TIMESTAMP
        )
      )

      mockThingRepo.findByMacAddress(anyString) returns Some(thing)
      mockCommandRepo.findByCommand(anyLong, anyString) returns commandList

      // given
      val `type` = `TYPE`
      val macAddress = MAC_ADDRESS
      var command = COMMAND

      // when
      val result = service.getThingCommandListByType(`type`, macAddress, command)

      // then
      val expectedResult = Right(thing, commandList)

      result mustEqual(expectedResult)
    }

  }

}
