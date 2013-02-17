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
/** Any observation of clinical physical quantities (typically 
 * measured by some device in the home) is embedded in an object 
 * of this type that expresses the measurement context of the 
 * actual values measured. This entail device characteristics, 
 * organizational context and stewardship, etc. 
 *  
 * DO NOT CHANGE NAMING OF FIELDS NOR METHOD AS THE 
 * SERVER SIDE DESERIALIZER ARE DEPENDENT UPON 
 * THESE NAMES. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
          ) 
public final class StandardTeleObservation { 
  /** The patient ID - could obviously in DK be CPR 
   * but may be any unique identifier */ 
  private String patientCPR; 
   
  /** The OID of the organization whose device 
   * provides this observation. Recommend that 
   * an HL7 OID is used 
   */ 
  private String organizationUID; 
   
  /** The unique ID of the treatment that resulted in 
   * this observation being recorded. I.e. 
   * if you observe FVC and FEV1 the it may be because 
   * you have a KOL diagnosis, and the document/ 
   * diagnosis must be referred to using this id. 
   */ 
  private String treatmentID; 
   
  /** Time when this observation was constructed 
   * on the client's side. UNIX long integer format 
   * used. */ 
  private long time; 
   
  /** The OID of the code system defining 
   * the semantics of the 'code' property of 
   * the associated observationSpecifics' 
   * ClinicalQuantity objects. 
   * Example: LOINC is codeSystem 2.16.840.1.113883.6.1 
   * in the HL7 OID, and if this is defined here, 
   * all clinical quantities are coded using LOINC. 
   */ 
  private String codeSystem; 
 
  /** the description of the device */ 
  private DeviceDescription deviceDescription; 
   
  /** the specific clincial quantities measured */ 
  private ObservationSpecifics observationSpecifics; 
   
  /** an optional string given by the patient about 
   * the observation. 
   */ 
  private String comment; 
 
  /** Default constructor to have bean properties. Do not 
   * use this in client code. Provided only for 
   * serialization and deserialization. 
   */ 
  public StandardTeleObservation() {} 
   
  /** Create an observation.  
   * Precondition: All parameters must NOT be null. 
   * @param patientCPR CPR identity of the patient this 
   * observation is made on 
   * @param organisationOID the unique Net4Care issued 
   * identity of the organization that operates the 
   * device that has made this observation 
   * @param treatmentID Unique ID of the treatment (behandling) 
   * that this observation is part of. Any device set up 
   * in a patients home must be set there for a reason, 
   * namely as part of a treatment. That is a central 
   * (net4care) organization has stewardship/is responsible 
   * for authorizing a treatment - this treatment must 
   * be associated with a unique ID and the teleobservation 
   * must tell it belongs to this by refering to it. 
   * @param codeSystem the coding system using HL7 OID that 
   * is used in all ClinicalQuantities defined in the 
   * ObservationSpecific delegate. Coding systems are 
   * typically LOINC (internationally) or UIPAC (Denmark). 
   * Consult the 'Codes' class for constants defining 
   * LOINC and UIPAC. 
   * @param deviceDescription the description of the device 
   * this observation stems from. 
   * @param obsspec the object that contains the actual 
   * measurements. */ 
  public StandardTeleObservation(String patientCPR,  
      String organisationOID, 
      String treatmentID, 
      String codeSystem, 
      DeviceDescription deviceDescription, 
      ObservationSpecifics obsspec ) { 
    this(patientCPR, organisationOID, treatmentID, 
        codeSystem, deviceDescription, obsspec, ""); 
  } 
 
  /** Create an observation.  
   * Precondition: All parameters must NOT be null. 
   * @param patientCPR CPR identity of the patient this 
   * observation is made on 
   * @param organisationOID the unique Net4Care issued 
   * identity of the organization that operates the 
   * device that has made this observation 
   * @param treatmentID Unique ID of the treatment (behandling) 
   * that this observation is part of. Any device set up 
   * in a patients home must be set there for a reason, 
   * namely as part of a treatment. That is a central 
   * (net4care) organization has stewardship/is responsible 
   * for authorizing a treatment - this treatment must 
   * be associated with a unique ID and the teleobservation 
   * must tell it belongs to this by refering to it. 
   * @param codeSystem the coding system using HL7 OID that 
   * is used in all ClinicalQuantities defined in the 
   * ObservationSpecific delegate. Coding systems are 
   * typically LOINC (internationally) or UIPAC (Denmark). 
   * Consult the 'Codes' class for constants defining 
   * LOINC and UIPAC. 
   * @param deviceDescription the description of the device 
   * this observation stems from. 
   * @param obsspec the object that contains the actual 
   * measurements.  
   * @param comment a (non-empty) comment that provides 
   * patient's remarks on the measurement. 
   * */ 
  public StandardTeleObservation(String patientCPR,  
      String organisationOID, 
      String treatmentID, 
      String codeSystem, 
      DeviceDescription deviceDescription, 
      ObservationSpecifics obsspec, 
      String comment) { 
    this.patientCPR = patientCPR; 
    this.organizationUID = organisationOID; 
    this.treatmentID = treatmentID; 
    this.codeSystem = codeSystem; 
    this.deviceDescription = deviceDescription; 
    this.observationSpecifics = obsspec; 
    time = System.currentTimeMillis(); 
    this.comment = comment; 
    if ( this.comment == null ) {  
      this.comment = ""; 
    } 
  } 
 
  /** set the timestamp of this STO. 
   * NOTE: should not be used by clients, 
   * but useful for testing. 
   * @param time new timestamp to set for this 
   * STO. 
   */ 
  public void setTime(long time) { 
    this.time = time; 
  } 
   
  /** Get the unique ID of the patient */ 
  public String getPatientCPR() { 
    return patientCPR; 
  } 
 
  public String getOrganizationUID() { 
    return organizationUID; 
  } 
 
  public String getTreatmentID() { 
    return treatmentID; 
  } 
   
  public long getTime() { 
    return time; 
  } 
   
  public String getCodeSystem() { 
    return codeSystem; 
  } 
 
  public DeviceDescription getDeviceDescription() { 
    return deviceDescription; 
  } 
 
  public ObservationSpecifics getObservationSpecifics() { 
    return observationSpecifics; 
  } 
   
  public String getComment() { 
    return comment; 
  } 
 
  public String toString() { 
      return "STO: "+getPatientCPR() + "/"+getOrganizationUID() + "/"+getDeviceDescription()+"/obs=("+getObservationSpecifics()+")"; 
  } 
 
  public String getDeviceDescriptionAsHumanReadableText() { 
    String value = "Device properties: "+ 
       "Type: "+deviceDescription.getType()+ 
       " Model: "+deviceDescription.getModel()+ 
       " Manufacturer: "+ deviceDescription.getManufacturer()+ 
       " Serial No: "+ deviceDescription.getSerialId()+ 
       " Part No: "+ deviceDescription.getPartNumber()+ 
       " Hardware Revision: "+ deviceDescription.getHardwareRevision()+ 
       " Software Revision: "+ deviceDescription.getSoftwareRevision()+"."; 
    return value; 
  } 
 
} 
 
