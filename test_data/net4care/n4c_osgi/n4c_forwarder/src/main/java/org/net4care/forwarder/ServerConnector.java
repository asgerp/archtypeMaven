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
 
import org.net4care.utility.Net4CareException; 
 
 
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern; 
/** An instance of ServerConnector is responsible for the 
 * actual upload of the on-the-wire formatted string 
 * of the observation to the Net4Care server side. 
 *  
 * You should not make instances of this interface but 
 * instead use the implementations present in the 
 * forwarder.delegate sub package! 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
          )
public interface ServerConnector { 
 
  /** Send the on-the-wire string to the server side. 
   *  
   * @param onTheWireFormat the observation in the adopted 
   * on-the-wire format. 
   * @return a future that will eventually tell the status 
   * of the transmission. 
   */ 
  public FutureResult sendToServer(String onTheWireFormat) throws IOException; 
 
  /** Send a query for a set of observations to the server. 
   *  
   * @param query the query for observations. Use one of 
   * those defined in the forwarder.query sub package as 
   * these are the ONLY ones the server understands. 
   * @param reponseType define the type of the 
   * returned observations, either as a list of 
   * StandardTeleObserations or as PHMR documents. 
   * @return a future with the query result as one big 
   * string that needs deserializing before it can 
   * by understood. 
   * @throws IOException 
   * @throws Net4CareException 
   * @throws UnknownCPRException  
   */ 
	public FutureResultWithAnswer queryToServer(Query query) throws IOException, Net4CareException; 
	 
 } 
