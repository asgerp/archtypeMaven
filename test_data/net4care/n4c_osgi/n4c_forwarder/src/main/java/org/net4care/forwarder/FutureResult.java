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
 
package org.net4care.forwarder; 
 
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern; 
/** The result of an asynchronous upload. This is basically 
 * a future that eventually will tell whether the upload 
 * was successful or not. 
 *  
 *   Inspired by the Netty project. 
 * 
 * @author Henrik Baerbak Christensen, Aarhus University 
 * */ 
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
          ) 

public interface FutureResult { 
   /** Return true if and only if this future 
    * is complete, regardless of whether the 
    * operation was successful or failed. 
    * @return true when the transmission is done. 
    */ 
   public boolean isDone(); 
    
   /** Tell whether the transmission was uploaded successfully. 
    * @return true if and only if the I/O operation was completed successfully. */ 
   public boolean isSuccess(); 
    
   /** Waits for this future to be completed without interruption. */ 
   public void awaitUninterruptibly(); 
 
   /** A description of the upload */ 
   public String result(); 
    
} 
