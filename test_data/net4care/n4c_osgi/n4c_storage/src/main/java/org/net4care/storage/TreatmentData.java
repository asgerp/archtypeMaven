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
 
/** The data of a treatment (behandling). Any device set up 
 * in a patients home must be set there for a reason, 
 * namely as part of a treatment. That is, a central 
 * (net4care) organization has stewardship/is responsible 
 * for authorizing a treatment. This interface defines 
 * the data associated with the treatment.     
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * */ 
 
public interface TreatmentData { 
 
  /** Get the CPR of the physician that has authorized 
   * this treatment. 
   * @return cpr of associated physician 
   */ 
  public abstract String getAuthorCPR(); 
 
  /** get the name of the organization that has 
   * stewardship, typically a hospital or a GP. 
   * @return 
   */ 
  public abstract String getStewardOrganizationName(); 
 
  /** get the address of the custodian organization 
   *  
   * @return address of custodian organization 
   */ 
  public abstract AddressData getCustodianAddr(); 
 
}