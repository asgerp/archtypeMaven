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
 
package org.net4care.locator; 
 
/** Specification of host name and ports. 
 *  
 * Refactoring to a run-time binding pending :) 
 *  
 * @author Henrik Baerbak Christensen, Net4Care, AU 
 * 
 */ 
public class HostSpecification { 
  private static String hostAddress = "n4cxds.nfit.au.dk"; 
  private static String registryPortNumber = "1025"; 
  private static String repositoryPortNumber = "1026"; 
 
  // Proxy ports - use membrane and rules to 
  // map from these ports to the corresponding 
  // 'real' port. 
  private static String PORT_REGISTRY_TEST = "1999"; 
  private static String PORT_REPOSITORY_TEST = "2000"; 
 
  public static void setHost(String newHostAddress) { 
    hostAddress = newHostAddress; 
  } 
  public static String getHostAddress() { 
    return hostAddress; 
  } 
  public static String getRegistryPort() { 
    return registryPortNumber; 
  } 
  public static String getRepositoryPort() { 
    return repositoryPortNumber; 
  } 
   
  public static String getRegistryProxyPort() { 
    return PORT_REGISTRY_TEST; 
  } 
  public static String getRepositoryProxyPort() { 
    return PORT_REPOSITORY_TEST; 
  } 
   
  public static void setRegistryProxyPort(String registryProxyPort) { 
    PORT_REGISTRY_TEST = registryProxyPort; 
  } 
  public static void setRepositoryProxyPort(String repositoryProxyPort) { 
    PORT_REPOSITORY_TEST = repositoryProxyPort; 
  } 
} 
