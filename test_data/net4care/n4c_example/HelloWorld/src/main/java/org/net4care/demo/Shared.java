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
 
package org.net4care.demo; 
 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.*; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
 
import java.net.*; 
 
/** Some shared functions for the demo. 
 * 
 * @author Net4Care, Henrik Baerbak Christensen, Aarhus University 
 */ 
 
public class Shared { 
  /** Setup a configuration of the Net4Care architecture */ 
  public static DataUploader setupN4CConfiguration(String serverAddress) { 
    DataUploader dataUploader = null; 
    Serializer serializer = new JacksonJSONSerializer();     
    try { 
      dataUploader =  
        new StandardDataUploader(serializer,  
                                 new HttpConnector( serverAddress )); 
    } catch (MalformedURLException e) { 
      e.printStackTrace(); 
      System.exit(1); 
    }  
    return dataUploader; 
  } 
}