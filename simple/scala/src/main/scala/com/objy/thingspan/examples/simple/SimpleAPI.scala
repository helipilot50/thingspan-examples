package com.objy.thingspan.examples.simple

import java.util.Date

import com.objy.data.ClassBuilder
import com.objy.data.Encoding
import com.objy.data.Instance
import com.objy.data.LogicalType
import com.objy.db.Date
import com.objy.data.Storage
import com.objy.data.Variable
import com.objy.data.dataSpecificationBuilder.IntegerSpecificationBuilder
import com.objy.data.dataSpecificationBuilder.ReferenceSpecificationBuilder
import com.objy.db.Connection
import com.objy.db.Objy
import com.objy.db.Transaction
import com.objy.db.TransactionMode
import com.objy.expression.ExpressionTreeBuilder
import com.objy.expression.OperatorExpressionBuilder
import com.objy.expression.language.LanguageRegistry
import com.objy.statement.Statement
import com.objy.data.schemaProvider.SchemaProvider
import com.objy.data.Reference
import com.objy.db.ObjectId


/*
 * Before you run this example, create a federated database in the directory 'data'
 * 
 * $ objy createFd -fdname simple -fdDirPath data
 * Objectivity/DB (TM) Create FD, Version: 12.0.0 develop May  3 2016
 * Copyright (c) Objectivity, Inc 2012, 2016. All rights reserved.
 * 
 * Federated Database successfully created:
 *   FD Dir Host      : flight1
 *   FD Dir Path      : /simple/data
 *   System DB file   : simple.fdb
 *   Boot file        : simple.boot
 *   Lock server host : flight1
 * 
 * 
 */
object SimpleAPI extends App {
  
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
    

    
    /*
     * Create a simple Schema
     */
    
    val provider = SchemaProvider.getDefaultPersistentProvider()
    /*
     * Every operation in THingSpan is done withing a transaction
     */
    var tx = new Transaction(TransactionMode.READ_UPDATE)
    try {
      /*
       * Create the schema for a 'Person' type
       */
      val personClassBuilder = new ClassBuilder("simple.Person").setSuperclass("ooObj")
          .addAttribute(LogicalType.STRING, "firstName")
          .addAttribute(LogicalType.STRING, "lastName")
          .addAttribute(LogicalType.DATE, "birthDate")
          .addAttribute(LogicalType.INTEGER, "shoeSize")
          
      val intSpec = new IntegerSpecificationBuilder(Storage.Integer.B64)
	            .setEncoding(Encoding.Integer.UNSIGNED)
	            .build()
		  
	    /*
		   * create a reference from 'Person' to 'Address' 
		   */
  		val refSpecBuilder = new ReferenceSpecificationBuilder()
  				.setReferencedClass("simple.Address")
  				.setIdentifierSpecification(intSpec)
  		val dataSpec = refSpecBuilder.build()
  		personClassBuilder.addAttribute("address", dataSpec)

  		/*
  		 * Create the schema for an 'Address' type
  		 */
      val addressClassBuilder = new ClassBuilder("simple.Address").setSuperclass("ooObj")
          .addAttribute(LogicalType.STRING, "street")
          .addAttribute(LogicalType.STRING, "city")
          .addAttribute(LogicalType.STRING, "state")
          .addAttribute(LogicalType.STRING, "country")
          
      val addressClass = addressClassBuilder.build()
      val personClass = personClassBuilder.build();
      
      /*
       * save the schema
       */
      provider.represent(addressClass);
      provider.represent(personClass);
      
      tx.commit();
      
  	} finally{
  		tx.close()
  	}

    /*
     * Write some data
     */
  	tx = new Transaction(TransactionMode.READ_UPDATE)
    try {
      // Get the persistent class for Address
      val addressClass = com.objy.data.Class.lookupClass("simple.Address")
      // Create an persistent instance
      var address = Instance.createPersistent(addressClass);
      
      // Add some attribute/field values to address
      address.getAttributeValue("street").set("1 Bond St")
      address.getAttributeValue("city").set("Ettalong Beach")
      address.getAttributeValue("state").set("NSW")
      address.getAttributeValue("country").set("Australia")

      // Get the persistent class for Person
      val personClass = com.objy.data.Class.lookupClass("simple.Person")
      // Create an persistent instance and add some attribute/field values to person
      var person = Instance.createPersistent(personClass);
      person.getAttributeValue("firstName").set("John")
      person.getAttributeValue("lastName").set("Smith")
      person.getAttributeValue("birthDate").set(new com.objy.db.Date(1970, 1, 1))
      person.getAttributeValue("shoeSize").set(12)
      // Relate address to person
      val addressReference = new Reference(address)
      person.getAttributeValue("address").set(addressReference)    

       // Create another Person
      var person2 = Instance.createPersistent(personClass);
      person2.getAttributeValue("firstName").set("Mary")
      person2.getAttributeValue("lastName").set("Brown")
      person2.getAttributeValue("birthDate").set(new com.objy.db.Date(1968, 2, 3))
      person2.getAttributeValue("shoeSize").set(6)


      tx.commit()
      
  	} finally{
  		tx.close()
  	}
  	
    /*
     * Read some data
     */
  	tx = new Transaction(TransactionMode.READ_ONLY)
    try {
      val ooObjClass = com.objy.data.Class.lookupClass("ooObj")
      val addressClass = com.objy.data.Class.lookupClass("simple.Address")
      val personClass = com.objy.data.Class.lookupClass("simple.Person")

      val lastName = "Smith"
      
      /*
       * Equlivent SQL: select * from Person where lastName = 'Smith'
       */
      val opExp = new OperatorExpressionBuilder("From")
            .addLiteral(new Variable(personClass))
            .addOperator(new OperatorExpressionBuilder("==")
                    .addObjectValue("lastName")
                    .addLiteral(new Variable(lastName))
        ).build()
      	
      val exprTreeBuilder = new ExpressionTreeBuilder(opExp)
			val exprTree = exprTreeBuilder.build(LanguageRegistry.lookupLanguage("DO"))
			
			val statement = new Statement(exprTree)
			
			val results = statement.execute()
		
			val pathItr = results.sequenceValue().iterator()
			
			while(pathItr.hasNext()){
  			val path = pathItr.next()
  			
  			// get the person
  			val personInstance = path.instanceValue()
  			val lastName = personInstance.getAttributeValue("lastName").stringValue
  			val firstName = personInstance.getAttributeValue("firstName").stringValue
  			val shoeSize = personInstance.getAttributeValue("shoeSize").intValue()
  			val birthDate = personInstance.getAttributeValue("birthDate").dateValue
  			println(s"Found $firstName $lastName")
  			println(s"\tBirth Date: $birthDate")
  			println(s"\tShoe Size: $shoeSize")
  			
  			// get the reference to the address
  			val addressRef = personInstance.getAttributeValue("address").referenceValue
  			// get the object instance
        val addressInstance = addressRef.getReferencedObject
  			val street = addressInstance.getAttributeValue("street").stringValue()
  			val city = addressInstance.getAttributeValue("city").stringValue()
  			val state = addressInstance.getAttributeValue("state").stringValue()
  			val country = addressInstance.getAttributeValue("country").stringValue()
  			println(s"Lives at:")
  			println(s"\t$street")
  			println(s"\t$city $state")
  			println(s"\t$country")
   			
			}

     
      tx.commit();
  	} finally{
  		tx.close()
  	}
      
    /*
     * Stop ThingSpan
     */
    Objy.shutdown()
}