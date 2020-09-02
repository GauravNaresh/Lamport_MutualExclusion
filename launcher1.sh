#!/bin/bash

# Change this to your netid
netid=mxv180000

# Root directory of your project
PROJDIR=$HOME/Desktop/AOS/Project2_final

# Directory where the config file is located on your local system
CONFIG=config.txt

# Directory your java classes are in
BINDIR=$PROJDIR/bin

# Your main project class
PROG=ProjectMain

n=0

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read line
    count=$(echo $line | awk '{print $1}')
    while [[ $n -lt $count ]]
    do
    	read line
    	p=$( echo $line | awk '{ print $1 }' )
        host=$( echo $line | awk '{ print $2 }' )
       	echo $p
	echo $host 
        gnome-terminal -e "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $p ; exec bash" &   

        n=$(( n + 1 ))
    done
)

