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
 
package org.net4care.xdsproxy; 
 
import java.text.*; 
import java.util.*; 
 
import javax.xml.parsers.*; 
import javax.xml.soap.SOAPException; 
 
import org.apache.axis.message.*; 
import org.net4care.common.*; 
import org.net4care.common.delegate.*; 
import org.net4care.storage.RegistryEntry; 
import org.w3c.dom.*; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
 
/** Class to build ebXML adhering to the 
 * XDS.b registry format based upon an instance 
 * of a N4C RegistryEntry that contains 
 * the meta data for a N4C observation. 
 *  
 * The code is still somewhat clunky due to 
 * Mark's trial-and-error code was 
 * made without any refactoring steps. 
 * At least it is encapsualated now. 
 *  
 * @author Henrik Baerbak Christensen,  
 * 
 */ 
public class EbXMLBuilder { 
  // Create a formatter for the format required by eBXML 
  private Format formatter; 
  private UUIDStrategy uuidStrategy; 
  private TimestampStrategy timestampStrategy; 
   
  final static String ns = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0"; 
 
  
  /** Construct an ebXML builder for production environment 
   * execution. 
   *  */ 
  public EbXMLBuilder() { 
    this( new StandardUUIDStrategy(), new StandardTimestampStrategy() ); 
  } 
   
  /** Construct an ebXML builder that takes 
   * a test stub for generating the UUID and for 
   * generating current time and thus 
   * makes the output under test control. 
   * @param uuidStrategy a test stub for the generator 
   * of UUIDs. 
   * @param timestampStrategy a test stub to define 
   * current time in milliseconds 
   */ 
  public EbXMLBuilder(UUIDStrategy uuidStrategy, TimestampStrategy timestampStrategy) { 
    // Formatting of datas the 'ebXML' way 
    formatter = new SimpleDateFormat("yyyyMMddHHmmss"); 
    this.uuidStrategy = uuidStrategy; 
    this.timestampStrategy = timestampStrategy; 
  } 
 
  /** Build SOAP body XML that represents a ProvideAndRegisterDocumentSetRequest 
   * as defined by the XDS.b standard. The xml will represent the ebXML header 
   * for a Net4Care observation as defined by the metadata given in the parameter. 
   * Also you MUST provide the uniqueID as defined by XDS.b, that is the 
   * unique ID of the provided document (generate it using Java UUID class). 
   * Similarly provide a UUID for the associated document that will go into 
   * the final message (It binds the metadata to the set of documents attached). 
   * @param n4cMetaData 
   * @param uniqueID 
   * @param associatedTargetObjectID 
   * @return 
   */ 
  public SOAPBodyElement buildProvideAndRegisterDocumentSetRequest(RegistryEntry n4cMetaData,  
      String uniqueID, String associatedTargetObjectID) { 
     
    Document computed; 
    computed = buildSubmitObjectsRequest( n4cMetaData, uniqueID, associatedTargetObjectID ); 
    SOAPBodyElement soapBody = new SOAPBodyElement(computed.getDocumentElement()); 
 
    SOAPBodyElement provideAndRegisterMsg = new SOAPBodyElement(); 
    provideAndRegisterMsg.setName("ProvideAndRegisterDocumentSetRequest"); 
    provideAndRegisterMsg.setAttribute("xmlns", "urn:ihe:iti:xds-b:2007"); 
    //provideAndRegisterMsg.setNamespaceURI("urn:ihe:iti:xds-b:2007"); 
     
    try { 
      provideAndRegisterMsg.addChild(soapBody); 
    } catch (SOAPException e) { 
      // TODO Auto-generated catch block 
      e.printStackTrace(); 
    } 
 
    return provideAndRegisterMsg; 
  } 
   
