package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def evaluateMethod: Action[AnyContent] = Action { implicit request =>
    val jsonResponse = Json.obj("message" -> "Evaluation endpoint is working!")
    Ok(jsonResponse)
  }
}