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
import java.util.*; 
 
import org.net4care.storage.*; 
import org.net4care.storage.queries.*; 
import org.net4care.utility.*; 
import org.w3c.dom.Document; 
 
/** FakeObject implementation of the XDS repository and Observation Cache. 
 * Stores all HL7 documents and observations in an in-memory list for 
 * much faster but non persistent storage to speed up testing. 
 *  
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
 
public class FakeObjectXDSAndObservationCache implements XDSRepository, 
    ObservationCache { 
 
  private List<Pair> repository; 
  private List<Pair> cache; 
   
  public FakeObjectXDSAndObservationCache() throws Net4CareException { 
    utterlyEmptyAllContentsOfTheDatabase(); 
  } 
   
  @Override 
  public synchronized void provideAndRegisterObservation(RegistryEntry metadata, 
      String serializedFormOfObservation) throws Net4CareException { 
    Pair pair = new Pair(); 
    pair.entry = metadata; 
    pair.value = serializedFormOfObservation; 
    cache.add(pair); 
  } 
 
  @Override 
  public synchronized List<String> retrieveObservationSet(XDSQuery query) 
      throws Net4CareException { 
    List<String> result = new ArrayList<String>();   
    retrieveValueFromInternalList(query, cache, result); 
    return result; 
  } 
 
  @Override 
  public synchronized void provideAndRegisterDocument(RegistryEntry metadata, 
      Document xmlDocument) throws Net4CareException { 
    Pair pair = new Pair(); 
    pair.entry = metadata; 
    pair.value = Utility.convertXMLDocumentToString(xmlDocument); 
    repository.add(pair); 
  } 
 
  @Override 
  public synchronized List<Document> retrieveDocumentSet(XDSQuery query) 
      throws Net4CareException { 
    List<String> xmlset; 
    List<Document> docset = new ArrayList<Document>(); 
    xmlset = retrieveDocumentSetAsXMLString(query); 
    for( String xml: xmlset ) {  
      docset.add( Utility.convertXMLStringToDocument(xml)); 
    } 
    return docset; 
  } 
 
  @Override 
  public synchronized List<String> retrieveDocumentSetAsXMLString(XDSQuery query) 
      throws Net4CareException { 
    List<String> result = new ArrayList<String>();       
    retrieveValueFromInternalList(query, repository, result); 
    return result; 
  } 
 
  private synchronized void retrieveValueFromInternalList(XDSQuery query,  
      List<Pair> repository, 
      List<String> result) { 
    for ( Pair item : repository ) { 
      RegistryEntry entry = item.entry; 
       
      if ( query.getClass() == XDSQueryPersonTimeInterval.class ) { 
        XDSQueryPersonTimeInterval qpti = (XDSQueryPersonTimeInterval) query; 
        if ( entry.getCpr().equals( qpti.getCpr() ) && 
             entry.getTimestamp() >= qpti.getBeginTimeInterval() && 
             entry.getTimestamp() <= qpti.getEndTimeInterval() ) { 
          result.add(item.value); 
        } 
      } else if ( query.getClass() == XDSQueryPersonTimeIntervalType.class ) { 
        XDSQueryPersonTimeIntervalType qptit = (XDSQueryPersonTimeIntervalType) query; 
        if ( entry.getCpr().equals( qptit.getCpr() ) && 
            entry.getTimestamp() >= qptit.getBeginTimeInterval() && 
            entry.getTimestamp() <= qptit.getEndTimeInterval() &&  
            entry.getCodeSystem().equals( qptit.getCodeSystem() ) &&  
            codesIsSuperSetOf( entry.getCodesOfValuesMeasured(), qptit.getObservationTypes() ) ) { 
          result.add(item.value); 
        } 
      } else if ( query.getClass() == XDSQueryPersonTimeIntervalTreatment.class ) { 
        XDSQueryPersonTimeIntervalTreatment qptitr = (XDSQueryPersonTimeIntervalTreatment) query; 
        if ( entry.getCpr().equals( qptitr.getCpr() ) && 
            entry.getTimestamp() >= qptitr.getBeginTimeInterval() && 
            entry.getTimestamp() <= qptitr.getEndTimeInterval() &&  
            entry.getTreatmentId().equals( qptitr.getTreatmentId() ) ) { 
          result.add(item.value); 
        } 
      } 
    } 
  } 
   
 
  private boolean codesIsSuperSetOf(String[] codesMeasured, 
      String[] codesQueried) { 
    Set<String> a = new HashSet<String>(); 
    a.addAll(Arrays.asList(codesMeasured)); 
    return a.containsAll(Arrays.asList(codesQueried)); 
  } 
 
  @Override 
  public void connect() throws Net4CareException { 
  } 
 
  @Override 
  public void disconnect() throws Net4CareException { 
  } 
 
  @Override 
  public void utterlyEmptyAllContentsOfTheDatabase() throws Net4CareException { 
    repository = new ArrayList<Pair>(); 
    cache = new ArrayList<Pair>(); 
  } 
 
} 
 
class Pair { 
  public RegistryEntry entry; 
  public String value; 
} 
