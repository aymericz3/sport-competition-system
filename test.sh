#!/usr/bin/env bash
set -e
CP="lib/gson-2.11.0.jar:lib/junit-platform-console-standalone-1.10.2.jar:out"
find src test -name '*.java' > sources.txt
javac -d out -cp "$CP" @sources.txt
java -jar lib/junit-platform-console-standalone-1.10.2.jar \
     --class-path "out:lib/gson-2.11.0.jar" --scan-class-path
