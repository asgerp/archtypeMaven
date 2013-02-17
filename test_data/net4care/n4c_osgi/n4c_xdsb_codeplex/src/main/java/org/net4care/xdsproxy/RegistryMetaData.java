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
 
import java.util.List; 
 
public class RegistryMetaData { 
	 
	private String patientID; 
	private String attachedDocumentMimeType; 
	private String attachedDocumentName; 
	private String attachedDocumentComment; 
	private String attachedDocumentCreationTime; 
	private String attachedDocumentServiceStartTime; 
	private String attachedDocumentServiceStopTime; 
	private List<String> attachedDocumentPatientInfo; 
	private String attachedDocumentLanguageCode; 
	private String attachedDocumentClassificationXnodeReprestation = ""; 
	private String attachedDocumentClassificationXValue = ""; 
	private String attachedDocumentClassificationYValue = ""; 
	private String attachedDocumentClassificationYnodeRepresentation = ""; 
	private String attachedDocumentClassificationYName = ""; 
	private String attachedDocumentClassificationZValue = ""; 
	private String attachedDocumentClassificationZnodeRepresentation = ""; 
	private String attachedDocumentClassificationZName = ""; 
	private String attachedDocumentClassificationIValue = ""; 
	private String attachedDocumentClassificationInodeRepresentation = ""; 
	private String attachedDocumentClassificationIName = "";	 
	private String attachedDocumentClassificationJValue = ""; 
	private String attachedDocumentClassificationJnodeRepresentation = ""; 
	private String attachedDocumentClassificationJName = "";	 
	private String attachedDocumentClassificationKValue = ""; 
	private String attachedDocumentClassificationKnodeRepresentation = ""; 
	private String attachedDocumentClassificationKName = ""; 
	private String attachedDocumentUniqueID; 
	private String submissionSetID; 
	private String submissionSetType; 
	private String submissionSetAuthor; 
	private String submissionSetCodingSchemeName; 
	private String submissionSetCodingSchemeValue; 
	private String submissionSetStatus; 
	// HBC : the association to the target document 
	private String associationTargetObjectID; 
	 
	 
	public String getAssociationTargetObjectID() { 
    return associationTargetObjectID; 
  } 
 
  public void setAssociationTargetObjectID(String associationTargetObjectID) { 
    this.associationTargetObjectID = associationTargetObjectID; 
  } 
 
  public RegistryMetaData(String patientID) { 
		this.patientID = patientID; 
	} 
 
	public String getPatientID() { 
		return this.patientID; 
	} 
 
	public String getAttachedDocumentMimeType() { 
		return attachedDocumentMimeType; 
	} 
 
	public void setAttachedDocumentMimeType(String attachedDocumentMimeType) { 
		this.attachedDocumentMimeType = attachedDocumentMimeType; 
	} 
 
	public String getAttachedDocumentName() { 
		return attachedDocumentName; 
	} 
	 
	/** 
	 * Any string value. Not restrictions found - check ebXML RS to be sure. 
	 * @param attachedDocumentName 
	 */ 
	public void setAttachedDocumentName(String attachedDocumentName) { 
		this.attachedDocumentName = attachedDocumentName; 
	} 
 
	public String getAttachedDocumentComment() { 
		return attachedDocumentComment; 
	} 
	 
	/** 
	 * Any string value. Not restrictions found - check ebXML RS to be sure. 
	 * @param attachedDocumentComment 
	 */ 
	public void setAttachedDocumentComment(String attachedDocumentComment) { 
		this.attachedDocumentComment = attachedDocumentComment; 
	} 
 
	public String getAttachedDocumentCreationTime() { 
		return attachedDocumentCreationTime; 
	} 
 
	/** 
	 * Day the document was created. Format: yyyymmdd 
	 * @param attachedDocumentCreationTime 
	 */ 
	public void setAttachedDocumentCreationTime( 
			String attachedDocumentCreationTime) { 
		this.attachedDocumentCreationTime = attachedDocumentCreationTime; 
	} 
 
	public String getAttachedDocumentServiceStartTime() { 
		return attachedDocumentServiceStartTime; 
	} 
 
	/** 
	 * Service start to stop time seems to be the duration of the performed service, e.g. lab tests 
	 * Format: yyyymmddhhmm 
	 * @param attachedDocumentServiceStartTime 
	 */ 
	public void setAttachedDocumentServiceStartTime( 
			String attachedDocumentServiceStartTime) { 
		this.attachedDocumentServiceStartTime = attachedDocumentServiceStartTime; 
	} 
 
