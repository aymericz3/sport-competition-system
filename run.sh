#!/usr/bin/env bash
set -e
./build.sh
java -cp "out:lib/gson-2.11.0.jar" com.sports.app.Main "$@"
