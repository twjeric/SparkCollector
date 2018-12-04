# SparkCollector
## Requirements

MacOS with Apache-Spark 2.4.0 and Java8 installed.

*   Java 8 `$ brew update; brew tap homebrew/cask-versions; brew cask install java`
*   Apache-Spark `$ brew install apache-spark`
## Usage

Build:

`$ mvn package`

Run: get stream inputs from tcp connection

`$ spark-submit --class collector.Network target/simple-project-1.0.jar` 