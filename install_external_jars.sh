#!/usr/bin/env bash


mvn install:install-file -Dfile=./external/ajira-0.3.jar -DgroupId=ajira -DartifactId=ajira-0.3 -Dversion=0.3 -Dpackaging=jar

mvn install:install-file -Dfile=./external/ambiverse_client/target/nlu-api-client-java-1.0.0.jar  -Dsources=./external/ambiverse_client/target/nlu-api-client-java-1.0.0-sources.jar -DgroupId=ambiverse -DartifactId=nlu-api-client-java-1.0.0 -Dversion=1.0.0 -Dpackaging=jar


#Ambiverse

#git submodule update --init --recursive
#
#pushd external/ambiverse_client
#
#cp resources/client_secrets_template.json resources/client_secrets.json


#sed -i -e 's/foo/bar/g' filename