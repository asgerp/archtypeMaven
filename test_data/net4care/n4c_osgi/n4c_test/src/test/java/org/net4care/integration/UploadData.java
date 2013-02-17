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
 
package org.net4care.integration; 
 
import static org.junit.Assert.assertTrue; 
 
import java.io.IOException; 
import java.text.SimpleDateFormat; 
import java.util.Calendar; 
import java.util.GregorianCalendar; 
 
import org.net4care.forwarder.FutureResult; 
import org.net4care.forwarder.delegate.HttpConnector; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.helper.HelperMethods; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
 
public class UploadData { 
 
	private static FutureResult result = null; 
	private static final String url = "http://localhost:8080/observation"; 
 
	/** 
	 * @param args 
	 */ 
	public static void main(String[] args) { 
		try { 
			System.out.println("Started uploading testdata ..."); 
			uploadTestData(); 
		} catch (IOException e) { 
			System.out.println("Uploading failed! " + e.toString()); 
		} 
	} 
 
	@SuppressWarnings("unused") 
	private static void uploadTestData() throws IOException { 
		HttpConnector connector = new HttpConnector(url); 
 
		Serializer serializer = new JacksonJSONSerializer(); 
 
		StandardDataUploader uploader = new StandardDataUploader(serializer, 
				connector); 
 
		double[][] testValues = new double[10][2]; 
		testValues[0][0] = 1.1; 
		testValues[0][1] = 2.2; 
		testValues[1][0] = 1.3; 
		testValues[1][1] = 3.0; 
		testValues[2][0] = 1.5; 
		testValues[2][1] = 1.4; 
		testValues[3][0] = 2.0; 
		testValues[3][1] = 1.2; 
		testValues[4][0] = 3.3; 
		testValues[4][1] = 0.3; 
		testValues[5][0] = 1.8; 
		testValues[5][1] = 1.5; 
		testValues[6][0] = 2.1; 
		testValues[6][1] = 2.3; 
		testValues[7][0] = 1.0; 
		testValues[7][1] = 1.0; 
		testValues[8][0] = 2.0; 
		testValues[8][1] = 2.0; 
		testValues[9][0] = 3.0; 
		testValues[9][1] = 0.5; 
 
		Calendar times[] = new GregorianCalendar[10]; 
		times[0] = new GregorianCalendar(); 
		times[0].set(2012, 0, 21, 14, 56); 
		times[1] = new GregorianCalendar(); 
		times[1].set(2012, 0, 21, 15, 54); 
		times[2] = new GregorianCalendar(); 
		times[2].set(2012, 0, 21, 20, 43); 
		times[3] = new GregorianCalendar(); 
		times[3].set(2012, 0, 22, 10, 12); 
		times[4] = new GregorianCalendar(); 
		times[4].set(2011, 2, 11, 01, 55); 
		times[5] = new GregorianCalendar(); 
		times[5].set(2012, 0, 15, 14, 16); 
		times[6] = new GregorianCalendar(); 
		times[6].set(2006, 1, 05, 16, 34); 
		times[7] = new GregorianCalendar(); 
		times[7].set(2010, 7, 22, 10, 12); 
		times[8] = new GregorianCalendar(); 
		times[8].set(2001, 9, 29, 22, 04); 
		times[9] = new GregorianCalendar(); 
		times[9].set(2005, 11, 31, 07, 45); 
 
		for (int i = 0; i < 10; i++) { 
			StandardTeleObservation sto; 
			if (i < 5) { 
				sto = HelperMethods.defineSpirometryObservation( 
						FakeObjectExternalDataSource.NANCY_CPR, 
						testValues[i][0], testValues[i][1]); 
			} else { 
				sto = HelperMethods.defineSpirometryObservation( 
						FakeObjectExternalDataSource.JENS_CPR, 
						testValues[i][0], testValues[i][1]); 
			} 
 
			// An STO has no timestamp until it is received on the server, 
			// here we fake that the server has stamped them with 
			// those exact dates above 
			sto.setTime(times[i].getTimeInMillis()); 
 
			// Define the desired time format for the graph. 
			SimpleDateFormat dateFormatter = new SimpleDateFormat( 
					"yyyy-MM-dd H:m"); 
 
			// Upload the first STO to the N4C cloud at time 'now' 
			result = uploader.upload(sto); 
			result.awaitUninterruptibly(); 
		} 
		System.out.println("Testdata successfully uploaded to server!"); 
	} 
} 
