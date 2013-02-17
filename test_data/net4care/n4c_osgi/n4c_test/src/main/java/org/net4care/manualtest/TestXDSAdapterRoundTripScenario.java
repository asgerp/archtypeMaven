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
 
package org.net4care.manualtest; 
 
import static org.junit.Assert.*; 
 
import java.io.IOException; 
import java.util.*; 
 
import org.w3c.dom.Document; 
 
import org.junit.*; 
 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.forwarder.query.*; 
import org.net4care.helper.HelperMethods; 
import org.net4care.locator.HostSpecification; 
import org.net4care.observation.*; 
import org.net4care.receiver.delegate.StandardObservationReceiver; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.*; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.*; 
import org.net4care.testdoubles.*; 
import org.net4care.utility.*; 
import org.net4care.xds.MSCodeplexXDSAdapter; 
 
/** A end-2-end test of uploading 
 * STOs and retrieving PHMR documents from 
 * a live MS Codeplex XDS. 
 *  
 * IMPORTANT: 
 *  
 * Review the 'HostSpecification.setHost()' line 
 * in the setup to ensure it has the proper 
 * address of a running XDS. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public class TestXDSAdapterRoundTripScenario { 
 
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
 
  // observations 
  StandardTeleObservation nancy_spiro1; 
  StandardTeleObservation nancy_spiro2; 
  StandardTeleObservation nancy_blood1; 
 
  // the timestamp 
  long now, tenSecondsLater, twentySecondsLater; 
 
  @Before 
  public void setup() throws Net4CareException, IOException { 
    // Fiddle with the time so we ensure that the uploads are 
    // not having the same timestamps. 
    now = System.currentTimeMillis() - 20000L; 
    tenSecondsLater = now + 10000L; 
    twentySecondsLater = now + 20000L; 
 
    nancy_spiro1 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 3.6, 3.2); 
    nancy_spiro1.setTime(now); 
    nancy_spiro2 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 4.6, 3.3); 
    nancy_spiro2.setTime(tenSecondsLater); 
    nancy_blood1 = HelperMethods.defineBloodPressureObservation(FakeObjectExternalDataSource.NANCY_CPR, 175.0, 87.0, 
        Codes.DK_REGION_MIDT_SKEJBY_SYGEHUS, "RM-001"); 
    nancy_blood1.setTime(twentySecondsLater); 
 
    // Common roles - both server and client side must of course agree 
    // upon the way StandardTeleObservations are serialized... 
    serializer = new JacksonJSONSerializer(); 
 
    // Due to the 'local method call' connector used by the 
    // client, I have to define 
    // the server side 'receiver' role first in order to pass it 
    // as reference to the client side... 
 
    // Server side and data tier roles 
     
   // -- ALTERNATIVE: use a persistent XDS based upon SQLite SQL database 
    //xds = new SQLiteXDSRepository();  
    xds = new MSCodeplexXDSAdapter(); 
    // VM at HBC home computer 
    // HostSpecification.setHost("192.168.1.14"); 
    // VM on lab machines 
    // HostSpecification.setHost("10.19.28.148"); 
    // VM running on CS@AU's serverfarm 
    HostSpecification.setHost("n4cxds.nfit.au.dk"); 
     
    xds.connect(); 
    xds.utterlyEmptyAllContentsOfTheDatabase(); // as we reuse the time stamps 
 
    // -- use a fake object implementation of the external data sources 
    externalDatasources = new FakeObjectExternalDataSource(); 
    // -- SQLiteXDSRepository also serves as ObservationCache! 
    cache = new NullObservationCache();  
    // -- and configure the server side's receiver object with 
    // the proper delegates... 
     
    //Set the server to use the ServerJSONSerializer instead. 
    Serializer serverSerializer = new ServerJSONSerializer(); 
     
    receiver = new StandardObservationReceiver( serverSerializer,  
        externalDatasources,  
        xds, cache ); 
    // Client side roles 
    // -- use a connector to the server that is simply method call 
    connector = new LocalSynchroneousCallConnector( receiver ); 
    // -- and configure the data uploader (forwarder) with 
    // these delegates. 
    uploader = new StandardDataUploader( serializer, connector );   
 
    // Upload the three observations... 
 
   FutureResult result; 
 
    // Upload the first STO  to the N4C cloud at time 'now' 
    result = uploader.upload(nancy_spiro1); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    // Upload the second STO at time 'ten seconds later' 
    result = uploader.upload(nancy_spiro2); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    // And upload the blood pressure 20 secs later 
    result = uploader.upload(nancy_blood1); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
  } 
 
  @After 
  public void teardown() throws Net4CareException { 
    // remember to disconnect the DB after each test case. 
    xds.disconnect(); 
  } 
 
 
  /** Validate that the version 0.3 PHMR query interface works  
   * @throws Net4CareException  
   * @throws IOException  
 * @throws UnknownCPRException */ 
  @Test 
  public void shouldRetrievePHMRDocuments() throws IOException, Net4CareException { 
    Query query; QueryResult res; 
 
    // First - query around 1 sec before 'now' and 11 secs after - should fetch the two observations 
    // stored at now and now+10secs 
    long start, end; 
    start = now-1000; 
    end = tenSecondsLater+1000; 
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, start, end); 
    query.setFormatOfReturnedObservations(Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD); 
    assertEquals( Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD, query.getFormatOfReturnedObservations() ); 
    QueryPersonTimeInterval qpti = (QueryPersonTimeInterval) query; 
    assertEquals( QueryKeys.ACCEPT_XML_DATA, qpti.getDescriptionMap().get(QueryKeys.FORMAT_KEY) ); 
     
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    List<String> phmrDocList; 
 
    phmrDocList = res.getDocumentList(); 
    assertEquals( 2, phmrDocList.size() ); 
    
    // Convert it into a real XML document 
    Document doc = Utility.convertXMLStringToDocument(phmrDocList.get(0)); 
     
    // do some smoke testing 
    assertEquals( FakeObjectExternalDataSource.NANCY_CPR,  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension",0,"id","patientRole",doc) ); 
    assertEquals( "FVC",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName",0,"code","observation",doc) ); 
    assertEquals( "3.6",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",0,"value","observation",doc) ); 
     
    //System.out.println( phmrDocList.get(0)); 
     
    // Query more documents 
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, now-1000, twentySecondsLater+1000); 
    query.setFormatOfReturnedObservations(Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    phmrDocList = res.getDocumentList(); 
    assertEquals( 3, phmrDocList.size() ); 
     
    doc = Utility.convertXMLStringToDocument(phmrDocList.get(1)); 
     
    assertEquals( "FVC",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName",0,"code","observation",doc) ); 
    assertEquals( "4.6",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",0,"value","observation",doc) ); 
   
    doc = Utility.convertXMLStringToDocument(phmrDocList.get(2)); 
     
    assertEquals( "Systolisk BT",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName",0,"code","observation",doc) ); 
    assertEquals( "175.0",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",0,"value","observation",doc) ); 
     
    //System.out.println( phmrDocList.get(1)); 
  } 
  
} 
