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
 
package org.net4care.forwarder.delegate; 
 
import java.io.IOException; 
import java.util.*; 
 
import org.net4care.forwarder.DataUploader; 
import org.net4care.forwarder.FutureResult; 
import org.net4care.forwarder.FutureResultWithAnswer; 
import org.net4care.forwarder.Query; 
import org.net4care.forwarder.QueryResult; 
import org.net4care.forwarder.ServerConnector; 
import org.net4care.serializer.Serializer; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.utility.Net4CareException; 
 
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
/** The default/standard implementation of the DataUploader interface. 
 * It basically just a) serializes the observation and b) uploads it  
 * to the Net4Care server side; and does the reverse for a query. 
 *  
 * It must be configured with appropriate delegates to handle the 
 * serializer and server connector roles. 
 *  
 * For a 'standard' distributed home application this is the 
 * JacksonJSONSerializer and the HTTPConnector.  
 *  
 * For testing purposes, you may use a 'in memory/local' 
 * connector, see the JUnit testing code for examples of this. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
@ArchTypeComponent(
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
        )
public class StandardDataUploader implements DataUploader { 
 
  Serializer serializer; 
  ServerConnector connector; 
   
  public StandardDataUploader(Serializer serializer,  
                              ServerConnector connector) { 
    this.serializer = serializer;   
    this.connector = connector; 
  } 
   
  @Override 
  public FutureResult upload(StandardTeleObservation tm) throws IOException { 
    String onTheWireFormat = serializer.serialize(tm);   
    FutureResult result = connector.sendToServer( onTheWireFormat ); 
    return result; 
  } 
 
  @Override 
    public QueryResult query(Query query) throws IOException, Net4CareException { 
    final FutureResultWithAnswer result = connector.queryToServer( query ); 
    //System.out.println(" result = "+result ); 
    result.awaitUninterruptibly(); // TODO: Does not work well under async mode :( 
    String largeMessage = result.getAnswer(); 
         
    List<StandardTeleObservation> list = null; 
    List<String> phmrList = null; 
     
    // Deserialize the string into the list of observations... 
    if ( query.getFormatOfReturnedObservations() == Query.QueryResponseType.STANDARD_TELE_OBSERVATION ) { 
      list = serializer.deserializeList(largeMessage); 
    } else { 
      phmrList = serializer.deserializeStringList(largeMessage); 
    } 
     
    QueryResult qr = new StandardDataUploaderQueryResult(result, list, phmrList); 
    return qr; 
  } 
} 
 
class StandardDataUploaderQueryResult implements QueryResult { 
  private FutureResultWithAnswer result; 
  private List<StandardTeleObservation> obslist; 
  private List<String> hl7list; 
   
  public StandardDataUploaderQueryResult(FutureResultWithAnswer result, 
      List<StandardTeleObservation> obslist, 
      List<String> hl7list) { 
    this.result = result; 
    this.obslist = obslist; 
    this.hl7list = hl7list; 
  } 
   
   
  @Override 
  public String result() { 
    return result.result(); 
  } 
   
  @Override 
  public boolean isSuccess() { 
    return result.isSuccess(); 
  } 
   
  @Override 
  public boolean isDone() { 
    return result.isDone(); 
  } 
   
  @Override 
  public void awaitUninterruptibly() { 
    result.awaitUninterruptibly(); 
  } 
   
  @Override 
  public List<StandardTeleObservation> getObservationList() { 
    return obslist; 
  } 
 
  @Override 
  public List<String> getDocumentList() { 
    return hl7list; 
  } 
}; 
