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
 
package org.net4care.simplestorage.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.net4care.security.BasicAuthHttpContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class Protocol extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public void setHttp(HttpService http) throws ServletException, NamespaceException {
    http.registerServlet("/simplestorage", this, null, new BasicAuthHttpContext());
  }

  public void unsetHttp(HttpService http) throws ServletException, NamespaceException {
    // Do nothing; deregistration is handled by OSGi
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
  throws ServletException, IOException {
    resp.setContentType("text/plain");
    resp.getWriter().write("PUT");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws ServletException, IOException {
    System.out.println("GET");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws ServletException, IOException {
    StringBuffer data = new StringBuffer();
    for (String line; (line = req.getReader().readLine()) != null;) {
      data.append(line);
    }
    resp.setContentType("text/plain");
    resp.getWriter().write("POST");
  }

  protected void activate(ComponentContext context) {
    System.out.println("Activated");
  }

  protected void deactivate(ComponentContext context) {
    System.out.println("Deactivated");
  }
}

