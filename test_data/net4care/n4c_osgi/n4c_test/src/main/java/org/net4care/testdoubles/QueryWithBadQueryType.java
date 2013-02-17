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
 
package org.net4care.testdoubles;

import java.util.Map;


import org.net4care.forwarder.query.QueryPersonTimeInterval;
import org.net4care.utility.QueryKeys;

public class QueryWithBadQueryType extends QueryPersonTimeInterval {

	public QueryWithBadQueryType(String cpr, long beginTimeInterval,
			long endTimeInterval) {
		super(cpr, beginTimeInterval, endTimeInterval);
	}

	@Override
	public Map<String, String> getDescriptionMap() {
		Map<String, String> superMap = super.getDescriptionMap();
		superMap.put( QueryKeys.QUERY_TYPE, "wrong querytype");
		return superMap;
	}
}
