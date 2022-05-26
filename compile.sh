#!/bin/bash

# Script to compile the java project with binaries and libraries
# usage compile.sh <operating_system>
if [ $# -ne 1 ]; then
    echo "usage: compile.sh <operating_system>"
    exit 1
fi

# Compile the project with windows
if [ "$1" = "w" ]; then
    echo "Compiling for windows"
    javac -d bin -cp "bin;lib/jade.jar" src/figbot/*.java
    exit 0
fi

# Compile the project with linux
if [ "$1" = "u" ]; then
    echo "Compiling for unix"
    javac -d bin -cp bin:lib/jade.jar src/figbot/*.java
    exit 0
fi
