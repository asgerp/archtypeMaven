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
 
package org.net4care.xdsproxy.delegate; 
 
import java.io.File; 
import java.rmi.RemoteException; 
import java.util.HashMap; 
 
import org.apache.axis.message.*; 
import org.net4care.xdsproxy.*; 
import org.tempuri.DocumentRepositoryServiceLocator; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
import _2007.xds_b.iti.ihe.XDSRepository; 
 
public class XDSRepositoryWrapperImpl implements XDSRepositoryWrapper { 
 
  private DocumentRepositoryServiceLocator repository_locator; 
 
  private XDSRepository repository_stub; 
   
  public XDSRepositoryWrapperImpl() { 
    repository_locator = new DocumentRepositoryServiceLocator(); 
  } 
 
  public void defineHTTPEndpoint() throws javax.xml.rpc.ServiceException { 
    repository_stub = repository_locator.getXDSRepository_HTTP_Endpoint(); 
  } 
 
  public void setHeader(String namespace, String partName, String headerValue) { 
    ((org.apache.axis.client.Stub) repository_stub).setHeader(namespace, partName, headerValue);   
    // Tweak the headers 
    // Thanks to Ricardo Fernandes - whoever he is - for this little piece of magic!  
    // (http://osdir.com/ml/axis-user-ws.apache.org/2009-11/msg00163.html)  
    SOAPHeaderElement[] headers = ((org.apache.axis.client.Stub) repository_stub).getHeaders(); 
    for (SOAPHeaderElement h : headers) { 
      h.setRole(null);     
    } 
  } 
 
  public MessageBody retrieveDocumentSet(MessageBody retriveDocument) throws java.rmi.RemoteException { 
    MessageBody result = repository_stub.retrieveDocumentSet(retriveDocument); 
    //System.out.println("Result = " + result); 
    return result; 
  } 
 
  public MessageBody provideAndRegisterDocumentSet( 
      SOAPBodyElement provideAndRegister, 
      HashMap<FileMetaData, File> documentAttachments) throws RemoteException { 
    MessageBody result = repository_stub.provideAndRegisterDocumentSet(provideAndRegister, documentAttachments); 
    return result; 
  } 
 
} 
