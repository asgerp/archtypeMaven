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
 
import org.junit.*; 
 
import java.util.List; 
import java.io.*; 
 
import org.net4care.forwarder.delegate.StandardDataUploader; 
 
import org.net4care.receiver.delegate.StandardObservationReceiver; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.serializer.delegate.ServerJSONSerializer; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.*; 
import org.net4care.storage.queries.*; 
 
import org.net4care.testdoubles.*; 
import org.net4care.utility.Net4CareException; 
 
/** Test the HomeDevice upload and retrieval scenario  
 *  
 * Author: Henrik Baerbak Christensen, AU. 
 */ 
public class TestUploadAndStorage extends CommonTestCases { 
    
  /** Define the N4C architecture using dependency injection, 
   * basically we construct the client side data uploader by 
   * injecting relevant delegate objects; and do the same 
   * with the server side. 
   * @throws Net4CareException  
   */ 
  @Before 
  public void setup() throws Net4CareException { 
    // Common roles - both server and client side must of course agree 
    // upon the way StandardTeleObservations are serialized... 
    serializer = new JacksonJSONSerializer(); 
 
    // Due to the 'local method call' connector used by the 
    // client, I have to define 
    // the server side 'receiver' role first in order to pass it 
    // as reference to the client side... 
 
    // Server side and data tier roles 
     
    // -- Use the fast in-memory databases 
    xds = new FakeObjectXDSAndObservationCache(); 
     
    // -- ALTERNATIVE: use a persistent XDS based upon SQLite SQL database 
    //xds =  new SQLiteXDSRepository("db-tuas.db");  
     
    xds.connect(); 
    // Clean the database as we otherwise get entries 
    // with the same timestamp between runs of the testcase 
    xds.utterlyEmptyAllContentsOfTheDatabase(); 
     
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
  } 
 
  @After 
  public void teardown() throws Net4CareException { 
    // remember to disconnect the DB after each test case. 
    xds.disconnect(); 
  } 
   
  /** Test that the upload scenario ALSO stores the 
   * serialized observations in a server side cache  
 * @throws IOException  
   * @throws Net4CareException */ 
  @Test public void shouldCacheObservations() throws IOException, Net4CareException { 
 
    long nowLong = t0 + 10; 
     
    createAndUploadASpirometryObservation(nowLong); 
      
     // Create a query that says: Get all documents related to Nancy, 
     // stored within the last ten seconds. 
     XDSQuery query =  
       new XDSQueryPersonTimeInterval( FakeObjectExternalDataSource.NANCY_CPR, 
                                        nowLong - 1, // +- 1 millisecond 
                                        nowLong + 1 
                                        );  
 
     List<String> list = cache.retrieveObservationSet(query);    
     assertNotNull(list); 
     assertEquals( 1, list.size() ); 
      
     // extract the JSON serialized from the cache and compare it 
     // to the one the serializer would produce 
     String computed = list.get(0); 
     String expected = serializer.serialize(sto); 
      
     assertEquals( expected, computed );    
  } 
   
   
} 
