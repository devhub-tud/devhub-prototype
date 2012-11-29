#!/bin/sh
PORT=8083
STOPPORT=8086
HALT_KEY=sdfli67usd7hrk344lwe1r
WAR_NAME=devhub.war
PIDFILE=jetty.pid
USER=devhub

waitForStop() {
  PID=`cat $PIDFILE`
  echo "Sending DevHub the stop signal"
  echo -e "$HALT_KEY\nstop" > /dev/tcp/localhost/$STOPPORT
  sleep 3;
  if [ "$(ps -p $PID | wc -l)" -gt 1 ]; then
    echo "Process still running";
    sleep 5;
    if [ "$(ps -p $PID | wc -l)" -gt 1 ]; then
      echo "Process hasent shut down. Killing it.";
      kill -9 $PID
      rm $PIDFILE;
      sleep 3;
    else
      echo "Shutdown complete"
    fi
  else
    echo "Shutdown complete"
  fi
}

startDevHub() {
    if [ -f $PIDFILE ] ; then
		PID=`cat $PIDFILE`;
                if [ "$(ps -p $PID | wc -l)" -gt 1 ]; then
                        echo "Process already running with id $(ps -p $PID | wc -l)"
                        exit 1
                else
                        chown $USER $PIDFILE
                fi
        else
                touch $PIDFILE
                chown $USER $PIDFILE
        fi
    echo "Starting DevHub"
    java -jar jetty-runner.jar --port $PORT --stop-port $STOPPORT --stop-key $HALT_KEY $WAR_NAME > jetty.out 2>&1 &
    echo $! > $PIDFILE
}

case "$1" in
  start)
    startDevHub;
    ;;
  stop)
    waitForStop;
    ;;
  restart)
    echo "Restarting DevHub"
    waitForStop;
    startDevHub;
    ;;
  *)
    echo "Use start, stop or restart for this script"
    ;;
esac


exit 0
