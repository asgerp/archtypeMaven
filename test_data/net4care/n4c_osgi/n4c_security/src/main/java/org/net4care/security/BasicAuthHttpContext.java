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
 
package org.net4care.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.net4care.security.delegate.AuditLogger;
import org.osgi.service.http.HttpContext;


/**
 * 
 * This class adds basic HTTP authentication to a servlet
 * when that servlet is accessed via HTTPS
 * 
 * It must be used as an HttpContext when registering an HttpServlet
 * with the OSGi HTTP service
 * 
 * TODO: implement proper user database
 * 
 * @author Klaus Marius Hansen, klausmh@diku.dk
 *
 */
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{4}")}
	           ) 
public class BasicAuthHttpContext implements HttpContext {
  private Map<String, byte[][]> userDB = new HashMap<String, byte[][]>();

  public BasicAuthHttpContext() {
    try {
      userDB.put("net4care", hashPassword("net4care", null));
    } catch (Exception e) {
      // TODO logging
      e.printStackTrace();
    }
  }

  private boolean testPassword(String username, String password) {
    if (userDB.containsKey(username)) {
      byte[] passwordAndSalt = userDB.get(username)[0];
      byte[] salt= userDB.get(username)[1];
      try {
        return Arrays.equals(hashPassword(password, salt)[0], passwordAndSalt);
      } catch (Exception e) {
        // TODO: log exception
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  /**
   * Hash a password with a random salt. Salting is done to 
   * protect from attacks via a rainbow table
   * 
   * @param password
   * @param salt: if non-null use this as salt, otherwise create random salt
   * 
   * @return an area of byte arrays where the first array is a 
   *         hash of password + a salt and the second array is the salt
   *         Encoding is UTF-8
   * @throws NoSuchAlgorithmException 
   * @throws IOException 
   */
 
  private byte[][] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, IOException {
    byte[][] result = new byte[2][];
    
    // Create salt if it does not exist
    if (salt == null) {
      salt = Double.toString(Math.random()).getBytes("UTF-8");
    }
    
    // Hash password + salt
    ByteArrayOutputStream passwordAndSalt = new ByteArrayOutputStream();
    passwordAndSalt.write(password.getBytes("UTF-8"));
    passwordAndSalt.write(salt);
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(passwordAndSalt.toByteArray());
    result[0] = md.digest();
    result[1] = salt;

    return result;
  }



  @Override
  public String getMimeType(String arg0) {
    return null;
  }

  @Override
  public URL getResource(String arg0) {
    return null;
  }

  @Override
  public boolean handleSecurity(HttpServletRequest req, HttpServletResponse res) throws IOException {
    if (req.getScheme().equals("http")) {
      // No security for HTTP
      return true;
    }
    if (isAuthenticated(req)) {
      return true;
    }
    res.sendError(HttpServletResponse.SC_FORBIDDEN);
    return false;
  }

  /**
   * Check if the request has the HTTP Basic Authentication headers and
   * that the user name and password match the user database
   * 
   * @param req
   * @return whether the request is authenticated
   */
  protected boolean isAuthenticated(HttpServletRequest req) {
    String authorizationHeader = req.getHeader("Authorization");
      
    	    
    //create Audit trace for the request
    Logger log = AuditLogger.getLogger();
    String auditText;
    auditText = "-- Start Trace of new HTTP Request -- \n Request URL : " + req.getRequestURL()
    		+ "\n Method : " + req.getMethod() 
    		+ " \n Session ID : " + req.getRequestedSessionId() 
    		+ " \n Remote Host : " + req.getRemoteHost() + " : " + req.getRemotePort()
    		+ " \n AuthorizationHeader : " + authorizationHeader
    		; 
    
    
    if (authorizationHeader == null) {
      log.info(auditText);
      return false;
    }
    String usernameAndPassword = new String(Base64.decodeBase64(authorizationHeader.substring("Basic".length() + 1).getBytes()));

    int userNameIndex = usernameAndPassword.indexOf(":");
    String username = usernameAndPassword.substring(0, userNameIndex);
    String password = usernameAndPassword.substring(userNameIndex + 1);
    
    auditText += "\n Username : " + username;
    log.info(auditText);    
    

    return testPassword(username, password);
  }
}
