# LettuceWrap
OOAD Semester Project


# Videos
* Project 7 project summary video on youtube at: https://youtu.be/o2flzs19sQU


# Disclaimer
* for any current CSCI 3155 students at CU Boulder, this is not how your Lettuce Interpreter operates in big step.


# to launch:
## prerequisites
* need `sbt` installed
* currently supports `java 11` (see `java -version`)
    * this is due to a restriction from the play framework: https://discuss.lightbend.com/t/play-sample-on-windows-java-lang-illegalstateexception-unable-to-load-cache-item/8663
    * error indicated by using the tool and seeing the following error on the backend: play.api.UnexpectedException... UncheckedExecutionException... IllegalStateException: Unable to load cache item
* need `npm` installed

## Instructions
* two terminals
* terminal 1:
    * cd scala-backend;
    * sbt compile;
        * only need to do this once or after changes to the scala baseline, not each time you run it
    * sbt run;
        * NOTE: in addition to ctrl + c killing this, just pressing enter will kill this
* terminal 2:
    * cd react-app;
    * npm install;
        * must run this the very first time.
        * must run this if making changes to package.json
    * npm run start;

## Instructions tl;dr
* two terminals
* terminal 1:
    * cd scala-backend;
    * sbt run;
* terminal 2:
    * cd react-app;
    * npm run start;


# TODO
* consider an update the UI with a page to explain the langauge grammar/a general how to page


# Development notes
Step 1: Build back-end and front-end

	npx create-react-app react-app
	cd react-scala-app

	# install material-ui
	npm install @mui/material @emotion/react @emotion/styled

	sbt new playframework/play-scala-seed.g8
	cd scala-backend
	sbt run


Step 2: create .env (in react-app to hopefully connet to scala-backend)
	REACT_APP_BACKEND_URL=http://localhost:9000

Step 3: In `scala-backend/conf/routes` add the /evaluate endpoint:

	# this looks super different now after messing with scala syntax
	POST   /evaluate         controllers.HomeController.evaluate()

Step 4: make the evaluate() method in `HomeController.scala`

	# just to make sure that the backend and frontend can communicate
	def evaluate(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
  	val json = request.body.asJson

  	json
   	 .flatMap(_.validate[String]((__ \ "expression")).asOpt)
  	  .map { expression =>
  	    Ok(Json.obj("result" -> expression))
   	 }
   	 .getOrElse {
   	   BadRequest(Json.obj("error" -> "Missing or invalid expression"))
   	 }
	}

Step 5: create `ExpressionForm.js` and `ExpressionEvaluator.js` components.

Step 6: add the components to App.js
