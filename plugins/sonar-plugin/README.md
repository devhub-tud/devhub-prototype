# Devhub Sonar plugin
This is a plugin for sonar to allow to sign in via DevHub.

## Running it
You can not run, [as the Sonar site suggests](http://docs.codehaus.org/display/SONAR/Coding+a+Plugin#CodingaPlugin-Howtoquicklystarttheplugin), the plugin from maven using the plugin.

You need to package it

	mvn clean package
	
And then copy it to your Sonar plugin direcotry `${sonar.home}/extentions/plugins/`. 

In the `${sonar.home}/conf/sonar.conf` you need to define two parameters for sonar to use the DevHub plugin. *(This is the reason the plugin doesnt work using their runner plugin)*

		sonar.security.realm=DevHubRealm
		devhub.url=http://where-ever.com

Then just run Sonar to let the plugin do it's work.