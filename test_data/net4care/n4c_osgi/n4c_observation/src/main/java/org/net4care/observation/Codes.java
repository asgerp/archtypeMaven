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
 
/** This is a collection of HL7 object ID (OID) for 
 * various constants. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
 import com.apkc.archtype.quals.*;
 @ArchTypeComponent(
           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
           ) 
public class Codes { 
 
  /** The LOINC code */ 
  public static final String LOINC_OID = "2.16.840.1.113883.6.1"; 
 
  // === Net4Care OIDs 
  /** 
   *  
  You have now registered the following OID: 
   
  OID 
  2.16.840.1.113883.3.1558 
   
  OID Key 
  8FE6306B-7F31-42FD-884F-66580DA6C94A 
   
Please record this for future reference; you will need both the OID and the OID Key to edit your entry. 
 
You can also edit your OID using the following link: 
 
http://www.hl7.org/oid/index.cfm?Comp_OID=2.16.840.1.113883.3.1558&OID_Key=8FE6306B-7F31-42FD-884F-66580DA6C94A 
 
   *  
   */ 
  public static final String NET4CARE_ROOT_OID = "2.16.840.1.113883.3.1558"; 
 
  // Branch 1 : Danish Medical coding systems 
  private static final String DK_MEDICAL_CODE_BRANCH = ".1"; 
   
  // UIPAC 
  public static final String UIPAC_OID = NET4CARE_ROOT_OID + DK_MEDICAL_CODE_BRANCH + ".1";  
   
   
  // Branch 2 : Danish public coding systems 
  private static final String DK_PUBLIC_CODE_BRANCH = ".2"; 
  public static final String DK_CPR_OID = NET4CARE_ROOT_OID + DK_PUBLIC_CODE_BRANCH + ".1"; 
 
  // Branch 10: Danish Hospital Organizational Units 
  private static final String DK_HOSPITAL_ORGANIZATIONAL_UNITS = ".10"; 
   
  public static final String DK_REGION_MIDT = NET4CARE_ROOT_OID + DK_HOSPITAL_ORGANIZATIONAL_UNITS + ".6"; // old 06 telephone area :) 
  
  // Skeyby sygehus 
  public static final String DK_REGION_MIDT_SKEJBY_SYGEHUS = DK_REGION_MIDT+".1"; 
 
  // Devices used in the net4care project  
  private static final String DK_DEVICE_TYPE_ROOT_CODE = ".3"; 
 
  public static final String NET4CARE_DEVICE_OID = NET4CARE_ROOT_OID + DK_DEVICE_TYPE_ROOT_CODE;  
 
} 
