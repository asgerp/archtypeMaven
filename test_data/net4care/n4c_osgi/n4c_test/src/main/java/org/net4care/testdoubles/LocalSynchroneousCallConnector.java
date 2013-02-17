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
 
package org.net4care.testdoubles; 
 
import java.io.IOException; 
import java.util.Map; 
 
import javax.management.RuntimeErrorException; 
 
import org.net4care.forwarder.FutureResult; 
import org.net4care.forwarder.FutureResultWithAnswer; 
import org.net4care.forwarder.Query; 
import org.net4care.forwarder.ServerConnector; 
import org.net4care.receiver.ObservationReceiver; 
import org.net4care.utility.Net4CareException; 
 
/** A ServerConnector implementation that is not distributed but 
 * simply makes local method calls to a provided ObservationReceiver 
 * instance. 
 *  
 * Use this for fast and easy JUnit testing, see several uses in 
 * the 'scenario' package. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public class LocalSynchroneousCallConnector implements ServerConnector { 
 
  public String lastSentString = null; 
 
  private ObservationReceiver receiver; 
 
  public LocalSynchroneousCallConnector(ObservationReceiver receiver) { 
    this.receiver = receiver; 
  } 
 
  @Override 
  public FutureResult sendToServer(String onTheWireFormat) { 
    lastSentString = onTheWireFormat; 
    try { 
      receiver.observationMessageReceived(onTheWireFormat); 
    } catch (Exception e2) { 
      throw new RuntimeException(e2); 
    } 
 
    FutureResult result = new FutureResult() { 
 
      @Override 
      public boolean isSuccess() { 
        return true; 
      } 
 
      @Override 
      public boolean isDone() { 
        return true; 
      } 
 
      @Override 
      public void awaitUninterruptibly() { 
      } 
 
      @Override 
      public String result() { 
        return "OK"; 
      } 
    }; 
    return result; 
  } 
 
  @Override 
  public FutureResultWithAnswer queryToServer(Query query)  
      throws Net4CareException, IOException { 
 
    Map<String,String> map = query.getDescriptionMap(); 
 
    final String listOfObsAsString = receiver.observationQueryReceived(map); 
     
     
    // System.out.println(" -----> "+listOfObsAsString); 
     
    FutureResultWithAnswer future = new FutureResultWithAnswer() { 
       
      @Override 
      public String result() { 
        return "OK"; 
      } 
       
      @Override 
      public boolean isSuccess() { 
        return true; 
      } 
       
      @Override 
      public boolean isDone() { 
        return true; 
      } 
       
      @Override 
      public void awaitUninterruptibly() { 
      } 
       
      @Override 
      public String getAnswer() { 
        return listOfObsAsString; 
      } 
    }; 
     
    // System.out.println(" returning queryToServer with "+future.getAnswer()); 
    return future; 
  } 
 
} 
