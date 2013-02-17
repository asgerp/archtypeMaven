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
 * DocumentRegistryServiceLocator.java 
 * 
 * MODIFIED by HBC to parameterize host and port number! 
 *  
 * This file was auto-generated from WSDL 
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter. 
 */ 
 
package org.tempuri; 
 
import javax.management.RuntimeErrorException; 
 
import org.net4care.locator.HostSpecification; 
 
 
public class DocumentRegistryServiceLocator extends org.apache.axis.client.Service implements org.tempuri.DocumentRegistryService { 
 
    public DocumentRegistryServiceLocator() { 
    } 
 
 
    public DocumentRegistryServiceLocator(org.apache.axis.EngineConfiguration config) { 
        super(config); 
    } 
 
    public DocumentRegistryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException { 
        super(wsdlLoc, sName); 
    } 
 
    // Use to get a proxy class for XDSRegistry_HTTP_Endpoint                          
    public java.lang.String getXDSRegistry_HTTP_EndpointAddress() { 
      String XDSRegistry_HTTP_Endpoint_address =  
          "http://" + HostSpecification.getHostAddress() + ":" + HostSpecification.getRegistryProxyPort() + 
          "/XdsService/XDSRegistry"; 
      return XDSRegistry_HTTP_Endpoint_address; 
    } 
 
    // The WSDD service name defaults to the port name. 
    private java.lang.String XDSRegistry_HTTP_EndpointWSDDServiceName = "XDSRegistry_HTTP_Endpoint"; 
 
    public java.lang.String getXDSRegistry_HTTP_EndpointWSDDServiceName() { 
        return XDSRegistry_HTTP_EndpointWSDDServiceName; 
    } 
 
    public void setXDSRegistry_HTTP_EndpointWSDDServiceName(java.lang.String name) { 
        XDSRegistry_HTTP_EndpointWSDDServiceName = name; 
    } 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTP_Endpoint() throws javax.xml.rpc.ServiceException { 
       java.net.URL endpoint; 
        try { 
            endpoint = new java.net.URL(getXDSRegistry_HTTP_EndpointAddress()); 
        } 
        catch (java.net.MalformedURLException e) { 
            throw new javax.xml.rpc.ServiceException(e); 
        } 
        return getXDSRegistry_HTTP_Endpoint(endpoint); 
    } 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTP_Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException { 
        try { 
            org.tempuri.XDSRegistry_HTTP_EndpointStub _stub = new org.tempuri.XDSRegistry_HTTP_EndpointStub(portAddress, this); 
            _stub.setPortName(getXDSRegistry_HTTP_EndpointWSDDServiceName()); 
            return _stub; 
        } 
        catch (org.apache.axis.AxisFault e) { 
            return null; 
        } 
    } 
 
    public void setXDSRegistry_HTTP_EndpointEndpointAddress(java.lang.String address) { 
      throw new RuntimeException("Method has been disabled"); 
      // XDSRegistry_HTTP_Endpoint_address = address; 
    } 
 
 
    // Use to get a proxy class for XDSRegistry_HTTPS_Endpoint 
    private java.lang.String XDSRegistry_HTTPS_Endpoint_address =  
        "http://" + HostSpecification.getHostAddress() + ":" + HostSpecification.getRegistryPort() + 
    		"/XdsService/XDSRegistry"; 
 
    public java.lang.String getXDSRegistry_HTTPS_EndpointAddress() { 
        return XDSRegistry_HTTPS_Endpoint_address; 
    } 
 
    // The WSDD service name defaults to the port name. 
    private java.lang.String XDSRegistry_HTTPS_EndpointWSDDServiceName = "XDSRegistry_HTTPS_Endpoint"; 
 
    public java.lang.String getXDSRegistry_HTTPS_EndpointWSDDServiceName() { 
        return XDSRegistry_HTTPS_EndpointWSDDServiceName; 
    } 
 
