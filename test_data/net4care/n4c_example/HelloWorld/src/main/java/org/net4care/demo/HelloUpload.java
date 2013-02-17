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
 
package org.net4care.demo; 
 
import java.io.*; 
import java.net.*; 
 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.*; 
 
import org.net4care.observation.*; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
 
/** Simple command line demo of defining an observation, and uploading 
 * it to the Net4Care server for (processing and) storage. 
 * 
 * You need to start a server first, execute 'mvn install 
 * pax:provision' in the n4c_receiver folder/bundle. 
 * 
 * @author Net4Care, Henrik Baerbak Christensen, Aarhus University 
 */ 
public class HelloUpload { 
  public static void main(String[] args) { 
    new HelloUpload("http://localhost:8080/observation"); 
  } 
   
  // The "uploader" which is responsible for sending 
  // observations to the Net4Care server 
  private DataUploader dataUploader; 
 
  /** Create and upload an observation on a running web server 
   * on the full address given 
   */ 
  public HelloUpload(String serverAddress) { 
    System.out.println( "Hello Net4Care World: Uploading an observation" ); 
    System.out.println( "   to server at address: "+serverAddress ); 
    System.out.println( "   (Make sure the Net4Care server is running!)" ); 
 
    // Configure the architecture 
    dataUploader = Shared.setupN4CConfiguration( serverAddress ); 
 
    // Define the actual measured values for the patient, 
    // here 3.5L for full lung capacity and 2.8L for 1 second 
    // lung capacity 
    Spirometry sp = new Spirometry( 3.5, 2.8,  
                                    false ); 
 
    // Define the information regarding the used device 
    DeviceDescription devdesc =  
      new DeviceDescription("Spirometry", "MODEL1", "Manufac1",  
                            "1", "1", "1.0", "1.0"); 
    // And finally define the observation itself. The patient ID is that 
    // of the 'standard' patient Nancy. 
    StandardTeleObservation sto = 
      new StandardTeleObservation("251248-4916", "MyOrgID",  
                                  "myTreatmentId", Codes.LOINC_OID,  
                                  devdesc, sp,  
                                  "I have difficulties in breathing." ); 
 
    // Upload the observation to the server... 
    FutureResult result = null; 
    try { 
      result = dataUploader.upload(sto); 
      result.awaitUninterruptibly(); 
    } catch (IOException e1) { 
      System.err.println("Currently unable to connect to server"); 
    } 
    if ( result == null ) { 
      System.out.println( "Upload returned a null result (was the server running?)." ); 
    } else { 
      System.out.println( "Upload result: "+result.isSuccess() ); 
      System.out.println( "Upload connection result: "+result.result() ); 
    } 
  } 
 
}