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
 
package org.net4care.testdoubles; 
 
import java.io.File; 
import java.util.ArrayList; 
import java.util.List; 
 
import org.net4care.storage.RegistryEntry; 
import org.net4care.storage.XDSQuery; 
import org.net4care.storage.XDSRepository; 
import org.net4care.utility.Net4CareException; 
import org.w3c.dom.Document; 
 
/** A Spy XDS that can spy on the last stored XML document. 
 * The retrieve method will always return the last stored document. 
 */ 
 
public class SpyXDS implements XDSRepository { 
 
  private Document lastStored = null; 
  private boolean isConnected = false; 
 
  public void provideAndRegisterDocument(RegistryEntry metadata,  
                                         Document xmlDocument) { 
    if ( ! isConnected )  
      throw new RuntimeException("XDSRepository (faked) is not connected!"); 
    lastStored = xmlDocument; 
  } 
   
  public List<String> retrieveDocumentSetAsXMLString(XDSQuery query) { 
    if ( ! isConnected )  
      throw new RuntimeException("XDSRepository (faked) is not connected!"); 
 
    String asXML; 
    try { 
      asXML = org.net4care.utility.Utility.convertXMLDocumentToString(lastStored); 
    } catch (Net4CareException e) { 
      throw new RuntimeException(e); 
    } 
    List<String> l = new ArrayList<String>(); 
    l.add(asXML); 
    return l; 
  } 
 
  public void connect() { 
    isConnected = true; 
  } 
   
  public void disconnect() { 
    isConnected = false; 
  } 
 
  public void utterlyEmptyAllContentsOfTheDatabase() { 
  } 
 
  @Override 
  public List<Document> retrieveDocumentSet(XDSQuery query) { 
    if ( ! isConnected )  
      throw new RuntimeException("XDSRepository (faked) is not connected!"); 
 
    List<Document> l = new ArrayList<Document>(); 
    l.add(lastStored); 
    return l; 
  } 
 
} 
