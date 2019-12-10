#!/usr/bin/env sh

mkdir -p compiled
javac -cp lib/jade.jar:src -d compiled src/Main.java
