#!/bin/bash
# govna - Startup script for govna via jetty-runner

# chkconfig: 35 85 15
# description: Govna is an applicaton that governs build time dependencies.
# processname: java -jar jettyrunner.jar govna.war
# config: /etc/govna.conf
# pidfile: /var/run/govna/govna.pid

. /etc/rc.d/init.d/functions

PID_DIR="/var/run"
PID_FILE="${PID_DIR}/govna.pid"
PORT="8069"
GOVNA_USER="govna"
GOVNA_GROUP="govna"
GOVNA_HOME="/opt/govna"
GOVNA_WAR="govna.war"
GOVNA_LOG="/var/log/govna-requests.log"
GOVNA_OUT="/var/log/govna.log"
JETTY_RUNNER_FILE="jetty-runner-8.1.9.v20130131.jar"

write_pid_file()
{
    PID=`ps -ef | grep jetty | grep -v grep | awk '{print $2}'`

    echo ${PID} > ${PID_FILE}

}

start()
{
    # Make sure the default pidfile directory exists
    if [ ! -d ${PID_DIR} ]; then
        install -d -m 0755 -o ${GOVNA_USER} -g ${GOVNA_GROUP} ${PID_DIR}
    fi

    # Verify we can run with Jetty runner
    if [ ! -f ${GOVNA_HOME}/${JETTY_RUNNER_FILE} ]; then
        echo "[ERROR] Missing Jetty Runner jar!"
        echo " Could not find: ${GOVNA_HOME}/${JETTY_RUNNER_FILE}"
        exit 1
    fi

    # is the govna.war there?
    if [ ! -f ${GOVNA_HOME}/${GOVNA_WAR} ]; then
        echo "[ERROR] Missing the govna.war!"
        echo " Could not find: ${GOVNA_HOME}/${GOVNA_WAR}"
        exit 1
    fi

    #
    #  Look we are a POC let's open some things up a bit.
    #
    ulimit -f unlimited
    ulimit -t unlimited
    ulimit -v unlimited
    ulimit -n 64000
    ulimit -m unlimited
    ulimit -u 32000

    echo -n "Starting Jetty Runner Govna Application:"
    cd ${GOVNA_HOME}
    java -jar ${JETTY_RUNNER_FILE} --port ${PORT} --log ${GOVNA_LOG} --out ${GOVNA_OUT} ${GOVNA_WAR} &
    RETVAL=$?
    echo
    [ ${RETVAL} -eq 0 ] && touch /var/lock/subsys/govna
    write_pid_file
}

stop()
{
  echo -n $"Stopping Jetty Runner Govna Application: "
  killproc -p "${PID_FILE}" -d 90
  RETVAL=$?
  echo
  [ ${RETVAL} -eq 0 ] && rm -f /var/lock/subsys/govna
}

restart () {
	stop
	start
}

RETVAL=0

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart|reload|force-reload)
    restart
    ;;
  condrestart)
    [ -f /var/lock/subsys/govna ] && restart || :
    ;;
  status)
    status -p ${PID_FILE}
    RETVAL=$?
    ;;
  *)
    echo "Usage: $0 {start|stop|status|restart|reload|force-reload|condrestart}"
    RETVAL=1
esac

exit ${RETVAL}

