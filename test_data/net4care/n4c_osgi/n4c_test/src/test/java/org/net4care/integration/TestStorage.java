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
 
package org.net4care.integration; 
 
import static org.junit.Assert.*; 
 
import java.util.List; 
 
import javax.xml.parsers.*; 
 
import org.junit.*; 
import org.junit.experimental.categories.Category; 
import org.net4care.observation.Codes; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.*; 
import org.net4care.storage.queries.*; 
import org.net4care.utility.*; 
import org.w3c.dom.*; 
 
/** Unit testing of the SQLite implementation of 
 * XDSRepository and ObservationCache. 
 *  
 * Creates a database 'xdstest.db' in the root 
 * where the test is run, you may inspect it 
 * after the tests have run. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen 
 *  */ 
public class TestStorage { 
 
  private XDSRepository xds; 
  private ObservationCache cache; 
   
  public static final String LOINC_OID = Codes.LOINC_OID; 
  public static final String FVC = "19868-9"; 
  public static final String FEV1 = "20150-9"; 
  public static final String GLUCOSE = "30164-6"; 
  private final String org_uid_viewcare = "org.net4care.org.careview"; 
  private final String org_uid_bpcare = "org.net4care.org.bloodcare"; 
  private final String treatmentId_kol = "EPJ-RM-324-12-22"; 
  private final String treatmentId_high_bp = "EPJ-RM-23-98-33"; 
   
  private Document doc_nancy1, doc_nancy2, doc_carsten, doc_nancy_bp; 
  private RegistryEntry meta_nancy1, meta_nancy2, meta_carsten, meta_nancy_bp; 
   
  private String doc1NancyAsXMLString,  
    doc2NancyAsXMLString, 
    docCarstenAsXMLString, 
    docBPNancyAsXMLString; 
   
  private long time1Nancy, time2Nancy, timeBPNancy, timeCarsten; 
 
  /** Create four observations for two patients; several 
   * spirometry observations LOINC encoded and one blood pressure 
   * UIPAC encoded. The stored HL7 documents are fakes that only 
   * have vague resemblence to real HL7. 
   * @throws Net4CareException  
   */ 
  @Before  
  @Category(IntegrationTest.class) 
  public void setup() throws Net4CareException { 
    xds = new SQLiteXDSRepository("xdstest.db"); 
     
    cache = (ObservationCache) xds;     
     
    // Create some data: three documents for Nancy  
    // and Carsten containing spirometry data. 
    java.util.Date d = new java.util.Date(); 
     
    long now = d.getTime(); 
    time1Nancy = now-10000; 
    time2Nancy = now-5000; 
    timeCarsten = now-500; 
    timeBPNancy = now- 7000; 
     
    // Make some "spirometry" documents 
    doc_nancy1 = makeDocument(FakeObjectExternalDataSource.NANCY_CPR, "3.7"); 
    meta_nancy1 = new RegistryEntry( FakeObjectExternalDataSource.NANCY_CPR, time1Nancy, LOINC_OID, 
        new String[] { FVC, GLUCOSE }, org_uid_viewcare, treatmentId_kol);  
 
    doc_nancy2 = makeDocument(FakeObjectExternalDataSource.NANCY_CPR, "3.9"); 
    meta_nancy2 = new RegistryEntry( FakeObjectExternalDataSource.NANCY_CPR, time2Nancy, LOINC_OID, 
        new String[] { FVC, FEV1 }, org_uid_viewcare, treatmentId_kol); 
     
    doc_carsten = makeDocument(FakeObjectExternalDataSource.CARSTEN_CPR, "4.2"); 
    meta_carsten = new RegistryEntry( FakeObjectExternalDataSource.CARSTEN_CPR, timeCarsten, LOINC_OID, 
        new String[] { FEV1 }, org_uid_viewcare, treatmentId_kol); 
     
    // Make a blood pressure measurement for Nancy but by another organization 
    // and referring to another treatment id 
    doc_nancy_bp = makeDocument(FakeObjectExternalDataSource.NANCY_CPR, "162"); 
    meta_nancy_bp = new RegistryEntry( FakeObjectExternalDataSource.NANCY_CPR, timeBPNancy, Codes.UIPAC_OID, 
        new String[] { "MCS88019" /* UIPAC home systolic pressure code */ }, org_uid_bpcare, treatmentId_high_bp);  
    
 
    // make comparisons strings 
    doc1NancyAsXMLString = Utility.convertXMLDocumentToString(doc_nancy1); 
    doc2NancyAsXMLString = Utility.convertXMLDocumentToString(doc_nancy2); 
    docCarstenAsXMLString = Utility.convertXMLDocumentToString(doc_carsten); 
    docBPNancyAsXMLString = Utility.convertXMLDocumentToString(doc_nancy_bp); 
     
    // Reset the XDS database to contain nothing... 
    xds.connect(); 
    xds.utterlyEmptyAllContentsOfTheDatabase(); 
 
    // And register the three documents. 
    xds.provideAndRegisterDocument(meta_nancy1, doc_nancy1); 
    xds.provideAndRegisterDocument(meta_nancy2, doc_nancy2); 
    xds.provideAndRegisterDocument(meta_carsten, doc_carsten);  
    xds.provideAndRegisterDocument(meta_nancy_bp, doc_nancy_bp);  
 
    cache.provideAndRegisterObservation(meta_nancy1, "(nancy1-serial)" ); 
    cache.provideAndRegisterObservation(meta_nancy2, "(nancy2-serial)" ); 
    cache.provideAndRegisterObservation(meta_nancy_bp, "(nancy-bp-serial)" ); 
    cache.provideAndRegisterObservation(meta_carsten, "(carsten-serial)" ); 
} 
   
