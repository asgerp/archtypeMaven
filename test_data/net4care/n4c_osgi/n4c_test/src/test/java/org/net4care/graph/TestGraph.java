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
 
package org.net4care.graph; 
 
import static org.junit.Assert.*; 
 
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.*; 
 
import org.junit.*; 
import org.net4care.cda.PHMRBuilder; 
import org.net4care.observation.*; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.ServerJSONSerializer; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.*; 
import org.net4care.storage.queries.XDSQueryPersonTimeIntervalType; 
import org.net4care.testdoubles.FakeObjectXDSAndObservationCache; 
import org.net4care.utility.Net4CareException; 
import org.net4care.utility.Utility; 
 
import com.smb.homeapp.Spirometry; 
 
/** Test the graph generator facilities of net4care 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
 
public class TestGraph { 
  private ObservationCache obscache; 
  private PHMRBuilder builder; 
  private ExternalDataSource eds; 
  private Serializer serializer; 
 
  @Before public void setup() throws Net4CareException { 
    // Create the observation cache 
    XDSRepository xds; 
     
    xds = new FakeObjectXDSAndObservationCache(); // new SQLiteXDSRepository("graph.db"); 
    xds.connect(); 
    xds.utterlyEmptyAllContentsOfTheDatabase(); 
 
    // SQLiteXDSRepository also plays the role of observation cache! 
    obscache = (ObservationCache) xds; 
 
    // Get us the fake External data sources 
    eds = new FakeObjectExternalDataSource(); 
 
    serializer = new ServerJSONSerializer(); 
     
    // Populate it with some observations in the date range 2012-01-21 
    // onwards 
    DeviceDescription dd = new DeviceDescription("t", "m", "m", "s", "p", "1.0", "1.0"); 
 
    // Define four spirometry readings for four different times 
    Spirometry s[] =  
        new Spirometry[] {  
        new Spirometry(1.1, 2.2),  
        new Spirometry(1.3, 3.0),  
        new Spirometry(1.5, 1.4),  
        new Spirometry(2.0, 1.2), 
        new Spirometry(3.3, 0.3), 
        new Spirometry(1.8, 1.5), 
        new Spirometry(2.1, 2.3), 
        new Spirometry(1.0, 1.0), 
        new Spirometry(2.0, 2.0), 
        new Spirometry(3.0, 0.5), 
    }; 
 
    Calendar times[] = new GregorianCalendar [10]; 
    times[0] = new GregorianCalendar(); 
    // month january = 0 !!! Cost my 1 hour :( - HBC 
    times[0].set(2012, 0, 21, 14, 56); 
    times[1] = new GregorianCalendar(); 
    times[1].set(2012, 0, 21, 15, 54); 
    times[2] = new GregorianCalendar(); 
    times[2].set(2012, 0, 21, 20, 43); 
    times[3] = new GregorianCalendar(); 
    times[3].set(2012, 0, 22, 10, 12); 
    times[4] = new GregorianCalendar(); 
    times[4].set(2011, 2, 11, 01, 55); 
    times[5] = new GregorianCalendar(); 
    times[5].set(2012, 0, 15, 14, 16); 
    times[6] = new GregorianCalendar(); 
    times[6].set(2006, 1, 05, 16, 34); 
    times[7] = new GregorianCalendar(); 
    times[7].set(2010, 7, 22, 10, 12); 
    times[8] = new GregorianCalendar(); 
    times[8].set(2001, 9, 29, 22, 04); 
    times[9] = new GregorianCalendar(); 
    times[9].set(2005, 11, 31, 07, 45); 
 
 
    // and define the four STOs and store them in the observation cache 
    for ( int i = 0; i < 10; i++ ) { 
      StandardTeleObservation sto; 
      if( i < 5 ) { 
        sto = new StandardTeleObservation(FakeObjectExternalDataSource.NANCY_CPR,  
            "n4c", "treatment-id", Codes.LOINC_OID, dd, s[i]);   
      } 
      else { 
        sto = new StandardTeleObservation(FakeObjectExternalDataSource.JENS_CPR,  
            "n4c", "treatment-id", Codes.LOINC_OID, dd, s[i]); 
      } 
 
      // An STO has no timestamp until it is received on the server, 
      // here we fake that the server has stamped them with 
      // those exact dates above 
      sto.setTime( times[i].getTimeInMillis() ); 
      // serialize the STO 
      String serialform = serializer.serialize(sto); 
      // build the registry entry 
      builder = new PHMRBuilder(eds, sto); 
      // and store it in the cache 
      obscache.provideAndRegisterObservation(builder.buildRegistryEntry(), serialform); 
    } 
  } 
   
   
  @Test public void shouldConvertFromReqToObsCacheRequest() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.NANCY_CPR, 
        "2012-01-2", 
        "[\n" +  
            "['2012-01-21 14:56', 1.1, 2.2]\n" + 
            "['2012-01-21 15:54', 1.3, 3.0]\n" + 
            "['2012-01-21 20:43', 1.5, 1.4]\n" +  
            "['2012-01-22 10:12', 2.0, 1.2]\n" + 
        "]"); 
  } 
 
   
  @Test public void shouldConvertFromReqToObsCacheRequest2() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.NANCY_CPR, 
        "2012-01-22", 
        "[\n['2012-01-22 10:12', 2.0, 1.2]\n]"); 
  } 
   
   
  @Test public void shouldConvertFromReqToObsCacheRequest3() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.NANCY_CPR, 
        "2000-01-22", 
        "[\n" +  
            "['2012-01-21 14:56', 1.1, 2.2]\n" + 
            "['2012-01-21 15:54', 1.3, 3.0]\n" + 
            "['2012-01-21 20:43', 1.5, 1.4]\n" +  
            "['2012-01-22 10:12', 2.0, 1.2]\n" + 
            "['2011-03-11 1:55', 3.3, 0.3]\n" + 
        "]"); 
  } 
   
   
  @Test public void shouldConvertFromReqToObsCacheRequest4() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.JENS_CPR, 
        "2000-01-01", 
        "[\n" +  
            "['2012-01-15 14:16', 1.8, 1.5]\n" + 
            "['2006-02-05 16:34', 2.1, 2.3]\n" + 
            "['2010-08-22 10:12', 1.0, 1.0]\n" + 
            "['2001-10-29 22:4', 2.0, 2.0]\n" + 
            "['2005-12-31 7:45', 3.0, 0.5]\n" + 
        "]"); 
  } 
   
   
  @Test public void shouldConvertFromReqToObsCacheRequest5() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.JENS_CPR, 
        "2006-01-01", 
        "[\n" +  
            "['2012-01-15 14:16', 1.8, 1.5]\n" + 
            "['2006-02-05 16:34', 2.1, 2.3]\n" + 
            "['2010-08-22 10:12', 1.0, 1.0]\n" + 
        "]"); 
  } 
   
   
  @Test public void shouldConvertFromReqToObsCacheRequest6() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.JENS_CPR, 
        "2008-01-01", 
        "[\n" +  
            "['2012-01-15 14:16', 1.8, 1.5]\n" + 
            "['2010-08-22 10:12', 1.0, 1.0]\n" + 
        "]"); 
  } 
   
   
  @Test public void shouldConvertFromReqToObsCacheRequest7() throws Net4CareException, ParseException { 
    shouldConvertFromReqToObsCacheRequest( 
        FakeObjectExternalDataSource.BIRGITTE_CPR, 
        "2008-01-01", 
        "[\n]"); 
  } 
 
   
  private void shouldConvertFromReqToObsCacheRequest(String cpr, String startdate, String expectedOutput) throws Net4CareException, ParseException { 
    long begin, end; 
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd"); 
    begin = dateFormatter.parse(startdate).getTime(); 
    end = System.currentTimeMillis(); 
    String[] codes = Utility.convertBarSeparatedStringToCodeList("|19868-9|20150-9|"); 
    XDSQuery query = new XDSQueryPersonTimeIntervalType(cpr, begin, end, Codes.LOINC_OID, codes); 
 
    List<String> list = obscache.retrieveObservationSet(query);     
    // Request a graph being generated for FVC and FEV1 
    List<StandardTeleObservation> stoList = new ArrayList<StandardTeleObservation>(); 
    for ( String str : list ) { 
      stoList.add(serializer.deserialize(str)); 
    } 
     
    String v = GraphGenerator.generateGoogleGraphFromListOfObservations(stoList); 
    assertEquals( expectedOutput, v); 
  } 
} 
