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
import java.util.ArrayList; 
import java.util.List; 
 
import javax.xml.bind.JAXBContext; 
import javax.xml.bind.JAXBException; 
import javax.xml.bind.Marshaller; 
 
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest; 
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1; 
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType; 
 
public class Utility { 
  protected static SlotType1 createSlotType(String name, List<String> values) { 
    ValueListType valueList = new ValueListType(); 
    valueList.getValue().addAll(values);    
    SlotType1 slotType = new SlotType1(); 
    slotType.setName(name); 
    slotType.setValueList(valueList); 
    return slotType; 
  } 
 
  protected static SlotType1 createSlotType(String name, String[] values) { 
    List<String> list = new ArrayList<String>(); 
    for(int i = 0; i < values.length; i++) { 
      list.add(values[i]); 
    } 
    return createSlotType(name, list); 
  } 
 
  protected static SlotType1 createSlotType(String name, String value) { 
    List<String> list = new ArrayList<String>(); 
    list.add(value); 
    return createSlotType(name, list); 
  } 
 
  protected static File createFileFromAdhocQueryRequest(AdhocQueryRequest query) { 
    JAXBContext jc; 
    Marshaller m; 
    File outputFile = null; 
    try { 
      jc = JAXBContext.newInstance("ihe.iti.xds_b._2007"); 
      m = jc.createMarshaller(); 
      String separator = System.getProperty("file.separator"); 
      outputFile = new File(System.getProperty("java.io.tmpdir")+ separator + "generatedFindDocumentsRequest.xml"); 
      m.marshal(query,outputFile); 
    } catch (JAXBException e) { 
      e.printStackTrace(); 
    } 
    return outputFile; 
  } 
} 
