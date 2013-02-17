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
 
package org.net4care.common; 
 
/** 
 * The strategy for generating UUID. Use this instead of 
 * the standard Java classes in order for test code to 
 * replace this strategy with a test stub. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public interface UUIDStrategy { 
  public String generateUUID(); 
} 
