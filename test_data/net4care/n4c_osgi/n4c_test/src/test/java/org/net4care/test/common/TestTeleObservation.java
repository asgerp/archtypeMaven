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
 
package org.net4care.test.common; 
 
import java.util.Iterator; 
 
import org.net4care.cda.*; 
import org.net4care.helper.HelperMethods; 
import org.net4care.observation.*; 
import com.smb.homeapp.Spirometry; 
 
import org.w3c.dom.Document; 
 
import org.junit.*; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
import org.net4care.utility.Net4CareException; 
 
import static org.junit.Assert.*; 
 
/** Bootstrapping tests for defining the STO datastructure. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public class TestTeleObservation { 
  private final String cpr = FakeObjectExternalDataSource.NANCY_CPR; 
  private final String org_uid = "org.net4care.org.viewcare"; 
 
  private StandardTeleObservation sto; 
  private Spirometry spiro; 
  private DeviceDescription device; 
 
  @Before public void setup() { 
    device = 
        new DeviceDescription("Spirometer", "SpiroCraft-II", "MyCompany", "584216", "69854", "2.1", "1.1"); 
    spiro = new Spirometry(3.7, 3.42); 
    sto =  
        new StandardTeleObservation(cpr, org_uid, "todo", Codes.LOINC_OID, device, spiro ); 
  } 
 
  @Test public void shouldStoreValues() { 
    assertEquals( cpr, sto.getPatientCPR() ); 
    assertEquals( org_uid, sto.getOrganizationUID() ); 
    assertEquals( device, sto.getDeviceDescription() ); 
    assertEquals( 3.7, ( (Spirometry) sto.getObservationSpecifics()).getFvc().getValue(), 0.0001 ); 
  } 
 
  @Test public void shouldGeneratePHMR() throws Net4CareException { 
      PHMRBuilder pmrh = new PHMRBuilder( new FakeObjectExternalDataSource(), sto); 
 
      Document doc = pmrh.buildDocument(); 
      assertNotNull( "The XML of PHMR must not be null", doc); 
 
      String xmlString =  
          org.net4care.utility.Utility.convertXMLDocumentToString(doc); 
 
      // Reenable printing to view the xml string 
      // System.out.println( xmlString ); 
 
      assertTrue(true); 
  } 
 
  @Test public void shouldHandleCommentsCorrectly() { 
    String commentstring = "I had a really bad night and slept bad."; 
    StandardTeleObservation stoCommented = 
        new StandardTeleObservation(cpr, org_uid, "todo", Codes.LOINC_OID, device, spiro, 
            commentstring ); 
    assertEquals( commentstring, stoCommented.getComment() ); 
  } 
  /** This test is a template test for ensuring that your 
   * newly defined observation class can be correctly serialized 
   * and deserialized. If it fails it is probably because you 
   * {@link ObservationSpecifics} class does NOT adhere to 
   * JavaBean requirements. 
   */ 
  @Test public void shouldEnsureIdempotencyOfBloodPressure() { 
    StandardTeleObservation bloodpressure =  
        HelperMethods.defineBloodPressureObservation(FakeObjectExternalDataSource.BIRGITTE_CPR, 134.0, 65.0, "myorg", "rm-01"); 
    long timestamp = 210000L; 
    bloodpressure.setTime(timestamp); 
     
    Serializer serializer = new JacksonJSONSerializer(); 
    String onthewireformat = serializer.serialize(bloodpressure); 
    StandardTeleObservation computed = serializer.deserialize(onthewireformat); 
     
    // Validate deserialization of the ObservationSpecific clinical quantities 
    assertEquals( Codes.UIPAC_OID, computed.getCodeSystem()); 
     
    Iterator<ClinicalQuantity> i = computed.getObservationSpecifics().iterator(); 
    ClinicalQuantity item; 
    item = i.next(); 
    assertEquals( 134.0, item.getValue(), 0.001 ); 
    assertEquals( "mm(Hg)", item.getUnit() ); 
    assertEquals( "MSC88019", item.getCode() ); 
    assertEquals( "Systolisk BT", item.getDisplayName() ); 
    item = i.next(); 
    assertEquals( 65.0, item.getValue(), 0.001 ); 
    assertEquals( "mm(Hg)", item.getUnit() );    
     
    // Validate that time stamp is the one assigned on the server side. 
    assertEquals( timestamp, computed.getTime() ); 
 } 
 
 
 
} 
 
