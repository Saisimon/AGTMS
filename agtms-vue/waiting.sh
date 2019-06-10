#!/bin/sh

: ${SLEEP_SECOND:=2}

wait_for() {
    echo Ping $1 ...
    while ! ping -c1 $1; do echo waiting...; sleep $SLEEP_SECOND; done
}

declare DEPENDS
declare CMD

while getopts "d:c:" arg
do
    case $arg in
        d)
            DEPENDS=$OPTARG
            ;;
        c)
            CMD=$OPTARG
            ;;
        ?)
            echo "unkonw argument"
            exit 1
            ;;
    esac
done

for var in ${DEPENDS//,/ }
do
    wait_for $var
done

eval $CMD