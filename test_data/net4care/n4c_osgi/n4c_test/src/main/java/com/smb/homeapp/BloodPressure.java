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
 
package com.smb.homeapp; 
 
import java.util.*; 
 
import org.net4care.observation.*; 
 
/** Example of how to define a blood pressure observation 
 * that uses the UIPAC encoding system. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public class BloodPressure implements ObservationSpecifics { 
 
  private ClinicalQuantity systolic, diastolic; 
  public BloodPressure(double sys, double dia) { 
    systolic = new ClinicalQuantity(sys, "mm(Hg)","MSC88019","Systolisk BT" ); 
    diastolic = new ClinicalQuantity(dia, "mm(Hg)","MSC88020","Diastolisk BT" ); 
  } 
   
  public BloodPressure() { } 
   
  public ClinicalQuantity getSystolic() { 
    return systolic; 
  } 
   
  public ClinicalQuantity getDiastolic() { 
    return diastolic; 
  } 
 
  @Override 
  public String getObservationAsHumanReadableText() { 
    return "Systolisk BT: "+systolic + " / Diastolisk BT: "+diastolic; 
  } 
 
  @Override 
  public Iterator<ClinicalQuantity> iterator() { 
    List<ClinicalQuantity> list = new ArrayList<ClinicalQuantity>(2); 
    list.add( systolic ); list.add( diastolic ); 
    return list.iterator(); 
  } 
   
  public String toString() { 
    return getObservationAsHumanReadableText(); 
  } 
 
} 
