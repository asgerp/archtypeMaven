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
 
package org.net4care.cda; 
 
import java.text.Format; 
import java.text.SimpleDateFormat; 
 
import java.util.*; 
 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
 
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
 
// === Important - no references to 'smb' package classes! 
 
import org.apache.log4j.Logger; 
import org.net4care.common.*; 
import org.net4care.common.delegate.*; 
import org.net4care.observation.ClinicalQuantity; 
import org.net4care.observation.Codes; 
import org.net4care.observation.StandardTeleObservation; 
 
import org.net4care.storage.*; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
 
 
/** PHMR: Public Health Monitoring Record / CDA specialization by 
 * Continua Alliance for detailing a home monitoring observation 
 * document using HL7. 
 *  
 * Role: Given a StandardTeleObservation instance and a reference 
 * to a proxy to the external datasources (CPR register; EHR access 
 * to a treatment), an object of this class can build a PHMR 
 * document in XML. 
 *  
 * It does build a PHMR (and a registry entry) but it is not 
 * a standard Builder pattern as there is only one format built 
 * and thus no need for a Directory and Builder role. 
 *  
 * The HL7/CDA/PHMR format is tricky, please use the following 
 * resources that are essential for understanding the format. 
 *  
 * Keith W Boone: "The CDA Book", Springer-Verlang London 2011 
 *  
 * Implementation Guide for CDA Release 2.0 
 * Personal Healthcare Monitoring Report (PHMR) 
 * (International Realm) 
 * Draft Standard for Trial Use 
 * Release 1.1 
 * October 2010 
 *  
 * @author Henrik Baerbak Christensen, Net4Care, Aarhus University 
 */ 
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
	           ) 

public class PHMRBuilder { 
 
  // OID for PHMR 
  private static final String PHMR_TEMPLATEID_ROOT_OID = "2.16.840.1.113883.10.20.9"; 
  private static final String PHMR_CODE_LOINC = "53576-5"; 
  private static final String PMHR_DISPLAYNAME = "Personal Health Monitoring Report"; 
 
  private static final String PHMR_TYPEID_EXTENSION = "POCD_HD000040"; 
  private static final String PHMR_TYPEID_ROOT = "2.16.840.1.113883.1.3"; 
   
  private static final String ISO_639_1_DANISH_LANGUAGECODE = "da"; 
 
  private final StandardTeleObservation observation; 
  private final String uniqueId; 
   
  // The reference to the proxy for the external databases that allow us 
  // to fetch person data and treatment data 
  private ExternalDataSource externalFeederDatabases; 
   
  private static Logger logger = Logger.getLogger(PHMRBuilder.class); 
   
  /** formatter that can translate from the unix long-encoded time format 
   * into the HL7 formatting. 
   */ 
  private static Format formatter = new SimpleDateFormat("yyyyMMddHHmmss"); 
 
   
  /** 
   * Construct a PHMR instance that can build an PHMR CDA HL7 
   * compliant document. 
   *  
   * @param externalFeederSystems a proxy to external services that can 
   * provide personal data (CPR register) and treatment data (EHR). 
   * @param sto the StandardTeleObservation that encapsulates the 
   * observed clinical measured quantities, device information, person 
   * id, etc. 
   */ 
  public PHMRBuilder(ExternalDataSource externalFeederSystems, StandardTeleObservation sto) { 
    this(externalFeederSystems, sto,  
        new StandardUUIDStrategy()); 
  } 
 
  /** Testing mode of the builder. 
   */ 
  public PHMRBuilder(ExternalDataSource externalFeederSystems, StandardTeleObservation sto, 
      UUIDStrategy uuidStrategy ) { 
    externalFeederDatabases = externalFeederSystems; 
    observation = sto; 
    uniqueId = uuidStrategy.generateUUID(); 
  } 
   
