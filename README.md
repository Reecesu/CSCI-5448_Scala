# LettuceWrap
**OOAD Semester Project**

Reece Suchocki & Spencer Wilson


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
    * npm --version
    * 9.6.3
    * node --version
    * v19.9.0

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