  public MessageBody buildRetrieveDocumentSet(String uniqueID) { 
    MessageBody retriveDocument; 
    retriveDocument = new MessageBody(); 
    MessageElement elem = new MessageElement(); 
    elem.setName("RetrieveDocumentSetRequest"); 
    elem.setNamespaceURI("urn:ihe:iti:xds-b:2007"); 
     
    MessageElement docReq = new MessageElement(); 
    docReq.setName("DocumentRequest"); 
     
    MessageElement docUID = new MessageElement(); 
    docUID.setName("DocumentUniqueId"); 
    docUID.setValue(uniqueID); 
 
    try { 
      docReq.addChild(docUID);  
      elem.addChild(docReq); 
    } catch (SOAPException e1) { 
      e1.printStackTrace(); 
    } 
     
    MessageElement[] eArray = {elem}; 
    retriveDocument.set_any(eArray); 
    return retriveDocument; 
  } 
 
 
  /** Actually package visibility is desirable of this method, refactoring pending. 
   * Build the SubmitObjectRequest substructure of the provide and register message. 
   *  
   * @param n4cMetaData 
   * @param uniqueID 
   * @param associatedTargetObjectID 
   * @return 
   */ 
  public Document buildSubmitObjectsRequest(RegistryEntry n4cMetaData, String uniqueID, String associatedTargetObjectID) { 
    // Create the metadata 
    RegistryMetaData eBXMLMetaData; 
     
    // Fill in the ID of the patient - MUST be known in the XDSb. 
    // For Codeplex implementation, ensure it is located in the XDSRegistryDB.Patient table's 
    // patientUID attribute. 
    eBXMLMetaData = new RegistryMetaData( n4cMetaData.getCpr() ); 
    ArrayList<String> pInfo = new ArrayList<String>(); 
    pInfo.add("SomeInfo"); 
    eBXMLMetaData.setAttachedDocumentPatientInfo(pInfo); 
    eBXMLMetaData.setAttachedDocumentComment("This is a Net4Care test document"); 
    String documentCreationTime = formatter.format(n4cMetaData.getTimestamp()); 
    eBXMLMetaData.setAttachedDocumentCreationTime( documentCreationTime ); 
    eBXMLMetaData.setAttachedDocumentServiceStartTime( documentCreationTime ); 
    eBXMLMetaData.setAttachedDocumentServiceStopTime( documentCreationTime ); 
 
    eBXMLMetaData.setAttachedDocumentUniqueID(uniqueID); 
 
    eBXMLMetaData.setAttachedDocumentName("Net4CareTestDocument"); 
    eBXMLMetaData.setAttachedDocumentLanguageCode("en-us"); 
    eBXMLMetaData.setAttachedDocumentMimeType("text/xml"); 
    eBXMLMetaData.setSubmissionSetAuthor("Net4Care^^Automatic"); 
    eBXMLMetaData.setSubmissionSetCodingSchemeName("Net4CareCodingScheme"); 
    eBXMLMetaData.setSubmissionSetCodingSchemeValue("Net4CareCodingSchemeValue"); 
    eBXMLMetaData.setSubmissionSetID("Net4CareSubmissionSetTest"); 
    eBXMLMetaData.setSubmissionSetStatus("Original"); 
    eBXMLMetaData.setSubmissionSetType("Test submissionset"); 
    eBXMLMetaData.setAssociationTargetObjectID(associatedTargetObjectID); 
 
    Document document = null; 
    try { 
      document = generatePnRRequest(eBXMLMetaData); 
    } catch (ParserConfigurationException e) { 
      // TODO Auto-generated catch block 
      e.printStackTrace(); 
    } 
 
    return document; 
  } 
 
   
  org.w3c.dom.Document generatePnRRequest(RegistryMetaData metaData) throws ParserConfigurationException { 
    DocumentBuilder docBuilder = (DocumentBuilderFactory.newInstance()).newDocumentBuilder(); 
    Document provideAndRegisterDocumentSetRequest = docBuilder.newDocument(); 
    Element root; 
     
     
    root = provideAndRegisterDocumentSetRequest.createElementNS(ns, "SubmitObjectsRequest"); 
    provideAndRegisterDocumentSetRequest.appendChild(root); 
    // The namespace appears twice in the XML that codeplex chews correctly, so I 
    // add it also under its default name - HBC 
    root.setAttribute("xmlns", ns); 
     
    Element registryObjectList = provideAndRegisterDocumentSetRequest.createElementNS(ns, "RegistryObjectList"); 
    root.appendChild(registryObjectList); 
     
    // HBC - the submission sets MUST refer to the id of the attached document 
    String submissionSetID = metaData.getAssociationTargetObjectID(); 
     
    //Meta for attachments 
    Element extrinsicObject = provideAndRegisterDocumentSetRequest.createElementNS( ns, "ExtrinsicObject"); 
     
    // HBC extrinsicObject.setAttribute("id", metaData.getAttachedDocumentUniqueID()); //Attached document id 
    extrinsicObject.setAttribute("id", submissionSetID); //Attached document id 
     
    extrinsicObject.setAttribute("mimeType", metaData.getAttachedDocumentMimeType()); 
    extrinsicObject.setAttribute("objectType", "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1"); //Check what this urn refers to (same for all extObjs in example) 
     
    Element extObjName = provideAndRegisterDocumentSetRequest.createElementNS( ns, "Name"); 
    Element extObjNameLocalString = provideAndRegisterDocumentSetRequest.createElementNS( ns,"LocalizedString"); 
    extObjNameLocalString.setAttribute("value", metaData.getAttachedDocumentName()); 
    extObjName.appendChild(extObjNameLocalString); 
    extrinsicObject.appendChild(extObjName); 
 
    Element extObjDesc = provideAndRegisterDocumentSetRequest.createElementNS( ns, "Description"); 
    extObjDesc.setTextContent(metaData.getAttachedDocumentComment()); 
    extrinsicObject.appendChild(extObjDesc); 
     
    ArrayList<String> values = new ArrayList<String>(); 
    values.add(metaData.getAttachedDocumentCreationTime()); 
    extrinsicObject.appendChild(createSlot("creationTime", values, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    values.add(metaData.getAttachedDocumentServiceStartTime()); 
    extrinsicObject.appendChild(createSlot("serviceStartTime", values, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    values.add(metaData.getAttachedDocumentServiceStopTime()); 
    extrinsicObject.appendChild(createSlot("serviceStopTime", values, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    values.add(metaData.getPatientID()); 
    extrinsicObject.appendChild(createSlot("sourcePatientId", values, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    for (String val : metaData.getAttachedDocumentPatientInfo()) { 
      values.add(val); 
    } 
    extrinsicObject.appendChild(createSlot("sourcePatientInfo", values, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    values.add(metaData.getAttachedDocumentLanguageCode()); 
    extrinsicObject.appendChild(createSlot("languageCode", values, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
     
    ArrayList<Element> slots = new ArrayList<Element>(); 
    values.clear(); 
    values.add(metaData.getAttachedDocumentClassificationXValue()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    //'Objective' should be check why and what else? 
    extrinsicObject.appendChild(createClassificationWithNodeRep("Objective", "urn:uuid:691b9c22-642f-d0c8-b8b6-bf0edbb75b08", "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a",  
        submissionSetID, slots, provideAndRegisterDocumentSetRequest)); 
    values.clear();slots.clear(); 
     
    values.add(metaData.getAttachedDocumentClassificationYValue()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    Element classCoding = createClassificationWithName(metaData.getAttachedDocumentClassificationYName(), "urn:uuid:fb57bcf4-7f7a-28ab-c29c-fe067854abbd", "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f",  
        submissionSetID, slots, provideAndRegisterDocumentSetRequest); 
        //nodeRep values should be checked why and what else? 
    classCoding.setAttribute("nodeRepresentation", metaData.getAttachedDocumentClassificationYnodeRepresentation()); 
    extrinsicObject.appendChild(classCoding); 
 
    values.clear();slots.clear(); 
    values.add(metaData.getAttachedDocumentClassificationYValue()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    Element formatCoding = createClassificationWithName(metaData.getAttachedDocumentClassificationZName(), "urn:uuid:2d676c3f-33e0-742d-fe6b-3ed475a5c75d", "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d",  
        submissionSetID, slots, provideAndRegisterDocumentSetRequest); 
        //nodeRep values should be checked why and what else? 
    formatCoding.setAttribute("nodeRepresentation", metaData.getAttachedDocumentClassificationZnodeRepresentation()); 
    extrinsicObject.appendChild(formatCoding); 
 
    values.clear();slots.clear(); 
    values.add(metaData.getAttachedDocumentClassificationIValue()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    Element healthcareFacilityTypeCoding = createClassificationWithName(metaData.getAttachedDocumentClassificationIName(), "urn:uuid:a6e536d1-b496-4006-e243-5c94b6db7c07", "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1",  
        submissionSetID, slots, provideAndRegisterDocumentSetRequest); 
        //nodeRep values should be checked why and what else? 
    healthcareFacilityTypeCoding.setAttribute("nodeRepresentation", metaData.getAttachedDocumentClassificationInodeRepresentation()); 
    extrinsicObject.appendChild(healthcareFacilityTypeCoding);     
 
    values.clear();slots.clear(); 
    values.add(metaData.getAttachedDocumentClassificationJName()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    Element practiceSettingCoding = createClassificationWithName(metaData.getAttachedDocumentClassificationJName(), "urn:uuid:241be963-c4f3-d799-a6a5-d5a4636a8f0f", "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead",  
        submissionSetID, slots, provideAndRegisterDocumentSetRequest); 
        //nodeRep values should be checked why and what else? 
    practiceSettingCoding.setAttribute("nodeRepresentation", metaData.getAttachedDocumentClassificationJnodeRepresentation()); 
    extrinsicObject.appendChild(practiceSettingCoding);      
     
    values.clear();slots.clear(); 
    values.add(metaData.getAttachedDocumentClassificationKValue()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    Element typeCoding = createClassificationWithName(metaData.getAttachedDocumentClassificationKName(), "urn:uuid:7aec7490-a293-12ae-d70b-8976b4f1f703",  
        "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983",  
        submissionSetID, slots, provideAndRegisterDocumentSetRequest); 
        //nodeRep values should be checked why and what else? 
    typeCoding.setAttribute("nodeRepresentation", metaData.getAttachedDocumentClassificationKnodeRepresentation()); 
    extrinsicObject.appendChild(typeCoding);       
     
    // Set the uniqueID 
    Element externalIdentifier = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ExternalIdentifier"); 
    externalIdentifier.setAttribute("id", "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"); 
    // HBC, not present in IVANs externalIdentifier.setAttribute("registryObject", metaData.getAttachedDocumentUniqueID()); 
    externalIdentifier.setAttribute("identificationScheme", "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab"); // HBC, Match Ivans 
    externalIdentifier.setAttribute("value", metaData.getAttachedDocumentUniqueID()); 
    Element externalIdentifierName = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Name"); 
    Element externalIdentifierLocalString = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
    externalIdentifierLocalString.setAttribute("value", "XDSDocumentEntry.uniqueId"); 
    externalIdentifierName.appendChild(externalIdentifierLocalString); 
    externalIdentifier.appendChild(externalIdentifierName); 
    extrinsicObject.appendChild(externalIdentifier); 
     
    // Set the patient ID 
    Element externalIdentifier2 = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ExternalIdentifier"); 
    externalIdentifier2.setAttribute("id", "urn:uuid:9afb1c3f-942c-6676-77e2-38fdc3f32a47"); 
    // HBC, not in Ivans code : externalIdentifier2.setAttribute("registryObject", metaData.getAttachedDocumentUniqueID()); 
    externalIdentifier2.setAttribute("identificationScheme", "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427"); 
    externalIdentifier2.setAttribute("value", metaData.getPatientID()); //Pulled from Registry DB. Registry should get these from the Patient Identity Feed 
    Element externalIdentifier2Name = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Name"); 
    Element externalIdentifier2LocalString = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
    externalIdentifier2LocalString.setAttribute("value", "XDSDocumentEntry.patientId"); 
    externalIdentifier2Name.appendChild(externalIdentifier2LocalString); 
    externalIdentifier2.appendChild(externalIdentifier2Name); 
    extrinsicObject.appendChild(externalIdentifier2); 
     
     
    registryObjectList.appendChild(extrinsicObject); 
     
    Element registryPackage = provideAndRegisterDocumentSetRequest.createElementNS(ns,"RegistryPackage"); 
    registryPackage.setAttribute("id", metaData.getSubmissionSetID()); //SubmissionSet id 
     
    //Meta-data for submissionset 
    /* 
     * Minimum meta-data set: 
     * RegistryPackage: 
     *  -> SubmissionSetTime 
     *  -> Name 
     *  -> Classification:authorId 
     *  -> Classification:codingSchemeId 
     *  -> ExternalIdentifier:thisUniqueId (submissionSet) 
     *  -> ExternalIdentifier:thisSourceId (submissionSet) 
     *  -> ExternalIdentifier:thisPatientId 
     * Classification (of submissionSet) 
     * ExtrinsicObject (0-n, of attachment(s)) 
     * Association (0-n, of attachment(s)) 
     */ 
    Element regPackSlot = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Slot"); 
     
    ////////////////////- RegistryObjectList:RegistryPackage:SubmissionSetTime START -//////////////////// 
    regPackSlot.setAttribute("name", "submissionTime"); 
    Element regPackSlotValueList = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ValueList"); 
    Element regPackSlotValue = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Value");    
     
    String today = formatter.format( timestampStrategy.getCurrentTimeInMillis() ); 
         
    regPackSlotValue.setTextContent(today); //Submission data, e.g. 20120224 [yyyy-mm-dd] 
    regPackSlotValueList.appendChild(regPackSlotValue); 
    regPackSlot.appendChild(regPackSlotValueList); 
    registryPackage.appendChild(regPackSlot); 
    ////////////////////- RegistryObjectList:RegistryPackage:SubmissionSetTime END -//////////////////// 
     
    ////////////////////- RegistryObjectList:RegistryPackage:Name START -//////////////////// 
    Element regPackName = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Name"); 
    Element regPackNameValue = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
    regPackNameValue.setAttribute("value", metaData.getSubmissionSetType()); //Name of Submission type, e.g. "Lab + Discharge" 
    regPackName.appendChild(regPackNameValue); 
    registryPackage.appendChild(regPackName); 
    ////////////////////- RegistryObjectList:RegistryPackage:Name END -//////////////////// 
     
    ////////////////////- RegistryObjectList:RegistryPackage:Classification:authorId START -//////////////////// 
    slots = new ArrayList<Element>(); 
    values = new ArrayList<String>(); 
    values.add(metaData.getSubmissionSetAuthor()); 
    slots.add(createSlot("authorPerson", values, provideAndRegisterDocumentSetRequest)); 
    registryPackage.appendChild(createClassification("authorId", "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d", metaData.getSubmissionSetID(), slots, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    slots.clear(); 
//    values.add("foobar clinic"); 
//    values.add("barbaz area"); 
//    slots.add(createSlot("authorInstitution", values, provideAndRegisterDocumentSetRequest)); 
//    values.clear(); 
//    values.add("author role"); 
//    slots.add(createSlot("authorRole", values, provideAndRegisterDocumentSetRequest)); 
//    values.clear(); 
//    values.add("author speciality"); 
//    slots.add(createSlot("authorSpeciality", values, provideAndRegisterDocumentSetRequest)); 
//    values.clear(); 
    ////////////////////- RegistryObjectList:RegistryPackage:Classification:authorId END -//////////////////// 
 
    ////////////////////- RegistryObjectList:RegistryPackage:Classification:codingScheme START -//////////////////// 
    values.add(metaData.getSubmissionSetCodingSchemeValue()); 
    slots.add(createSlot("codingScheme", values, provideAndRegisterDocumentSetRequest)); 
    registryPackage.appendChild(createClassificationWithName(metaData.getSubmissionSetCodingSchemeName(), "codingSchemeId",  
        "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500", metaData.getSubmissionSetID(), slots, provideAndRegisterDocumentSetRequest)); 
    values.clear(); 
    slots.clear(); 
       
//    Element regPackDesc = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Description"); 
//    Element regPackDescValue = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
//    regPackDescValue.setAttribute("value", ""); //E.g. Post-Op Lab + Discharge summary 
//    regPackDesc.appendChild(regPackDescValue); 
//    registryPackage.appendChild(regPackDesc); 
     
    ////////////////////- RegistryObjectList:RegistryPackage:Classification:codingScheme END -//////////////////// 
 
    ////////////////////- RegistryObjectList:RegistryPackage:ExternalIdentifier:thisUniqueId START -//////////////////// 
    Element externalIdThisUniqueId = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ExternalIdentifier"); 
    externalIdThisUniqueId.setAttribute("id", "thisUniqueId"); 
    externalIdThisUniqueId.setAttribute("registryObject", metaData.getSubmissionSetID()); 
    externalIdThisUniqueId.setAttribute("identificationScheme", "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8"); 
    externalIdThisUniqueId.setAttribute("value", uuidStrategy.generateUUID()); 
    Element thisUniqueIdName = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Name"); 
    Element thisUniqueIdLocalString = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
    thisUniqueIdLocalString.setAttribute("value", "XDSSubmissionSet.uniqueId"); 
    thisUniqueIdName.appendChild(thisUniqueIdLocalString); 
    externalIdThisUniqueId.appendChild(thisUniqueIdName); 
    registryPackage.appendChild(externalIdThisUniqueId); 
    ////////////////////- RegistryObjectList:RegistryPackage:ExternalIdentifier:thisUniqueId END -////////////////////     
 
    ////////////////////- RegistryObjectList:RegistryPackage:ExternalIdentifier:sourceUniqueId START -//////////////////// 
    Element externalIdThisSourceId = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ExternalIdentifier"); 
    externalIdThisSourceId.setAttribute("id", "thisSourceId"); 
    externalIdThisSourceId.setAttribute("registryObject", metaData.getSubmissionSetID()); 
    externalIdThisSourceId.setAttribute("identificationScheme", "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832"); 
    externalIdThisSourceId.setAttribute("value", uuidStrategy.generateUUID()); 
    Element thisSourceIdName = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Name"); 
    Element thisSourceIdLocalString = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
    thisSourceIdLocalString.setAttribute("value", "XDSSubmissionSet.sourceId"); 
    thisSourceIdName.appendChild(thisSourceIdLocalString); 
    externalIdThisSourceId.appendChild(thisSourceIdName); 
    registryPackage.appendChild(externalIdThisSourceId); 
    ////////////////////- RegistryObjectList:RegistryPackage:ExternalIdentifier:sourceUniqueId END -//////////////////// 
     
    ////////////////////- RegistryObjectList:RegistryPackage:ExternalIdentifier:thisPatientId START -//////////////////// 
    Element externalIdThispatientId = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ExternalIdentifier"); 
    externalIdThispatientId.setAttribute("id", "thisPatientId"); 
    externalIdThispatientId.setAttribute("registryObject", metaData.getSubmissionSetID()); 
    externalIdThispatientId.setAttribute("identificationScheme", "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446"); 
    externalIdThispatientId.setAttribute("value", metaData.getPatientID()); 
    Element thispatientIdName = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Name"); 
    Element thispatientIdLocalString = provideAndRegisterDocumentSetRequest.createElementNS(ns,"LocalizedString"); 
    thispatientIdLocalString.setAttribute("value", "XDSSubmissionSet.patientId"); 
    thispatientIdName.appendChild(thispatientIdLocalString); 
    externalIdThispatientId.appendChild(thispatientIdName); 
    registryPackage.appendChild(externalIdThispatientId); 
    ////////////////////- RegistryObjectList:RegistryPackage:ExternalIdentifier:thisPatientId END -//////////////////// 
     
    registryObjectList.appendChild(registryPackage); 
     
    ////////////////////- RegistryObjectList:Classification START -//////////////////// 
    Element regObjListClass = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Classification"); 
    regObjListClass.setAttribute("id", "urn:uuid:"+uuidStrategy.generateUUID()+":thisIsRandomGenNoClueWhatItIsFor"); 
    regObjListClass.setAttribute("classifiedObject", metaData.getSubmissionSetID()); 
    regObjListClass.setAttribute("classificationNode", "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd"); 
    registryObjectList.appendChild(regObjListClass); 
    ////////////////////- RegistryObjectList:Classification END -//////////////////// 
     
    ////////////////////- RegistryObjectList:Association START -////////////////////     
    Element association = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Association"); 
    association.setAttribute("id", "urn:uuid:"+uuidStrategy.generateUUID()+":thisIsRandomGenNoClueWhatItIsFor"); 
    association.setAttribute("associationType", "HasMember"); 
    association.setAttribute("sourceObject", metaData.getSubmissionSetID()); 
    association.setAttribute("targetObject", submissionSetID); 
    Element assSlot = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Slot"); 
    assSlot.setAttribute("name", "SubmissionSetStatus"); 
    Element valueList = provideAndRegisterDocumentSetRequest.createElementNS(ns,"ValueList"); 
    Element value = provideAndRegisterDocumentSetRequest.createElementNS(ns,"Value"); 
    value.setTextContent("Original"); // new submission of contents (?) HBC 
    value.setNodeValue(metaData.getSubmissionSetStatus()); 
    valueList.appendChild(value); 
    assSlot.appendChild(valueList); 
    association.appendChild(assSlot); 
    ////////////////////- RegistryObjectList:Association END -////////////////////     
     
    registryObjectList.appendChild(association); 
     
     
    return provideAndRegisterDocumentSetRequest; 
  } 
  private Element createClassification(String classId, String classScheme, String classObject, ArrayList<Element> slots, Document docBuilder) { 
    Element classification = docBuilder.createElementNS(ns,"Classification"); 
    classification.setAttribute("id", classId); 
    classification.setAttribute("classificationScheme", classScheme); 
    classification.setAttribute("classifiedObject", classObject); 
     
    for (Element s : slots) { 
      classification.appendChild(s); 
    } 
     
    return classification; 
  } 
   
  private Element createClassificationWithName(String nameValue, String classId, String classScheme, String classObject, ArrayList<Element> slots, Document docBuilder) { 
    Element classification = createClassification(classId, classScheme, classObject, slots, docBuilder); 
    Element className = docBuilder.createElementNS(ns,"Name"); 
    Element nameLocalString = docBuilder.createElementNS(ns,"LocalizedString"); 
    nameLocalString.setAttribute("value", nameValue); 
    className.appendChild(nameLocalString); 
    classification.appendChild(className); 
     
    return classification; 
  } 
   
  private Element createClassificationWithNodeRep(String nodeRep, String classId, String classScheme, String classObject, ArrayList<Element> slots, Document docBuilder) { 
    Element classification = createClassification(classId, classScheme, classObject, slots, docBuilder); 
    classification.setAttribute("nodeRepresentation", nodeRep); 
    return classification; 
  } 
   
  private Element createSlot(String slotName, ArrayList<String> values, Document docBuilder) { 
    Element slot = docBuilder.createElementNS(ns,"Slot"); 
    slot.setAttribute("name", slotName); 
    Element valueList = docBuilder.createElementNS(ns,"ValueList"); 
    for (String val : values) { 
      Element value = docBuilder.createElementNS(ns,"Value"); 
      value.setTextContent(val); 
      valueList.appendChild(value); 
    } 
    slot.appendChild(valueList); 
     
    return slot;   
  } 
 
} 
