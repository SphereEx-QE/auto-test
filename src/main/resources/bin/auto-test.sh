#!/bin/bash

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
    echo "usage: auto-test.sh [-h ip] [-P port] [-d dbname] [-u user] [-p password] [-f feature] [-t tag] [-c casename] [-T type] [-l]"
    echo "  -h|--host  ip: shardingsphere proxy ip"
    echo "  -P|--port: shardingsphere proxy port"
    echo "  -d|--dbname: shardingsphere proxy dbname"
    echo "  -u|--user: shardingsphere proxy user"
    echo "  -p|--password: shardingsphere proxy password"
    echo "  -f|--feature: run the cases of the the feature"
    echo "  -t|--tag: run the cases of the tag"
    echo "  -c|--casenames: run this cases "
    echo "  -l|--list: list the cases "
    echo "  --dbtype: DB type "
    echo "  --clienttype: client type, eg: proxy|jdbc"
    exit 0
}

if [ $# == 0 ] ; then
    print_usage
fi

host=""
port=""
db_name=""
user=""
password=""
feature=""
tag=""
casename=""
list=1
dbType=""
clienttype=""
GETOPT_ARGS=`getopt -o h:P:d:u:p:f:t:c:l --long host:,port:,db_name:,user:,password:,feature:,tag:,casename:,dbtype:,clienttype:,list "$@"`
while [ -n "$1" ]
do
        case "$1" in
                -h|--host) host=$2; shift 2;;
                -P|--port) port=$2; shift 2;;
                -d|--db_name) db_name=$2; shift 2;;
                -u|--user) user=$2; shift 2;;
                -p|--password) password=$2; shift 2;;
                -f|--feature) feature=$2; shift 2;;
                -t|--tag) tag=$2; shift 2;;
                -c|--casename) casename=$2; shift 2;;
                --dbtype) dbType=$2; shift 2;;
                --clienttype) clienttype=$2; shift 2;;
                -l|--list) list=0; shift 1;;
                --) break ;;
                *) break ;;
        esac
done
JAVA_OPTS=""
if [ "$host" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Dip=${host}"
fi
if [ "$port" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Dport=${port}"
fi
if [ "$db_name" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Ddbname=${db_name}"
fi
if [ "$user" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Duser=${user}"
fi
if [ "$password" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Dpassword=${password}"
fi
if [ "$feature" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Dfeature=${feature}"
fi
if [ "$tag" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Dtag=${tag}"
fi
if [ "$dbType" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -DdbType=${dbType}"
fi
if [ "$clienttype" != "" ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -DclientType=${clienttype}"
fi
if [ $list -eq 0 ]; then
    JAVA_OPTS=" ${JAVA_OPTS} -Dlist=${list}"
    java ${JAVA_OPTS} -classpath ${CLASS_PATH} ${MAIN_CLASS} ${casename}
    exit 0
fi

echo " java_option: ${JAVA_OPTS}"

echo "The classpath is ${CLASS_PATH}"

nohup java ${JAVA_OPTS} -classpath ${CLASS_PATH} ${MAIN_CLASS} ${casename} >> ${STDOUT_FILE} 2>&1 &
sleep 1
echo "Please check the STDOUT file: $STDOUT_FILE"
