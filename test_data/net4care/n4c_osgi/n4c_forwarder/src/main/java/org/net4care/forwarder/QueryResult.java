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
 
import java.util.List; 
 
import org.net4care.observation.StandardTeleObservation; 

import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern; 
 
/** Encapsulate the actual result of a successful query 
 * for a set of observations to the server. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
          ) 
public interface QueryResult extends FutureResult {  
  /** Return a list of observations that resulted from 
   * a query to the server. Returns an empty list 
   * in case no observations matched the query. 
   * Returns null in case the query was for 
   * another format than StandardTeleObservations. 
   */ 
  public List<StandardTeleObservation> getObservationList(); 
   
  /** Return a list of PHMR XML documents that resulted from 
   * a query to the server. Returns an empty list 
   * in case no observations matched the query. 
   * Returns null in case the query was for 
   * another format than PHMR Documents. 
   */ 
  // TODO: Change from strings to XML documents 
  public List<String> getDocumentList(); 
} 
