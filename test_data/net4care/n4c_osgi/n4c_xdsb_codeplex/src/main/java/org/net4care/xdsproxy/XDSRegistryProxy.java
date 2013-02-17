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
 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.rmi.RemoteException; 
 
import javax.xml.parsers.ParserConfigurationException; 
import javax.xml.rpc.ServiceException; 
 
import org.apache.axis.message.MessageElement; 
import org.apache.axis.message.SOAPBodyElement; 
import org.apache.axis.message.SOAPHeaderElement; 
import org.apache.axis.utils.XMLUtils; 
import org.net4care.locator.HostSpecification; 
import org.tempuri.DocumentRegistryServiceLocator; 
import org.xml.sax.SAXException; 
 
import _2007.xds_b.iti.ihe.XDSRegistry; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
public class XDSRegistryProxy { 
	 
	private XDSRegistry registry_stub; 
	private DocumentRegistryServiceLocator registry_locator; 
	 
	/** 
	 * Method for easy access to the XDS.b registry "Registry Stored Query" transaction. A predefined set of registry queries  
	 * are stored in the registry and have corresponding unique IDs. The XML file contains the uniqueID of the transaction and other meta-data. 
	 *  
	 * TODO: Will probably only work on LAN 
	 * @param storedRequestXMLFile A Java File object containing the XML file describing the XDS.b RegistryStoredQuery.  
	 * Example: File paramFile = new File(System.getProperty("user.dir")+"\\Files\\StoredQueryGetDocumentRequest.xml"); 
	 * @return A org.apache.axis.message.MessageElement[] containing the result of the query.  
	 * If all went well, the result document(s) will be in [0]. 
	 * @throws ServiceException  
	 * @throws IOException  
	 * @throws SAXException  
	 * @throws ParserConfigurationException  
	 */ 
	public MessageElement[] registryStoredQuery(StoredQuery query) throws ServiceException, ParserConfigurationException, SAXException, IOException { 
		registry_locator = new DocumentRegistryServiceLocator(); 
		registry_stub = registry_locator.getXDSRegistry_HTTP_Endpoint(); 
		 
		SOAPBodyElement[] msgElements = new SOAPBodyElement[1]; 
		 
		FileInputStream fis = new FileInputStream(query.getRequestXMLFile()); 
		msgElements[0] = new SOAPBodyElement(XMLUtils.newDocument(fis).getDocumentElement()); 
		MessageBody message = new MessageBody(); 
		message.set_any(msgElements); 
		 
 
		((org.apache.axis.client.Stub) registry_stub).setHeader("http://www.w3.org/2005/08/addressing",  
		    "To", "http://"+HostSpecification.getHostAddress()+":"+HostSpecification.getRegistryPort()+"/XdsService/XDSRegistry"); 
 
		((org.apache.axis.client.Stub) registry_stub).setHeader("http://www.w3.org/2005/08/addressing", "Action", "urn:ihe:iti:2007:RegistryStoredQuery"); 
 
		SOAPHeaderElement[] headers = ((org.apache.axis.client.Stub) registry_stub).getHeaders(); 
 
		//Thanks to Ricardo Fernandes - whoever he is - for this little piece of magic! (http://osdir.com/ml/axis-user-ws.apache.org/2009-11/msg00163.html)  
		for (SOAPHeaderElement h : headers) { 
		      h.setRole(null); 
		} 
		 
		try { 
			MessageBody result = registry_stub.registryStoredQuery(message); 
			return result.get_any(); 
		} catch (RemoteException e) {	 
			e.printStackTrace(); 
			return null; 
		} 
	} 
} 