  @After 
  public void teardown() throws Net4CareException { 
    xds.disconnect(); 
  } 
   
  /** Query XDS for documents for a patient in a given time interval  
   * @throws Net4CareException */ 
  @Test 
  @Category(IntegrationTest.class) 
  public void shouldRetrieveDocumentsOnCPRAndTime() throws Net4CareException { 
    List<String> retrievedAsString; 
    XDSQuery query; 
     
    // query nancy's readings for the valid time interval 
    query = new XDSQueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR,  
                                        time1Nancy, time2Nancy); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertTrue( retrievedAsString.contains(doc1NancyAsXMLString)); 
    assertTrue( retrievedAsString.contains(doc2NancyAsXMLString)); 
    assertTrue( retrievedAsString.contains(docBPNancyAsXMLString)); 
    assertEquals( 3, retrievedAsString.size() ); 
 
    // query nancy's readings in 1970'ies, should be empty 
    query = new XDSQueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, 0, 10000); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 0, retrievedAsString.size() ); 
     
    // query nancy's readings in interval that only has the last one 
    query = new XDSQueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR,  
                                        time2Nancy-100, time2Nancy+200); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 1, retrievedAsString.size() ); 
    assertTrue( retrievedAsString.contains(doc2NancyAsXMLString));  
  
    // query carsten's reading... 
    query = new XDSQueryPersonTimeInterval(FakeObjectExternalDataSource.CARSTEN_CPR,  
                                        time2Nancy, timeCarsten); 
 
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query ); 
    assertEquals( docCarstenAsXMLString, retrievedAsString.get(0)); 
    assertEquals( 1, retrievedAsString.size() ); 
  }   
 
  /** Query for documents for patient, given time, and observation type  
   * @throws Net4CareException */ 
