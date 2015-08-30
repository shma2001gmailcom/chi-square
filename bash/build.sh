#!/usr/bin/env bash
#set M2_HOME here
M2_HOME='/opt/apache-maven-3.2.2/'
export M2_HOME
M2=${M2_HOME}/bin
export M2
PATH=${PATH}:${M2}
export PATH
projectHome=../
cd ${projectHome}
mvn clean package
cp -r src/main/resources/ target/

