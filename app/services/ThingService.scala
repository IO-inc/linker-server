package services

import com.google.inject.{Inject, Singleton}
import data.ErrorMessage
import models._

/**
  * Created by Rachel on 2017. 7. 30..
  */
@Singleton
class ThingService @Inject()(
                              thingRepo: ThingRepo,
                              commandRepo: CommandRepo){

  import scala.concurrent.ExecutionContext.Implicits.global

  def getThingCommandListByType(`type`: String, macAddress: String, command: String): Either[String, (Thing, Seq[Command])] = {

    thingRepo.findByMacAddress(macAddress) match {
      case Some(thing) => {
        if (thing.`type`.get != `type`) return Left(ErrorMessage.INVALID_THING_TYPE)

        val thingId = thing.id
        val commandList = commandRepo.findByCommand(thingId, command)
        Right(thing, commandList)
      }
      case None => Left(ErrorMessage.NO_THING)
    }

  }

}
