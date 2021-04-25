# FactSpotting

An engine to spot mentions of fact (in triple formats) in text corpora either indexed over Elasticsearch or through Bing API. The fact can appear in text in positive or negative context. Fact Spotting Engine is used as the under laying evidence curotor for _ExFaKT_

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

~~## Running Fact Spotting~~

~~run `mvn install` to generate `*.sh` files to run different project parts. The generated scripts can be found under `assemble/bin`~~

~~Note: The reserved memory can be edited from `pom.xml` file before installing or the generated `.sh` afterwards.~~

~~After installing to run use script `exper_cli.sh` is runs java class `SentenceRetrievalExperiment.java`
TODO: add description for the parameters~~













