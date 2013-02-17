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
 
import static org.junit.Assert.*; 
 
import java.io.IOException; 
import java.text.SimpleDateFormat; 
import java.util.*; 
 
import org.junit.*; 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.forwarder.query.*; 
import org.net4care.helper.HelperMethods; 
import org.net4care.observation.*; 
import org.net4care.receiver.delegate.StandardObservationReceiver; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.*; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.*; 
import org.net4care.storage.queries.XDSQueryPersonTimeIntervalType; 
import org.net4care.testdoubles.*; 
import org.net4care.utility.*; 
import org.w3c.dom.Document; 
 
import com.smb.homeapp.Spirometry; 
 
/** Upload a set of STOs to a server, and 
 * query subsets of these back to the client. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public class TestServerQueryScenario { 
 
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
    // Fiddle with the time so we ensure that the two uploads are 
    // not having the same timestamps. 
    now = System.currentTimeMillis(); 
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
     
    // -- use a fast in-memory database 
    xds = new FakeObjectXDSAndObservationCache(); 
     
    // -- ALTERNATIVE: use a persistent XDS based upon SQLite SQL database 
    // xds =  new SQLiteXDSRepository("db-query.db"); 
     
    xds.connect(); 
    xds.utterlyEmptyAllContentsOfTheDatabase(); // as we reuse the time stamps 
 
    // -- use a fake object implementation of the external data sources 
    externalDatasources = new FakeObjectExternalDataSource(); 
    // -- SQLiteXDSRepository also serves as ObservationCache! 
    cache = (ObservationCache) xds; 
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
 
  /** Test that the requesting of observations from the cache works from 
   * the client side.  
 * @throws UnknownCPRException */ 
  @Test 
  public void shouldQueryForCPRAndTime() throws IOException, Net4CareException { 
    StandardTeleObservation computed1, computed2; 
 
    Query query; QueryResult res; 
    List<StandardTeleObservation> obslist; 
 
    // First - query around the first time "now" 
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, now-20, now+20); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    // assert returned cached values 
    obslist = res.getObservationList(); 
    assertEquals( 1, obslist.size() ); 
 
    computed1 = obslist.get(0); 
    assertEquals ( nancy_spiro1.toString(), computed1.toString()); 
    assertEquals ( now, computed1.getTime() ); 
     
    // Next - query fine grained around 10 seconds later 
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, tenSecondsLater-20, tenSecondsLater+20); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 1, obslist.size() ); 
 
    computed1 = obslist.get(0); 
    assertEquals ( nancy_spiro2.toString(), computed1.toString()); 
 
    // Finally try and query both   
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, now-20, tenSecondsLater+20); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 2, obslist.size() ); 
 
    computed1 = obslist.get(0); 
    computed2 = obslist.get(1); 
    assertEquals ( nancy_spiro1.toString(), computed1.toString()); 
    assertEquals ( nancy_spiro2.toString(), computed2.toString()); 
  } 
 
  /** Test the query on measured type...  
 * @throws UnknownCPRException */ 
  @Test 
  public void shouldRequestBasedUponTypeOfObservation() throws IOException, Net4CareException { 
    StandardTeleObservation computed1, computed2, computed3; 
 
 
    Query query; QueryResult res; 
    List<StandardTeleObservation> obslist; 
 
    // First - query around the first time "now" 
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR,  
        now-20, twentySecondsLater+20); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 3, obslist.size() ); 
 
    computed3 = obslist.get(2); 
    assertEquals ( nancy_blood1.toString(), computed3.toString()); 
 
    // Query only Spirometry values in LOINC 
    query = new QueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
        now-20, twentySecondsLater+20, Codes.LOINC_OID,  "|20150-9|19868-9|" ); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 2, obslist.size() ); 
 
    computed1 = obslist.get(0); 
    computed2 = obslist.get(1); 
    assertEquals ( nancy_spiro1.toString(), computed1.toString()); 
    assertEquals ( nancy_spiro2.toString(), computed2.toString()); 
 
    // Query for something that is certainly not in the cache 
    query = new QueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
        now-20, twentySecondsLater+20, Codes.LOINC_OID,  "|20150-9|NotThere|" ); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 0, obslist.size() ); 
 
    // Query only Blood values in UIPAC 
    query = new QueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
        now-20, twentySecondsLater+20, Codes.UIPAC_OID, "|MSC88020|" ); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 1, obslist.size() ); 
 
    computed1 = obslist.get(0); 
    assertEquals ( nancy_blood1.toString(), computed1.toString()); 
 
    // Query only Blood values in but wrongly specify LOINC, should return empty list 
    query = new QueryPersonTimeIntervalType(FakeObjectExternalDataSource.NANCY_CPR,  
        now-20, twentySecondsLater+20, Codes.LOINC_OID, "|MSC88020|" ); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    obslist = res.getObservationList(); 
    assertEquals( 0, obslist.size() ); 
  } 
 
  /** Validate that the version 0.3 PHMR query interface works  
   * @throws Net4CareException  
   * @throws IOException  
 * @throws UnknownCPRException */ 
  @Test 
  public void shouldRetrievePHMRDocuments() throws IOException, Net4CareException { 
    Query query; QueryResult res; 
 
    // First - query around the first time "now" 
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, now-20, now+20); 
    query.setFormatOfReturnedObservations(Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD); 
    assertEquals( Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD, query.getFormatOfReturnedObservations() ); 
    QueryPersonTimeInterval qpti = (QueryPersonTimeInterval) query; 
    assertEquals( QueryKeys.ACCEPT_XML_DATA, qpti.getDescriptionMap().get(QueryKeys.FORMAT_KEY) ); 
     
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    List<String> phmrDocList; 
 
    phmrDocList = res.getDocumentList(); 
    assertEquals( 1, phmrDocList.size() ); 
    
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
    query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR, now-20, tenSecondsLater+20); 
    query.setFormatOfReturnedObservations(Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD); 
    res = uploader.query(query); 
    res.awaitUninterruptibly(); 
 
    phmrDocList = res.getDocumentList(); 
    assertEquals( 2, phmrDocList.size() ); 
     
    doc = Utility.convertXMLStringToDocument(phmrDocList.get(1)); 
     
    assertEquals( "FVC",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName",0,"code","observation",doc) ); 
    assertEquals( "4.6",  
        HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value",0,"value","observation",doc) ); 
     
    //System.out.println( phmrDocList.get(1)); 
  } 
   
  /** What does this test Larsson? -HBC */ 
  @Test 
  public void testXSDQueryInTimeInterval() throws Net4CareException, IOException { 
    xds.utterlyEmptyAllContentsOfTheDatabase(); 
 
    StandardTeleObservation sto1 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 1.1, 2.2); 
    StandardTeleObservation sto2 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 1.3, 3.0); 
    StandardTeleObservation sto3 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 1.5, 1.4); 
    StandardTeleObservation sto4 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 2.0, 1.2); 
    StandardTeleObservation sto5 = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 3.3, 0.3); 
 
    Calendar times[] = new GregorianCalendar [5]; 
    times[0] = new GregorianCalendar(); 
    times[0].set(2012, 0, 21, 14, 56); 
    times[1] = new GregorianCalendar(); 
    times[1].set(2012, 0, 21, 15, 54); 
    times[2] = new GregorianCalendar(); 
    times[2].set(2012, 0, 21, 20, 43); 
    times[3] = new GregorianCalendar(); 
    times[3].set(2012, 0, 22, 10, 12); 
    times[4] = new GregorianCalendar(); 
    times[4].set(2011, 2, 11, 01, 55); 
 
    FutureResult result = null; 
 
    sto1.setTime(times[0].getTimeInMillis()); 
    result = uploader.upload(sto1); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    sto2.setTime(times[1].getTimeInMillis()); 
    result = uploader.upload(sto2); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    sto3.setTime(times[2].getTimeInMillis()); 
    result = uploader.upload(sto3); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    sto4.setTime(times[3].getTimeInMillis()); 
    result = uploader.upload(sto4); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    sto5.setTime(times[4].getTimeInMillis()); 
    result = uploader.upload(sto5); 
    result.awaitUninterruptibly();   
    assertTrue( result.isSuccess() ); 
 
    XDSQuery query =  
        new XDSQueryPersonTimeIntervalType( 
            FakeObjectExternalDataSource.NANCY_CPR, 
            times[0].getTimeInMillis(), 
            now, 
            Codes.LOINC_OID, 
            Utility.convertBarSeparatedStringToCodeList("|20150-9|19868-9|")); 
    List<String> queryResult = null; 
    queryResult = cache.retrieveObservationSet(query); 
    assertEquals( 4, queryResult.size() ); 
 
    //Define the desired time format for the graph. 
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd H:m"); 
    StringBuilder str = new StringBuilder(); 
 
    str.append("[\n"); 
     
    for ( String sform : queryResult ) { 
 
      //Fetch a Iterator for the current STO. 
      StandardTeleObservation sto = serializer.deserialize(sform); 
      ObservationSpecifics obsSpec = sto.getObservationSpecifics(); 
      Iterator<ClinicalQuantity> obsQuantities =  obsSpec.iterator(); 
 
      //Convert timestamp to desired time format. 
      String timeStr = dateFormatter.format(sto.getTime()); 
      str.append("['" + timeStr + "', "); 
 
      double clinicalValue;   
      while( obsQuantities.hasNext() ) { 
        clinicalValue = obsQuantities.next().getValue(); 
        str.append(clinicalValue); 
        if(obsQuantities.hasNext()) 
          str.append(", "); 
      } 
      str.append("]\n"); 
    } 
    str.append("]"); 
     
    assertEquals( 
        "[\n" +  
            "['2012-01-21 14:56', 1.1, 2.2]\n" + 
            "['2012-01-21 15:54', 1.3, 3.0]\n" + 
            "['2012-01-21 20:43', 1.5, 1.4]\n" +  
            "['2012-01-22 10:12', 2.0, 1.2]\n" + 
            "]", 
            str.toString()); 
    
     
  } 
 
  /** Simple test of the serializer - are the operators idem potent? */ 
  @Test 
  public void serializeAndDeserializeList() { 
    List<String> list = new ArrayList<String>(); 
    list.add(serializer.serialize(nancy_spiro1)); 
    list.add(serializer.serialize(nancy_spiro2)); 
 
    List<StandardTeleObservation> toList = serializer.deserializeList(serializer.serializeList(list)); 
    assertEquals(2, toList.size()); 
    assertEquals(3.6, ((Spirometry) toList.get(0).getObservationSpecifics()).getFvc().getValue(), 0.001); 
    assertEquals(4.6, ((Spirometry) toList.get(1).getObservationSpecifics()).getFvc().getValue(), 0.001); 
  } 
 
} 
