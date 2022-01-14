#!/bin/bash

SERVER_NAME=ShardingSphere-Proxy

cd `dirname $0`
cd ..
DEPLOY_DIR=`pwd`

LOGS_DIR=${DEPLOY_DIR}/logs
if [ ! -d ${LOGS_DIR} ]; then
    mkdir ${LOGS_DIR}
fi

STDOUT_FILE=${LOGS_DIR}/stdout.log
CLASS_PATH=.:${DEPLOY_DIR}/lib/*:${DEPLOY_DIR}/conf/*

MAIN_CLASS=com.sphereex.Bootstrap

print_usage() {
#    echo "usage: auto-test.sh [ip] [port] [dbname] [user] [password] [feature] [tag] [casename]"
    echo "usage: auto-test.sh [ip] [port] [dbname] [user] [password] [casename]"
    echo "  ip: shardingsphere proxy ip, not null"
    echo "  port: shardingsphere proxy port, not null"
    echo "  dbname: shardingsphere proxy dbname, not null"
    echo "  user: shardingsphere proxy user, not null"
    echo "  password: shardingsphere proxy password, not null"
#    echo "  feature: cases in the feature will run"
#    echo "  tag: must define feature, cases with the tag will run"
#    echo "  casename: must define feature and tag, the case will run"
    echo "  casename: separate case names with commas "
    exit 0
}

if [ "$1" == "-h" ] || [ "$1" == "--help" ] || [ $# == 0 ] ; then
    print_usage
fi

if [ $# -lt 5 ]; then
      echo "dbinfo is incomplete"
      exit 0
fi

JAVA_OPTS=" -Dip=$1 -Dport=$2 -Ddbname=$3 -Duser=$4 -Dpassword=$5"
APP_ARGS=""

#if [ $# == 6 ]; then
#    JAVA_OPTS= " $JAVA_OPTS -Dfeature=$6"
#fi
#
#if [ $# == 7 ]; then
#    JAVA_OPTS= " $JAVA_OPTS -Dtag=$7"
#fi

if [ $# == 6 ]; then
    APP_ARGS=$6
#    JAVA_OPTS= " $JAVA_OPTS -Dcasename=$8"
fi

echo " java_option: $JAVA_OPTS"

echo "The classpath is ${CLASS_PATH}"

nohup java ${JAVA_OPTS} -classpath ${CLASS_PATH} ${MAIN_CLASS} ${APP_ARGS} >> ${STDOUT_FILE} 2>&1 &
sleep 1
echo "Please check the STDOUT file: $STDOUT_FILE"
