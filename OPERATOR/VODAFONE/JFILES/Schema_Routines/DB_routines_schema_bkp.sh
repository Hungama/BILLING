#!/bin/bash

FILE_PID="/home/ivr/jfiles/Schema_Routines"
 if [ -f $FILE_PID ];
   then
        echo "File $FILE_PID exists"
        while read line
        do
                `kill -9 $line`
                echo "kill -9 $line"
        done < /home/ivr/jfiles/Schema_Routines/running_process
fi

p1=`ps ux | awk '/Schema_Routines_Bkp.sh/ && !/awk/ {print $2}'`
echo "$p1">/home/ivr/jfiles/Schema_Routines/running_process
echo $p1
IP="10.43.248.137"
user="billing"
pass="billing@voda#123"
a=`date +%Y%m%d --date="$1  days ago"`
b=`date +%Y%m --date="$1  days ago"`
a1=`date +%Y-%m-%d --date="$1  days ago"`
   
        echo "mysqldump -h $IP -u $user -p$pass --no-data --no-create-info --routines master_db > /home/ivr/jfiles/Schema_Routines/routines_$a.sql"
        mysqldump -h $IP -u $user -p$pass --no-data --no-create-info --routines master_db > /home/ivr/jfiles/Schema_Routines/routines_$a.sql

        echo "mysqldump -h $IP -u $user -p$pass --no-data --triggers master_db > /home/ivr/jfiles/Schema_Routines/schema_$a.sql"
        mysqldump -h $IP -u $user -p$pass --no-data --triggers master_db > /home/ivr/jfiles/Schema_Routines/schema_$a.sql

