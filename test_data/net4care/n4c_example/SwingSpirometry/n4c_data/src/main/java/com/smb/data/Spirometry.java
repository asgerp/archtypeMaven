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
 
package com.smb.data; 
 
import java.util.*; 
 
import org.net4care.observation.*; 
 
/** This is a special observation class defined by a 
 * SMB. The key aspect is that Net4Care does not have 
 * direct access to this particular class. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 *  
 */ 
public class Spirometry implements ObservationSpecifics { 
   
  private ClinicalQuantity fvc, fev1; 
   
  public ClinicalQuantity getFvc() { 
    return fvc; 
  } 
 
  public ClinicalQuantity getFev1() { 
    return fev1; 
  } 
   
  public Spirometry() { } 
   
  /** Create a spirometry observation 
   *  
   * @param fvc FVC value 
   * @param fev1 FEV1 value 
   */ 
  public Spirometry(double fvc, double fev1) { 
    this.fvc = new ClinicalQuantity(fvc, "L", "19868-9", "FVC"); // LOINC FVC 
    this.fev1 = new ClinicalQuantity(fev1, "L","20150-9", "FEV1"); 
  } 
 
  public String toString() { 
    return "Spiro: "+getFev1() + "/"+getFvc(); 
  } 
 
  @Override 
  public Iterator<ClinicalQuantity> iterator() { 
    List<ClinicalQuantity> list = new ArrayList<ClinicalQuantity>(2); 
    list.add( fvc ); list.add(fev1); 
    return list.iterator(); 
  } 
 
  @Override 
  public String getObservationAsHumanReadableText() { 
    return "Measured: "+getFvc().toString() + " / " + getFev1().toString(); 
  } 
  
} 
