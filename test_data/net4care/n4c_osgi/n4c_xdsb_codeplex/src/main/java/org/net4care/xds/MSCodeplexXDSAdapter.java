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
 
package org.net4care.xds; 
 
import java.io.*; 
import java.text.*; 
import java.util.*; 
 
import javax.xml.parsers.ParserConfigurationException; 
import javax.xml.rpc.ServiceException; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
 
import org.apache.axis.encoding.Base64; 
import org.apache.axis.message.*; 
import org.net4care.common.UUIDStrategy; 
import org.net4care.common.delegate.StandardUUIDStrategy; 
import org.net4care.storage.*; 
import org.net4care.storage.queries.XDSQueryPersonTimeInterval; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
import org.net4care.utility.QueryKeys; 
import org.net4care.utility.Utility; 
import org.net4care.xdsproxy.*; 
import org.w3c.dom.*; 
import org.xml.sax.SAXException; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
 
/** Implementation of the XDSRepository interface that is an adapter 
 * between the Net4Care XDSRepository interface assumed by 
 * the N4C server architecture and the 
 * Microsoft Codeplex implementation of the XDS 
 * registry and repository. 
 *  
 * @author HBC and Larsson 
 * 
 */ 
public class MSCodeplexXDSAdapter implements XDSRepository { 
 
	private XDSRepositoryProxy repositoryProxy; 
	private XDSRegistryProxy registryProxy; 
 
	// Create a formatter for the format required by eBXML 
	private Format formatter; 
 
	/** The builder for creating ebXML formatted XDS.b messages */ 
	private EbXMLBuilder ebxmlBuilder; 
 
