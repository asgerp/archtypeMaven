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
 
package org.net4care.main; 
 
import java.net.URL; 
 
import javax.servlet.Servlet; 
 
import org.mortbay.jetty.Server; 
import org.mortbay.jetty.servlet.Context; 
import org.mortbay.jetty.servlet.ServletHolder; 
import org.net4care.locator.HostSpecification; 
import org.net4care.receiver.ObservationReceiver; 
import org.net4care.receiver.delegate.*; 
import org.net4care.serializer.delegate.*; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.*; 
import org.net4care.testdoubles.FakeObjectXDSAndObservationCache; 
import org.net4care.xds.MSCodeplexXDSAdapter; 
 
/** A non OSGi way of starting a N4C server. Simply 
 * run it using the mvn command line (see 'run-server.bat' 
 * in the root folder of this bundle.) 
 *  
 * You may test it using the ManualServerTest program of 
 * the n4c_test folder/bundle. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
public class ServerMain { 
   
  public final static int INMEMORY = 1; 
  public final static int SQL = 2; 
  public final static int CODEPLEX = 1; 
   
    private static String serverAddress = "http://localhost:8082"; 
    public static void main(String[] args) throws Exception { 
      int choice = INMEMORY; 
      if ( args[0].equals("sql") ) { 
        System.out.println("Server configured with SQLite as backend."); 
        choice = SQL; 
      } else if ( args[0].equals("xds") ) { 
        System.out.println("Server configured with Codeplex as backend."); 
        choice = CODEPLEX; 
      } else if ( args[0].equals("fast")){ 
        System.out.println("Server configured with In-memory as backend."); 
      } else { 
        System.out.println("Usage: run-server <type>, where type is {fast,sql,xds} determine backend type."); 
        System.exit(0); 
      } 
  
      new ServerMain(choice); 
      System.out.println("Started N4C server at "+serverAddress); 
    } 
    /** 
     */ 
    public ServerMain(int choice) throws Exception { 
       
      // Configure the N4C framework 
      org.net4care.serializer.Serializer serializer = new ServerJSONSerializer(); 
 
      XDSRepository xds = null; 
      ObservationCache obscache = null; 
       
      if ( choice == INMEMORY ) { 
        xds = new FakeObjectXDSAndObservationCache(); 
        xds.connect(); 
        obscache = (ObservationCache) xds;         
      } else if ( choice == SQL ) { 
        xds = new SQLiteXDSRepository("mytest.db"); 
        xds.connect(); 
        obscache = (ObservationCache) xds; 
      } else if ( choice == CODEPLEX ) { 
        xds = new MSCodeplexXDSAdapter(); 
        HostSpecification.setHost("n4cxds.nfit.au.dk"); 
        obscache = new FakeObjectXDSAndObservationCache(); 
      } 
       
      ObservationReceiver receiver =  
          new StandardObservationReceiver( serializer,  
              new FakeObjectExternalDataSource(), 
              xds, obscache); 
  
      // Configure the N4C servlet   
      Servlet servlet = new StandardHttpServlet(serializer,receiver); 
       
      // And configure Jetty with the servlet 
      Server server = new Server(new URL(serverAddress).getPort());     
      Context root = new Context(server, "/", Context.SESSIONS); 
       
      root.addServlet(new ServletHolder(servlet), "/*"); 
      server.start(); 
    } 
} 
