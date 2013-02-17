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
 
import java.net.InetAddress; 
import java.net.UnknownHostException; 
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
import java.rmi.server.UnicastRemoteObject; 
 
import org.apache.log4j.Logger; 
import org.net4care.receiver.delegate.StandardHttpServlet; 
 
import com.apkc.archtype.quals.*;
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
          ) 
public class RMIServer implements RMIInterface { 
 
	private Logger logger = Logger.getLogger(RMIServer.class); 
	private int thisPort = 20016; 
	private StandardHttpServlet httpservlet = null; 
 
	public RMIServer(StandardHttpServlet httpservlet) { 
		this.httpservlet = httpservlet; 
		try { 
			logger.info((InetAddress.getLocalHost()).toString()); 
			UnicastRemoteObject.exportObject(this, thisPort); 
			Registry registry = LocateRegistry.createRegistry(thisPort); 
			registry.rebind("StandardHttpServlet", this); 
			logger.info("Exported RMI Object"); 
		} catch (RemoteException e) { 
			logger.error("Error in exporting UnicastRemoteObject", e); 
		}  
		catch (UnknownHostException e) { 
//			 TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
 
	} 
 
	public void killSwitch() throws RemoteException { 
		httpservlet.kill(); 
	} 
} 
