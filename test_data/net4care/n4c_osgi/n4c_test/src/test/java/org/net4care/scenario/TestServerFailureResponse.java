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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.net4care.forwarder.DataUploader;
import org.net4care.forwarder.Query;
import org.net4care.forwarder.QueryResult;
import org.net4care.forwarder.delegate.HttpConnector;
import org.net4care.forwarder.delegate.StandardDataUploader;
import org.net4care.forwarder.query.QueryPersonTimeInterval;
import org.net4care.integration.IntegrationTest;
import org.net4care.serializer.Serializer;
import org.net4care.serializer.delegate.ServerJSONSerializer;
import org.net4care.storage.delegate.FakeObjectExternalDataSource;
import org.net4care.testdoubles.QueryWithBadQueryType;
import org.net4care.utility.Net4CareException;

public class TestServerFailureResponse {

	DataUploader uploader;
	HttpConnector connector;
	Serializer serializer;

	@Before
	public void setup() throws Net4CareException, IOException {
		serializer = new ServerJSONSerializer();
		connector = new HttpConnector( "http://localhost:8080/observation" );
		uploader = new StandardDataUploader( serializer, connector );  
	}
	
	@Test
	@Category(IntegrationTest.class)
	public void queryResultShouldBeUnsuccessful() throws IOException {
		Query query; QueryResult res;

		//Make query with invalid person id: bimse.
		query = new QueryPersonTimeInterval("bimse",1344111111L, 1344438719L);
		res = uploader.query(query);
		res.awaitUninterruptibly();
		
		assertFalse(res.isSuccess());
		assertEquals("0004 - The CPR nr: 'bimse' does not exist in the system.", res.result());
		assertNull(res.getDocumentList());
	}
	
	@Test
	@Category(IntegrationTest.class)
	public void queryWithNegativTimeIntervalShouldFail() throws IOException {
		Query query; QueryResult res;

		//Make query with negative time value.
		query = new QueryPersonTimeInterval(FakeObjectExternalDataSource.NANCY_CPR,-1L, 1344438719L);
		res = uploader.query(query);
		res.awaitUninterruptibly();
		assertFalse(res.isSuccess());
		assertEquals("0001 - Query parameter 'intervalstart' or 'intervalend' is not a valid timestamp.", res.result());
		assertNull(res.getDocumentList());
		
	}
	
	@Test
	@Category(IntegrationTest.class)
	public void queryWithWrongQueryTypeShouldFail() throws IOException {
		Query query; QueryResult res;

		//Make query with bad querytype.
		query = new QueryWithBadQueryType(FakeObjectExternalDataSource.NANCY_CPR, 1344111111L, 1344438719L);
		res = uploader.query(query);
		res.awaitUninterruptibly();
		assertFalse(res.isSuccess());
		assertEquals(
				"0002 - Unknown query " +
				"{QueryType=wrong querytype, cpr=251248-4916, format=application/json, " +
				"intervalend=1344438719, intervalstart=1344111111} received in StandardObservationReceiver.", res.result());
		assertNull(res.getDocumentList());
		
	}
	
}
