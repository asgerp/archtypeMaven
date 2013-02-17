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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class TelekatDocumentsList {

	private ArrayList<File> listOfTelekatDocuments;

	/**
	 * 
	 * @param folder  Folder to search for telekat documents in.
	 */
	public TelekatDocumentsList(String folder) {
		listOfTelekatDocuments = new ArrayList<File>();
		File startingFolder = new File(folder);
		findTelekatDocuments(startingFolder);
	}

	public Iterator<File> getFileIterator() {
		return listOfTelekatDocuments.iterator();
	}

	private void findTelekatDocuments(File dir) {
		File[] a = dir.listFiles();
		for (File f : a) {
			if (f.isDirectory()) {
				findTelekatDocuments(f);
			} else if (f.getName().endsWith(".xml") && fileIsTelekatObservation(f) ) {
				listOfTelekatDocuments.add(f);
			}
		}
	}

	private boolean fileIsTelekatObservation(File f) {
		try {
			Scanner scan = new Scanner(f);
			scan.nextLine();
			String line = scan.nextLine();
			return line.contains("<ClinicalDocument");
		} catch(Exception e) { 
			return false;
		} 
	}

}
