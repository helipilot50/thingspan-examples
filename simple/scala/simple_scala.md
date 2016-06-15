# Simple Example

This simple example demonstrates how to use the THingSpan API from Scala. It Covers:

- Schema creation
- Object creation
- Object retrieval

## How to build
To build this example, ensure you have the following dependencies:

- Java JDK 1.7+
- Scala 2.10+
- SBT 0.13
- Spark 1.6+

### Install ThingSpan

Install ThingSpan by 

- Downloading
- Installing

The shell script `setup.sh` will start the ThingSpan lock server and create a federated database

```bash
	$ ./setup.sh
```
 

First clone the GitHub repository https://github.com/helipilot50/thingspan-examples and change directory to `simple/scala`

```bash
	$ git clone https://github.com/helipilot50/thingspan-examples
	$ cd simple/scala
```

The run the SBT build with:

```bash
	$ sbt assembly
```

The build will create the uber JAR `

## How to run

Create a ThingSpan federated database.



