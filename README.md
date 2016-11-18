# FactChecking

Checking the correctness of a fact based on some text corpus. The fact can appear in text in positive or negative context.

## Installing External Libs

run

`sh install_external_jar.sh`

## running

`mvn compile`
`mvn package`

## Stics Corpus

package 'de.mpii.sticsAnalysis' parses stics news corpus 

class 'de.mpii.sticsAnalysis.CLIClass' offers an interface to load a file of stics articles. Then, it can be queried with list of YAGO3 entities separated with comas. It returns the sentences that contain **any of these entities** in the documents that **contains all of them**.

to run it use `sh ./assemble/bin/stics_data.sh <stics file uncompressed> <-f: to write results to a file>`


## General Text Corpus 

(In progress no running script yet!)



