#!/bin/bash

WHICH="/usr/bin/which"



        if [ "$#" -ne 1 ]
        then
                clear
                echo ""
                echo "Usage: startMultiThread <<Count>>"
                echo""
                exit 5
        fi
        count=$1

        echo "count " $count

        for(( i = 0; i < $count; i++ ))
  do
                #echo "Starting loop" $i
                nohup java  RESUBreciver &
        done