  private Document doc; 
  /** Build and return an XML representation of this 
   * PHMR. 
   * @return the XML representation of the PHMR. 
   * @throws Net4CareException  
   */ 
  public Document buildDocument() throws Net4CareException { 
    // Create the XML document... 
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance(); 
    DocumentBuilder docBuilder = null; 
    try { 
       docBuilder = dbfac.newDocumentBuilder(); 
    } catch ( ParserConfigurationException e ) { 
     logger.error("Error in Parser configuration", e); 
     throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, "Error in Parser configuration"); 
    } 
    doc = docBuilder.newDocument(); 
     
     
    // ================= Begin the CDA header === 
     
    // CONF-PHMR-1: 
    Element root = doc.createElement("ClinicalDocument"); 
    root.setAttribute("xmlns","urn:hl7-org:v3"); 
    root.setAttribute("classCode","DOCCLIN"); 
    root.setAttribute("moodCode","EVN"); 
 
    // Required by the apache serializer for handling xsi 
    root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance"); 
     
    doc.appendChild(root); 
     
    buildHeader(root); 
     
    // =================================================== Body 
     
    // Section 3.1 
    // The CDA book p 171 
    Element component = doc.createElement("component"); 
    component.setAttribute("typeCode", "COMP"); 
    root.appendChild(component); 
     
    // CONF-PHMR-43 
    Element structuredBody = doc.createElement("structuredBody"); 
    structuredBody.setAttribute("classCode", "DOCBODY"); 
    structuredBody.setAttribute("moodCode", "EVN"); 
    component.appendChild(structuredBody); 
     
    // The CDA book p 174 - fig 15.5 
    Element section1Component = doc.createElement("component"); 
    section1Component.setAttribute("typeCode", "COMP"); 
    structuredBody.appendChild(section1Component); 
     
    // According to the XSD (and Boone p. 174 paragraph 15.2 I think) 
    // each individual section must be encloses in its own 
    // component... 
     
    // Sec 3.3.1 Medical Equipment 
     
    // CONF-PHMR-49 
    Element section; 
    section = doc.createElement("section"); 
    section1Component.appendChild(section); 
     
    appendTemplateID(section, "2.16.840.1.113883.10.20.1.7"); 
    appendTemplateID(section, "2.16.840.1.113883.10.20.9.1"); 
     
    // CONF-PHMR-45 
    appendCode(section, "46264-8",Codes.LOINC_OID); // LOINC History of medical device use 
     
    // Unstructured Text - Optional 
    Element title = doc.createElement("title"); 
    title.setTextContent("Medical Equipment"); 
    section.appendChild(title); 
    Element text = doc.createElement("text"); 
    section.appendChild(text); 
       
    String medicalEquipmentText; 
    medicalEquipmentText = observation.getDeviceDescriptionAsHumanReadableText(); 
    text.setTextContent(medicalEquipmentText); 
      
     
    // CONF-PHMR-50 - Section 3.5.2: Device Definition Organizers 
    Element entry = doc.createElement("entry"); 
    entry.setAttribute("typeCode", "COMP"); 
 
    Element organizer = doc.createElement("organizer"); 
    // CONF-PHMR-69 
    organizer.setAttribute("classCode", "CLUSTER"); 
    organizer.setAttribute("moodCode", "EVN"); 
    // CONF-PHMR-70 
    appendTemplateID(organizer, "2.16.840.1.113883.10.20.9.4"); 
  
    Element status2 = doc.createElement("statusCode"); 
    status2.setAttribute("code", "completed"); 
    organizer.appendChild(status2); 
     
    // CONF-PHMR-71 / Boone p. 209 
    Element participant = doc.createElement("participant"); 
    organizer.appendChild(participant); 
     
    participant.setAttribute("typeCode", "SBJ"); 
    appendTemplateID(participant, "2.16.840.1.113883.10.20.9.9");   
     
    Element participantrole = doc.createElement("participantRole"); 
    participantrole.setAttribute("classCode", "MANU"); 
    participant.appendChild(participantrole); 
     
