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

package org.net4care.observation; 
 
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;

 
/** A clinical physical quantity, based upon the 
 *  implementation of PQ from HL7/CDA standard. 
 *  See "The CDA Book" p. 41 by Keith W. Boone. 
 *  
 * This class enhances PQ with an exact clinical 
 * identification of what it represents using 
 * a code in some code system. 
 *  
 * The enclosing StandardTeleObservation object 
 * is responsible for defining the actual 
 * coding system, like LOINC or UIPAC. 
 *  
 * NOTE: This class is highly coupled to the  
 * ServerJSONSerializer - any changes even 
 * in field naming of this class will break 
 * the server side deserialization. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 */ 
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
          ) 
public final class ClinicalQuantity { 
  private double value; 
  private String unit; 
  /** The code identifying the specific physical 
   * quantity measured. 
   * Example: FEV1 is code 20150-9 in LOINC. 
   * Note - the actual code system defining 
   * the interpretation of this code is defined 
   * by the codeSystem property of the enclosing 
   * StandardTeleObservation object. 
   */ 
  private String code; 
  /** the displayname of the code. 
   * Example: FEV1. 
   */ 
  private String displayName; 
   
 
  /** The default constructor should not be used by 
   * client code, but is necessary to uphold the 
   * bean properties required by some serializers. 
   */ 
  public ClinicalQuantity() {} 
   
  /** Construct a read-only PhysicalQuantity that 
   * measures some specific clinical value 
   * denoted by its code in some coding system 
   * (like UIPAC, LOINC, SNOMED CT, etc.). 
   *   
   * The unit must be UCUM coded, 
   * see the Regenstrief Institute website. 
   * @param value value, e.g. 200 
   * @param unit unit, e.g. "mg" 
   * @param code the code that identifies the clinical 
   * quantity this object represents, e.g. "20150-9" 
   * represents FEV1 in LOINC coding system 
   * @param displayName the human readable name of 
   * the clinical quantity, e.g. "FEV1" 
   */ 
  public ClinicalQuantity(double value, String unit, 
      String code, String displayName) { 
    super(); 
    this.value = value; 
    this.unit = unit; 
    this.code = code; 
    this.displayName = displayName; 
  } 
  public double getValue() { 
    return value; 
  } 
  public String getUnit() { 
    return unit; 
  }   
  public String getCode() { 
    return code; 
  } 
  public String getDisplayName() { 
    return displayName; 
  } 
 
  public String toString() { 
      return getDisplayName()+":"+getValue() + " "+ getUnit(); 
  } 
} 
