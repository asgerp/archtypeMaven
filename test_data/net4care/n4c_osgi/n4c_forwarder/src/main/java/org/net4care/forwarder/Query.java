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
 
package org.net4care.forwarder; 
 
import java.util.Map; 
 
/** Any query for a set of observations must 
 * implement this interface. 
 *  
 * NOTE! Developers of client applications need not 
 * care about this interface, only the concrete 
 * classes in the 'query' subpackage are 
 * relevant as these are the only ones that 
 * can be handled by the server. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 * */ 
public interface Query { 
  /** Return a (key,value) set that describes 
   * this query. For instance m.get('cpr') must 
   * return the cpr of the person this query is about. 
   * @return a (key,value) defining the query. 
   */ 
  public Map<String,String> getDescriptionMap();  
   
  /** Optionally you can define the query to 
   * return PHMR documents instead of StandardTeleObservations 
   * using this method. 
   */ 
  public void setFormatOfReturnedObservations(QueryResponseType responseType); 
  /** Return the set type of the returned observations.*/ 
  public QueryResponseType getFormatOfReturnedObservations(); 
   
  /** Define the type of the returned observations from a query, either 
   * STO's or PHMR's. 
   */ 
  enum QueryResponseType { STANDARD_TELE_OBSERVATION, PERSONAL_HEALTH_MONITORING_RECORD }; 
} 
