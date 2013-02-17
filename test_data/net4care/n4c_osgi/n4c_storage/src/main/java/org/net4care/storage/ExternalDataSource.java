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
 
import org.net4care.utility.Net4CareException; 
 
/** Interface to External data sources 
 * (feeder systems) for CPR and person data; and EHR that 
 * has treatment data. 
 
 * @author Henrik Baerbak Christensen, Aarhus University 
 
 *  */ 
public interface ExternalDataSource { 
 
  /** Given a valid CPR, return data about the person. 
   * @param cpr a valid CPR as string 
   * @throws UnknownCPRException if CPR is not i datasource. 
   * @return data about the person 
   */ 
  public PersonData getPersonData(String cpr) throws Net4CareException; 
   
  /** Given the ID of a valid treatment, return a record 
   * of information outlining the treatment, specifically 
   * information on the assigned physician. 
   * @param treatmentID valid id of a treatment that makes 
   * sense to the associated EHR system. 
   * @return a record of information outlining the treatment 
   */ 
  public TreatmentData getTreatmentData(String treatmentID); 
 
} 
