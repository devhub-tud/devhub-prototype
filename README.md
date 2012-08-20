# Build

Build the project with

    $ mvn install

# Configure

You will need to set the `REPO` environment variable, so the execution wrapper script knows where to find the maven dependencies. For example:

    $ export REPO=$HOME/.m2/repository

# Run

Now you can run your webapp with:

    $ sh dev-env-admin-web/target/bin/webapp

(the wrapper script is not executable by default).

# Develop
You can start the application with maven by first installing from the parent project, and then browsing to the web project and executing:

	# mvn exec:java