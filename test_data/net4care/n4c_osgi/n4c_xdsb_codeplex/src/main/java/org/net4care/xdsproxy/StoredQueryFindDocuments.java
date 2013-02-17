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
 
import java.io.File; 
import java.util.List; 
 
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest; 
import oasis.names.tc.ebxml_regrep.xsd.query._3.ResponseOptionType; 
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType; 
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1; 
 
 
 
/** Stored XDS query to find all documents related to a given person 
 * in a given time interval. 
 *  
 * @author Net4Care, Morten Larsson, AU 
 */ 
public class StoredQueryFindDocuments implements StoredQuery { 
 
  private boolean returnObjectRef; 
 
  private String patientID; 
  private String[] entryStatus; 
  private String serviceStartTimeFrom; 
  private String serviceStartTimeTo; 
  private String serviceEndTimeFrom; 
  private String serviceEndTimeTo; 
 
  /** 
   * Create new FindDocument stored query. 
   * @param patientID  The unique patient identifier. e.g. Social Security number. 
   * @param beginTimeInterval  Start of the time interval to query in. 
   * @param endTimeInterval  End of the time interval to query in. 
   */ 
  public StoredQueryFindDocuments(String patientID, String beginTimeInterval, String endTimeInterval) { 
    returnObjectRef = true; 
    this.patientID = patientID; 
    //TODO: Get rid of the hardcoded entryStatus below. 
    this.entryStatus = new String[] { "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved", "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated" }; 
    this.serviceStartTimeFrom = beginTimeInterval; 
    this.serviceStartTimeTo = endTimeInterval; 
    this.serviceEndTimeFrom = beginTimeInterval; 
    this.serviceEndTimeTo = endTimeInterval;  
  } 
 
  
  public void setQueryTypeToLeafClass() { 
    returnObjectRef = false; 
  } 
 
  
  public void setQueryTypeToObjectRef() { 
    returnObjectRef = true; 
  } 
 
  
  public File getRequestXMLFile() { 
    AdhocQueryRequest req = new AdhocQueryRequest();    
 
    AdhocQueryType queryType = new AdhocQueryType(); 
    queryType.setId("urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d"); 
 
    ResponseOptionType responceOption = new ResponseOptionType(); 
    if(returnObjectRef) 
      responceOption.setReturnType("ObjectRef"); 
    else 
      responceOption.setReturnType("LeafClass"); 
    responceOption.setReturnComposedObjects(true); 
 
    List<SlotType1> slotList = queryType.getSlot(); 
 
    slotList.add( 
        Utility.createSlotType("$XDSDocumentEntryPatientId", patientID)); 
 
    slotList.add( 
        Utility.createSlotType("$XDSDocumentEntryStatus", entryStatus)); 
 
    slotList.add( 
        Utility.createSlotType("$XDSDocumentEntryServiceStartTimeFrom", serviceStartTimeFrom)); 
 
    slotList.add( 
        Utility.createSlotType("$XDSDocumentEntryServiceStartTimeTo", serviceStartTimeTo)); 
 
    slotList.add( 
        Utility.createSlotType("$XDSDocumentEntryServiceEndTimeFrom", serviceEndTimeFrom)); 
 
    slotList.add( 
        Utility.createSlotType("$XDSDocumentEntryServiceEndTimeTo", serviceEndTimeTo)); 
 
    req.setResponseOption(responceOption); 
    req.setAdhocQuery(queryType); 
 
    return Utility.createFileFromAdhocQueryRequest(req); 
  }  
 
} 
