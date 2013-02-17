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
 * XDSRepository_HTTP_EndpointStub.java 
 * 
 * This file was auto-generated from WSDL 
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter. 
 */ 
 
package org.tempuri; 
 
import java.io.File; 
import java.io.UnsupportedEncodingException; 
import java.net.URLEncoder; 
import java.util.HashMap; 
 
import javax.activation.DataHandler; 
import javax.activation.FileDataSource; 
import javax.xml.soap.SOAPException; 
 
import org.apache.axis.attachments.AttachmentPart; 
import org.apache.axis.message.MessageElement; 
import org.apache.axis.message.SOAPBodyElement; 
import org.net4care.xdsproxy.FileMetaData; 
import org.w3c.dom.DOMException; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
public class XDSRepository_HTTP_EndpointStub extends org.apache.axis.client.Stub implements _2007.xds_b.iti.ihe.XDSRepository { 
	private java.util.Vector cachedSerClasses = new java.util.Vector(); 
	private java.util.Vector cachedSerQNames = new java.util.Vector(); 
	private java.util.Vector cachedSerFactories = new java.util.Vector(); 
	private java.util.Vector cachedDeserFactories = new java.util.Vector(); 
 
	static org.apache.axis.description.OperationDesc [] _operations; 
 
	static { 
		_operations = new org.apache.axis.description.OperationDesc[2]; 
		_initOperationDesc1(); 
	}
 
	private static void _initOperationDesc1(){ 
		org.apache.axis.description.OperationDesc oper; 
		org.apache.axis.description.ParameterDesc param; 
		oper = new org.apache.axis.description.OperationDesc(); 
		oper.setName("ProvideAndRegisterDocumentSet"); 
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "input"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.microsoft.com/Message", "MessageBody"), com.microsoft.schemas.Message.MessageBody.class, false, false); 
		oper.addParameter(param); 
		oper.setReturnType(new javax.xml.namespace.QName("http://schemas.microsoft.com/Message", "MessageBody")); 
		oper.setReturnClass(com.microsoft.schemas.Message.MessageBody.class); 
		oper.setReturnQName(new javax.xml.namespace.QName("", "ProvideAndRegisterDocumentSetResult")); 
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT); 
		oper.setUse(org.apache.axis.constants.Use.LITERAL); 
		_operations[0] = oper; 
 