	public String getAttachedDocumentServiceStopTime() { 
		return attachedDocumentServiceStopTime; 
	} 
 
	/** 
	 * Service start to stop time seems to be the duration of the performed service, e.g. lab tests 
	 * Format: yyyymmddhhmm 
	 * @param attachedDocumentServiceStopTime 
	 */ 
	public void setAttachedDocumentServiceStopTime( 
			String attachedDocumentServiceStopTime) { 
		this.attachedDocumentServiceStopTime = attachedDocumentServiceStopTime; 
	} 
 
	public List<String> getAttachedDocumentPatientInfo() { 
		return attachedDocumentPatientInfo; 
	} 
 
	/** 
	 * Information about the patient. 1-n values as Strings.  
	 * The Codeplex reference implementation used HL7 2.x values. 
	 * E.g.:  
	 * PID-3|pid1^^^domain 
	 * PID-5|Doe^John^^^ 
	 * PID-7|19560527 
	 * PID-8|M 
	 * PID-11|100 Main St^^Metropolis^Il^44130^USA 
	 * @param attachedDocumentPatientInfo 
	 */ 
	public void setAttachedDocumentPatientInfo( 
			List<String> attachedDocumentPatientInfo) { 
		this.attachedDocumentPatientInfo = attachedDocumentPatientInfo; 
	} 
 
	public String getAttachedDocumentLanguageCode() { 
		return attachedDocumentLanguageCode; 
	} 
 
	/** 
	 * Language the document is written in.  
	 * Not restrictions found - check ebXML RS to be sure. 
	 * E.g.: en-us 
	 * @param attachedDocumentLanguageCode 
	 */ 
	public void setAttachedDocumentLanguageCode( 
			String attachedDocumentLanguageCode) { 
		this.attachedDocumentLanguageCode = attachedDocumentLanguageCode; 
	} 
 