	/** Create the adapter for MS Codeplex. 
	 * The address of the registry and repository as well  
	 * as port numbers can be set by calling the static methods 
	 * of HostSpecification. 
	 */ 
	public MSCodeplexXDSAdapter() { 
		try { 
			repositoryProxy = new XDSRepositoryProxy(); 
		} catch (ServiceException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
		registryProxy = new XDSRegistryProxy(); 
		formatter = new SimpleDateFormat("yyyyMMddHHmmss"); 
		ebxmlBuilder = new EbXMLBuilder(); 
	}  
 
	public void provideAndRegisterDocument(RegistryEntry metadata, Document xmlDocument) throws Net4CareException { 
		// Generate the uniqueID (our HL7 document's ID that we can retrieve from the 
		// registry and next use to retrieve it from the repository), as well as the 
		// x-reference ID to the document within the on-the-wire HTTP block. 
		String uniqueID, associatedTargetObjectID; 
		UUIDStrategy uuidStrategy = new StandardUUIDStrategy(); 
 
		uniqueID = uuidStrategy.generateUUID(); 
		associatedTargetObjectID = uuidStrategy.generateUUID(); 
 
		MessageElement[] result = null; 
		 
		// Allow only one thread at a time to a) write to the temporary file b) register a document 
		synchronized(this) { 
			// The xmlDocument must be written to a file, prepare for that 
			HashMap<FileMetaData, File> attachments = writeXML2TemporaryFile(xmlDocument, associatedTargetObjectID); 
 
			// Generate the SOAP message which is the ebXML that define all meta data for the xmlDocument 
			SOAPBodyElement soapMessage = null; 
			soapMessage =  
					ebxmlBuilder.buildProvideAndRegisterDocumentSetRequest(metadata, uniqueID, associatedTargetObjectID); 
 
			// Finally - use our proxy to the XDS to send the message. 
			try { 
				result = repositoryProxy.provideAndRegisterDocumentSet(soapMessage, attachments); 
			} catch (ServiceException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} catch (ParserConfigurationException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} catch (SAXException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} catch (IOException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} 
			//System.out.println("Result of ProvideAndRegisterDocumentSet transaction: " + result);	 
		} 
		
		// if the provide&Register went well, the result is actually null - do not ask me why!
		if ( result != null ) {
		  Element asElement = result[0]; 

		  //Check if the repository reported an error  
		  //and throw an Net4CareException containing the error response. 
		  NodeList t = asElement.getElementsByTagName("RegistryError"); 
		  if(t.getLength() > 0) { 
		    String errorMessage =t.item(0).getAttributes().getNamedItem("codeContext").getTextContent(); 
		    throw new Net4CareException(Constants.ERROR_XDS_REGISTRY, errorMessage); 
		  } 
		}
	} 
 
	public List<String> retrieveDocumentSetAsXMLString(XDSQuery net4careXDSQuery) 
			throws Net4CareException { 
 
		// TODO: Tidy up the code 
		// TODO: Clarify exception cases and handle gracefully 
 
		// Setup the return value array. 
		List<String> returnValue = new ArrayList<String>(); 
 
		// Convert the N4C query into encoded query according to type 
		StoredQuery query; 
		if ( net4careXDSQuery.getClass() == XDSQueryPersonTimeInterval.class ) { 
			XDSQueryPersonTimeInterval qpti = (XDSQueryPersonTimeInterval) net4careXDSQuery; 
			String t1 = formatter.format( qpti.getBeginTimeInterval() ); 
			String t2 = formatter.format( qpti.getEndTimeInterval() ); 
 
			query = new StoredQueryFindDocuments(qpti.getCpr(), t1, t2 ); 
 
			query.setQueryTypeToLeafClass(); 
 
			MessageElement[] result = null; 
			try { 
				result = registryProxy.registryStoredQuery(query); 
			} catch (ServiceException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} catch (ParserConfigurationException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} catch (SAXException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} catch (IOException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
			} 
 
			// Fill in the array of uniqueId's that match 
			ArrayList<String> uniqueIdMatches = new ArrayList<String>(); 
 
			// TODO: Should parse it up using JAXB but avoiding the learning curve at present 
			Element asElement = result[0]; 
			//System.out.println("Result of RegistryStoredQuery transaction: " + Utility.convertXMLDocumentToString(asElement)); 
 
			//Check if the registry reported an error  
			//and throw an Net4CareException containing the error response. 
			NodeList t = asElement.getElementsByTagName("RegistryError"); 
			if(t.getLength() > 0) { 
				String errorMessage =t.item(0).getAttributes().getNamedItem("codeContext").getTextContent(); 
				throw new Net4CareException(Constants.ERROR_XDS_REGISTRY, errorMessage); 
			} 
			// Iterate all nodes tagged by 'ExternalIdentifier', one of them contains 
			// the XDSDocumentEntry.uniqueId as 'localizedString'. This node's 
			// value attribute contains the uniqueId that identify the id of the 
			// document in the repository 
			NodeList nodeList = asElement.getElementsByTagName("ExternalIdentifier"); 
			//System.out.println("The list is size "+ nodeList.getLength() ); 
			for ( int i = 0; i < nodeList.getLength(); i++ ) { 
				Node item = nodeList.item(i); 
				//System.out.println(" node "+i + ": "+ Utility.convertXMLDocumentToString(item)); 
				Element itemAsElement = (Element) item; 
				NodeList localizedString = itemAsElement.getElementsByTagName("LocalizedString"); 
				Element one = (Element) localizedString.item(0); 
				//System.out.println( "  one = "+ one + " has attr="+ one.hasAttributes() ); 
				String value = one.getAttribute("value"); 
				//System.out.println( "  attr = "+ value ); 
				if ( value.equals("XDSDocumentEntry.uniqueId") ) { 
					String uniqueId = itemAsElement.getAttribute("value"); 
					//System.out.println(" **** GOT IT, ID = "+uniqueId); 
					uniqueIdMatches.add(uniqueId); 
				} 
			} 
 
			// Now we have extracted the uniqueId's from the registry and 
			// stored them in the arraylist uniqueIdMatches, now fetch 
			// the HL7 documents from the repository 
 
			for ( String uniqueId : uniqueIdMatches) { 
				result = null; 
				try { 
					MessageBody retrieveDocumentSetMessage = ebxmlBuilder.buildRetrieveDocumentSet(uniqueId); 
					result = repositoryProxy.retriveDocumentSet(retrieveDocumentSetMessage); 
				} catch (ServiceException e1) { 
					// TODO Auto-generated catch block 
					e1.printStackTrace(); 
				} 
				//System.out.println("Result of RetriveDocumentSet transaction: " + result[0]); 
				try {//Let's do it Java style! 
					//System.out.println("Result of RetriveDocumentSet transaction Document: " + result[1]);   
					//System.out.println("Actual document extracted from XML and decoded from Base64...:"); 
					MessageElement doc = result[1]; 
 
					@SuppressWarnings("rawtypes") 
					Iterator iter = doc.getChildElements(); 
					while(iter.hasNext()) { 
						MessageElement e = (MessageElement) iter.next(); 
						if (e.getName().equals("Document")) { 
							byte[] decoded = Base64.decode(e.getValue()); 
							String theresult = new String(decoded); 
							returnValue.add(theresult); 
							//System.out.println(new String(decoded)); 
						} 
 
					} 
				} catch (ArrayIndexOutOfBoundsException e) {  
					e.printStackTrace(); 
				} 
			}  
		} else { 
			throw new Net4CareException(Constants.ERROR_INVALID_QUERYTYPE, "This type of query is not supported."); 
		} 
		return returnValue; 
	} 
 
	public void connect() throws Net4CareException { 
		// Silently ignored 
	} 
	public void disconnect() throws Net4CareException { 
		// Silently ignored 
	} 
	public void utterlyEmptyAllContentsOfTheDatabase() throws Net4CareException { 
		// Not supported by XDS - silently ignored... 
	} 
 
	/** Write a XML document to a temporary file and return a hashmap 
	 * that defines associated ID. 
	 * @param xmlDocument 
	 * @param associatedTargetObjectID 
	 * @return 
	 * @throws TransformerFactoryConfigurationError 
	 */ 
	private HashMap<FileMetaData, File> writeXML2TemporaryFile(Document xmlDocument, 
			final String associatedTargetObjectID) 
					throws TransformerFactoryConfigurationError { 
 
		HashMap<FileMetaData, File> attachments = new HashMap<FileMetaData, File> (); 
 
		// TODO: 2) Fix a smarter directory to store things 
		String separator = System.getProperty("file.separator"); 
		String filename = System.getProperty("java.io.tmpdir")+ separator + "tmp_xml.txt"; 
		// Prepare the output file 
		File xmlDocumentFile = new File(filename); 
 
		// http://www.exampledepot.com/egs/javax.xml.transform/WriteDom.html 
		try { 
			// Prepare the DOM document for writing 
			Source source = new DOMSource(xmlDocument); 
 
			Result result = new StreamResult(xmlDocumentFile); 
 
			// Write the DOM document to the file 
			Transformer xformer = TransformerFactory.newInstance().newTransformer(); 
			xformer.transform(source, result); 
		} catch (TransformerConfigurationException e) { 
		} catch (TransformerException e) { 
		} 
		// associate the file with the attachements 
		attachments.put(new FileMetaData("text/xml", associatedTargetObjectID), xmlDocumentFile); 
		return attachments; 
	} 
 
	public List<Document> retrieveDocumentSet(XDSQuery query) 
			throws Net4CareException { 
		List<String> xmlset; 
		List<Document> docset = new ArrayList<Document>(); 
		xmlset = retrieveDocumentSetAsXMLString(query); 
		for( String xml: xmlset ) {  
			docset.add( Utility.convertXMLStringToDocument(xml)); 
		} 
		return docset; 
	} 
} 
