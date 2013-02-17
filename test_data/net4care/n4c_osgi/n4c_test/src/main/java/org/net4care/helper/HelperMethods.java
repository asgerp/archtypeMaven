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
 
package org.net4care.helper; 
 
import org.net4care.observation.Codes; 
import org.net4care.observation.DeviceDescription; 
import org.net4care.observation.StandardTeleObservation; 
import org.w3c.dom.Document; 
import org.w3c.dom.NamedNodeMap; 
import org.w3c.dom.NodeList; 
 
import com.smb.homeapp.BloodPressure; 
import com.smb.homeapp.Spirometry; 
 
public class HelperMethods { 
   
  /** get the value of a specific attribute with an enclosing node. 
   * Example: 
   *  
   * The PHMR contains a deeply nested observation node like this 
               <observation classCode="OBS" moodCode="EVN"> 
                  <templateId root="2.16.840.1.113883.10.20.1.31"/> 
                  <templateId root="2.16.840.1.113883.10.20.9.8"/> 
                  <code code="20150-9" codeSystem="2.16.840.1.113883.6.1" displayName="FEV1"/> 
                  <value unit="L" value="3.42" xsi:type="PQ"/> 
                </observation> 
 
   * The following test will pass:  
   *  assertEquals( "L", getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc( "unit", 1, "value", "observation", phmrDoc) ); 
   
   * @param attributeName name of the attribute whose value is returned 
   * @param nodeIndex the number of the node named 'nodeName' in the child list of node 'enclosingNodeName' 
   * @param nodeName name of the node with the attribute 
   * @param enclosingNodeName name of the node that encloses the node 
   * @param doc the XML document 
   * @return the value of the attribute or null if not found. 
   */ 
  public static String getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc( String attributeName, int nodeIndex, String nodeName, 
      String enclosingNodeName, Document doc) { 
    NodeList list = doc.getElementsByTagName(enclosingNodeName); 
      NodeList childrenOfEnclosed = list.item(nodeIndex).getChildNodes(); 
      for ( int j = 0; j < childrenOfEnclosed.getLength(); j++ ) { 
        if ( childrenOfEnclosed.item(j).getNodeName().equals(nodeName) ) { 
          NamedNodeMap nnm = childrenOfEnclosed.item(j).getAttributes(); 
          return nnm.getNamedItem(attributeName).getNodeValue(); 
        } 
    } 
    return null; 
  } 
   
  /** Helper to create a spirometry observation, as we do this in quite a few places 
   * in the set of test cases. 
   * @param cpr 
   * @param fvc 
   * @param fev1 
   * @return 
   */ 
  public static StandardTeleObservation defineSpirometryObservation(String cpr, double fvc, double fev1) { 
    final String org_uid = "org.net4care.org.mycompany"; 
 
    // Create a spirometry observation for Nancy 
    DeviceDescription device = 
        new DeviceDescription("Spirometer", "SpiroCraft-II", "MyCompany",  
                              "584216", "69854", "2.1", "1.1"); 
    Spirometry spiro = new Spirometry(fvc, fev1); 
    StandardTeleObservation sto =  
      new StandardTeleObservation(cpr, org_uid, "treatment-id",  
                                  Codes.LOINC_OID, device, spiro ); 
    return sto; 
  } 
   
  public static StandardTeleObservation defineBloodPressureObservation(String cpr, double systolic, double diastolic, 
      String organizationOID, String treatmentID) { 
    DeviceDescription device_blood = 
        new DeviceDescription("BloodMeter", "BloodCraft-II",  
            "MyOtherCompany", "584216", "69854", "2.1", "1.1"); 
    BloodPressure blood = new BloodPressure(systolic, diastolic); 
    StandardTeleObservation bloodpressure_sto =  
        new StandardTeleObservation(cpr, organizationOID, treatmentID,  
            Codes.UIPAC_OID, device_blood, blood ); 
 
    return bloodpressure_sto; 
  } 
} 
