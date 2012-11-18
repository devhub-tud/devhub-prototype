# DevHub

This application is meant for Teachers from the TU-Delft to manage git repositories combined with a Jenkins and Sonar instance. 

The project is sponsored by Arie van Deursen and was initially developed by David Hartveld, Michael de Jong and Alex Nederlof.

![image](http://home.tudelft.nl/fileadmin/Default/Templates/images/logo.gif)

## Configure
Under *web/env* you can put your configuration. You must create a config folder structure like this:

	web/src/local/META-INF/persistence.xml
	web/src/local/serverconfig.json
	web/src/production/META-INF/persistence.xml
	web/src/production/serverconfig.json

You can find and example `persistence.xml` in `db/src/test/resources` and an example `serverconfig.json` in `web/env/serverconfig.json.example`.

## Build
Build the project with

    $ mvn clean package

In the web application module you will find the working War. This was can be deployed in any servlet container.

## Run for development
You can run the application from Eclipse by starting: `nl.tudelft.ewu.dea.DevHubServer.java`

You can also run the application using Maven by first installing the everything using:

	mvn install
	
And then browsing to the web module and running:
	
	mvn exec:java
	
## Deployement
You can deploy the application into any servlet container compatible with Servlet API 2.5 or later. You can also rund it using the [Jetty-runner](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jetty-runner%22). You can find an example start-stop script in `web/scripts/`.


## Test
All tests are run by Maven except for the Jasmine Javascript tests. They are started by running

	$ mvn jasmine:bdd
	
This will be automated sometime in the furute, when the Jasmine plugin is functioning properly.

## Develop
Make sure you use the codestile you can find in the root folder. The best way is to enable this as a post-save action in Eclipse.

To work with Coffeescript in Eclipse, [this Eclipse Coffeescript plugin can be used](https://github.com/adamschmideg/coffeescript-eclipse). Note that this plugin depends on XText which can be found in the Eclipse Market place.