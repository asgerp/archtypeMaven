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
 
import java.text.SimpleDateFormat; 
import java.util.Iterator; 
import java.util.List; 
 
import org.net4care.observation.ClinicalQuantity; 
import org.net4care.observation.ObservationSpecifics; 
import org.net4care.observation.StandardTeleObservation; 
 
/** Generator to generate strings in the Google Chart Tools 
 * format for a list of StandardTeleObservations. 
 *  
 * @author Net4Care, Morten Larsson (MLN), AU. 
 * Minor changes by HBC 
 */ 
import com.apkc.archtype.quals.*;
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
          ) 
	
public class GraphGenerator { 
 
  /** Given a list of STO's generate the corresponding 
   * graph in the Google Chart Tools format. 
   * @param list  A list of serialized STO's.  
   * @return graph in Google Chart Tools format 
   */ 
  public static String generateGoogleGraphFromListOfObservations(List<StandardTeleObservation> list) { 
 
    StringBuilder graphOutput = new StringBuilder(); 
     
    //Define the desired time format for the graph. 
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd H:m"); 
     
    graphOutput.append("[\n"); 
     
    for ( StandardTeleObservation sto : list ) { 
       
      //Fetch a Iterator for the current STO. 
      ObservationSpecifics obsSpec = sto.getObservationSpecifics(); 
      Iterator<ClinicalQuantity> obsQuantities =  obsSpec.iterator(); 
       
      //Convert timestamp to desired time format. 
      String timeStr = dateFormatter.format(sto.getTime()); 
      graphOutput.append("['" + timeStr + "', "); 
       
      double clinicalValue;   
      while( obsQuantities.hasNext() ) { 
        clinicalValue = obsQuantities.next().getValue(); 
        graphOutput.append(clinicalValue); 
        if(obsQuantities.hasNext()) 
          graphOutput.append(", "); 
      } 
      graphOutput.append("]\n"); 
    } 
    graphOutput.append("]"); 
 
    return graphOutput.toString(); 
  } 
}