# ThingSpan Examples

This repository contains some simple examples of how to use ThingSpan. Each example illistrates one or more ThingSpan features. As best as possible, each example is self contained and does not rely in data outside this repository. Each example avoids the use of features of programming languages or frameworks that could distract or confuse the reader.

The repository is organised by levels:
- [Simple](simple/README.md)
- Easy - Future
- Moderate - Future
- Advanced - Future
- Impossibly complex (only for those who have no life :)

Each level has subdirectories for you favorite language. If the language is not there, there is no example, so no whining. 

For each example there will be a README describing how to build and run the example.

Overtime, these examples will be added apon to form a more comprehensive set of tutorials.

# Getting started

Code examplecan be found at:
https://github.com/helipilot50/thingspan-examples

Clone the repository with:
```bash
	$ git clone https://github.com/helipilot50/thingspan-examples
```


# What is ThingSpan
ThingSpan is a petabyte-scalable graph distributed database platform designed specifically to allow applications to extract actionable insights from related data to enable real-time relationship discovery. 

ThingSpan leverages open-source ecosystem by integrating the Hadoop and Spark ecosystem. Scaling out is accomplished using YARN, Kafka, and Samza and Spark. ThingSpan takes advantage of Spark’s workflow management and data transformation. ThingSpan offers a seamless integration with Spark by providing it’ s own custom via DataFrame which allows ThingSpan to ingest streaming data while maintaining and persisting relationships as first-class logical models. 

Applications and platforms using ThingSpan can concurrently perform high-speed parallel ingest, as well as complex navigation and pathfinding graph queries. ThingSpan can be used in combination with Spark Streaming, Spark SQL, MLlib and any Spark library, or can be part of a bespoke written application.

ThingSpan excels in a mixed workload of writes and queries. Rather than being confined to a batch ETL or read-only queries, ThingSpan can ingest business events, construct nodes and edges in real-time and, at the same time, perform graph queries, without the loss of performance. 
 
ThingSpan takes advantage of commodity hardware and open source cluster technologies such as YARN and Mesos.  This means that ThingSpan can scale out easily on dedicated hardware, with cloud providers like EC2, GCE, Digital Ocean, Azure, etc, or with ‘bare metal’ hosting providers like Internap, RackSpace, Softlayer, etc.

## Why Graph			
In order to generate the high-performance, high-speed processing power and the sophisticated contextual analysis needed within the financial industry, an enterprise-class graph analytics solution is essential.
					
Relationships between elements of data are recorded when the data are written, rather than discovered at query time. This means that insight can be discovered at high velocity and high throughput, int real time; rather than using traditional costly JOIN queries.

Institutions need a highly scalable, real-time graph analytics platform to analyze massive volumes of data for risk, fraud, compliance, business performance, predictive analytics, and other benchmarks that are high priority to organizations. 

Because of ThingsSpan’s scalability and velocity organizations can capture streaming data and analyze it in relation to historical and contextual data to identify opportunities and risks across a broad array of use cases.

## Scale is everything 

Data stored in a Graph preserves the relationship between elements of data, but that alone is limited by the scaling capacity of the graph database. 

Being able to scale a Graph to petabytes of data allows for Data Science to discover deeper and more intricate insights. A petabyte sized graph can equate to trillions of nodes and edges, ThingSpan can store this in one federated graph, without paying a penalty in throughput or latency.
