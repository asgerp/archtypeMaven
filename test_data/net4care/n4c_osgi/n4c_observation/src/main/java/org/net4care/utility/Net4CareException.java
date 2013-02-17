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
 
package org.net4care.utility;

public class Net4CareException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final String message;
  private final String errorCode;
  
  /**
   * 
   * @param errorCode  A Net4Care errorcode that classify the exception.
   * @param message    A human readable errormessage.
   */
  public Net4CareException(String errorCode,String message) {
    this.message = message;
    this.errorCode = errorCode;
  }

  @Override
  public String toString() {
    if(message == null)
      return errorCode + "";
    return errorCode + " - " + message;
  }
  
  /**
   * 
   * @return  The associated Net4Care error code for the exception.
   */
  public String getErrorCode() {
	  return errorCode;
  }
  
}
