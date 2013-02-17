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
 
package org.net4care.receiver.delegate; 
 
import java.util.*; 
 
import org.apache.log4j.Logger; 
import org.net4care.cda.PHMRBuilder; 
import org.net4care.serializer.Serializer; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.receiver.ObservationReceiver; 
import org.net4care.storage.*; 
import org.net4care.storage.queries.*; 
import org.net4care.utility.*; 
import org.w3c.dom.Document; 
 
/** A standard implementation of the ObservationReceiver interface. 
 * This standard implementation is provided with delegates that 
 * serve the roles as a) serializer b) external data source and 
 * c) XDS repository. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
	           ) 
public class StandardObservationReceiver implements ObservationReceiver { 
 
  private XDSRepository xds; 
  private ObservationCache cache; 
  private Serializer serializer; 
  private ExternalDataSource externalDatasources; 
  private Logger logger = Logger.getLogger(StandardObservationReceiver.class); 
 
 
  /** Construct an observation receiver that uses the provided 
   * delegates to deserialize, contact external data sources, and  
   * eventually store the generated PHMR in a XDS repository. 
   * @param serializer the serializer to use 
   * @param externalDataSource the proxy to the external feeder systems to use 
   * @param xds the proxy to the XDS repository to use. PRECONDITION - 
   * the XDS repository has been properly connected to its database. 
   */ 
  public StandardObservationReceiver(Serializer serializer,  
      ExternalDataSource externalDataSource,  
      XDSRepository xds, 
      ObservationCache cache) { 
    this.externalDatasources = externalDataSource; 
    this.serializer = serializer; 
    this.xds = xds; 
    this.cache = cache; 
  } 
 
  /** 
   * Construct an observation receiver that assumes that dependency 
   * injection by the OSGi platform takes care of needed dependencies.  
   * Do not use this in client code. 
   *  
   */ 
  public StandardObservationReceiver() { 
  } 
 
  protected void setXds(XDSRepository xds) throws Net4CareException { 
    logger.info("StandardObservationReiver configured with XDS: "+xds.toString()); 
    this.xds = xds; 
    xds.connect();  
  } 
 
  protected void unsetXds(XDSRepository xds) throws Net4CareException { 
    if ( xds != null ) { xds.disconnect(); } 
    this.xds = null; 
  } 
 
  protected void setObservationCache(ObservationCache cache) { 
    //logger.info("StandardObservationReiver configured with ObservationCache: "+cache.toString()); 
    this.cache = cache; 
    // TODO: should we connect here as well? 
  } 
 
  protected void unsetObservationCache(ObservationCache cache) { 
    this.cache = null; 
  } 
 
  protected void setSerializer(Serializer serializer) { 
    this.serializer = serializer; 
  } 
 
  protected void unsetSerializer(Serializer serializer) { 
    this.serializer = null; 
  } 
 
  protected void setExternalDataSource(ExternalDataSource externalDataSources) { 
    this.externalDatasources = externalDataSources; 
  } 
 
  protected void unsetExternalDataSource(ExternalDataSource externalDataSources) { 
    this.externalDatasources = null; 
  } 
 
  @Override 
  public StandardTeleObservation observationMessageReceived(String messagePayload)  
      throws Net4CareException { 
    StandardTeleObservation deserialized =  
        serializer.deserialize(messagePayload); 
 
    createAndRegisterPHMR(messagePayload, deserialized); 
 
    return deserialized; 
  } 
 
  @Override 
  public String observationQueryReceived(Map<String, String> query) throws Net4CareException { 
    // TODO: add more query types - HBC 
    long begin, end; 
    String codeSystem, codeListInBarFormat, queryType; 
    XDSQuery xdsquery = null; 
 
    // Validate existence of CPR 
    if (query.get(QueryKeys.CPR_KEY) != null && query.get(QueryKeys.CPR_KEY).length() > 0) { 
    	externalDatasources.getPersonData(query.get(QueryKeys.CPR_KEY)); 
    } 
	 
    // TODO: Remove to make this (variable end) a mandatory parameter. MLN 
    if(query.get( QueryKeys.END_TIME_INTERVAL ) == null) { 
      query.put(QueryKeys.END_TIME_INTERVAL, System.currentTimeMillis()+""); 
    } 
     
    try { 
      begin = Long.parseLong( query.get( QueryKeys.BEGIN_TIME_INTERVAL) ); 
      end =  Long.parseLong( query.get( QueryKeys.END_TIME_INTERVAL) ); 
      //Check if the time intervals are negative. 
      if(begin < 0 || end < 0) 
    	  throw new Net4CareException(Constants.ERROR_INVALID_PARAM, 
    	          "Query parameter '" + QueryKeys.BEGIN_TIME_INTERVAL + "' or '" +  
    	              QueryKeys.END_TIME_INTERVAL + "' is not a valid timestamp."); 
    } 
    catch(NumberFormatException e) { 
      throw new Net4CareException(Constants.ERROR_INVALID_PARAM, 
          "Query parameter " + QueryKeys.BEGIN_TIME_INTERVAL + " or " +  
              QueryKeys.END_TIME_INTERVAL + " is not a valid timestamp."); 
    } 
     
    // Fetch parameters 
    String cpr = query.get( QueryKeys.CPR_KEY ); 
    codeSystem = query.get( QueryKeys.CODE_SYSTEM ); 
    codeListInBarFormat = query.get( QueryKeys.CODE_LIST_BAR_FORMAT ); 
     
    	 
    queryType = query.get( QueryKeys.QUERY_TYPE ); 
    // TODO: Remove to make queryType mandatory. MLN 
    if(queryType == null) 
      queryType = ""; 
 
    if ( queryType.equals( QueryKeys.PERSON_TIME_QUERY ) ) { 
      xdsquery = new XDSQueryPersonTimeInterval(cpr, begin, end); 
    }  
    //TODO: Remove "queryType.equals("")" to make queryType a mandatory GET parameter. MLN   
    else if ( queryType.equals( QueryKeys.PERSON_TIME_OBSERVATION_TYPE_QUERY ) || queryType.equals("") ) {    
      String listOfCodes[] = Utility.convertBarSeparatedStringToCodeList(codeListInBarFormat); 
      xdsquery = new XDSQueryPersonTimeIntervalType(cpr, begin, end, codeSystem, listOfCodes); 
    }  
    else { 
      throw new Net4CareException(Constants.ERROR_INVALID_QUERYTYPE, "Unknown query "+query+" received in StandardObservationReceiver."); 
    }    
 
    String bigstring = null; 
     
    // Decided to return JSON or XML 
    String format = query.get(QueryKeys.FORMAT_KEY); 
     
    if ( format.equals(QueryKeys.ACCEPT_JSON_DATA) || format.equals(QueryKeys.ACCEPT_GRAPH_DATA)) { 
      List<String> list = cache.retrieveObservationSet(xdsquery); 
      bigstring = serializer.serializeList(list); 
      //System.out.println("Bigstring = "+bigstring); 
    } else if ( format.equals(QueryKeys.ACCEPT_XML_DATA )) { 
      List<Document> listOfDocuments = xds.retrieveDocumentSet(xdsquery); 
       
      List<String> list = new ArrayList<String>(); 
      for ( Document d : listOfDocuments ) { 
        list.add( Utility.convertXMLDocumentToString(d)); 
      } 
      bigstring = serializer.serializeList(list); 
      //System.out.println( "Big="+bigstring); 
    } 
 
    return bigstring; 
  } 
 
  private void createAndRegisterPHMR(String serializedForm, StandardTeleObservation deserialized) throws Net4CareException { 
    PHMRBuilder phmr_document = new PHMRBuilder(externalDatasources, deserialized); 
 
    Document xml = phmr_document.buildDocument(); 
    RegistryEntry md = phmr_document.buildRegistryEntry(); 
 
    // Store in the XDS 
    xds.provideAndRegisterDocument(md, xml); 
 
    // Store in the cache 
    cache.provideAndRegisterObservation(md, serializedForm); 
  } 
}  
