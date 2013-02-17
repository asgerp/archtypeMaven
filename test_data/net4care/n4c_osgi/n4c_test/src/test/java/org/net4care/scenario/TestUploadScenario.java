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
 
package org.net4care.scenario; 
 
import java.io.IOException; 
import java.net.MalformedURLException; 
import java.util.ArrayList; 
import java.util.Iterator; 
import java.util.List; 
 
import org.junit.*; 
 
import org.net4care.forwarder.delegate.HttpConnector; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.observation.ClinicalQuantity; 
import org.net4care.observation.Codes; 
import org.net4care.observation.DeviceDescription; 
import org.net4care.observation.ObservationSpecifics; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.receiver.delegate.StandardObservationReceiver; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.serializer.delegate.ServerJSONSerializer; 
import org.net4care.storage.delegate.*; 
import org.net4care.testdoubles.*; 
import org.net4care.utility.Net4CareException; 
 
/** 
 * Inherit the test cases of CommonTestCase; setup the test case to use a 
 * FakeXDS and null object observation cache. 
 *  
 * For more realistic test cases, review TestUploadAndStorage, 
 *  
 */ 
public class TestUploadScenario extends CommonTestCases { 
 
	/** 
	 * Define the N4C architecture using dependency injection, basically we 
	 * construct the client side datauploader by injecting relevant delegate 
	 * objects; and do the same with the server side. 
	 *  
	 * @throws Net4CareException 
	 */ 
	@Before 
	public void setup() throws Net4CareException { 
		// Common roles - both server and client side must of course agree 
		// upon the way StandardTeleObservations are serialized... 
		serializer = new JacksonJSONSerializer(); 
 
		// Due to the 'local method call' connector used by the 
		// client, I have to define 
		// the server side 'receiver' role first in order to pass it 
		// as reference to the client side... 
 
		// Server side and data tier roles 
		// -- use a Fake XDS 
		xds = new SpyXDS(); 
		xds.connect(); 
		// -- use a fake object implementation of the external data sources 
		externalDatasources = new FakeObjectExternalDataSource(); 
		// -- SQLiteXDSRepository also serves as ObservationCache! 
		cache = new NullObservationCache(); 
		// -- and configure the server side's receiver object with 
		// the proper delegates... 
 
		// Set the server to use the ServerJSONSerializer instead. 
		Serializer serverSerializer = new ServerJSONSerializer(); 
 
		receiver = new StandardObservationReceiver(serverSerializer, 
				externalDatasources, xds, cache); 
 
		// Client side roles 
		// -- use a connector to the server that is simply method call 
		connector = new LocalSynchroneousCallConnector(receiver); 
		// -- and configure the data uploader (forwarder) with 
		// these delegates. 
		uploader = new StandardDataUploader(serializer, connector); 
	} 
 
	@After 
	public void teardown() throws Net4CareException { 
		xds.disconnect(); 
	} 
 
 
	// Create an example spirometry observation 
	private StandardTeleObservation defineSpirometryObservation() { 
		final String org_uid = "org.net4care.com.smb"; 
 
		DeviceDescription device = new DeviceDescription("Spirometer", 
				"SpiroCraft-II", "MyCompany", "584216", "69854", "2.1", "1.1"); 
		Spirometry spiro = new Spirometry(3.7, 5.6); 
		StandardTeleObservation sto = new StandardTeleObservation( 
				FakeObjectExternalDataSource.NANCY_CPR, org_uid, "todo", 
				Codes.LOINC_OID, device, spiro); 
		return sto; 
	} 
 
	/** 
	 * This is a special observation class defined by a SMB. The key aspect is 
	 * that Net4Care does not have direct access to this particular class. 
	 *  
	 * @author Henrik Baerbak Christensen, Aarhus University 
	 *  
	 */ 
	public class Spirometry implements ObservationSpecifics { 
 
		private ClinicalQuantity fvc, fev1; 
 
		public ClinicalQuantity getFvc() { 
			return fvc; 
		} 
 
		public ClinicalQuantity getFev1() { 
			return fev1; 
		} 
 
		public Spirometry() { 
		} 
 
		/** 
		 * Create a spirometry observation 
		 *  
		 * @param fvc 
		 *            FVC value 
		 * @param fev1 
		 *            FEV1 value 
		 */ 
		public Spirometry(double fvc, double fev1) { 
			this.fvc = new ClinicalQuantity(fvc, "L", "19868-9", "FVC"); // LOINC 
																			// FVC 
			this.fev1 = new ClinicalQuantity(fev1, "L", "20150-9", "FEV1"); 
		} 
 
		public String toString() { 
			return "Spiro: " + getFev1() + "/" + getFvc(); 
		} 
 
		@Override 
		public Iterator<ClinicalQuantity> iterator() { 
			List<ClinicalQuantity> list = new ArrayList<ClinicalQuantity>(2); 
			list.add(fvc); 
			list.add(fev1); 
			return list.iterator(); 
		} 
 
		@Override 
		public String getObservationAsHumanReadableText() { 
			return "Measured: " + getFvc().toString() + " / " 
					+ getFev1().toString(); 
		} 
 
	} 
} 
