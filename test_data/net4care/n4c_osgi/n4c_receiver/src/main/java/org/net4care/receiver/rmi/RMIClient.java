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
 
package org.net4care.receiver.rmi; 
 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
 
 
public class RMIClient { 
 
	public static void main(String[] args) { 
		RMIInterface rmiServer; 
		Registry registry; 
		String serverAddress = "127.0.0.1"; 
		try { 
			System.out.println("-----------------\n" + 
							   " Shutdown Server \n" + 
							   "-----------------"); 
			// get the registry 
			registry = LocateRegistry.getRegistry(serverAddress, 20016); 
			// look up the remote object 
			rmiServer = (RMIInterface) (registry.lookup("StandardHttpServlet")); 
			rmiServer.killSwitch(); 
		} catch (Exception e) { 
			// ignore exceptions caused by the remote source being shut down 
		} 
 
	} 
} 
