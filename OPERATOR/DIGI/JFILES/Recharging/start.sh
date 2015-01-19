#!/bin/bash
#set -x
#elinks --dump "http://119.82.69.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8586968482&shortcode=HUNVOC&msgtype=plaintext&msg=RECHARGING_STARTED%20on%20219%20server"
#elinks --dump "http://119.82.69.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8586968481&shortcode=HUNVOC&msgtype=plaintext&msg=RECHARGING_STARTED%20on%20219%20server"
CLASSPATH=$CLASSPATH:.:/home/ivr/lib/activemq-all-5.5.0.jar:/home/ivr/lib/saaj-api.jar:/home/ivr/lib/log4j-1.2.14.jar:/home/ivr/lib/slf4j-api-1.5.11.jar:/home/ivr/lib/slf4j-log4j12-1.5.11.jar:/home/ivr/lib/mysql.jar;
export CLASSPATH
cd /home/ivr/jfiles/Recharging/ 
javac Recharging_main.java
 java Recharging_main  >> gsk.txt
