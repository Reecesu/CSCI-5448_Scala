package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import saladbar._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def evaluateMethod: Action[AnyContent] = Action { implicit request =>

    try {
      val json = request.body.asJson.get
      val parser = new saladbar.Parser
      val se = (json \ "expression").as[String]
      println(s"processing expression: $se")
      val e = parser.parse(se)

      val evaluationConditions = json \ "evaluationConditions"
      val sScope = (evaluationConditions \ "scope").as[String]
      val sTypes = (evaluationConditions \ "types").as[String]
      val sLazyEager = (evaluationConditions \ "lazyEager").as[String]
      val scope = if (sScope == LexicalScope.toString) LexicalScope else DynamicScope
      val types = if (sTypes == NoConversions.toString) NoConversions else ImplicitConversions
      val lazyEager = if (sLazyEager == EagerCondition.toString) EagerCondition else LazyCondition
      val interpreter = new Interpreter(new EvalConditions(scope, types, lazyEager))
      val lout = interpreter.evaluate(e){ r => r }
      val loutFormatted = lout map { e => e.toString }
      
       // TODO: reformat in agreed upon way
      val jsonResponse = Json.obj("message" -> loutFormatted)
      println(jsonResponse)

      Ok(jsonResponse)

    } catch {
      case thrown: Throwable => println(thrown) ; Ok(Json.obj("TODO" -> thrown.toString))
    }
  }
}
