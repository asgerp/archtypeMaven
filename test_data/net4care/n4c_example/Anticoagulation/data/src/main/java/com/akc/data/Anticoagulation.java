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
 
package com.akc.data; 
 
import java.util.*; 
 
import org.net4care.observation.*; 
 
/** This is a special observation class defined by a 
 * AKC. The key aspect is that Net4Care does not have 
 * direct access to this particular class. 
 *  
 * @author Morten Larsson (MLN), AU 
 *  
 */ 
public class Anticoagulation implements ObservationSpecifics { 
   
  private ClinicalQuantity INR, tablets; 
   
  public ClinicalQuantity getINR() { 
    return INR; 
  } 
 
  public ClinicalQuantity getTablets() { 
    return tablets; 
  } 
   
  public Anticoagulation() { } 
   
  /** Create a anticoagulation observation 
   *  
   * @param INR Coagulation tissue factor 
   * @param tablets Number of tables 
   */ 
  public Anticoagulation(double coag, double tablets) { 
    this.INR = new ClinicalQuantity(coag, "INR", "34714-6", "INR Bld"); 
    this.tablets = new ClinicalQuantity(tablets, "tbl","57262-8", "MA"); 
  } 
 
  public String toString() { 
    return "Anticoagulation: "+getINR() + "/"+getTablets(); 
  } 
 
  @Override 
  public Iterator<ClinicalQuantity> iterator() { 
    List<ClinicalQuantity> list = new ArrayList<ClinicalQuantity>(2); 
    list.add( INR ); list.add(tablets); 
    return list.iterator(); 
  } 
 
  @Override 
  public String getObservationAsHumanReadableText() { 
    return "Measured: "+getINR().toString() + " / " + getTablets().toString(); 
  } 
  
} 