		oper = new org.apache.axis.description.OperationDesc(); 
		oper.setName("RetrieveDocumentSet"); 
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "input"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.microsoft.com/Message", "MessageBody"), com.microsoft.schemas.Message.MessageBody.class, false, false); 
		oper.addParameter(param); 
		oper.setReturnType(new javax.xml.namespace.QName("http://schemas.microsoft.com/Message", "MessageBody")); 
		oper.setReturnClass(com.microsoft.schemas.Message.MessageBody.class); 
		oper.setReturnQName(new javax.xml.namespace.QName("", "RetrieveDocumentSetResult")); 
		oper.setStyle(org.apache.axis.constants.Style.DOCUMENT); 
		oper.setUse(org.apache.axis.constants.Use.LITERAL); 
		_operations[1] = oper; 
 
	} 
 
	public XDSRepository_HTTP_EndpointStub() throws org.apache.axis.AxisFault { 
		this(null); 
	} 
 
	public XDSRepository_HTTP_EndpointStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault { 
		this(service); 
		super.cachedEndpoint = endpointURL; 
	} 
 
	public XDSRepository_HTTP_EndpointStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault { 
		if (service == null) { 
			super.service = new org.apache.axis.client.Service(); 
		} else { 
			super.service = service; 
		} 
		((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2"); 
		java.lang.Class cls; 
		javax.xml.namespace.QName qName; 
		javax.xml.namespace.QName qName2; 
		java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class; 
		java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class; 
		java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class; 
		java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class; 
		java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class; 
		java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class; 
		java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class; 
		java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class; 
		java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class; 
		java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class; 
		qName = new javax.xml.namespace.QName("http://schemas.microsoft.com/Message", "MessageBody"); 
		cachedSerQNames.add(qName); 
		cls = com.microsoft.schemas.Message.MessageBody.class; 
		cachedSerClasses.add(cls); 
		cachedSerFactories.add(beansf); 
		cachedDeserFactories.add(beandf); 
 
	} 
 
	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException { 
		try { 
			org.apache.axis.client.Call _call = super._createCall(); 
			if (super.maintainSessionSet) { 
				_call.setMaintainSession(super.maintainSession); 
			} 
			if (super.cachedUsername != null) { 
				_call.setUsername(super.cachedUsername); 
			} 
			if (super.cachedPassword != null) { 
				_call.setPassword(super.cachedPassword); 
			} 
			if (super.cachedEndpoint != null) { 
				_call.setTargetEndpointAddress(super.cachedEndpoint); 
			} 
			if (super.cachedTimeout != null) { 
				_call.setTimeout(super.cachedTimeout); 
			} 
			if (super.cachedPortName != null) { 
				_call.setPortName(super.cachedPortName); 
			} 
			java.util.Enumeration keys = super.cachedProperties.keys(); 
			while (keys.hasMoreElements()) { 
				java.lang.String key = (java.lang.String) keys.nextElement(); 
				_call.setProperty(key, super.cachedProperties.get(key)); 
			} 
			// All the type mapping information is registered 
			// when the first call is made. 
			// The type mapping information is actually registered in 
			// the TypeMappingRegistry of the service, which 
			// is the reason why registration is only needed for the first call. 
			synchronized (this) { 
				if (firstCall()) { 
					// must set encoding style before registering serializers 
					_call.setEncodingStyle(null); 
					for (int i = 0; i < cachedSerFactories.size(); ++i) { 
						java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i); 
						javax.xml.namespace.QName qName = 
								(javax.xml.namespace.QName) cachedSerQNames.get(i); 
						java.lang.Object x = cachedSerFactories.get(i); 
						if (x instanceof Class) { 
							java.lang.Class sf = (java.lang.Class) 
									cachedSerFactories.get(i); 
							java.lang.Class df = (java.lang.Class) 
									cachedDeserFactories.get(i); 
							_call.registerTypeMapping(cls, qName, sf, df, false); 
						} 
						else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) { 
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) 
									cachedSerFactories.get(i); 
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) 
									cachedDeserFactories.get(i); 
							_call.registerTypeMapping(cls, qName, sf, df, false); 
						} 
					} 
				} 
			} 
			return _call; 
		} 
		catch (java.lang.Throwable _t) { 
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t); 
		} 
	} 
 
	public com.microsoft.schemas.Message.MessageBody provideAndRegisterDocumentSet(SOAPBodyElement SOAPbodyElem, HashMap<FileMetaData, File> documentAttachments) throws java.rmi.RemoteException { 
		if (super.cachedEndpoint == null) { 
			throw new org.apache.axis.NoEndPointException(); 
		} 
		org.apache.axis.client.Call _call = createCall(); 
 
		_call.setOperation(_operations[0]); 
		_call.setUseSOAPAction(true); 
		_call.setSOAPActionURI("urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b"); 
		_call.setEncodingStyle(null); 
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.TRUE); 
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.TRUE); 
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS); 
		_call.setOperationName(new javax.xml.namespace.QName("", "ProvideAndRegisterDocumentSet")); 
		 
		//WARNING HACKS: The following lines is added manually to the generated code!  
		//Axis cannot understand the action 'provideAndRegisterDocumentSet' as it is apparently not a standard action.  
		//Thus we are forced to disable checks for 'understandable' header-actions 
		_call.setProperty(org.apache.axis.client.Call.CHECK_MUST_UNDERSTAND, Boolean.FALSE); 
		_call.setProperty(org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT, org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT_MTOM); 
 
		setRequestHeaders(_call); 
		//setAttachments(_call); 
		 
		//Manually set attachments, so we can define content-ID and add it to the <Document> elements in the ProvideAndRegister request 
 
		for (FileMetaData meta : documentAttachments.keySet()) { 
//			System.out.println(meta.getMIMEtype()); 
			DataHandler dhandle = new DataHandler(new FileDataSource(documentAttachments.get(meta))); 
			 
			AttachmentPart a = new AttachmentPart(dhandle); 
			 
			//Add Document elements to SOAP message 
			MessageElement attach = new MessageElement(); 
			attach.setName("Document"); 
			attach.setAttribute("id", meta.getContentID()); 
 
			MessageElement attachRef = new MessageElement(); 
			attachRef.setName("Include"); 
			attachRef.setNamespaceURI("http://www.w3.org/2004/08/xop/include"); 
			attachRef.setPrefix("xop"); 
			//attachRef.setAttribute("xmlns", "http://www.w3.org/2004/08/xop/include"); 
			 
			String uri = "http://tempuri.org/"+a.getContentId(); 
			//URI uri = new URI(); 
			try { 
				attachRef.setAttribute("href", "cid:"+URLEncoder.encode(uri, "UTF-8")); 
				a.setContentId(/*URLEncoder.encode(uri, "UTF-8")*/uri); 
			} catch (DOMException e1) { 
				e1.printStackTrace(); 
			} catch (UnsupportedEncodingException e1) { 
				e1.printStackTrace(); 
			} 
						 
			//"cid:http%3A%2F%2Ftempuri.org%2F1%2F"+a.getContentId() 
			 
			_call.addAttachmentPart(a); 
 
			try { 
				attach.addChild(attachRef); 
				SOAPbodyElem.addChild(attach); 
			} catch (SOAPException e) { 
				e.printStackTrace(); 
			} 
		} 
		 
		MessageElement[] msgElements = new SOAPBodyElement[1]; 
		msgElements[0] = SOAPbodyElem; 
 
		MessageBody message = new MessageBody(); 
		 
		message.set_any(msgElements); 
		 
		try {         
			java.lang.Object _resp = _call.invoke(new java.lang.Object[] {message}); 
			 
			if (_resp instanceof java.rmi.RemoteException) { 
				throw (java.rmi.RemoteException)_resp; 
			} 
			else { 
//				try { 
//					System.out.println(_call.getResponseMessage().getSOAPBody()); 
//					System.out.println(_call.getResponseMessage().getSOAPEnvelope().getAsString()); 
//				} catch (SOAPException e) { 
//					e.printStackTrace(); 
//				} catch (Exception e) { 
//					e.printStackTrace(); 
//				} 
 
				extractAttachments(_call); 
				try { 
					return (com.microsoft.schemas.Message.MessageBody) _resp; 
				} catch (java.lang.Exception _exception) { 
					return (com.microsoft.schemas.Message.MessageBody) org.apache.axis.utils.JavaUtils.convert(_resp, com.microsoft.schemas.Message.MessageBody.class); 
				} 
			} 
		} catch (org.apache.axis.AxisFault axisFaultException) { 
			throw axisFaultException; 
		} 
	} 
 
	public com.microsoft.schemas.Message.MessageBody retrieveDocumentSet(com.microsoft.schemas.Message.MessageBody input) throws java.rmi.RemoteException { 
		if (super.cachedEndpoint == null) { 
			throw new org.apache.axis.NoEndPointException(); 
		} 
		org.apache.axis.client.Call _call = createCall(); 
		_call.setOperation(_operations[1]); 
		_call.setUseSOAPAction(true); 
		_call.setSOAPActionURI("urn:ihe:iti:2007:RetrieveDocumentSet"); 
		_call.setEncodingStyle(null); 
		_call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.TRUE); 
		_call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.TRUE); 
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP12_CONSTANTS); 
		_call.setOperationName(new javax.xml.namespace.QName("", "RetrieveDocumentSet")); 
		_call.setProperty(org.apache.axis.client.Call.CHECK_MUST_UNDERSTAND, Boolean.FALSE); 
		_call.setProperty(org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT, org.apache.axis.client.Call.ATTACHMENT_ENCAPSULATION_FORMAT_MTOM); 
		 
		setRequestHeaders(_call); 
		setAttachments(_call); 
		try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {input}); 
 
		if (_resp instanceof java.rmi.RemoteException) { 
			throw (java.rmi.RemoteException)_resp; 
		} 
		else { 
			extractAttachments(_call); 
			try { 
				return (com.microsoft.schemas.Message.MessageBody) _resp; 
			} catch (java.lang.Exception _exception) { 
				return (com.microsoft.schemas.Message.MessageBody) org.apache.axis.utils.JavaUtils.convert(_resp, com.microsoft.schemas.Message.MessageBody.class); 
			} 
		} 
		} catch (org.apache.axis.AxisFault axisFaultException) { 
			throw axisFaultException; 
		} 
	} 
 
} 
