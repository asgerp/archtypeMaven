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
 * DocumentRepositoryServiceLocator.java 
 * 
 * This file was auto-generated from WSDL 
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter. 
 *  
 * MODIFICATIONS MADE (by HBC) TO ALLOW DYNAMIC CHANGES TO ADDRESS 
 */ 
 
package org.tempuri; 
 
import org.net4care.locator.HostSpecification; 
 
 
public class DocumentRepositoryServiceLocator extends org.apache.axis.client.Service implements org.tempuri.DocumentRepositoryService { 
 
    public DocumentRepositoryServiceLocator() { 
    } 
 
 
    public DocumentRepositoryServiceLocator(org.apache.axis.EngineConfiguration config) { 
        super(config); 
    } 
 
    public DocumentRepositoryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException { 
        super(wsdlLoc, sName); 
    } 
 
    // Use to get a proxy class for XDSRepository_HTTP_Endpoint 
    //    private java.lang.String XDSRepository_HTTP_Endpoint_address = "http://a02447:1026/XdsService/XDSRepository"; 
    // Test with membrane move the port to 2000 
 
    public java.lang.String getXDSRepository_HTTP_EndpointAddress() { 
      String XDSRepository_HTTP_Endpoint_address =  
          "http://" + HostSpecification.getHostAddress() + ":" + HostSpecification.getRepositoryProxyPort() + 
          "/XdsService/XDSRepository"; 
      return XDSRepository_HTTP_Endpoint_address; 
    } 
 
    // The WSDD service name defaults to the port name. 
    private java.lang.String XDSRepository_HTTP_EndpointWSDDServiceName = "XDSRepository_HTTP_Endpoint"; 
 
    public java.lang.String getXDSRepository_HTTP_EndpointWSDDServiceName() { 
        return XDSRepository_HTTP_EndpointWSDDServiceName; 
    } 
 
    public void setXDSRepository_HTTP_EndpointWSDDServiceName(java.lang.String name) { 
        XDSRepository_HTTP_EndpointWSDDServiceName = name; 
    } 
 
    public _2007.xds_b.iti.ihe.XDSRepository getXDSRepository_HTTP_Endpoint() throws javax.xml.rpc.ServiceException { 
       java.net.URL endpoint; 
        try { 
            endpoint = new java.net.URL(getXDSRepository_HTTP_EndpointAddress()); 
        } 
        catch (java.net.MalformedURLException e) { 
            throw new javax.xml.rpc.ServiceException(e); 
        } 
        return getXDSRepository_HTTP_Endpoint(endpoint); 
    } 
 
    public _2007.xds_b.iti.ihe.XDSRepository getXDSRepository_HTTP_Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException { 
        try { 
            org.tempuri.XDSRepository_HTTP_EndpointStub _stub = new org.tempuri.XDSRepository_HTTP_EndpointStub(portAddress, this); 
            _stub.setPortName(getXDSRepository_HTTP_EndpointWSDDServiceName()); 
            return _stub; 
        } 
        catch (org.apache.axis.AxisFault e) { 
            return null; 
        } 
    } 
 
    public void setXDSRepository_HTTP_EndpointEndpointAddress(java.lang.String address) { 
      throw new RuntimeException("Setting the address has been disabled..."); 
        //XDSRepository_HTTP_Endpoint_address = address; 
    } 
 
    /** 
     * For the given interface, get the stub implementation. 
     * If this service has no port for the given interface, 
     * then ServiceException is thrown. 
     */ 
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException { 
        try { 
            if (_2007.xds_b.iti.ihe.XDSRepository.class.isAssignableFrom(serviceEndpointInterface)) { 
                org.tempuri.XDSRepository_HTTP_EndpointStub _stub =  
                    new org.tempuri.XDSRepository_HTTP_EndpointStub(new java.net.URL(getXDSRepository_HTTP_EndpointAddress()), this); 
                _stub.setPortName(getXDSRepository_HTTP_EndpointWSDDServiceName()); 
                return _stub; 
            } 
        } 
        catch (java.lang.Throwable t) { 
            throw new javax.xml.rpc.ServiceException(t); 
        } 
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName())); 
    } 
 
    /** 
     * For the given interface, get the stub implementation. 
     * If this service has no port for the given interface, 
     * then ServiceException is thrown. 
     */ 
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException { 
        if (portName == null) { 
            return getPort(serviceEndpointInterface); 
        } 
        java.lang.String inputPortName = portName.getLocalPart(); 
        if ("XDSRepository_HTTP_Endpoint".equals(inputPortName)) { 
            return getXDSRepository_HTTP_Endpoint(); 
        } 
        else  { 
            java.rmi.Remote _stub = getPort(serviceEndpointInterface); 
            ((org.apache.axis.client.Stub) _stub).setPortName(portName); 
            return _stub; 
        } 
    } 
 
    public javax.xml.namespace.QName getServiceName() { 
        return new javax.xml.namespace.QName("http://tempuri.org/", "DocumentRepositoryService"); 
    } 
 
    private java.util.HashSet ports = null; 
 
    public java.util.Iterator getPorts() { 
        if (ports == null) { 
            ports = new java.util.HashSet(); 
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "XDSRepository_HTTP_Endpoint")); 
        } 
        return ports.iterator(); 
    } 
 
    /** 
    * Set the endpoint address for the specified port name. 
    */ 
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException { 
         
      if ("XDSRepository_HTTP_Endpoint".equals(portName)) { 
        setXDSRepository_HTTP_EndpointEndpointAddress(address); 
      } 
      else  
      { // Unknown Port Name 
        throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName); 
      } 
    } 
 
    /** 
    * Set the endpoint address for the specified port name. 
    */ 
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException { 
        setEndpointAddress(portName.getLocalPart(), address); 
    } 
 
} 
