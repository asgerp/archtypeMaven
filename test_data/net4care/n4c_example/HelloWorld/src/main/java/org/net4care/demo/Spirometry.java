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
 
package org.net4care.demo; 
 
import java.util.*; 
 
import org.net4care.observation.*; 
 
/** This is an example of how a SMB will adapt 
 * Net4Care for a specific set of measurements  
 * used for their given program/device. 
 * 
 * You create a class implementing the ObservationSpecifics 
 * interface. This class MUST OBEY JAVA BEAN PROPERTIES. 
 *  
 * This demo class defines two spirometry (lung capacity) 
 * values as well as the answer to a questionaire question. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 *  
 */ 
public class Spirometry implements ObservationSpecifics { 
   
  private ClinicalQuantity fvc, fev1; 
  private boolean questionAAnswer; 
 
  public ClinicalQuantity getFvc() { 
    return fvc; 
  } 
 
  public ClinicalQuantity getFev1() { 
    return fev1; 
  } 
 
  public boolean getQuestionAAnswer() { 
    return questionAAnswer; 
  } 
 
  public Spirometry() { } 
   
  /** Create a spirometry observation 
   *  
   * @param fvc FVC value 
   * @param fev1 FEV1 value 
   * @param questionAAnswer the boolean answer to the questionaire's 
   * question A) "Are you feeling well?" 
   */ 
  public Spirometry(double fvc, double fev1, boolean questionAAnswer) { 
    this.fvc = new ClinicalQuantity(fvc, "L", "19868-9", "FVC"); // LOINC FVC 
    this.fev1 = new ClinicalQuantity(fev1, "L","20150-9", "FEV1"); 
    this.questionAAnswer = questionAAnswer; 
  } 
 
  public String toString() { 
    return "Spiro: "+getFev1() + "/"+getFvc() + "("+getQuestionAAnswer()+")"; 
  } 
 
  @Override 
  public Iterator<ClinicalQuantity> iterator() { 
    List<ClinicalQuantity> list = new ArrayList<ClinicalQuantity>(2); 
    list.add( fvc ); list.add(fev1); 
    return list.iterator(); 
  } 
 
  @Override 
  public String getObservationAsHumanReadableText() { 
    return "Measured: "+getFvc().toString() + " / " + getFev1().toString() 
      + "("+getQuestionAAnswer()+")"; 
  } 
  
} 
