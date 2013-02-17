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
 
package org.net4care.storage; 
 
/** A data container for the meta data that defines 
 * an entry in the XDS registry. It contains 
 * enough information to identify a PHMR document 
 * in an XDS repository. It is stored in the 
 * XDSRegistry. 
 *  
 * It is also (re)used as the entry description for 
 * the observations stored in the ObservationCache. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public class RegistryEntry { 
  private String cpr; 
  private long timestamp; 
  private String codeSystem; 
  private String[] codesOfValuesMeasured; 
  private String organizationId; 
  private String treatmentId; 
   
  /** Create a RegistryEntry object. 
   *  
   * @param cpr CPR no of person  
   * @param timestamp timestamp when data was created 
   * @param codeSystem encoding system of the stored clinical quantities 
   * @param codesOfValuesMeasured codes (in the codeSystem) of 
   * all clincial quantities that this metadata represents 
   * @param organizationId id of the organization whose 
   * devices produced the clinical quantities 
   * @param treatmentId id of the treatment that warrented 
   * these clinical quantities to be measured - typically 
   * id of a diagnosis (like "has KOL") in an EPJ system 
   * which meant the physician prescribed the use of a device 
   * to measure clinical quantities (like lung function FEV1, etc.) 
   */ 
  public RegistryEntry(String cpr, long timestamp,  
                  String codeSystem, 
                  String[] codesOfValuesMeasured,  
                  String organizationId,  
                  String treatmentId ) { 
    super(); 
    this.cpr = cpr; 
    this.timestamp = timestamp; 
    this.codeSystem = codeSystem; 
    this.codesOfValuesMeasured = codesOfValuesMeasured; 
    this.organizationId = organizationId; 
    this.treatmentId = treatmentId; 
  } 
   
  public String getCpr() { 
    return cpr; 
  } 
  public long getTimestamp() { 
    return timestamp; 
  } 
 
  public String getCodeSystem() { 
    return codeSystem; 
  } 
 
  public String[] getCodesOfValuesMeasured() { 
    return codesOfValuesMeasured; 
  } 
 
  public String getOrganizationId() { 
    return organizationId; 
  } 
 
  public String getTreatmentId() { 
    return treatmentId; 
  } 
   
  public String toString() { 
    return "MetaData ["+getCpr()+"/"+getTimestamp()+"/"+getCodeSystem()+"/"+ 
        getOrganizationId() + "/"+ 
        getTreatmentId()+"]"; 
  } 
} 