	public String getAttachedDocumentClassificationXValue() { 
		return attachedDocumentClassificationXValue; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:691b9c22-642f-d0c8-b8b6-bf0edbb75b08 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. But the classification MUST be in the request.. 
	 * @param attachedDocumentClassificationXValue 
	 */ 
	public void setAttachedDocumentClassificationXValue( 
			String attachedDocumentClassificationXValue) { 
		this.attachedDocumentClassificationXValue = attachedDocumentClassificationXValue; 
	} 
 
	public String getAttachedDocumentClassificationXnodeReprestation() { 
		return attachedDocumentClassificationXnodeReprestation; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:691b9c22-642f-d0c8-b8b6-bf0edbb75b08 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. The example uses 'Objective'. The classification MUST be in the  
	 * request.. 
	 * @param attachedDocumentClassificationXnodeReprestation 
	 */ 
	public void setAttachedDocumentClassificationXnodeReprestation( 
			String attachedDocumentClassificationXnodeReprestation) { 
		this.attachedDocumentClassificationXnodeReprestation = attachedDocumentClassificationXnodeReprestation; 
	} 
 
	public String getAttachedDocumentClassificationYValue() { 
		return attachedDocumentClassificationYValue; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:fb57bcf4-7f7a-28ab-c29c-fe067854abbd 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C confidentialityCodes' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationYValue 
	 */ 
	public void setAttachedDocumentClassificationYValue( 
			String attachedDocumentClassificationYValue) { 
		this.attachedDocumentClassificationYValue = attachedDocumentClassificationYValue; 
	} 
 
	 
	public String getAttachedDocumentClassificationYnodeRepresentation() { 
		return attachedDocumentClassificationYnodeRepresentation; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:fb57bcf4-7f7a-28ab-c29c-fe067854abbd 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. Example uses '1.3.6.1.4.1.21367.2006.7.104'. The classification  
	 * MUST be in the request..  
	 * @param attachedDocumentClassificationYValue 
	 */ 
	public void setAttachedDocumentClassificationYnodeRepresentation( 
			String attachedDocumentClassificationYnodeRepresentation) { 
		this.attachedDocumentClassificationYnodeRepresentation = attachedDocumentClassificationYnodeRepresentation; 
	} 
 
	public String getAttachedDocumentClassificationYName() { 
		return attachedDocumentClassificationYName; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:fb57bcf4-7f7a-28ab-c29c-fe067854abbd 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C document test' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationYValue 
	 */ 
	public void setAttachedDocumentClassificationYName( 
			String attachedDocumentClassificationYName) { 
		this.attachedDocumentClassificationYName = attachedDocumentClassificationYName; 
	} 
 
	public String getAttachedDocumentClassificationZValue() { 
		return attachedDocumentClassificationZValue; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:2d676c3f-33e0-742d-fe6b-3ed475a5c75d 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C formatCodes' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationZValue 
	 */ 
	public void setAttachedDocumentClassificationZValue( 
			String attachedDocumentClassificationZValue) { 
		this.attachedDocumentClassificationZValue = attachedDocumentClassificationZValue; 
	} 
 
	public String getAttachedDocumentClassificationZnodeRepresentation() { 
		return attachedDocumentClassificationZnodeRepresentation; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:2d676c3f-33e0-742d-fe6b-3ed475a5c75d 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'CDAR2/IHE 1.0' in example. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationZnodeRepresentation 
	 */ 
	public void setAttachedDocumentClassificationZnodeRepresentation( 
			String attachedDocumentClassificationZnodeRepresentation) { 
		this.attachedDocumentClassificationZnodeRepresentation = attachedDocumentClassificationZnodeRepresentation; 
	} 
 
	public String getAttachedDocumentClassificationZName() { 
		return attachedDocumentClassificationZName; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:2d676c3f-33e0-742d-fe6b-3ed475a5c75d 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C document test' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationZName 
	 */ 
	public void setAttachedDocumentClassificationZName( 
			String attachedDocumentClassificationZName) { 
		this.attachedDocumentClassificationZName = attachedDocumentClassificationZName; 
	} 
 
	public String getAttachedDocumentClassificationIValue() { 
		return attachedDocumentClassificationIValue; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:a6e536d1-b496-4006-e243-5c94b6db7c07 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C healthcareFacilityTypeCodes' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationIValue 
	 */ 
	public void setAttachedDocumentClassificationIValue( 
			String attachedDocumentClassificationIValue) { 
		this.attachedDocumentClassificationIValue = attachedDocumentClassificationIValue; 
	} 
 
	public String getAttachedDocumentClassificationInodeRepresentation() { 
		return attachedDocumentClassificationInodeRepresentation; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:a6e536d1-b496-4006-e243-5c94b6db7c07 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'Hospital Setting' in example. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationInodeRepresentation 
	 */ 
	public void setAttachedDocumentClassificationInodeRepresentation( 
			String attachedDocumentClassificationInodeRepresentation) { 
		this.attachedDocumentClassificationInodeRepresentation = attachedDocumentClassificationInodeRepresentation; 
	} 
 
	public String getAttachedDocumentClassificationIName() { 
		return attachedDocumentClassificationIName; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:a6e536d1-b496-4006-e243-5c94b6db7c07 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C document test' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationIName 
	 */ 
	public void setAttachedDocumentClassificationIName( 
			String attachedDocumentClassificationIName) { 
		this.attachedDocumentClassificationIName = attachedDocumentClassificationIName; 
	} 
 
	public String getAttachedDocumentClassificationJValue() { 
		return attachedDocumentClassificationJValue; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:241be963-c4f3-d799-a6a5-d5a4636a8f0f 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C practiceSettingCodes' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationJValue 
	 */ 
	public void setAttachedDocumentClassificationJValue( 
			String attachedDocumentClassificationJValue) { 
		this.attachedDocumentClassificationJValue = attachedDocumentClassificationJValue; 
	} 
 
	public String getAttachedDocumentClassificationJnodeRepresentation() { 
		return attachedDocumentClassificationJnodeRepresentation; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:241be963-c4f3-d799-a6a5-d5a4636a8f0f 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'Laboratory' in example. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationJnodeRepresentation 
	 */ 
	public void setAttachedDocumentClassificationJnodeRepresentation( 
			String attachedDocumentClassificationJnodeRepresentation) { 
		this.attachedDocumentClassificationJnodeRepresentation = attachedDocumentClassificationJnodeRepresentation; 
	} 
 
	public String getAttachedDocumentClassificationJName() { 
		return attachedDocumentClassificationJName; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:241be963-c4f3-d799-a6a5-d5a4636a8f0f 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C document test' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationJName 
	 */ 
	public void setAttachedDocumentClassificationJName( 
			String attachedDocumentClassificationJName) { 
		this.attachedDocumentClassificationJName = attachedDocumentClassificationJName; 
	} 
 
	public String getAttachedDocumentClassificationKValue() { 
		return attachedDocumentClassificationKValue; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:7aec7490-a293-12ae-d70b-8976b4f1f703 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C typeCodes' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationKValue 
	 */ 
	public void setAttachedDocumentClassificationKValue( 
			String attachedDocumentClassificationKValue) { 
		this.attachedDocumentClassificationKValue = attachedDocumentClassificationKValue; 
	} 
 
	public String getAttachedDocumentClassificationKnodeRepresentation() { 
		return attachedDocumentClassificationKnodeRepresentation; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:7aec7490-a293-12ae-d70b-8976b4f1f703 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'Laboratory Report' in example. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationKnodeRepresentation 
	 */ 
	public void setAttachedDocumentClassificationKnodeRepresentation( 
			String attachedDocumentClassificationKnodeRepresentation) { 
		this.attachedDocumentClassificationKnodeRepresentation = attachedDocumentClassificationKnodeRepresentation; 
	} 
 
	public String getAttachedDocumentClassificationKName() { 
		return attachedDocumentClassificationKName; 
	} 
 
	/** 
	 * ClassificationID is: urn:uuid:7aec7490-a293-12ae-d70b-8976b4f1f703 
	 * No clue where to look it up nor what it is. As far as I can tell,  
	 * any string will work -- the registry doesn't even seem to validate the 
	 * value. 'N4C document test' work. The classification MUST be in the  
	 * request..  
	 * @param attachedDocumentClassificationKName 
	 */ 
	public void setAttachedDocumentClassificationKName( 
			String attachedDocumentClassificationKName) { 
		this.attachedDocumentClassificationKName = attachedDocumentClassificationKName; 
	} 
 
	public String getAttachedDocumentUniqueID() { 
		return attachedDocumentUniqueID; 
	} 
 
	/** 
	 * Document uniqueID. Must be unique in the repository. Any string value seems 
	 * to work. Check ebXML RS to be sure. NB: This is the same ID used to query  
	 * and retrieve the document! 
	 * @param attachedDocumentUniqueID 
	 */ 
	public void setAttachedDocumentUniqueID(String attachedDocumentUniqueID) { 
		this.attachedDocumentUniqueID = attachedDocumentUniqueID; 
	} 
 
	public String getSubmissionSetID() { 
		return submissionSetID; 
	} 
 
	/** 
	 * Id of this SubmissionSet. Any string seems to work.  
	 * @param submissionSetID 
	 */ 
	public void setSubmissionSetID(String submissionSetID) { 
		this.submissionSetID = submissionSetID; 
	} 
 
	public String getSubmissionSetType() { 
		return submissionSetType; 
	} 
 
	/** 
	 * The type of this SubmissionSet. Any string seems to work. 
	 * E.g. 'Lab + discharge' 
	 * @param submissionSetType 
	 */ 
	public void setSubmissionSetType(String submissionSetType) { 
		this.submissionSetType = submissionSetType; 
	} 
 
	public String getSubmissionSetAuthor() { 
		return submissionSetAuthor; 
	} 
 
	/** 
	 * Author of this Submission set. Any string seems to work. 
	 * Example uses HL7 2.x: ^Foo^Bar^^^^Dr^MD  
	 * @param submissionSetAuthor 
	 */ 
	public void setSubmissionSetAuthor(String submissionSetAuthor) { 
		this.submissionSetAuthor = submissionSetAuthor; 
	} 
 
	public String getSubmissionSetCodingSchemeName() { 
		return submissionSetCodingSchemeName; 
	} 
 
	/** 
	 * Coding scheme Name of this SubmissionSet. Any string seems to work. 
	 * e.g. N4C TEST works  
	 * @param submissionSetCodingScheme 
	 */ 
	public void setSubmissionSetCodingSchemeName(String submissionSetCodingSchemeName) { 
		this.submissionSetCodingSchemeName = submissionSetCodingSchemeName; 
	} 
 
	public String getSubmissionSetCodingSchemeValue() { 
		return submissionSetCodingSchemeValue; 
	} 
	 
	/** 
	 * Coding scheme value of this SubmissionSet. Any String seems to work. 
	 * E.g. Net4Care XDS Proxy Test works 
	 * @param submissionSetCodingSchemeValue 
	 */ 
	public void setSubmissionSetCodingSchemeValue( 
			String submissionSetCodingSchemeValue) { 
		this.submissionSetCodingSchemeValue = submissionSetCodingSchemeValue; 
	} 
 
	public String getSubmissionSetStatus() { 
		return submissionSetStatus; 
	} 
 
	/** 
	 * Status of the SubmissionSet. Default value is 'Original' 
	 * Check IHE XDS.b or ebXML RS 3.0 for other values.  
	 * @param submissionSetStatus 
	 */ 
	public void setSubmissionSetStatus(String submissionSetStatus) { 
		this.submissionSetStatus = submissionSetStatus; 
	} 
} 
