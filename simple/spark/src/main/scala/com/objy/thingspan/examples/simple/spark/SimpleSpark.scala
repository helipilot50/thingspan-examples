package com.objy.thingspan.examples.simple.spark

import scala.collection.mutable.ArrayBuffer

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types.DateType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

import com.objy.data.ClassBuilder
import com.objy.data.Encoding
import com.objy.data.LogicalType
import com.objy.data.Storage
import com.objy.data.dataSpecificationBuilder.IntegerSpecificationBuilder
import com.objy.data.dataSpecificationBuilder.ReferenceSpecificationBuilder
import com.objy.data.schemaProvider.SchemaProvider
import com.objy.db.Connection
import com.objy.db.Objy
import com.objy.db.Transaction
import com.objy.db.TransactionMode

object SimpleSpark extends App{
  
  var connection: Connection = null
  
  var conf = new SparkConf()
  	.setMaster("local[*]")
		.setAppName("SimpleSpark")
		

	// Turn off extra info for serializer exceptions (not working)
	conf.set("spark.serializer.extraDebugInfo", "false")

	val sc = new SparkContext(conf)
	val sqlContext = new SQLContext(sc);

	// start ThingSpan
	Objy.startup
	
  if (connection == null)
    connection = new Connection("../data/simple.boot")
	
	 
  // Create a simple ThingSpan Schema - if you haven't already
  val provider = SchemaProvider.getDefaultPersistentProvider()
  
  var tx = new Transaction(TransactionMode.READ_UPDATE)
  try {
    val personClassBuilder = new ClassBuilder("simple.Person").setSuperclass("ooObj")
        .addAttribute(LogicalType.STRING, "firstName")
        .addAttribute(LogicalType.STRING, "lastName")
        .addAttribute(LogicalType.DATE, "birthDate")
        .addAttribute(LogicalType.INTEGER, "shoeSize")
        
    val intSpec = new IntegerSpecificationBuilder(Storage.Integer.B64)
	            .setEncoding(Encoding.Integer.UNSIGNED)
	            .build()
		   
		val refSpecBuilder = new ReferenceSpecificationBuilder()
				.setReferencedClass("simple.Address")
				.setIdentifierSpecification(intSpec)
		
		val dataSpec = refSpecBuilder.build()
		personClassBuilder.addAttribute("address", dataSpec)

    
    val addressClassBuilder = new ClassBuilder("simple.Address").setSuperclass("ooObj")
        .addAttribute(LogicalType.STRING, "street")
        .addAttribute(LogicalType.STRING, "city")
        .addAttribute(LogicalType.STRING, "state")
        .addAttribute(LogicalType.STRING, "country")
        
    val addressClass = addressClassBuilder.build()
    val personClass = personClassBuilder.build();
           
    provider.represent(addressClass);
    provider.represent(personClass);
    
    tx.commit();
    
	} finally{
		tx.close()
	}
		
  /*
   * Write some data
   */
	
	// create schemas for Spark DataFrames 
	val personSchema =
        StructType(
            Array(
              StructField("firstName", StringType, true),
              StructField("lastName", StringType, true),
              StructField("birthDate", DateType, true),
              StructField("shoeSize", IntegerType, true)
            ))
            
	val addressSchema =
        StructType(
            Array(
              StructField("street", StringType, true),
              StructField("city", StringType, true),
              StructField("state", StringType, true),
              StructField("country", StringType, true)
            ))
            
  // Make an address
  var addressArray = ArrayBuffer[Row]()
  addressArray += Row("1 Bond St", "Ettalong Beach", "NSW", "Australia")
  val addressRDD = sc.parallelize(addressArray)
  // Apply the schema to the RDD.
    var addressDF = sqlContext.createDataFrame(addressRDD, addressSchema)
 
    // Write to ThingSpan
	addressDF.write.mode(SaveMode.Overwrite).
			format("com.objy.spark.sql").
			option("objy.bootFilePath", "../data/simple.boot").
			option("objy.dataClassName", "simple.Address").
			save()

  // Make some people
  var peopleArray = ArrayBuffer[Row]()
  peopleArray += Row("John", "Smith", new java.sql.Date(1970, 1, 1), 12) 
  peopleArray += Row("Mary", "Brown", new java.sql.Date(1968, 2, 3), 6)
  val peopleRDD = sc.parallelize(peopleArray)
  // Apply the schema to the RDD.
  var personDF = sqlContext.createDataFrame(peopleRDD, personSchema)
  
  // Write to ThingSpan
	personDF.write.mode(SaveMode.Overwrite).
			format("com.objy.spark.sql").
			option("objy.bootFilePath", "../data/simple.boot").
			option("objy.dataClassName", "simple.Person").
			save()
		
  /*
   * Create relationships
   */
	personDF = sqlContext.read.
	format("com.objy.spark.sql").
	option("objy.bootFilePath", "../data/simple.boot").
	option("objy.dataClassName", "simple.Person").
	option("objy.addOidColumn", "personOid").
	load 
	personDF.registerTempTable("personTable")

	addressDF = sqlContext.read.
	format("com.objy.spark.sql").
	option("objy.bootFilePath", "../data/simple.boot").
	option("objy.dataClassName", "simple.Address").
	option("objy.addOidColumn", "addressOid").
	load 
	addressDF.registerTempTable("addressTable")
		
	/*
	 * Spark SQL for Person and Address
	 */

  val joinDF = sqlContext.sql("""
    SELECT personTable.personOid as person, 
    personTable.lastName as lastName,
    addressTable.addressOid as address, 
    addressTable.city as city
    FROM personTable   
    WHERE lastName = 'Smith' and city = 'Ettalong Beach'""")

	joinDF.printSchema()

	joinDF.show(2)

	println("*** Start writing join to ThingSpan")
	joinDF.write.
		mode(SaveMode.Append).
			format("com.objy.spark.sql").
			option("objy.bootFilePath", "../data/simple.boot").
			option("objy.dataClassName", "simple.Person").
			option("objy.updateByOid", "personOid").
			save() 
	println("*** Finished writing join to ThingSpan")
  /*
   * Read some data
   */
		
		
  // Stop ThingSpan
  Objy.shutdown()
	// stop spark	
	sc.stop
  
}