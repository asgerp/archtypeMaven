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
 
package org.net4care.manualtest; 
 
import static org.junit.Assert.*; 
 
import org.apache.axis.message.MessageElement; 
import org.junit.*; 
 
import java.io.File; 
import java.io.StringWriter; 
import java.io.Writer; 
import java.util.*; 
 
import javax.xml.parsers.*; 
 
import org.net4care.locator.HostSpecification; 
import org.net4care.storage.*; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
import org.net4care.storage.queries.XDSQueryPersonTimeInterval; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
import org.net4care.xds.MSCodeplexXDSAdapter; 
import org.net4care.xdsproxy.StoredQueryFindDocuments; 
import org.net4care.xdsproxy.XDSRegistryProxy; 
 
import org.w3c.dom.*; 
 
import com.sun.org.apache.xml.internal.serialize.OutputFormat; 
import com.sun.org.apache.xml.internal.serialize.XMLSerializer; 
 
import sun.security.krb5.internal.HostAddress; 
 
/** Test Storing and retrieving document using  
 * our adapter for the Microsoft Codeplex 
 * XDS repository and registry implementation. 
 *  
 * This is currently a set of non reproducable tests; remove the 
 * @Ingore on the shouldStoreDocument, and run the test case a 
 * couple of times to get data into the XDS,  
 * and reenable the @Ignore. Now you will be able to have 
 * passable tests for a 24 hour time window. 
 *  
 * @author HBC and Morten Larsson, AU 
 * 
 */ 
 
public class TestXDSProxy { 
 
	//Hint: Really a XDSProxy. The type will be changed later. 
	private static MSCodeplexXDSAdapter xdsProxy; 
 
	private Document doc_nancy1; 
	private RegistryEntry meta_nancy1; 
 
	private long time1Nancy, time2Nancy; 
 
	@Before 
	public void setup() { 
		// On my VM snapshot it insists that the timezone is VET ??? 
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1")); 
		System.out.println( "Date  = "+cal.getTime()); 
		long now = cal.getTime().getTime(); 
		time1Nancy = now-10000; 
		time2Nancy = now-5000; 
 
		xdsProxy = new MSCodeplexXDSAdapter(); 
 
		// Make a "spirometry" document 
		doc_nancy1 = makeDocument(NANCY_CPR, "3.8"); 
		meta_nancy1 = new RegistryEntry( NANCY_CPR, time1Nancy, LOINC_OID, 
				new String[] { FVC, GLUCOSE }, org_uid_viewcare, treatmentId_kol);  
 
		// Use below if testing on a local XDS installation - enter the IP 
		// of the local machine with the XDS on. 
		//HostSpecification.setHost("192.168.1.14"); 
 
		// Reenable below comments to circumvent the proxy redirection of 
		// membrane 
		HostSpecification.setRegistryProxyPort(HostSpecification.getRegistryPort()); 
		HostSpecification.setRepositoryProxyPort(HostSpecification.getRepositoryPort()); 
	} 
 
	// set to ignore as one element is added to the SQL DB every time! 
	@Ignore 
	@Test 
	public void shouldStoreHL7Document() throws Net4CareException { 
		// Store it in the XDS repository and registry... 
		xdsProxy.provideAndRegisterDocument(meta_nancy1, doc_nancy1); 
	} 
	@Ignore 
	@Test 
	public void shouldRetrieveHL7Document() throws Net4CareException { 
 
		// Query nancy's readings for the valid time interval. 
		long starting = time1Nancy - 1000*60*60*24; // 24 hours back in time 
		XDSQuery query = new XDSQueryPersonTimeInterval(NANCY_CPR, starting, time2Nancy+1000); 
		List<String> retrievedAsString = xdsProxy.retrieveDocumentSetAsXMLString(query);    
 
		String expected3 = org.net4care.utility.Utility.convertXMLDocumentToString(doc_nancy1); 
 
 
		assertEquals( 2, retrievedAsString.size() ); 
 
		// Document 2 
		Document doc2 = org.net4care.utility.Utility.convertXMLStringToDocument( retrievedAsString.get(1)); 
 
		String computed3 = org.net4care.utility.Utility.convertXMLDocumentToString(doc2); 
		/* 
    System.out.println( "Expected: "+ expected3); 
    System.out.println( "Computed: " + computed3); 
		 */ 
		assertEquals( expected3, computed3 ); 
	} 
 
 
	@Test 
	public void registryShouldReturnErrorOnBadQueryId() throws Exception { 
		StoredQueryFindDocuments query =  
				new StoredQueryFindDocuments(FakeObjectExternalDataSource.NANCY_CPR, "0", System.currentTimeMillis()+""); 
		File file = query.getRequestXMLFile(); 
 
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder(); 
		Document doc = docBuilder.parse(file); 
 
		Node idAttribute = doc.getElementsByTagName("AdhocQuery").item(0).getAttributes().getNamedItem("id"); 
		//Change value, before: urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d 
		idAttribute.setNodeValue("urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0e"); 
 
		XDSRegistryProxy registryProxy = new XDSRegistryProxy(); 
		MessageElement[] result = registryProxy.registryStoredQuery(query); 
		Element asElement = result[0]; 
		NodeList t = asElement.getElementsByTagName("RegistryError"); 
		assertTrue(t.getLength() > 0); 
	} 
 
