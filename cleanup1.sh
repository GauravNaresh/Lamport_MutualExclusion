#!/bin/bash


# Change this to your netid
netid=mxv180000

#
# Root directory of your project
PROJDIR=/home/013/m/mx/mxv180000/Desktop/AOS/Project2_final

#
# Directory where the config file is located on your local system
CONFIGLOCAL=/home/013/m/mx/mxv180000/Desktop/AOS/Project2_final/config.txt

n=0

cat $CONFIGLOCAL | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read line
    count=$(echo $line | awk '{print $1}')
    while [[ $n -lt $count ]]
    do
    	read line
        host=$( echo $line | awk '{ print $2 }' )

        echo $host
        ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "$netid@$host" killall -u $netid &
        sleep 1

        n=$(( n + 1 ))
    done
   
)


echo "Cleanup complete"
