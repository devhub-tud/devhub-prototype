This application is meant for Teachers from the TU-Delft to manage git repositories combined with a Jenkins and Sonar instance. 

The project is sponsored by Arie van Deursen and was initially developed by David Hartveld, Michael de Jong and Alex Nederlof.

![image](http://home.tudelft.nl/fileadmin/Default/Templates/images/logo.gif)

# Build

Build the project with

    $ mvn clean package

In the web application module you will find the working Jar.

# Run for development

Now you can run your webapp by going to the dev-env-admin-web folder and commanding maven with:

    $ mvn brew:compile jetty:run

This will start the application on port 8080. The coffee scripts will be compiled but not updated if you change them. If you plan to develop coffeescript, open a seperate command window and run. 
	
	$ mvn brew:compile -Dbrew.watch=true
	
Because this is a blocking thread, it cannot be run together with jetty:run, hence the two windows.

# Develop
Make sure you use the codestile you can find in the root folder. The best way is to enable this as a post-save action in Eclipse.

To work with Coffeescript in Eclipse, [this Eclipse Coffeescript plugin can be used](https://github.com/adamschmideg/coffeescript-eclipse). Note that this plugin depends on XText which can be found in the Eclipse Market place.