    public void setXDSRegistry_HTTPS_EndpointWSDDServiceName(java.lang.String name) { 
        XDSRegistry_HTTPS_EndpointWSDDServiceName = name; 
    } 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTPS_Endpoint() throws javax.xml.rpc.ServiceException { 
       java.net.URL endpoint; 
        try { 
            endpoint = new java.net.URL(XDSRegistry_HTTPS_Endpoint_address); 
        } 
        catch (java.net.MalformedURLException e) { 
            throw new javax.xml.rpc.ServiceException(e); 
        } 
        return getXDSRegistry_HTTPS_Endpoint(endpoint); 
    } 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTPS_Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException { 
        try { 
            org.tempuri.XDSRegistry_HTTPS_EndpointStub _stub = new org.tempuri.XDSRegistry_HTTPS_EndpointStub(portAddress, this); 
            _stub.setPortName(getXDSRegistry_HTTPS_EndpointWSDDServiceName()); 
            return _stub; 
        } 
        catch (org.apache.axis.AxisFault e) { 
            return null; 
        } 
    } 
 
    public void setXDSRegistry_HTTPS_EndpointEndpointAddress(java.lang.String address) { 
        XDSRegistry_HTTPS_Endpoint_address = address; 
    } 
 
    /** 
     * For the given interface, get the stub implementation. 
     * If this service has no port for the given interface, 
     * then ServiceException is thrown. 
     * This service has multiple ports for a given interface; 
     * the proxy implementation returned may be indeterminate. 
     */ 
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException { 
        try { 
            if (_2007.xds_b.iti.ihe.XDSRegistry.class.isAssignableFrom(serviceEndpointInterface)) { 
                org.tempuri.XDSRegistry_HTTP_EndpointStub _stub =  
                    new org.tempuri.XDSRegistry_HTTP_EndpointStub(new java.net.URL(getXDSRegistry_HTTP_EndpointAddress()), this); 
                _stub.setPortName(getXDSRegistry_HTTP_EndpointWSDDServiceName()); 
                return _stub; 
            } 
            if (_2007.xds_b.iti.ihe.XDSRegistry.class.isAssignableFrom(serviceEndpointInterface)) { 
                org.tempuri.XDSRegistry_HTTPS_EndpointStub _stub = new org.tempuri.XDSRegistry_HTTPS_EndpointStub(new java.net.URL(XDSRegistry_HTTPS_Endpoint_address), this); 
                _stub.setPortName(getXDSRegistry_HTTPS_EndpointWSDDServiceName()); 
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
        if ("XDSRegistry_HTTP_Endpoint".equals(inputPortName)) { 
            return getXDSRegistry_HTTP_Endpoint(); 
        } 
        else if ("XDSRegistry_HTTPS_Endpoint".equals(inputPortName)) { 
            return getXDSRegistry_HTTPS_Endpoint(); 
        } 
        else  { 
            java.rmi.Remote _stub = getPort(serviceEndpointInterface); 
            ((org.apache.axis.client.Stub) _stub).setPortName(portName); 
            return _stub; 
        } 
    } 
 
    public javax.xml.namespace.QName getServiceName() { 
        return new javax.xml.namespace.QName("http://tempuri.org/", "DocumentRegistryService"); 
    } 
 
    private java.util.HashSet ports = null; 
 
    public java.util.Iterator getPorts() { 
        if (ports == null) { 
            ports = new java.util.HashSet(); 
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "XDSRegistry_HTTP_Endpoint")); 
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "XDSRegistry_HTTPS_Endpoint")); 
        } 
        return ports.iterator(); 
    } 
 
    /** 
    * Set the endpoint address for the specified port name. 
    */ 
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException { 
         
if ("XDSRegistry_HTTP_Endpoint".equals(portName)) { 
            setXDSRegistry_HTTP_EndpointEndpointAddress(address); 
        } 
        else  
if ("XDSRegistry_HTTPS_Endpoint".equals(portName)) { 
            setXDSRegistry_HTTPS_EndpointEndpointAddress(address); 
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
