#!/bin/bash
CLASSPATH=.:/usr/java/jdk1.6.0_26/lib:/home/ivr/lib/mysql-connector-java-3.1.11a-bin.jar:/home/ivr/lib/activemq-all-5.4.2.jar:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/slf4j-log4j12-1.6.1.jar:/home/ivr/lib/slf4j-simple-1.5.8.jar

dt=`date +%Y-%m-%d --date="$1 days ago"`
dt1="$(date +"%Y-%m-%d")"
file="/usr/local/apache-tomcat-6.0.35/logs/localhost_access_log.$dt.txt"
file1="/home/ivr/jfiles/Alert_System/recorecord.csv"
echo $dt
echo $file

if [ -f $file ]
then
echo "File already Processed"
export CLASSPATH
cd /home/ivr/jfiles/Alert_System/
#/usr/java/jdk1.6.0_26/bin/javac Voda_Alert_Demo.java
/usr/java/jdk1.6.0_26/bin/java Voda_Alert_Demo
mysql -h 10.43.248.137 -u ivr -pivr  test -e "truncate table test.voda_recoAlert;"
echo "truncate table successfully"
echo "now data is loading into table"
mysql -h 10.43.248.137  -u ivr -pivr test -e "LOAD DATA LOCAL INFILE '/home/ivr/jfiles/Alert_System/recorecord.csv' INTO TABLE voda_recoAlert  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n' (msisdn,event,status,tranid);"
else
echo "File not found"
fi


