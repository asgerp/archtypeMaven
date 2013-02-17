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
 
package org.net4care.forwarder.query; 
 
import java.util.HashMap; 
import java.util.Map; 
 
import org.net4care.forwarder.Query; 
import org.net4care.utility.QueryKeys; 
 
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
/** A query for observations on a given person in 
 * a given time interval. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
@ArchTypeComponent(
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
        )
public class QueryPersonTimeInterval implements Query { 
  private String cpr; 
  private long beginTimeInterval; 
  private long endTimeInterval; 
  private QueryResponseType responseType; 
   
  /** Define a query. Time is defined in terms 
   * of Unix time (long integer milliseconds). 
   *  
   * @param cpr person to query documents for 
   * @param beginTimeInterval beginning of the time interval 
   * @param endTimeInterval end of the time interval 
   */ 
  public QueryPersonTimeInterval(String cpr, long beginTimeInterval, 
      long endTimeInterval) { 
    super(); 
    this.cpr = cpr; 
    if ( beginTimeInterval > endTimeInterval ) { 
      throw new RuntimeException("Your begin time is later than your end time."); 
    } 
    this.beginTimeInterval = beginTimeInterval; 
    this.endTimeInterval = endTimeInterval; 
    responseType = QueryResponseType.STANDARD_TELE_OBSERVATION; 
  } 
   
  String getCpr() { 
    return cpr; 
  } 
  long getBeginTimeInterval() { 
    return beginTimeInterval; 
  } 
  long getEndTimeInterval() { 
    return endTimeInterval; 
  } 
   
  public String toString() { 
    return "Forwarder.QueryPersonTimeInterval for "+getCpr()+" in time ("+getBeginTimeInterval()+"-"+getEndTimeInterval()+")"; 
  } 
 
  @Override 
  public Map<String, String> getDescriptionMap() { 
    Map<String,String> themap = new HashMap<String,String>(); 
    themap.put(QueryKeys.FORMAT_KEY, QueryKeys.ACCEPT_JSON_DATA); 
    if ( getFormatOfReturnedObservations() == QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD ) { 
      themap.put(QueryKeys.FORMAT_KEY, QueryKeys.ACCEPT_XML_DATA); 
    } 
    themap.put( QueryKeys.QUERY_TYPE, QueryKeys.PERSON_TIME_QUERY ); 
    themap.put( QueryKeys.CPR_KEY, getCpr()); 
    themap.put( QueryKeys.BEGIN_TIME_INTERVAL, ""+getBeginTimeInterval()); 
    themap.put( QueryKeys.END_TIME_INTERVAL, ""+getEndTimeInterval()); 
    return themap; 
  } 
 
  @Override 
  public void setFormatOfReturnedObservations(QueryResponseType responseType) {    
    this.responseType = responseType; 
  } 
 
  @Override 
  public QueryResponseType getFormatOfReturnedObservations() { 
    return responseType; 
  } 
} 
