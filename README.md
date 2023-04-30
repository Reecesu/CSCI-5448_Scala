# LettuceWrap
OOAD Semester Project


# to launch:
## prerequisites
* need `sbt` installed
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
    * NEEDS TESTING:
        * try to work without this command
        * npm install @material-ui/core;
        * if not needed then remove this part from the readme
        * if needed then update readme to note that this is needed the very first time you run the code
    * npm install;
        * must run this the very first time.
        * must run this if making changes to package.json
    * npm start;

## Instructions tl;dr
* two terminals
* terminal 1:
    * cd scala-backend;
    * sbt run;
* terminal 2:
    * cd react-app;
    * npm start;


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
