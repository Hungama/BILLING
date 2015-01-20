#!/bin/bash
CLASSPATH=.:/usr/java/jdk1.6.0_26/lib:/home/ivr/lib/mysql-connector-java-3.1.11a-bin.jar:/home/ivr/lib/activemq-all-5.4.2.jar:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/slf4j-log4j12-1.6.1.jar:/home/ivr/lib/slf4j-simple-1.5.8.jar
export CLASSPATH
cd /home/ivr/jfiles/unsubApp/UnsubAlert
/usr/java/jdk1.6.0_26/bin/javac VodafoneUnsubAlert.java
/usr/java/jdk1.6.0_26/bin/java VodafoneUnsubAlert
