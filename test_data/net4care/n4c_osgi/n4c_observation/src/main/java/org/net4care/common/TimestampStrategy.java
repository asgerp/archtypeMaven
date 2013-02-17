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
 
/** Strategy for getting current time in milliseconds (UNIX format). 
 * Preferably use this instead of using the Java classes directly 
 * as it allows test stubs to be injected. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public interface TimestampStrategy { 
 
  public long getCurrentTimeInMillis(); 
 
} 