    Element playdevice = doc.createElement("playingDevice"); 
    participantrole.appendChild(playdevice); 
    createDeviceElements(playdevice); 
 
       
     
    // TODO: Figure 24 indicate participant role object, x-check CCD 
     
    entry.appendChild(organizer); 
     
    section.appendChild(entry); 
     
    // ====================== Results section === 
     
    // Must be wrapped in own component tag! 
    Element section2Component = doc.createElement("component"); 
    section2Component.setAttribute("typeCode", "COMP"); 
    structuredBody.appendChild(section2Component); 
 
     
    // Sec 3.3.3 Results 30954-2 
    section = doc.createElement("section"); 
    section2Component.appendChild(section); 
    appendTemplateID(section, "2.16.840.1.113883.10.20.1.14"); 
    appendTemplateID(section, "2.16.840.1.113883.10.20.9.14"); 
    appendCode(section, "30954-2", Codes.LOINC_OID); // Relevant diagnostic tests or laboratory data 
     
    // Unstructured Part 
    title = doc.createElement("title"); 
    title.setTextContent("Results"); 
    section.appendChild(title); 
     
    text = doc.createElement("text");         
    String result = observation.getObservationSpecifics().getObservationAsHumanReadableText(); 
    text.setTextContent(result); 
    section.appendChild(text); 
     
    // Structured part 
    // =========================== Begin Entry 
    Iterator<ClinicalQuantity> i = observation.getObservationSpecifics().iterator();  
    while ( i.hasNext() ) { 
      // CONF-PHMR-58 
      entry = doc.createElement("entry"); 
      entry.setAttribute("typeCode", "COMP"); 
      section.appendChild(entry); 
 
      organizer = doc.createElement("organizer"); 
      organizer.setAttribute("classCode", "CLUSTER"); 
      organizer.setAttribute("moodCode", "EVN"); 
      entry.appendChild(organizer); 
 
      appendTemplateID(organizer, "2.16.840.1.113883.10.20.1.35"); // TODO: Verify this OID 
 
      // TODO: p 35. what is the id??? 
      // TODO: p 35 what is the code / "Tests" what does that mean? 
      Element statusCode = doc.createElement("statusCode"); 
      statusCode.setAttribute("code", "completed"); 
      organizer.appendChild(statusCode); 
 
      // Results - Component 
      component = doc.createElement("component"); 
      organizer.appendChild(component); 
 
      Element observationElement = doc.createElement("observation"); 
      observationElement.setAttribute("classCode", "OBS"); 
      observationElement.setAttribute("moodCode", "EVN"); 
      component.appendChild(observationElement); 
      appendTemplateID(observationElement, "2.16.840.1.113883.10.20.1.31"); 
      appendTemplateID(observationElement, "2.16.840.1.113883.10.20.9.8"); 
 
      // TODO: id in observation 
 
 
 
 
      ClinicalQuantity observedQuantity = i.next(); 
      String value = ""+observedQuantity.getValue(),  
          unit = observedQuantity.getUnit(), 
          code = observedQuantity.getCode(), 
          displayName = observedQuantity.getDisplayName(), 
          codeSystem = observation.getCodeSystem(); 
 
      appendCode(observationElement, code, codeSystem, displayName);  
 
      Element valueElement = doc.createElement("value"); 
      observationElement.appendChild(valueElement); 
      valueElement.setAttribute("xsi:type", "PQ");  // TODO: reintroduce namespace for xsi 
      valueElement.setAttribute("value", value); 
      valueElement.setAttribute("unit", unit); 
 
      // TODO must provide IEEE translation 
 
      // TODO: targetSiteCode 
 
      // TODO: participants 
 
      // TODO: entryRelationship 
 
      // ============ End of Entry 
    } 
     
