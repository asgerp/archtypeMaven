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
 
package org.net4care.storage; 
 
/** Data for a person: address, telephone, gender, birthtime, etc. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public class PersonData { 
 
  private String CPR; 
  private String given; 
  private String family; 
  private String gender; 
  private String birthTime; 
   
  private AddressData addr; 
   
  private String telecom; 
   
  public PersonData(String cpr, String given, String family, 
      String gender, String birthTime, String telecom, 
      AddressData addr ) { 
    CPR = cpr; 
    this.given = given; 
    this.family = family; 
    this.gender = gender; 
    this.birthTime = birthTime; 
    this.telecom = telecom; 
    this.addr = addr; 
  } 
   
  public String getCPR() { 
    return CPR; 
  } 
 
  public String getGiven() { 
    return given; 
  } 
 
  public String getFamily() { 
    return family; 
  } 
 
  public String getGender() { 
    return gender; 
  } 
 
  public String getBirthTime() { 
    return birthTime; 
  } 
 
  public String getStreet() { 
    return addr.getStreet(); 
  } 
 
  public String getPostalCode() { 
    return addr.getPostalCode(); 
  } 
 
  public String getCity() { 
    return addr.getCity(); 
  } 
 
  public AddressData getAddr() { 
    return addr; 
  } 
 
  public String getTelecom() { 
    return telecom; 
  } 
 
  public String getCountry() { 
    return addr.getCountry(); 
  } 
} 
