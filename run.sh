#!/usr/bin/env sh

# ./run.sh 3 70 4 11 12 13 14 2 60 5 7 6 5 4 1 50 6 8 9 10 2 15

java -cp compiled:lib/jade.jar Main "$@"
