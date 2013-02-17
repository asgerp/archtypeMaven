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
 
package org.net4care.stresstest; 
 
import java.io.IOException; 
import java.net.MalformedURLException; 
 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.*; 
import org.net4care.helper.HelperMethods; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
 
/** Stress testing for timing number of updates  
 *  
 * Initial numbers for 100 uploads: 
 *  
 * local machine, sqlite: 22 - 25 secs 
 * wireless, server on same router, sqlite: 26-35 secs 
 * wireless, server on same router, null: 1.5-1.9 secs (measured over 1000 uploads) 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public class StressTest { 
  public static void main(String[] args) throws IOException { 
    System.out.println("Net4Care Timinig test. Measure the upload speed of STOs."); 
    System.out.println(" arg len =" + args.length); 
     
    StressTest test = new StressTest(); 
    test.run(100); 
  } 
   
  // Address of server 
  private String url = "http://localhost:8080/observation";   
  // private String url = "http://192.168.0.10:8080/observation";  // HBC local net 
 
  // Client/presentation tier roles 
  DataUploader uploader; 
  ServerConnector connector; 
  Serializer serializer; 
  StandardTeleObservation sto; 
 
  public StressTest() throws MalformedURLException { 
    // Client side roles   
    serializer = new JacksonJSONSerializer(); 
    // -- use a connector to the server that is simply method call 
    connector = new HttpConnector( url ); 
    // -- and configure the data uploader (forwarder) with 
    // these delegates. 
    uploader = new StandardDataUploader( serializer, connector );  
     
    sto = HelperMethods.defineSpirometryObservation( FakeObjectExternalDataSource.NANCY_CPR, 3.7, 3.42); 
 
  } 
   
  public void run(int count) throws IOException { 
 
    long start, stop; 
    System.out.println("Begin..."); 
     
    start = System.currentTimeMillis(); 
    for ( int i = 0; i < count; i ++) { 
      // Upload the STO  to the N4C cloud 
      FutureResult result = uploader.upload(sto); 
      result.awaitUninterruptibly();   
    } 
    stop = System.currentTimeMillis(); 
    System.out.println("End..."); 
    System.out.println(" Upload of "+count+" STOs took: "+ (stop-start)+ " ms."); 
     
  } 
} 
