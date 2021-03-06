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
 
package org.net4care.storage.delegate;

import java.util.List;

import org.net4care.storage.RegistryEntry;
import org.net4care.storage.XDSQuery;
import org.net4care.storage.XDSRepository;
import org.w3c.dom.Document;

/** Null object implementation of XDSRepository that does absolutely
 * nothing. All return values are null.
 */
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{3}")}
	           ) 
public class NullXDSRepository implements XDSRepository {

  @Override
  public void provideAndRegisterDocument(RegistryEntry metadata, 
                                         Document xmlDocument) {}
  @Override
  public List<Document> retrieveDocumentSet(XDSQuery query){
    return null;
  }

  @Override
  public List<String> retrieveDocumentSetAsXMLString(XDSQuery query) {
    return null;
  }

  public void connect() {}
  
  public void disconnect() {}

  public void utterlyEmptyAllContentsOfTheDatabase() {
  }

  public String toString() {
    return "NullXDSRepository.";
  }

}
