# FactChecking

Checking the correctness of a fact based on some text corpus. The fact can appear in text in positive or negative context.

## running

'mvn compile'

'mvn package'

## Stics Corpus

package 'de.mpii.sticsAnalysis' parses stics news corpus 

class 'de.mpii.sticsAnalysis.CLIClass' offers an interface to load a file of stics articles. Then it can be quired with list of YAGO3 entities sperated with comas. It returns the sentences that contain any of these entities in the documents that contains all of them.



