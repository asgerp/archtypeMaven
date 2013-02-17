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
 
/** 
 * XDSRepository.java 
 * 
 * This file was auto-generated from WSDL 
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter. 
 */ 
 
package _2007.xds_b.iti.ihe; 
 
import java.io.File; 
import java.util.HashMap; 
 
import org.apache.axis.message.SOAPBodyElement; 
import org.net4care.xdsproxy.FileMetaData; 
import org.net4care.xdsproxy.RegistryMetaData; 
 
 
public interface XDSRepository extends java.rmi.Remote { 
    public com.microsoft.schemas.Message.MessageBody provideAndRegisterDocumentSet(SOAPBodyElement SOAPbodyElem, HashMap<FileMetaData, File> documentAttachments) throws java.rmi.RemoteException; 
    public com.microsoft.schemas.Message.MessageBody retrieveDocumentSet(com.microsoft.schemas.Message.MessageBody input) throws java.rmi.RemoteException; 
} 