@Test 
@Category(IntegrationTest.class) 
  public void shouldRetrieveDocumentsOnCPRAndTimeAndObsType() throws Net4CareException { 
    List<String> retrievedAsString; 
    XDSQuery query; 
 
    // Query nancy's FEV1 readings only, which is one document 
    query = new XDSQueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
                                            0, time2Nancy, 
                                            LOINC_OID, new String[]{ FEV1 } ); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 1, retrievedAsString.size() ); 
    assertEquals( doc2NancyAsXMLString, retrievedAsString.get(0)); 
     
    // Query nancy's FVC readings only (which is both) 
    query = new XDSQueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
                                            0, time2Nancy, 
                                            LOINC_OID, new String[]{ FVC } ); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 2, retrievedAsString.size() ); 
    assertTrue( retrievedAsString.contains(doc1NancyAsXMLString)); 
    assertTrue( retrievedAsString.contains(doc2NancyAsXMLString)); 
     
    // Query nancy's FVC and FEV1 readings 
    query = new XDSQueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
                                            0, time2Nancy, 
                                            LOINC_OID,  
                                            new String[]{ FEV1, FVC } ); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 1, retrievedAsString.size() ); 
    assertTrue( retrievedAsString.contains(doc2NancyAsXMLString)); 
     
    // No queries are satisfied for a NON LOINC code 
    query = new XDSQueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
                                            0, time2Nancy, 
                                            "2.SOME.OTHER.HL7.CODING",  
                                            new String[]{ FEV1, FVC } ); 
     
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 0, retrievedAsString.size() );   
  } 
 
  /** Query for documents for patient, time and treatment  
   * @throws Net4CareException */ 
  @Test 
  @Category(IntegrationTest.class) 
  public void shouldRetrieveDocumentsOnCPRAndTimeAndTreatment() throws Net4CareException { 
    List<String> retrievedAsString; 
    XDSQuery query; 
 
    // Query nancy's documents on a single treatment on BP 
    query = new XDSQueryPersonTimeIntervalTreatment(FakeObjectExternalDataSource.NANCY_CPR, 0, 
        time2Nancy, treatmentId_high_bp ); 
 
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 1, retrievedAsString.size() ); 
    assertTrue( retrievedAsString.contains(docBPNancyAsXMLString)); 
 
    // and on Spirometry 
    query = new XDSQueryPersonTimeIntervalTreatment(FakeObjectExternalDataSource.NANCY_CPR, 0, 
        time2Nancy, treatmentId_kol ); 
    retrievedAsString = xds.retrieveDocumentSetAsXMLString( query );    
    assertEquals( 2, retrievedAsString.size() ); 
    assertTrue( retrievedAsString.contains(doc1NancyAsXMLString)); 
    assertTrue( retrievedAsString.contains(doc2NancyAsXMLString));  
  } 
 
  /** Query for cached observations for patient at given time  
   * @throws Net4CareException */ 
  @Test  
  @Category(IntegrationTest.class) 
  public void shouldRetrieveCachedObservations() throws Net4CareException { 
     
    List<String> retrievedAsString; 
    XDSQuery query; 
  
    // query nancy's readings for the valid time interval 
    query = new XDSQueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR,  
                                        time1Nancy, time2Nancy); 
    retrievedAsString = cache.retrieveObservationSet(query); 
    assertNotNull("There should be an observation cached.", retrievedAsString); 
     
    assertEquals( 3, retrievedAsString.size() ); 
    assertEquals( "(nancy1-serial)", retrievedAsString.get(0)); 
    assertEquals( "(nancy2-serial)", retrievedAsString.get(1)); 
    assertEquals( "(nancy-bp-serial)", retrievedAsString.get(2)); 
     
  } 
   
  @Test  
  @Category(IntegrationTest.class) 
  public void shouldHandleTwoUpdateWithSameTimeStampCorrect() throws Net4CareException { 
    long now = System.currentTimeMillis(); 
    time1Nancy = now; 
    time2Nancy = now; 
 
    doc_nancy1 = makeDocument(FakeObjectExternalDataSource.NANCY_CPR, "4.1"); 
    meta_nancy1 = new RegistryEntry( FakeObjectExternalDataSource.NANCY_CPR, time1Nancy, LOINC_OID, 
        new String[] { FVC, GLUCOSE }, org_uid_viewcare, treatmentId_kol);  
 
    doc_nancy2 = makeDocument(FakeObjectExternalDataSource.NANCY_CPR, "4.2"); 
    meta_nancy2 = new RegistryEntry( FakeObjectExternalDataSource.NANCY_CPR, time2Nancy, LOINC_OID, 
        new String[] { FVC, FEV1 }, org_uid_viewcare, treatmentId_kol); 
 
    xds.provideAndRegisterDocument(meta_nancy1, doc_nancy1); 
    xds.provideAndRegisterDocument(meta_nancy2, doc_nancy2); 
 
    cache.provideAndRegisterObservation(meta_nancy1, "(sametime-nancy1)" ); 
    cache.provideAndRegisterObservation(meta_nancy2, "(sametime-nancy2)" ); 
 
     
    List<String> retrievedAsString; 
    XDSQuery query; 
  
    // query nancy's readings for the valid time interval 
    query = new XDSQueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR,  
                                        time1Nancy-2000, time1Nancy+2000); 
     
    // Frist the hl7 document 
    List<Document> doclist = xds.retrieveDocumentSet(query); 
    assertNotNull("There should be documents present.", doclist); 
     
    assertEquals( 2, doclist.size() ); 
     
    assertEquals( Utility.convertXMLDocumentToString(doc_nancy1),  
        Utility.convertXMLDocumentToString(doclist.get(0)) ); 
    assertEquals( Utility.convertXMLDocumentToString(doc_nancy2),  
        Utility.convertXMLDocumentToString(doclist.get(1)) ); 
 
    // Second, the observations 
    retrievedAsString = cache.retrieveObservationSet(query); 
    assertNotNull("There should be an observation cached.", retrievedAsString); 
     
    assertEquals( 2, retrievedAsString.size() ); 
    assertEquals( "(sametime-nancy1)", retrievedAsString.get(0)); 
    assertEquals( "(sametime-nancy2)", retrievedAsString.get(1)); 
 
  } 
  // === Helpers 
  /** Make a XML document that is a bit like HL7 
   *  
   * @param cprNo cpr of person this document belongs to 
   * @param value a 'measured value' 
   * @return XML document 
   */ 
  private Document makeDocument(String cprNo, String value) { 
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance(); 
    DocumentBuilder docBuilder = null; 
    try { 
       docBuilder = dbfac.newDocumentBuilder(); 
    } catch ( ParserConfigurationException e ) { 
      e.printStackTrace(); 
    } 
    Document doc = docBuilder.newDocument(); 
     
    Element recordTarget = doc.createElement("recordTarget"); 
    recordTarget.setAttribute("typeCode", "RCT"); 
    recordTarget.setAttribute("contextControlCode", "OP"); 
    doc.appendChild(recordTarget); 
     
    Element patientRole = doc.createElement("patientRole"); 
    patientRole.setAttribute("classCode", "PAT"); 
    recordTarget.appendChild(patientRole); 
     
    Element cpr = doc.createElement("CPR"); 
    cpr.setAttribute("value", cprNo); 
    patientRole.appendChild(cpr); 
 
    Element obs = doc.createElement("observation"); 
    obs.setAttribute("classCode", "OBS"); 
    obs.setTextContent(value); 
    recordTarget.appendChild(obs); 
 
    return doc; 
  } 
 
  // === Stepping stone tests 
 
  /** Tests to validate that the metadata classes have 
   * been properly integrated into the project */ 
  @Test 
  @Category(IntegrationTest.class) 
  public void shouldInstantiateMetaData() { 
    RegistryEntry md = new RegistryEntry("020354-1232", 23L, Codes.UIPAC_OID, 
                               new String[]{ "MCS88015", "MCS88019" }, 
                               "net4care.careview", 
                               "EPJ-RM-324-32"); 
    assertEquals( 23L, md.getTimestamp() ); 
    assertEquals( "net4care.careview", md.getOrganizationId() ); 
    assertEquals( "EPJ-RM-324-32", md.getTreatmentId() ); 
 
    XDSQueryPersonTimeInterval query = 
      new XDSQueryPersonTimeInterval("020354-1232", 0L, 1000L ); 
    assertEquals( "020354-1232", query.getCpr() ); 
  } 
} 
