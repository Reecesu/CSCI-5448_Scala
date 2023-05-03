# Development notes
DISCLAIMER: these are likely out of date

Step 1: Build back-end and front-end

	npx create-react-app react-app
	cd react-app

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

Step 4: make the evaluate() method in `scala-backend/app/controllers/HomeController.scala`

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
