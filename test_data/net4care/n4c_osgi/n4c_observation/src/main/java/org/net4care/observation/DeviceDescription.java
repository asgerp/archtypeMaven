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
 
package org.net4care.observation; 
 
/** A description of a device.  
 * Inspired by the properties mentioned in the PHMR document. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
import com.apkc.archtype.quals.*;
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
          ) 

public class DeviceDescription { 
  /** Construct a description of a device. (Most of 
   * these parameters are deduced from the PHMR Draft 
   * section 3.3.1 "Medical Equipment".) 
   * @param type type of apparatus (what does it measure) 
   * @param model the model name 
   * @param manufacturer the organization manufacturing the device 
   * @param serialId serial id of this device 
   * @param partNumber part id if any 
   * @param hardwareRevision revision of the hardware 
   * @param softwareRevision revision of the software 
   */ 
  public DeviceDescription(String type, String model, String manufacturer, 
      String serialId, String partNumber, String hardwareRevision, 
      String softwareRevision) { 
    super(); 
    this.type = type; 
    this.model = model; 
    this.manufacturer = manufacturer; 
    this.serialId = serialId; 
    this.partNumber = partNumber; 
    this.hardwareRevision = hardwareRevision; 
    this.softwareRevision = softwareRevision; 
  } 
   
  /** to have bean properties */ 
  DeviceDescription() {} 
   
  public String getType() { 
    return type; 
  } 
  public String getModel() { 
    return model; 
  } 
  public String getManufacturer() { 
    return manufacturer; 
  } 
  public String getSerialId() { 
    return serialId; 
  } 
  public String getPartNumber() { 
    return partNumber; 
  } 
  public String getHardwareRevision() { 
    return hardwareRevision; 
  } 
  public String getSoftwareRevision() { 
    return softwareRevision; 
  } 
  private String type; 
  private String model; 
  private String manufacturer; 
  private String serialId; 
  private String partNumber; 
  private String hardwareRevision; 
  private String softwareRevision; 
 
  public String toString() { 
    return "Device: "+getManufacturer()+"/"+getModel()+"/"+getSerialId(); 
  } 
} 
