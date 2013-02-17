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


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.*;
import org.junit.experimental.categories.Category;
import org.net4care.observation.Codes;
import org.net4care.receiver.delegate.StandardHttpServlet;
import org.net4care.storage.delegate.*;
import org.net4care.utility.Net4CareException;
import org.net4care.utility.QueryKeys;

/** Test the example StandardHTTPServlet for GET requests, net4care
 * 
 * Not auto. mvn tests because the need the server to be running and
 * it is not at the time of test.
 * So run these tests in eclipse after starting a server instance with mvn pax:provision
 * in folder net4care/n4c_osgi/n4c_receiver. 
 * 
 * @author Net4Care, Morten Larsson, AU
 */
public class TestStandardHTTPServlet {

  private static final String url = "http://localhost:8080/observation";  
  private static final String LOINC_OID = Codes.LOINC_OID;
  private static final String FVC = "19868-9";
  private static final String FEV1 = "20150-9";
 //private static StandardDataUploader uploader = null;
  private static String[] params = new String[4];

  @BeforeClass
  @Category(IntegrationTest.class)
  public static void beforeClass() throws Net4CareException, IOException {
    try {
      URL u = new URL(url);
      HttpURLConnection urlConn = (HttpURLConnection) u.openConnection();
      urlConn.connect();
    } 
    catch (IOException e) { 
      Assert.fail("Server not up! \n\n" + e.toString());
    }    

    params[2] = "codesystem=" + LOINC_OID;
    params[3] = "codelist=|" + FVC + "|" + FEV1 + "|";
  }

