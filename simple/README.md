# Simple Example

This simple example demonstrates how to use the ThingSpan API. It Covers:

- Schema creation
- Object creation
- Object retrieval

The example is available for the following environments:

- [Scala](scala/README.md)
- Java - Future
- Spark - Future

Each environment contains specific build and run instructions

## Install ThingSpan

Install ThingSpan by 

- Downloading
- Installing

The shell script `setup.sh` will start the ThingSpan lock server and create a federated database. The lock manager must be running before you can execute the example. But the federated database only needs to be created once.

```bash
	$ ./setup.sh
```
This script also has commands, commented out, that export the schema and delete the federated database. 


