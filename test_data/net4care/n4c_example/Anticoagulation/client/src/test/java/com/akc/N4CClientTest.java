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
 
package com.akc; 
 
import java.io.IOException; 
import java.net.SocketException; 
 
import junit.framework.TestCase; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.forwarder.DataUploader; 
import org.net4care.forwarder.FutureResult; 
import org.net4care.forwarder.Query; 
import org.net4care.forwarder.QueryResult; 
import org.net4care.forwarder.delegate.HttpConnector; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.forwarder.query.QueryPersonTimeInterval; 
import org.net4care.observation.Codes; 
import org.net4care.observation.DeviceDescription; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.utility.Net4CareException; 
 
import com.akc.data.*; 
 
public class N4CClientTest extends TestCase { 
  static final String NANCY_CPR = "251248-4916"; 
  private Serializer serializer = new JacksonJSONSerializer(); 
  DataUploader uploader; 
  String server = "http://localhost:8080/observation"; 
 
  public void setUp() throws Exception { 
    uploader = new StandardDataUploader(serializer, new HttpConnector(server)); 
 
  } 
  public void testSendAndQueryObservation() throws IOException, Net4CareException { 
    try { 
      FutureResult future = uploader.upload(defineAnticoagulationObservation()); 
      assertEquals(true, future.isSuccess()); 
 
      long now = System.currentTimeMillis();  
      Query query = new QueryPersonTimeInterval(NANCY_CPR, now-20*1000, now+20*1000); 
      QueryResult answer = uploader.query(query); 
      assertEquals(1, answer.getObservationList().size()); 
 
      StandardTeleObservation observation = answer.getObservationList().get(0); 
      Anticoagulation measurement = (Anticoagulation) observation.getObservationSpecifics();   
      // TODO: Reenable assertEquals(3.7, measurement.getCoag().getValue()); 
    } catch (SocketException e) { 
      // Ignore connection issues for test 
    } 
  } 
 
  // Create an example anticoagulation observation 
  private StandardTeleObservation defineAnticoagulationObservation() { 
    final String org_uid = "org.net4care.com.smb"; 
 
    DeviceDescription device = 
      new DeviceDescription("Anticoagulation", "CoaguCheck-XS", "Roche", "1", "1", "1.0", "1.0"); 
    Anticoagulation coagSpec = new Anticoagulation(3.7, 5.6); 
    StandardTeleObservation sto =  
      new StandardTeleObservation(NANCY_CPR, org_uid, "todo", Codes.LOINC_OID, device, coagSpec ); 
    return sto; 
  } 
} 
