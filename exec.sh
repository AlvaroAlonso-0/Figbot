#!/bin/bash

# Script to execute the java project with binaries and libraries
# usage exec.sh <operating_system>
if [ $# -ne 1 ]; then
    echo "usage: exec.sh <operating_system>"
    exit 1
fi

# Execute the project with windows
if [ "$1" = "w" ]; then
    java -cp "bin;lib/jade.jar" figbot.Figbot
    exit 0
fi

# Execute the project with unix
if [ "$1" = "u" ]; then
    java -cp bin:lib/jade.jar figbot.Figbot
    exit 0
fi
