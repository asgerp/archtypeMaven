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
import java.util.*; 
 
import java.text.SimpleDateFormat; 
 
import org.net4care.observation.*; 
 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.*; 
import org.net4care.forwarder.query.*; 
 
import org.net4care.utility.Net4CareException; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
 
/** Simple command line demo of query of observations for the 
 * last 10 minutes. Interleave calling this with calls to the 
 * HelloUpload program so the server has some observations stored. 
 * 
 * To start a server, execute 'mvn install pax:provision' in the 
 * n4c_receiver folder/bundle. 
 * 
 * @author Net4Care, Henrik Baerbak Christensen, Aarhus University 
 */ 
public class HelloQuery { 
  public static void main(String[] args) { 
    new HelloQuery("http://localhost:8080/observation"); 
  } 
   
  // The "uploader" which is responsible for sending 
  // observations to the Net4Care server 
  private DataUploader dataUploader; 
 
  /** Create and upload an observation on a running web server 
   * on the full address given 
   */ 
  public HelloQuery(String serverAddress) { 
    System.out.println( "Hello Net4Care World: " 
                        +"Query all observations for last 10 minutes" ); 
    System.out.println( "   to server at address: "+serverAddress ); 
    System.out.println( "   (Make sure the Net4Care server is running!)" ); 
 
    // Configure the architecture 
    dataUploader = Shared.setupN4CConfiguration( serverAddress ); 
 
    // Define a query for a given person id and time interval 
    String personId = "251248-4916"; 
 
    Query query;  
 
    // Define a time interval spanning from now and 10 minutes back in time 
    long now = System.currentTimeMillis(); 
    long tenminutesago = now - 1000L * 60L * 10L; 
    // Select the requrired query by picking the proper class implementing 
    // the query interface 
    query = new QueryPersonTimeInterval(personId, tenminutesago, now); 
    System.out.println("The query is: "+ query+"\n" ); 
     
    // Send the query to the server. 
    QueryResult res = null; 
    try { 
      res = dataUploader.query(query); 
      res.awaitUninterruptibly(); 
    } catch (IOException e) { 
      System.out.println("Currently unable to connect to server"); 
      e.printStackTrace(); 
      System.exit(-1); 
    } catch (Net4CareException e) { 
      System.out.println("Encountered a Net4Care exception \n" 
                         +"\n Please inspect the console for details"); 
      e.printStackTrace(); 
      System.exit(-1); 
    } 
 
    // Retrieve the list of observations...  
    List<StandardTeleObservation> obslist; 
    System.out.println( "=== Observations from the last 10 minutes ===" ); 
    obslist = res.getObservationList(); 
 
    // First output observations using just the toString() method 
    System.out.println( " --- RAW FORMAT - using toString() ---" ); 
    for ( StandardTeleObservation sto : obslist ) { 
      System.out.println( sto.toString() ); 
    } 
 
    // Next output observations using the API of StandardTeleObservation 
    // as well as the special Spirometry class we ourselves have defined. 
    System.out.println( " --- OWN FORMAT - using API ---" ); 
    String result = null; 
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
 
    for ( StandardTeleObservation sto : obslist ) { 
      System.out.println( "-*-*-*-*-*-" );  
      long timestamp = sto.getTime();  
      Date d = new Date(timestamp); 
      Spirometry spiro = (Spirometry) sto.getObservationSpecifics(); 
      String nicedate = sdf.format( d ); 
      result = "  At date/time: "+nicedate+"\n"+ 
        "    FVC="+spiro.getFvc().getValue()+spiro.getFvc().getUnit()+ 
          " FEV1="+spiro.getFev1().getValue()+spiro.getFev1().getUnit()+ 
        ".\n"+ 
        "    Answer to question \"Feeling well?\" : " 
        +spiro.getQuestionAAnswer()+"\n"+ 
        "    Comment: "+ sto.getComment(); 
      System.out.println( result ); 
    } 
    System.out.println( "End." ); 
 } 
}