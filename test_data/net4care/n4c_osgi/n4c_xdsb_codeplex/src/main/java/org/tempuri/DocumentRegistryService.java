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
 * DocumentRegistryService.java 
 * 
 * This file was auto-generated from WSDL 
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter. 
 */ 
 
package org.tempuri; 
 
public interface DocumentRegistryService extends javax.xml.rpc.Service { 
    public java.lang.String getXDSRegistry_HTTP_EndpointAddress(); 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTP_Endpoint() throws javax.xml.rpc.ServiceException; 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTP_Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException; 
    public java.lang.String getXDSRegistry_HTTPS_EndpointAddress(); 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTPS_Endpoint() throws javax.xml.rpc.ServiceException; 
 
    public _2007.xds_b.iti.ihe.XDSRegistry getXDSRegistry_HTTPS_Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException; 
} 
