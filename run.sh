#!/bin/bash
if [ $# -lt 1 ]; then
    sbt run
else
    sbt "run-main com.sconysoft.robocode.Main 2551"
fi
