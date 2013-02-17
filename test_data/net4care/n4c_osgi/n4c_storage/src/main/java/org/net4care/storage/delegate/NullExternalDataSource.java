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

import org.net4care.storage.AddressData;
import org.net4care.storage.ExternalDataSource;
import org.net4care.storage.PersonData;
import org.net4care.storage.TreatmentData;

import com.apkc.archtype.quals.*;
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{3}")}
          ) 
public class NullExternalDataSource implements ExternalDataSource {

	@Override
	public PersonData getPersonData(String cpr) {
		PersonData pd = new PersonData(cpr, "given","family", "-","19000101","99999999", new AddressData("", "", "", "", "") );
		return pd;
	}

	@Override
	public TreatmentData getTreatmentData(String treatmentID) {
		return new TreatmentData(){
			@Override
			public String getAuthorCPR() {
				return "";
			}

			@Override
			public String getStewardOrganizationName() {
				return "";
			}

			@Override
			public AddressData getCustodianAddr() {
				return new AddressData("", "", "", "", "");
			}};
	}

}