	@Test (expected = Net4CareException.class)   
	public void repositoryShouldReturnErrorOnUnknownCPR() throws Exception { 
		xdsProxy.provideAndRegisterDocument( 
				new RegistryEntry( "BIMSE", time1Nancy, LOINC_OID, 
						new String[] { FVC, GLUCOSE }, org_uid_viewcare, treatmentId_kol), 
						makeDocument("BIMSE", "3.8") ); 
	} 
 
	/** Make a XML document that is a bit like HL7 
	 *  
	 * @param cprNo cpr of person this document belongs to 
	 * @param value a 'measured value' 
	 * @return XML document 
	 */ 
	private Document makeDocument(String cprNo, String value) { 
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder docBuilder = null; 
		try { 
			docBuilder = dbfac.newDocumentBuilder(); 
		} catch ( ParserConfigurationException e ) { 
			e.printStackTrace(); 
		} 
		Document doc = docBuilder.newDocument(); 
 
		Element recordTarget = doc.createElement("recordTarget"); 
		recordTarget.setAttribute("typeCode", "RCT"); 
		recordTarget.setAttribute("contextControlCode", "OP"); 
		doc.appendChild(recordTarget); 
 
		Element patientRole = doc.createElement("patientRole"); 
		patientRole.setAttribute("classCode", "PAT"); 
		recordTarget.appendChild(patientRole); 
 
		Element cpr = doc.createElement("CPR"); 
		cpr.setAttribute("value", cprNo); 
		patientRole.appendChild(cpr); 
 
		Element obs = doc.createElement("observation"); 
		obs.setAttribute("classCode", "OBS"); 
		obs.setTextContent(value); 
		recordTarget.appendChild(obs); 
 
		return doc; 
	} 
 
	// Patients 
	public static final String NANCY_CPR = "251248-4916"; 
	public static final String JENS_CPR = "120753-2355"; 
	public static final String BIRGITTE_CPR = "030167-1648"; 
	public static final String OLE_CPR = "161134-1341"; 
	public static final String LISE_CPR = "010189-1626"; 
	public static final String MOHAMMED_CPR = "120575-4343"; 
	public static final String IBRAHIM_CPR = "190964-9735"; 
	public static final String BAHAR_CPR = "250255-1234"; 
	public static final String JANE_CPR = "301212-1642"; 
	public static final String LAILA_CPR = "092987-1264"; 
 
	// Physicains 
	public static final String CARSTEN_CPR = "040156-1505"; 
	public static final String ANNE_VINHOLT = "161167-1254"; 
	public static final String DONALD_DOC = "121212-1111"; 
 
 
	public static final String LOINC_OID = "2.16.840.1.113883.6.1"; 
	public static final String FVC = "19868-9"; 
	public static final String FEV1 = "20150-9"; 
	public static final String GLUCOSE = "30164-6"; 
 
	private final String org_uid_viewcare = "org.net4care.org.careview"; 
	private final String org_uid_bpcare = "org.net4care.org.bloodcare"; 
	private final String treatmentId_kol = "EPJ-RM-324-12-22"; 
	private final String treatmentId_high_bp = "EPJ-RM-23-98-33"; 
} 
