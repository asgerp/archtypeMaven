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
 
import java.util.Map; 
 
import org.net4care.forwarder.Query; 
import org.net4care.utility.QueryKeys; 
 
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
/** A query for a given person in a given time and a given set of types of observation 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
@ArchTypeComponent(
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
        )
public class QueryPersonTimeIntervalType extends QueryPersonTimeInterval implements Query { 
 
  private String codeSystem; 
  private String codeListInBarFormat; 
   
  /** Construct a query for a given person in a given time interval and 
   * only those observations that match a given set of types of observations. 
   * @param cpr CPR of person 
   * @param beginTimeInterval start of interval in Unix time milliseconds 
   * @param endTimeInterval end of interval 
   * @param codeSystem HL7 OID of the code system used in the list of 
   * codes below. 
   * @param codeListInBarSeparatedFormat the list of codes as one 
   * string separated by vertical bar "|". If you have an array 
   *  of codes, use the Utility converter functions to produce this 
   *  string. 
   */ 
  public QueryPersonTimeIntervalType(String cpr, long beginTimeInterval, long endTimeInterval, 
      String codeSystem, String codeListInBarSeparatedFormat) { 
    super(cpr, beginTimeInterval, endTimeInterval); 
    this.codeSystem = codeSystem; 
    this.codeListInBarFormat = codeListInBarSeparatedFormat; 
  } 
 
  @Override 
  public Map<String, String> getDescriptionMap() { 
    Map<String,String> descriptionMap = super.getDescriptionMap(); 
    descriptionMap.put(QueryKeys.QUERY_TYPE, QueryKeys.PERSON_TIME_OBSERVATION_TYPE_QUERY); 
    descriptionMap.put(QueryKeys.CODE_SYSTEM, this.codeSystem); 
    descriptionMap.put(QueryKeys.CODE_LIST_BAR_FORMAT, this.codeListInBarFormat); 
    return descriptionMap; 
  } 
 
} 
