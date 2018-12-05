# SparkCollector
## Requirements

MacOS with Apache-Spark 2.4.0 and Java8 installed.

*   Java 8 `$ brew update; brew tap homebrew/cask-versions; brew cask install java`
*   Apache-Spark `$ brew install apache-spark`
## Usage

Write code: extends AbstractCollector to use the project

Build:

`$ mvn package`

Run: collect data from one seed.

`$ spark-submit --class collector.Example target/simple-project-1.0.jar`

Output examples: 

- Time: 1543984800000 ms
- (293,www.bbc.com#)
- (293,www.bbc.com#orb-modules)
- (293,www.bbc.com)
- (278,www.bbc.co.uk/news)
- (268,www.bbc.co.uk)
- (243,www.bbc.co.uk/sport)
- (74,www.bbc.co.uk/accessibility)

Http Request:

- http://localhost:8500

