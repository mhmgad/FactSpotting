# FactSpotting

Checking the correctness of a fact based on some text corpus. The fact can appear in text in positive or negative context.

## Installing External Libs

run

`sh install_external_jar.sh`

## running

`mvn compile`
`mvn package`


## Elasticsearch Remote Access

This is a problem with elasticsearch 5.x, when one binds an address different than localhost to the server, it is switched to production mode which may not work easily due to some checks. 
Therefore, the easiest way is to tunnel the connection to the hosting server and forword the port requests. This can be achieved by calling

`ssh -f <server> -L <serverPort>:localhost:<localPort> -N`

for instant if it is hosted on `sedna`

`ssh -f sedna.mpi-inf.mpg.de -L 9200:localhost:9200 -N `

## Configuration

file `src/main/resources/factchecking.properties` can be used to configure the spotting. 
TODO: add description for the configuration parameters

## Running Fact Spotting 

run `mvn install` to generate `*.sh` files to run different project parts. The generated scripts can be found under `assemble/bin`

Note: The reserved memory can be edited from `pom.xml` file before installing or the generated `.sh` afterwards.

After installing to run use script `exper_cli.sh` is runs java class `SentenceRetrievalExperiment.java`
TODO: add description for the parameters 





## ~~Stics Corpus (Old-deprecated)~~

~~package 'de.mpii.sticsAnalysis' parses stics news corpus~~

~~class 'de.mpii.sticsAnalysis.CLIClass' offers an interface to load a file of stics articles. Then, it can be queried with list of YAGO3 entities separated with comas. It returns the sentences that contain **any of these entities** in the documents that **contains all of them**.~~

~~to run it use `sh ./assemble/bin/stics_data.sh <stics file uncompressed> <-f: to write results to a file>`~~










