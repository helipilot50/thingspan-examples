package com.objy.thingspan.examples.simple

import java.util.Date

import com.objy.data.ClassBuilder
import com.objy.data.Encoding
import com.objy.data.Instance
import com.objy.data.LogicalType
import com.objy.data.Reference
import com.objy.data.Storage
import com.objy.data.Variable
import com.objy.data.dataSpecificationBuilder.IntegerSpecificationBuilder
import com.objy.data.dataSpecificationBuilder.ReferenceSpecificationBuilder
import com.objy.data.schemaProvider.SchemaProvider
import com.objy.db.Connection
import com.objy.db.Date
import com.objy.db.Objy
import com.objy.db.Transaction
import com.objy.db.TransactionMode
import com.objy.expression.language.Language
import com.objy.statement.Statement
import com.objy.expression.language.LanguageRegistry


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
    var language = LanguageRegistry.lookupLanguage("DO") 
  
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
      
      var createStatement = new Statement(language, 
        """CREATE @simple.Person {firstName = 'John', lastName = 'Smith', birthDate = 1970-01-01, shoeSize = 12, address = 
              CREATE @simple.Address {street = '1 Bond St', city = 'Ettalong Beach', state = 'NSW', country = 'Australia'}}""")
      var results = createStatement.execute()        

      createStatement = new Statement(language, 
        """CREATE @simple.Person {firstName = 'Mary', lastName = 'Brown', birthDate = 1968-03-02, shoeSize = 6 }""")
      results = createStatement.execute() 
      
      tx.commit()
      
  	} finally{
  		tx.close()
  	}
  	
    /*
     * Read some data
     */
  	tx = new Transaction(TransactionMode.READ_ONLY)
    try {
      
      // Read the persistent types
      val ooObjClass = com.objy.data.Class.lookupClass("ooObj")
      val addressClass = com.objy.data.Class.lookupClass("simple.Address")
      val personClass = com.objy.data.Class.lookupClass("simple.Person")
      
      // Form a DO query for a Person where the  last name is Smith
			val statement = new Statement(language,
			      "FROM simple.Person WHERE lastName == 'Smith' return *")
			
      // Execute the statement
			val results = statement.execute()
		
			//get the Iterator for the results 
			val path = results.sequenceValue().iterator()
			
			// iterate over the returned path
			while(path.hasNext()){
  			val segment = path.next()
  			
  			// get the person
  			val personInstance = segment.instanceValue()
  			val lastName = personInstance.getAttributeValue("lastName").stringValue
  			val firstName = personInstance.getAttributeValue("firstName").stringValue
  			val birthDate = personInstance.getAttributeValue("birthDate").dateValue
        val shoeSize = personInstance.getAttributeValue("shoeSize").intValue()
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