package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.{Json, JsValue}

import saladbar._

/**
  * HomeController
  * 
  * OO PATTERN: Proxy
  * OO PATTERN: Controller
  *
  * @param controllerComponents
  */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def evaluateMethod: Action[AnyContent] = Action { implicit request =>

    try {
      val json = request.body.asJson.get
      val UserInput(evaluationConditions, expression) = UserInput(json)

      val interpreter = new Interpreter(evaluationConditions)
      val EvaluationResponse(jsonResponse) = interpreter.evaluate(expression){ 
        steppedExpressions => EvaluationResponse(expression, steppedExpressions)
      }
      println(jsonResponse)

      Ok(jsonResponse)

    } catch {
      case thrown: Throwable => println(thrown) ; Ok(Json.obj("TODO" -> thrown.toString))
    }
  }
}

case class UserInput(evaluationConditions: EvalConditions, expresion: Expr)
object UserInput {
  /**
   * {
   *  evaluationConditions: {
   *    scope: <>,
   *    types: <>,
   *    lazyEager: <>,
   *  },
   *  expression: <>
   * }
   */
  def apply(json: JsValue) = {
      val evaluationConditions = json \ "evaluationConditions"
      val sScope = (evaluationConditions \ "scope").as[String]
      val sTypes = (evaluationConditions \ "types").as[String]
      val sLazyEager = (evaluationConditions \ "lazyEager").as[String]
      val scope = if (sScope == LexicalScope.toString) LexicalScope else DynamicScope
      val types = if (sTypes == NoConversions.toString) NoConversions else ImplicitConversions
      val lazyEager = if (sLazyEager == EagerCondition.toString) EagerCondition else LazyCondition
      val evalConditions = new EvalConditions(scope, types, lazyEager)
      println(evalConditions)
      println(sScope)
      println(LexicalScope.toString)

      val parser = new saladbar.Parser
      val se = (json \ "expression").as[String]
      println(s"processing expression: $se\nwith evaluationconditions: $evalConditions")
      val e = parser.parse(se)

      new UserInput(evalConditions, e)
  }
}


/**
 * {
 *  "expression": "<>",
 *  "value": "<>",
 *  "steps": ["<>"]
 * }
 */
case class EvaluationResponse(json: JsValue)
object EvaluationResponse {
  def apply(originalExpression: Expr, steppedExpressions: List[Expr]) = {
    val value = steppedExpressions.last
    val json = Json.obj("expression" -> originalExpression.toString(),
        "value" -> value.toString(),
        "steps" -> (steppedExpressions map { e => e.toString() })
        );
    new EvaluationResponse(json)
  }
}
