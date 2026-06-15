#!/usr/bin/env bash
set -e
CP="lib/gson-2.11.0.jar"
find src -name '*.java' > sources.txt
javac -d out -cp "$CP" @sources.txt
echo "Build OK → out/"
