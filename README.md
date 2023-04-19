# LettuceWrap
OOAD Semester Project

# to launch:
## prerequisites
* need `sbt` installed
* need `npm` installed

## instructions
* two terminals
* terminal 1:
    * cd scala-backend;
    * sbt compile;
        * only need to do this once or after changes to the scala baseline, not each time you run it
    * sbt run;
        * NOTE: in addition to ctrl + c killing this, just pressing enter will kill this
* terminal 2:
    * cd react-app;
    * NOTE: unsure if we need to load package.json in some way or if that is handled for us by `npm` 
        * only need to do this once or after changes to package.json, not each time you run it
    * npm start;

## instructions short
* two terminals
* terminal 1:
    * cd scala-backend;
    * sbt run;
* terminal 2:
    * cd react-app;
    * npm start;


# development notes
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