  @Test 
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.NANCY_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=1327104000000";
    assertEquals(
        "[" + 
            "['2012-01-21 14:56', 1.1, 2.2]" +
            "['2012-01-21 15:54', 1.3, 3.0]" +
            "['2012-01-21 20:43', 1.5, 1.4]" + 
            "['2012-01-22 10:12', 2.0, 1.2]" +
            "]",
            HttpRequest(url, params)[0]);
  }

  @Test 
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput2() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.NANCY_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=1327190400000";
    assertEquals(
        "[['2012-01-22 10:12', 2.0, 1.2]]",
        HttpRequest(url, params)[0]);
  }

  @Test
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput3() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.NANCY_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=948499200000";
    assertEquals(
        "[" + 
            "['2012-01-21 14:56', 1.1, 2.2]" +
            "['2012-01-21 15:54', 1.3, 3.0]" +
            "['2012-01-21 20:43', 1.5, 1.4]" + 
            "['2012-01-22 10:12', 2.0, 1.2]" +
            "['2011-03-11 1:55', 3.3, 0.3]" +
            "]",
            HttpRequest(url, params)[0]);
  }

  @Test 
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput4() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.JENS_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=946684800000";    
    assertEquals(
        "[" + 
            "['2012-01-15 14:16', 1.8, 1.5]" +
            "['2006-02-05 16:34', 2.1, 2.3]" +
            "['2010-08-22 10:12', 1.0, 1.0]" +
            "['2001-10-29 22:4', 2.0, 2.0]" +
            "['2005-12-31 7:45', 3.0, 0.5]" +
            "]",
            HttpRequest(url, params)[0]);
  }

  @Test 
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput5() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.JENS_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=1136073600000";
    assertEquals(
        "[" + 
            "['2012-01-15 14:16', 1.8, 1.5]" +
            "['2006-02-05 16:34', 2.1, 2.3]" +
            "['2010-08-22 10:12', 1.0, 1.0]" +
            "]",
            HttpRequest(url, params)[0]);
  }

  @Test 
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput6() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.JENS_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=1199145600000";
    assertEquals(
        "[" + 
            "['2012-01-15 14:16', 1.8, 1.5]" +
            "['2010-08-22 10:12', 1.0, 1.0]" +
            "]",
            HttpRequest(url, params)[0]);
  }

  @Test 
  @Category(IntegrationTest.class)
  public void shouldReturnRightGraphInput7() throws Net4CareException, IOException {
    params[0] = QueryKeys.CPR_KEY+"=" + FakeObjectExternalDataSource.BIRGITTE_CPR;
    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=1199145600000";
    assertEquals(
        "[]",
        HttpRequest(url, params)[0]);
  }

  @Test 
  @Category(IntegrationTest.class)
  public void unkownCPR() throws IOException {
	    params[0] = QueryKeys.CPR_KEY+"=000000-0000";
	    params[1] = QueryKeys.BEGIN_TIME_INTERVAL+"=1199145600000";
	    // status of response is not found and the body is empty
//	    assertEquals(
//		        new UnknownCPRException("000000-0000").toString(),
//		        HttpRequest(url, params)[0]); 
	    assertEquals(
	        "404",
	        HttpRequest(url, params)[1]); 
  }
  
  
  
  @Test 
  @Category(IntegrationTest.class)
  public void invalidAcceptHeader() {

  }
  
  @Test 
  @Category(IntegrationTest.class)
  public void emptySearch() {
	  String[] result = HttpRequest(url, new String[]{});
	  assertTrue(result[0].length() > 0);
	  assertEquals("200", result[1]);
  }
  
  @Test 
  @Category(IntegrationTest.class)
  public void testSQLInjection() {

  }
  
  
  @Test 
  @Category(IntegrationTest.class)
  public void testRESTPost() {

  }
  
  @Test 
  @Category(IntegrationTest.class)
  public void testRESTPut() {
	  URL url;
	try {
		url = new URL(TestStandardHTTPServlet.url);
		  HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		  httpCon.setDoOutput(true);
		  httpCon.setRequestMethod("PUT");
		  OutputStreamWriter out = new OutputStreamWriter(
		      httpCon.getOutputStream());
		  out.write("Resource content");
		  out.close();
	} catch (MalformedURLException e) {
		Assert.fail(e.toString());
	} catch (ProtocolException e) {
		Assert.fail(e.toString());
	} catch (IOException e) {
		Assert.fail(e.toString());
	}

  }
  
  @Test 
  @Category(IntegrationTest.class)
  public void testRESTDelete() {
	  URL url;
	try {
		url = new URL("http://www.example.com/resource");
		  HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		  httpCon.setDoOutput(true);
		  httpCon.setRequestProperty(
		      "Content-Type", "application/x-www-form-urlencoded" );
		  httpCon.setRequestMethod("DELETE");
		  httpCon.connect();
	} catch (MalformedURLException e) {
		Assert.fail(e.toString());
	} catch (IOException e) {
		Assert.fail(e.toString());
	}

  }
  
  @Test 
  @Category(IntegrationTest.class)
  public void testRESTOptions() {

  }
  
  @Test 
  @Category(IntegrationTest.class)
  public void testRESTTrace() {

  }
  

  private String[] HttpRequest(String url, String[] params)  {

    if(params.length > 0)
      url += "?";

    
    for(int i = 0; i < params.length; i++) 
      url += params[i] + "&";

    
    
    if(params.length > 0)
    	url += QueryKeys.FORMAT_KEY+"="+QueryKeys.ACCEPT_GRAPH_DATA;
//      url = url.substring(0, url.length()-1);
    URL requestURL;
	try {
		requestURL = new URL(url);
	} catch (MalformedURLException e) {
		return new String[]{e.toString(), ""};
	}

    URLConnection server = null;
    String result = "";
    int status = 500;
	try {
		server = requestURL.openConnection();
//		server.setRequestProperty("accept", QueryKeys.ACCEPT_GRAPH_DATA);

		server.connect();
		
		if ( server instanceof HttpURLConnection)
		{
			   HttpURLConnection httpConnection = (HttpURLConnection) server;

			   status = httpConnection.getResponseCode();
		}
	    BufferedReader in = new BufferedReader(
		        new InputStreamReader(
		            server.getInputStream()));
	
		    String inputLine;
	
		    while ((inputLine = in.readLine()) != null) 
		      result += inputLine;
		    in.close();  
	} catch (IOException e) {
//		result = e.toString();
	}


	return new String[]{result, ""+status};

  }
  
  public static String excutePost(String targetURL, String urlParameters)
  {
    URL url;
    HttpURLConnection connection = null;  
    try {
      //Create connection
      url = new URL(targetURL);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", 
           "application/x-www-form-urlencoded");
			
      connection.setRequestProperty("Content-Length", "" + 
               Integer.toString(urlParameters.getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");  
			
      connection.setUseCaches (false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      //Send request
      DataOutputStream wr = new DataOutputStream (
                  connection.getOutputStream ());
      wr.writeBytes (urlParameters);
      wr.flush ();
      wr.close ();

      //Get Response	
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer(); 
      while((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();

    } catch (Exception e) {

      e.printStackTrace();
      return null;

    } finally {

      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
  


  

}