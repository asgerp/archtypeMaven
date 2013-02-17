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
 
package org.net4care.telekat;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.net4care.forwarder.DataUploader;
import org.net4care.forwarder.FutureResult;
import org.net4care.forwarder.delegate.HttpConnector;
import org.net4care.forwarder.delegate.StandardDataUploader;
import org.net4care.observation.StandardTeleObservation;
import org.net4care.receiver.delegate.StandardObservationReceiver;
import org.net4care.serializer.Serializer;
import org.net4care.serializer.delegate.JacksonJSONSerializer;
import org.net4care.serializer.delegate.ServerJSONSerializer;
import org.net4care.storage.ExternalDataSource;
import org.net4care.storage.ObservationCache;
import org.net4care.storage.XDSRepository;
import org.net4care.storage.delegate.FakeObjectExternalDataSource;
import org.net4care.storage.delegate.NullObservationCache;
import org.net4care.storage.delegate.SQLiteXDSRepository;
import org.net4care.testdoubles.LocalSynchroneousCallConnector;
import org.net4care.utility.Net4CareException;
import org.xml.sax.SAXException;

public class ParseTelekatFiles {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, Net4CareException {
	  System.out.println("");
	  if ( args.length != 1 ) {
	    System.out.println("Usage: ParseTelekatFiles <rootfolder>");
	    System.exit(0);
	  }
		STOFromTelekatBuilder d = 
				new STOFromTelekatBuilder(args[0]);
		printPersonIDAnalysis(d.getSTOS());
		/**
		 * ServerJSONSerializer s = new ServerJSONSerializer();
		 * 
		 * Below is the 3 methods that should be used to manipulate/read the Telekat data.
		 * */ 
		 uploadTelekatDataToServer(d.getSTOS());
		  //d.writeSTOSToAFile("telekat_data.json");
		  //getObservationsFromFile("telekat_data.json");
		 

	}

	/**
	 * @param file  The file and path to the file to read the observations from.  
	 * @throws IOException
	 */
	private static List<StandardTeleObservation> getObservationsFromFile( String file ) throws IOException {
		List<StandardTeleObservation> stos = new ArrayList<StandardTeleObservation>();
		ServerJSONSerializer s = new ServerJSONSerializer();
		String sl = s.serializeList(readFile(file));
		for(StandardTeleObservation sto : s.deserializeList(sl)) {
			stos.add(sto);
		}		
		return stos;
	}

	/**
	 * This method can be used to count the number of person-ids in a list of StandardTeleObservations. 
	 * It also reports how many observation each person has in the list and the total number 
	 * of observations in the list.
	 * 
	 * @param stos  List of StandardTeleobservations to make an analysis of.
	 */
	private static void printPersonIDAnalysis(ArrayList<StandardTeleObservation> stos) {
		ArrayList<String> knownIds = new ArrayList<String>();
		ArrayList<Integer> count = new ArrayList<Integer>();

		for(int i = 0; i < 30; i++) {
			count.add(i, 1);
		}
		String id = "";
		int index = -1;
		int temp = 0;
		long firstTime = 0L, lastTime = 0L;
		for(StandardTeleObservation s : stos) {
			// analyse person ids
		  id = s.getPatientCPR();
			if(knownIds.contains(id)) {
				index = knownIds.indexOf(id);
				temp = count.get(index)+1;
				count.set(index, temp);
			}
			else {
				knownIds.add(id);
			}
			// analyse time stamps
			long time = s.getTime();
			if ( firstTime == 0L ) { firstTime = time; }
			if ( time < firstTime ) { firstTime = time; }
			if ( time > lastTime ) { lastTime = time; }
		}
		System.out.println("PERSON-ID ANALYSIS");
		System.out.println("------------------");
		System.out.println("Number of person-ids found: " + knownIds.size() + "\n");
		System.out.println("Number of documents for each person-id:");
		int total = 0;
		for(String s : knownIds) {
			index = knownIds.indexOf(s);
			total += count.get(index);
			System.out.println(index+1 + ". " + count.get(index) + " (" + s +")");
		}
		System.out.println("\nTotal number of documents: " + total);
		
		System.out.println( "TIME ANALYSIS");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date early = new Date( firstTime );
		Date late = new Date( lastTime );
		System.out.println("Earliest : " + format.format(early) + " Lastest: "+ format.format(late));
	}
	
	/**
	 * This method uploads a list of StandardTeleObservations to the Net4Care server.
	 * It can be configured by changing the concrete classes initialized in the start
	 * of this method.
	 * @param stos
	 * @throws IOException
	 * @throws Net4CareException
	 */
	private static void uploadTelekatDataToServer(ArrayList<StandardTeleObservation> stos) throws IOException, Net4CareException {
		//Configuration:
		Serializer serializer = new JacksonJSONSerializer();
		XDSRepository xds = new SQLiteXDSRepository();
		xds.connect();
		ExternalDataSource externalDatasources = new FakeObjectExternalDataSource();
		ObservationCache cache = new NullObservationCache();
		Serializer serverSerializer = new ServerJSONSerializer();
		StandardObservationReceiver receiver = new StandardObservationReceiver(serverSerializer,
				externalDatasources, xds, cache);
		HttpConnector connector = new HttpConnector("http://localhost:8080/observation");
		//LocalSynchroneousCallConnector connector = new LocalSynchroneousCallConnector(receiver);

		DataUploader uploader = new StandardDataUploader(serializer, connector);
		
		//Batch algorithm:		
		long start = System.currentTimeMillis();
		int count = 0;
		for(StandardTeleObservation s : stos) {
			System.out.println("Uploading STO nr. " + count);
			FutureResult result = uploader.upload(s);
			result.awaitUninterruptibly();  
			if(!result.isSuccess()) 
				System.out.println(s + " Could not be uploaded.");
			count++;
		}

		long end = System.currentTimeMillis();

		System.out.println("Execution time was "+(end-start)+" ms.");
	    

	}
	
	private static ArrayList<String> readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		ArrayList<String> strListOfSTOs = new ArrayList<String>();		
		String line = null;
		while( ( line = reader.readLine() ) != null ) {
			strListOfSTOs.add(line);
		}
		return strListOfSTOs;
	}

}
