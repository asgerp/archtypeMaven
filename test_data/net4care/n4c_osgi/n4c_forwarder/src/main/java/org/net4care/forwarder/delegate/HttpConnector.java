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
 
package org.net4care.forwarder.delegate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.net4care.forwarder.FutureResult;
import org.net4care.forwarder.FutureResultWithAnswer;
import org.net4care.forwarder.Query;
import org.net4care.forwarder.ServerConnector;

import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
/**
 * (Synchronous) ServerConnector based on HTTP
 *  
 * @author Klaus Marius Hansen, klausmh@diku.dk
 *
 */
@ArchTypeComponent(
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
        )
public class HttpConnector implements ServerConnector {
  private static SSLSocketFactory sslFactory = null;
  private URL url;
  private String server;
  private String charset = "UTF-8";
  
  /**
   * 
   */
  static {
    //for localhost testing only
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
        new javax.net.ssl.HostnameVerifier(){

          public boolean verify(String hostname,
              javax.net.ssl.SSLSession sslSession) {
            if (hostname.equals("localhost")) {
              return true;
            }
            return false;
          }
        });
  }

  public HttpConnector(String server) throws MalformedURLException {
    url = new URL(server);
    this.server = server;    
  }

  @Override
  public FutureResult sendToServer(String onTheWireFormat) throws IOException {
    boolean result = false;
    String message = null;

    HttpURLConnection connection = openConnection(url);
    connection.setRequestProperty("accept", "application/json");
    connection.setDoOutput(true);
    connection.getOutputStream().write(onTheWireFormat.getBytes());

    result = (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
    message = connection.getResponseCode()+connection.getResponseMessage();
    
    // System.out.println(result);
    // System.out.println(message);

    return new HttpConnectorFutureResult(result, message);
  }

  /**
   * 
   * Opens a URL connection. 
   * 
   * If the url is an HTTPS URL and
   * a keystore is trusted (by calling HttpConnector.trustKeystore(), 
   * a special SSL factory that trusts the keystore is used
   * 
   * @param url
   * @return
   * @throws IOException
   */
  private HttpURLConnection openConnection(URL url) throws IOException {
    //make a check for the type of connection
    
    HttpURLConnection result;
    if (url.getProtocol().equalsIgnoreCase("https") ){
      HttpsURLConnection s_conn = (HttpsURLConnection)url.openConnection();
      s_conn.setSSLSocketFactory(sslFactory);      
      result = s_conn;
      
    }
    else {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      result = conn;
    }
    
    return result;
  }

  @Override
  public FutureResultWithAnswer queryToServer(Query query) throws IOException {

    StringBuffer parameters = new StringBuffer();
    for (String key: query.getDescriptionMap().keySet()) {
      parameters.append(String.format("%s%s=%s", 
          parameters.length() == 0 ? "" : "&", 
              URLEncoder.encode(key, charset), 
              URLEncoder.encode(query.getDescriptionMap().get(key), charset)));
    }
    URL url = new URL(String.format("%s?%s", server, parameters.toString()));
    HttpURLConnection connection = openConnection(url);
    connection.setRequestProperty("accept", "application/json");
    
    StringBuffer data = new StringBuffer();
    
    // The server will send a 4xx if an error occurred. This 
    // causes connection.getInputStream() to throw an FileNotFound.
    // Therefore the cond. below.
    if(connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        for (String line; (line = reader.readLine()) != null;) {
          data.append(line);
        }
        reader.close();
    }
    else {
    	data.append("[]");
    }
    boolean isSuccess = (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
    String result = connection.getResponseMessage();
    String answer = data.toString();

    return new HttpConnectorFutureResultWithAnswer(isSuccess, result, answer);
  };


  /**
   * 
   * Trust the certificates from the input keystore for
   * all future instances of HttpConnector
   * 
   * @param input: an InputStream to the keystore to trust
   * @throws IOException 
   * @throws KeyStoreException 
   * @throws NoSuchAlgorithmException 
   * @throws KeyManagementException 
   * @throws CertificateException 
   */
  public static void trustKeystore(InputStream input) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException{
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(input, null);
    
    TrustManagerFactory tmf = 
    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keyStore);
    SSLContext ctx = SSLContext.getInstance("TLS");
    ctx.init(null, tmf.getTrustManagers(), null);
    sslFactory = ctx.getSocketFactory();
    

  }

  private class HttpConnectorFutureResult implements FutureResult {
    private boolean isSuccess;
    private String result;

    public HttpConnectorFutureResult(boolean result, String message) {
      this.isSuccess = result;
      this.result = message;
    }

    @Override
    public void awaitUninterruptibly() {
      // Do nothing, call is synchronous
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public boolean isSuccess() {
      return isSuccess;
    }

    @Override
    public String result() {
      return result;
    }

  }

  public class HttpConnectorFutureResultWithAnswer implements
  FutureResultWithAnswer {

    private boolean isSuccess;
    private String result;
    private String answer;

    public HttpConnectorFutureResultWithAnswer(boolean isSuccess, String result,
        String answer) {
      this.isSuccess = isSuccess;
      this.result = result;
      this.answer = answer;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public boolean isSuccess() {
      return isSuccess;
    }

    @Override
    public void awaitUninterruptibly() {
      // Do nothing
    }

    @Override
    public String result() {
      return result;
    }

    @Override
    public String getAnswer() {
      return answer;
    }
  }
}
