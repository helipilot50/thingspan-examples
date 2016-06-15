# Simple Example

This simple example demonstrates how to use the THingSpan API from Scala. Th this example you will seed how to create a Schema programatically, create persistent objects and read the objects from the database.

## How to build
To build this example, ensure you have the following dependencies:

- Java JDK 1.7+
- Scala 2.10+
- SBT 0.13

Change directory to `simple/scala`

```bash
	$ cd simple/scala
```

The run the SBT build with:

```bash
	$ sbt assembly
```

The build will create the uber JAR in `target/scala-2.10` with the JAR name of: `Scala-assembly-1.0.jar`

## How to run
Run the following command and you will see output printed to the console.
```bash
	$ sbt run
```

## Discussion

The example is a deliberately simple Scala program consisting of a single object `SimpleAPI` that extends the `App` trait. The Scala code is elementary so as not to distract you from understanding the ThingSpan low level API.

### Connecting to ThingSpan
The first step is to connect ThingSpan
```scala
    var connection: Connection = null
  
    /*
     * Start ThingSpan - The ThingSpan library is written in C/C++, 
     * this call performs the JNI binding
     */
    Objy.startup()
    
    /*
     * Connect to the database
     */
    if (connection == null)
      connection = new Connection("../data/simple.boot")
```
This statement `Objy.startup()` binds the Scala/Java API to the underlying implementation in C/C++. To connect to the database, create a `Connection` with 
```scala
new Connection("../data/simple.boot")
```
The constructor takes a single parameter that is a path to the "boot file".

### Creating Schema

### Writing data

### Reading Data