    return doc; 
  } 
 
  /** Build a meta data / registry entry presentation of the data stored in this 
   * PHMR. The metadata is used to identify the PHMR's central 
   * searchable contents in the XDS registry so lookup of the 
   * PHMR can be made in the XDS repository */ 
  public RegistryEntry buildRegistryEntry() { 
    // A bit cumbersome - to insert all codes for measured 
    // clinical quantities we insert them into a String list 
    // that next is converted into a string array. 
    List<String> list = new ArrayList<String>(); 
    Iterator<ClinicalQuantity> cqi =  
      observation.getObservationSpecifics().iterator(); 
    while( cqi.hasNext() ) { 
      list.add( cqi.next().getCode() ); 
    } 
 
    String[] codesOfMeasuredValues =  
      new String[ list.size() ]; 
    codesOfMeasuredValues = list.toArray(codesOfMeasuredValues); 
 
    // Finally, fill in all meta data information 
    RegistryEntry metadata =  
      new RegistryEntry( observation.getPatientCPR(), 
                    observation.getTime(), 
                    observation.getCodeSystem(),  
                    codesOfMeasuredValues, 
                    observation.getOrganizationUID(), 
                    observation.getTreatmentID() ); 
    return metadata; 
  } 
 
  private Element appendCode(Element context, String code, String codeSystem, 
      String displayName) { 
    Element codeElement = appendCode(context, code, codeSystem); 
    codeElement.setAttribute("displayName" , displayName); 
    return codeElement; 
  } 
 
  private Element appendCode(Element context, String code, String codeSystem) { 
    Element codeElement = doc.createElement("code"); 
    codeElement.setAttribute("code", code);  
    codeElement.setAttribute("codeSystem", codeSystem); 
    context.appendChild(codeElement); 
    return codeElement; 
  } 
 
  private Element appendTemplateID(Element section, String rootOID) { 
    Element templateId; 
    templateId = doc.createElement("templateId"); 
    templateId.setAttribute("root", rootOID); 
    section.appendChild(templateId); 
    return templateId; 
  } 
 
  /** build the CDA header - please consult the Boone book chapter 14. */ 
  private void buildHeader(Element root) throws Net4CareException { 
 
    // The sequencing is important of the following tags (and not 
    // quite what is defined in the PHMR report!), refer to 
    // Boone chapter 14. 
     
    // CONF-PHMR-? - SECTION 2.6 
    Element typeId = doc.createElement("typeId"); 
    typeId.setAttribute("extension", PHMR_TYPEID_EXTENSION); 
    typeId.setAttribute("root", PHMR_TYPEID_ROOT); 
    root.appendChild(typeId); 
     
    // CONF-PHMR-2: 
    Element templateId = doc.createElement("templateId"); 
    templateId.setAttribute("root", PHMR_TEMPLATEID_ROOT_OID); 
    root.appendChild(templateId); 
 
    // CONF-PHMR-12 & 14 
    Element id = createId(Codes.NET4CARE_ROOT_OID, uniqueId); 
    root.appendChild(id); 
 
    // CONF-PHMR-3:    
    Element code = doc.createElement("code"); 
    code.setAttribute("code", PHMR_CODE_LOINC); 
    code.setAttribute("displayName", PMHR_DISPLAYNAME); 
 
    code.setAttribute("codeSystem", Codes.LOINC_OID); 
    code.setAttribute("codeSystemName", "LOINC"); 
    root.appendChild(code); 
     
  
    // CONF-PHMR-15 
    Element title = doc.createElement("title"); 
    title.setTextContent("Net4Care teleobservation for patient "+observation.getPatientCPR() ); 
    root.appendChild(title); 
     
    // CONF-PHMR-16 
    Element effectiveTime = doc.createElement("effectiveTime"); 
    Date observationTime = new Date( observation.getTime() ); 
    String observationTimeAsHL7String = formatter.format(observationTime); 
    effectiveTime.setAttribute("value", observationTimeAsHL7String); 
    root.appendChild(effectiveTime); 
 
    // Section 2.9 
    Element confidentialitycode = doc.createElement("confidentialityCode"); 
    confidentialitycode.setAttribute("code", "N"); 
    confidentialitycode.setAttribute("codeSystem", "2.16.840.1.113883.5.25"); 
    root.appendChild(confidentialitycode); 
 
    // CONF-PHMR-17-20 
    Element languageCode = doc.createElement("languageCode"); 
    languageCode.setAttribute("code", ISO_639_1_DANISH_LANGUAGECODE); 
    root.appendChild(languageCode); 
     
    // Sec 2.11 omitted (why version numbers of measured values?) 
     
    // CONF-PHMR-24-28 
    Element recordTarget = generateRecordTarget(); 
    root.appendChild(recordTarget); 
     
     
    // CONF-PHMR-29-32 
    // Author is interpreted as the physician who has requested 
    // the telemedical surveillance 
    Element author = generateAuthor(); 
    root.appendChild(author); 
     
    // Section 2.13.5 Custodian 
    Element custodian = generateCustodian(); 
    root.appendChild(custodian); 
     
     
    // Section 2.14 serviceEvent 
    // CONF-PHMR-40-42 
    Element documentationOf = doc.createElement("documentationOf"); 
    Element serviceEvent = doc.createElement("serviceEvent"); 
    serviceEvent.setAttribute("classCode", "MPROT"); 
    effectiveTime = doc.createElement("effectiveTime"); 
    Element low = doc.createElement("low"); 
    low.setAttribute("value", observationTimeAsHL7String); 
    Element high = doc.createElement("high"); 
    high.setAttribute("value", observationTimeAsHL7String); 
    effectiveTime.appendChild(low); 
    effectiveTime.appendChild(high); 
    serviceEvent.appendChild(effectiveTime); 
    documentationOf.appendChild(serviceEvent); 
    root.appendChild(documentationOf); 
  } 
 
  private Element generateCustodian() { 
    Element custodian = doc.createElement("custodian"); 
    custodian.setAttribute("typeCode", "CST"); 
    Element assignedCustodian = doc.createElement("assignedCustodian"); 
    assignedCustodian.setAttribute("classCode", "ASSIGNED"); 
    custodian.appendChild(assignedCustodian); 
     
    Element representedCustodianOrganization = doc.createElement("representedCustodianOrganization"); 
    representedCustodianOrganization.setAttribute("classCode", "ORG"); 
    representedCustodianOrganization.setAttribute("determinerCode", "INSTANCE"); 
    // TODO: review what is needed here... 
    String org = observation.getOrganizationUID(); 
    representedCustodianOrganization.appendChild( createId(org, "1")); 
     
    // Probably lookup using both the organization and treatment id 
    TreatmentData td = getTreatmentData(observation.getTreatmentID()); 
     
    Element name = doc.createElement("name"); 
    name.setTextContent( td.getStewardOrganizationName() );  
    representedCustodianOrganization.appendChild(name); 
     
    Element telecom = generateTelecom(td.getCustodianAddr().getTelecom()); 
    representedCustodianOrganization.appendChild(telecom);   
     
    Element addr = generateAddr(td.getCustodianAddr()); 
    representedCustodianOrganization.appendChild(addr); 
 
    assignedCustodian.appendChild(representedCustodianOrganization); 
    return custodian; 
  } 
 
  private Element createId(String root, String extension) { 
    Element id = doc.createElement("id"); 
    id.setAttribute("root", root); 
    id.setAttribute("extension", extension); 
    return id; 
  } 
 
  private Element generateRecordTarget() throws Net4CareException { 
    // CONF-PHMR-24-28 
 
    //System.out.println( "STO="+observation); 
    String cprOfPatient = observation.getPatientCPR(); 
    PersonData pd = getPersonData(cprOfPatient); 
     
    // The CDA book p 149 
    Element recordTarget = doc.createElement("recordTarget"); 
    recordTarget.setAttribute("typeCode", "RCT"); 
    recordTarget.setAttribute("contextControlCode", "OP"); 
     
    Element patientRole = doc.createElement("patientRole"); 
    patientRole.setAttribute("classCode", "PAT"); 
     
    // Decision - we use CPR number as ID under the codeSystem 
    // of Danish CPR 
    Element id = createId(Codes.DK_CPR_OID, pd.getCPR()); 
    patientRole.appendChild(id); 
         
    // ADDRESS 
    // CONF-PHMR-5 
    Element addr = generateAddr(pd.getAddr()); 
    patientRole.appendChild(addr); 
     
    Element telecom = generateTelecom(pd.getTelecom()); 
    patientRole.appendChild(telecom); 
     
    // PATIENT 
     
    // The CDA book p 161 
    // CONF-PHMR-4 
    Element patient = doc.createElement("patient"); 
    patient.setAttribute("classCode", "PSN"); 
    patient.setAttribute("determinerCode", "INSTANCE"); 
     
    Element name = generateName(pd); 
    patient.appendChild(name); 
     
    Element administrativeGenderCode = doc.createElement("administrativeGenderCode"); 
    administrativeGenderCode.setAttribute("code", pd.getGender() ); 
    // TODO validate this is hl7 AdministrativGender code OID 
    administrativeGenderCode.setAttribute("codeSystem", "2.16.840.1.113883.5.1");  
    patient.appendChild(administrativeGenderCode); 
     
    Element birthTime = doc.createElement("birthTime"); 
    birthTime.setAttribute("value", pd.getBirthTime()); 
    patient.appendChild(birthTime); 
     
    // Add the CPR number: see http://www.ringholm.de/docs/04300_en.htm under recordTarget 
    /* REMOVED as it does not validate by the schema 
    Element asOtherID = doc.createElement("asOtherIDs"); 
    Element cpr = createId(Codes.DK_CPR_OID, pd.getCPR()); 
    asOtherID.appendChild(cpr); 
    patient.appendChild(asOtherID); 
    */ 
     
    // TODO: If minor - append the guardian information CONF-PHMR-27 
     
    // TODO: Provide 'providerOrganization' ??? 
     
    patientRole.appendChild(patient); 
     
    recordTarget.appendChild(patientRole); 
    return recordTarget; 
  } 
 
  private Element generateTelecom(String telecomNr) { 
    Element telecom = doc.createElement("telecom"); 
    telecom.setAttribute("value", "tel:"+telecomNr); 
    return telecom; 
  } 
 
  private Element generateName(PersonData pd) { 
    Element name = doc.createElement("name"); 
    Element given = doc.createElement("given"); 
    given.setTextContent( pd.getGiven() ); 
    Element family = doc.createElement("family"); 
    family.setTextContent( pd.getFamily() ); 
    name.appendChild(given); 
    name.appendChild(family); 
    return name; 
  } 
 
  private Element generateAddr(AddressData pd) { 
    Element addr = doc.createElement("addr"); 
    Element streetAddressLine = doc.createElement("streetAddressLine"); 
    streetAddressLine.setTextContent(pd.getStreet()); 
    Element city = doc.createElement("city"); 
    city.setTextContent(pd.getCity()); 
    Element postalCode = doc.createElement("postalCode"); 
    postalCode.setTextContent(pd.getPostalCode()); 
    Element country = doc.createElement("country"); 
    country.setTextContent(pd.getCountry()); 
    addr.appendChild(streetAddressLine); 
    addr.appendChild(city); 
    addr.appendChild(postalCode); 
    addr.appendChild(country); 
    return addr; 
  } 
   
  private Element generateAuthor() throws Net4CareException { 
    // The CDA book p 151 
    Element author = doc.createElement("author"); 
    author.setAttribute("typeCode", "AUT"); 
    author.setAttribute("contextControlCode", "OP"); 
     
    Element time = doc.createElement("time"); 
    long obstime = observation.getTime(); 
    time.setAttribute("value", formatter.format(obstime) ); 
    author.appendChild(time); 
     
    Element assignedAuthor = doc.createElement("assignedAuthor"); 
    assignedAuthor.setAttribute("classCode", "ASSIGNED"); 
    author.appendChild(assignedAuthor); 
     
    /* Alternative A: Consider the clinician who prescribed the device 
     * as author. For the code that instead use an authoring device as 
     * author, see commented code below. 
     */ 
    // The CDA book p 151 
    TreatmentData td = getTreatmentData( observation.getTreatmentID() ); 
    PersonData pd = getAuthorData( td.getAuthorCPR() ); 
     
    // CONF-PHMR-30 
     
    Element authorid = createId(observation.getOrganizationUID(), "1"); 
    assignedAuthor.appendChild(authorid); 
     
    Element addr = generateAddr(pd.getAddr()); 
    assignedAuthor.appendChild(addr); 
     
     
    Element telecom = generateTelecom(pd.getTelecom()); 
    assignedAuthor.appendChild(telecom); 
 
    Element assignedPerson = doc.createElement("assignedPerson"); 
    Element name = generateName(pd); 
    assignedPerson.appendChild(name); 
    assignedAuthor.appendChild(assignedPerson); 
 
    /* Alternative B: Reenable the code below to insert authoring device as the author instead 
     * of the clinician who prescribed the device. I am in doubt what is the 
     * better choice. HBC. 
  
    // CONF-PHMR-30 -  
    Element authorId = createId(Codes.NET4CARE_DEVICE_OID, observation.getDeviceDescription().getSerialId() ); 
    assignedAuthor.appendChild(authorId); 
     
    // According to schema, addresse and telecom is NOT necessary! 
    // Element addr = generateAddr(pd.getAddr()); 
    // author.appendChild(addr); 
     
    // Element telecom = generateTelecom(pd.getTelecom()); 
    // author.appendChild(telecom); 
     
    // Boone book p 167 
    Element assignedAuthoringDevice = doc.createElement("assignedAuthoringDevice"); 
    assignedAuthoringDevice.setAttribute("classCode", "DEV"); 
    assignedAuthoringDevice.setAttribute("determinerCode", "INSTANCE"); 
    assignedAuthor1.appendChild(assignedAuthoringDevice); 
 
    createDeviceElements(assignedAuthoringDevice); 
    */ 
     
    return author; 
  } 
 
  private void createDeviceElements(Element assignedAuthoringDevice) { 
    // Review the POCD_MT000040.XSD schema for the details of the device 
    // Also Boone p. 209 
    Element manufacturerModelName = doc.createElement("manufacturerModelName"); 
    String manufacturerModelNameString =  
        observation.getDeviceDescription().getManufacturer() + " / Model: " + 
            observation.getDeviceDescription().getModel() + " / Part: " + 
            observation.getDeviceDescription().getPartNumber(); 
    manufacturerModelName.setTextContent(manufacturerModelNameString); 
     
    assignedAuthoringDevice.appendChild(manufacturerModelName); 
     
    Element softwareName = doc.createElement("softwareName"); 
    String swName = "SerialNr: "+observation.getDeviceDescription().getSerialId() + " / HW Rev. " + 
        observation.getDeviceDescription().getHardwareRevision() + " / SW Rev. " + 
        observation.getDeviceDescription().getSoftwareRevision(); 
    softwareName.setTextContent(swName); 
    assignedAuthoringDevice.appendChild(softwareName); 
  } 
   
  private TreatmentData getTreatmentData(String treatmentID) { 
    TreatmentData td = externalFeederDatabases.getTreatmentData(treatmentID); 
    return td; 
  } 
 
  private PersonData getPersonData(String cprOfPatient) throws Net4CareException { 
    PersonData pd = externalFeederDatabases.getPersonData(cprOfPatient); 
    return pd; 
  } 
   
  private PersonData getAuthorData(String cprOfAuthor) throws Net4CareException { 
    PersonData pd = externalFeederDatabases.getPersonData(cprOfAuthor); 
    return pd; 
  } 
 
 
} 
