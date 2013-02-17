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
 
import java.io.*; 
import java.rmi.RemoteException; 
import java.util.ArrayList; 
import java.util.Calendar; 
import java.util.HashMap; 
import java.util.List; 
 
import javax.xml.namespace.QName; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
import javax.xml.rpc.ServiceException; 
import javax.xml.rpc.handler.HandlerInfo; 
import javax.xml.rpc.handler.HandlerRegistry; 
import javax.xml.soap.SOAPException; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
 
import org.apache.axis.message.MessageElement; 
import org.apache.axis.message.SOAPBodyElement; 
import org.apache.axis.message.SOAPHeaderElement; 
import org.apache.axis.utils.XMLUtils; 
import org.net4care.locator.HostSpecification; 
import org.net4care.xdsproxy.delegate.XDSRepositoryWrapperImpl; 
import org.tempuri.DocumentRepositoryServiceLocator; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.xml.sax.SAXException; 
 
import _2007.xds_b.iti.ihe.XDSRepository; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
/** A Java proxy that accepts raw XML messages, formatted 
 * as SOAP ebXML or Microsoft MessageBody according to 
 * the ebXML formatting as outlined by XDS.b technical 
 * framework report. 
 *  
 * Use the EbXMLBuilder class to generate the 
 * necessary XML documents from the internal 
 * Net4Care datastructures. 
 *  
 * Do not use this class directly in Net4Care, 
 * instead use the MSCodeplexXDSProxy that 
 * combines the XML building and the actual 
 * calls to XDS. 
 *  
 * Clean Code that Works. Mark Surrow made it work, 
 * Henrik Baerbak cleaned it up as best he could. 
 *  
 * @author Mark Surrow and Henrik Baerbak Christensen 
 * 
 */ 
 
public class XDSRepositoryProxy { 
	private XDSRepositoryWrapper repository_wrapper; 
	 
	public XDSRepositoryProxy() throws ServiceException { 
	  repository_wrapper = new XDSRepositoryWrapperImpl(); 
	} 
	 
  public MessageElement[] retriveDocumentSet(MessageBody retrieveDocument) throws ServiceException { 
  
		repository_wrapper.defineHTTPEndpoint(); 
    repository_wrapper.setHeader("http://www.w3.org/2005/08/addressing",  
        "To", "http://"+HostSpecification.getHostAddress()+":"+HostSpecification.getRepositoryPort()+ 
        "/XdsService/XDSRepository"); 
    repository_wrapper.setHeader("http://www.w3.org/2005/08/addressing", "Action", "urn:ihe:iti:2007:RetrieveDocumentSet");    
		 
		try {		 
      MessageBody result = repository_wrapper.retrieveDocumentSet(retrieveDocument); 
			return result.get_any(); 
		} catch (RemoteException e) {	 
			e.printStackTrace(); 
			return null; 
		}			 
	} 
 
	public MessageElement[] provideAndRegisterDocumentSet(SOAPBodyElement ebXMLCodedProvideAndRegisterMessage,  
	      HashMap<FileMetaData, File> documentAttachments)  
	          throws ServiceException, ParserConfigurationException, SAXException, IOException { 
 
		repository_wrapper.defineHTTPEndpoint(); 
    // TODO: hard binding to a02447 !!! 
    repository_wrapper.setHeader("http://www.w3.org/2005/08/addressing",  
        "To", "http://" + HostSpecification.getHostAddress() + ":" + HostSpecification.getRepositoryPort()+ 
        		"/XdsService/XDSRepository"); 
    repository_wrapper.setHeader("http://www.w3.org/2005/08/addressing", "Action", "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b"); 
   
 
		/////////// Reenable to add debugging info (? left over from Marks code) 
		/* 
		HandlerRegistry registry = repository_locator.getHandlerRegistry(); 
		List<HandlerInfo> handlerList = new ArrayList<HandlerInfo>(); 
		handlerList.add(new HandlerInfo(ActionHandler.class, null, null)); 
		QName port = (QName) repository_locator.getPorts().next(); 
		registry.setHandlerChain(port, handlerList); 
		 */   
		 
		// Finally, transmit the package over the wire... 
		try {		 
      MessageBody result =  
          repository_wrapper.provideAndRegisterDocumentSet(ebXMLCodedProvideAndRegisterMessage,  
              documentAttachments); 
			return result.get_any(); 
		} catch (RemoteException e) {	 
			e.printStackTrace(); 
			return null; 
		}		 
	} 
 
} 
