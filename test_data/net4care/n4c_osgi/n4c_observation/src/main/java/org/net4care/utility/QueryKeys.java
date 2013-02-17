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
 
package org.net4care.utility; 
 
/** A set of constants that define queries. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public class QueryKeys { 
   
  public final static String QUERY_TYPE = "QueryType"; 
   
  // The different values available for query type 
  public final static String PERSON_TIME_QUERY = "PersonTimeQuery"; 
  public final static String PERSON_TIME_OBSERVATION_TYPE_QUERY = "PersonTimeTypeQuery"; 
    
  // Keys for stored attributes 
  public final static String CPR_KEY = "cpr";  
  public final static String BEGIN_TIME_INTERVAL = "intervalstart"; 
  public final static String END_TIME_INTERVAL = "intervalend"; 
  public final static String CODE_SYSTEM = "codesystem"; 
  public final static String CODE_LIST_BAR_FORMAT = "codelist"; 
 
  // Key for return formats 
  public final static String FORMAT_KEY = "format"; 
   
  // Accepted format values of the returned response from the receiver/server 
  public static final String ACCEPT_GRAPH_DATA = "application/graph"; // Not fully implemented 
  public static final String ACCEPT_JSON_DATA = "application/json"; // STO's in JSON format 
  public static final String ACCEPT_XML_DATA = "application/xml"; // PHMR observations in HL7 XML. 
 
} 
