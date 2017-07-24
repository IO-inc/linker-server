package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}
import services.LinkerService

import scala.concurrent.ExecutionContext

/**
  * Created by mijeongpark on 2017. 7. 24..
  */
class LinkerController @Inject()(cc: ControllerComponents,
                                 linkerService: LinkerService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

}
