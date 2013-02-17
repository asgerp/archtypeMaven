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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.net4care.observation.StandardTeleObservation;
import org.net4care.serializer.Serializer;
import org.net4care.serializer.delegate.ServerJSONSerializer;
import org.xml.sax.SAXException;

public class STOFromTelekatBuilder {

	private ArrayList<StandardTeleObservation> stos = new ArrayList<StandardTeleObservation>();

	public STOFromTelekatBuilder(String folder) throws ParserConfigurationException, SAXException, IOException {
		TelekatDocumentsList tdl = new TelekatDocumentsList(folder);
		Iterator<File> iter = tdl.getFileIterator();
		File f; TelekatDocument t;
		while(iter.hasNext()) {
			f = iter.next();
      System.out.println("Processing: "+f.getName());
			t = new TelekatDocument(f);
			stos.add(t.getStandardTeleObservation());
		}
	}

	public ArrayList<StandardTeleObservation> getSTOS() {
		return stos;
	}

	public boolean writeSTOSToAFile(String fileName) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			Serializer serializer = new ServerJSONSerializer();
			for(StandardTeleObservation s : stos) {
				out.write(serializer.serialize(s));
				out.newLine();
			}
			out.close();
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	
}
