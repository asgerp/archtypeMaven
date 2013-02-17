/*
 * Copyright 2012 Net4Care, www.net4care.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
package org.net4care.storage; 
 
import java.util.List; 
 
import org.net4care.utility.Net4CareException; 
import org.w3c.dom.Document; 
 
/** Interface to an XDS repository for storing HL7 
 * documents. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public interface XDSRepository { 
 
 /** Store and register a valid HL7 document in the XDS affinity domain. 
   *  
   * @param the metadata defining the XDS registry entry to serve for 
   * finding the HL7 document 
   * @param xmlDocument a valid HL7 document in XML to store in 
   * the repository 
   * @throws Net4CareException  
   */ 
  public void provideAndRegisterDocument(RegistryEntry metadata,  
          Document xmlDocument) throws Net4CareException; 
 
  /** Retrieve XML documents from the XDS that 
   * satisfy the constraints set in the query expression 
   * @param query an instance of a query template, see class XDSQuery 
   * and subclasses in the .queries package. 
   * @return a set of xml documents, each representing a PHMR document 
   * that satisfies the query 
   * @throws Net4CareException  
   */ 
  public List<Document> retrieveDocumentSet(XDSQuery query) throws Net4CareException; 
 
  /** Retrieve XML documents (string encoded) from the XDS that 
   * satisfy the constraints set in the query expression 
   * @param query an instance of a query template, see class XDSQuery 
   * and subclasses in the .queries package. 
   * @return a set of XML strings, each representing a HL7 XML document 
   * that satisfies the query 
   * @throws Net4CareException  
   */ 
  public List<String> retrieveDocumentSetAsXMLString(XDSQuery query) throws Net4CareException; 
 
  /** Open database connection - must be invoked before any 
   * provideAndRegister or retrieve  
   * @throws Net4CareException */ 
  public void connect() throws Net4CareException; 
   
  /** Close the connection to the database  
   * @throws Net4CareException */ 
  public void disconnect() throws Net4CareException; 
 
  /** Clean the repository: Empty contents of all tables but keep the 
   * table structure.  
   * TODO: remove from the interface. 
   * @throws Net4CareException  
   */ 
  public void utterlyEmptyAllContentsOfTheDatabase() throws Net4CareException; 
} 
