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
 
package org.net4care.storage.queries; 
 
/** A query for a person in a timeinterval and for a set of types of 
 * measurements 
 *  
 * @author Net4Care, Henrik Baerbak Christensen. 
 */ 
public class XDSQueryPersonTimeIntervalType extends XDSQueryPersonTimeInterval { 
  private String codeSystem; 
  private String[] observationTypes; 
 
  /** Construct a Query. 
   *  
   * @param cpr CPR of person to look for. 
   * @param beginTimeInterval start of interval to look for (unix long) 
   * @param endTimeInterval end of interval to query 
   * @param codeSystem the encoding systems used for the codes in the next 
   * parameter 
   * @param observationTypes a set of strings defining the codes 
   * to look for (e.g. LOINC codes for FEV1 and FVC or similar.) 
   */ 
  public XDSQueryPersonTimeIntervalType(String cpr, long beginTimeInterval, 
      long endTimeInterval, String codeSystem, String[] observationTypes ) { 
    super(cpr,beginTimeInterval,endTimeInterval); 
    this.codeSystem = codeSystem; 
    this.observationTypes = observationTypes; 
  } 
 
  public String[] getObservationTypes() { 
    return observationTypes; 
  } 
 
  public String getCodeSystem() { 
    return codeSystem; 
  } 
} 
