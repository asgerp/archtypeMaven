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
 
import java.io.IOException; 
 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.utility.Net4CareException; 

import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
 
/** An instance of DataUploader is responsible for  
 * forwarding/sending/uploading a StandardTeleObservation instance from the home 
 * device to the Net4Care server side.  
 *  
 * It also provides a query interface that allows queries to be sent to the 
 * server for the set of observations satisfying a given query. 
 *  
 *  Acts as the 'Forwarder' role of the Forwarder-Receiver pattern, POSA 1 p. 307. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 *  
 */ 
@ArchTypeComponent(
           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
           ) 
public interface DataUploader { 
  /** Upload an observation to the Net4Care server side 
   * for storage. 
   *   
   * Precondition: sto is non-null and valid. 
   *   
   * @param sto the observation to upload. 
   * @return a FutureResult that is a future containing 
   * the result of the transmission (fail/success). 
   * @throws IOException  
   */ 
  public FutureResult upload(StandardTeleObservation sto) throws IOException; 
   
  /** Query for a set of observations satisfying certain criteria.  
   *  
   * @param query An instance of one of the concrete classes defined 
   * in the 'query' subpackage, that allows you to define search 
   * criteria on CPR, time interval, etc. 
   * @return the result of the query which is a future that  
   * when 'isSuccess()' is true can be accessed to retrieve a 
   * set of StandardTeleObservation's. 
   * @throws IOException 
   * @throws Net4CareException 
   * @throws UnknownCPRException 
   */ 
 
  public QueryResult query(Query query) throws IOException, Net4CareException; 
   
}