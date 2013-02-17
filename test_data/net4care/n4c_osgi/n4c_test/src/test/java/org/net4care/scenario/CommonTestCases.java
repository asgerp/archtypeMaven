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
 
package org.net4care.scenario; 
 
import static org.junit.Assert.assertEquals; 
import static org.junit.Assert.assertNotNull; 
import static org.junit.Assert.assertTrue; 
 
import java.io.IOException; 
import java.util.List; 
 
import javax.xml.parsers.ParserConfigurationException; 
 
import org.junit.Ignore; 
import org.junit.Test; 
import org.net4care.forwarder.DataUploader; 
import org.net4care.forwarder.FutureResult; 
import org.net4care.helper.HelperMethods; 
import org.net4care.observation.Codes; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.receiver.delegate.StandardObservationReceiver; 
import org.net4care.serializer.Serializer; 
import org.net4care.storage.ExternalDataSource; 
import org.net4care.storage.ObservationCache; 
import org.net4care.storage.XDSQuery; 
import org.net4care.storage.XDSRepository; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
import org.net4care.storage.queries.XDSQueryPersonTimeIntervalType; 
import org.net4care.testdoubles.LocalSynchroneousCallConnector; 
import org.net4care.utility.Net4CareException; 
import org.w3c.dom.Document; 
import org.xml.sax.SAXException; 
 
 
/** Baseclass for a set of different configured tests.  
 *  
 * Test the HomeDevice upload scenario: 
 * 1) Home Device does magic to define a spirometry observation 
 * 2) It sends it to the N4C cloud 
 * 3) We verify that a proper PHMR document is stored in the 
 * XDS somewhere. 
 *  
 *  
 * Author: Net4Care, Henrik Baerbak Christensen, AU. 
 */ 
@Ignore 
public class CommonTestCases { 
 
  // Client/presentation tier roles 
  DataUploader uploader; 
  LocalSynchroneousCallConnector connector; 
 
  // Business-tier/Server side roles 
  StandardObservationReceiver receiver; 
 
  // Common roles over presentation and business tiers 
  Serializer serializer; 
 
  // Data tier roles 
  XDSRepository xds; 
  ObservationCache cache; 
  ExternalDataSource externalDatasources; 
 
  // Observations 
  StandardTeleObservation sto; 
   
  // Define t0 for observations 
  long t0 = 20000L; 
 
  // Test a scenario of 'going all the way' 
  @Test 
  public void shouldStorePHMRProperlyAfterUpload()  
      throws ParserConfigurationException, SAXException, IOException, Net4CareException { 
 
    long nowLong = t0; 
     
    createAndUploadASpirometryObservation(nowLong); 
 
    // Now the DataUploader has serialized the STO, sent it to the 
    // server side ObservationReceiver that has deserialized it and 
    // generated a PHMR document, that is finally stored in the XDS. 
    // As we have configured the N4C architecture with an XDS 
    // reference we know, we can retrieve this HL7 document using 
    // a relevant query... 
 
    // Create a query that says: Get all documents related to Nancy, 
    // stored within the last ten seconds, and containing observations 
    // on FVC coded in the LOINC system. 
    XDSQuery query =  
        new XDSQueryPersonTimeIntervalType( FakeObjectExternalDataSource.NANCY_CPR, 
            nowLong - 1000, // now - 1 second 
            nowLong + 10, 
            Codes.LOINC_OID, 
            new String[]{ "19868-9" } // FVC 
            );  
 
    // Retrieve the set of documents 
    List<Document> thelist; 
      thelist = xds.retrieveDocumentSet(query); 
      // get the first one... 
      Document doc = thelist.get(0); 
 
      // System.out.println( "TestUploadAndStorage:  THE STRING IS: "+xmlString ); 
 
      // And assert that it indeed contains the proper information. 
      assertNotNull( doc ); 
 
      assertEquals( "19481225", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, "birthTime", "patient", doc)); 
 
      assertEquals( "19868-9", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 0, "code", "observation", doc)); 
      assertEquals( "FVC", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 0, "code", "observation", doc)); 
      assertEquals( "L", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 0, "value", "observation", doc)); 
      assertEquals( "3.7", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, "value", "observation", doc)); 
 
      // and FEV1 
      assertEquals( "20150-9", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 1, "code", "observation", doc)); 
      assertEquals( "FEV1", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 1, "code", "observation", doc)); 
      assertEquals( "L", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 1, "value", "observation", doc)); 
      assertEquals( "3.42", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 1, "value", "observation", doc)); 
 
  } 
 
 
  void createAndUploadASpirometryObservation(long timestamp) throws IOException { 
    // Create an observation 
    sto = HelperMethods.defineSpirometryObservation( FakeObjectExternalDataSource.NANCY_CPR, 3.7, 3.42); 
    sto.setTime(timestamp); 
     
    // Upload the STO  to the N4C cloud 
    FutureResult result = uploader.upload(sto); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
  } 
} 
