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
 
package org.net4care.receiver.delegate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.net4care.graph.GraphGenerator;
import org.net4care.observation.StandardTeleObservation;
import org.net4care.receiver.ObservationReceiver;
import org.net4care.receiver.rmi.RMIServer;
import org.net4care.security.BasicAuthHttpContext;
import org.net4care.serializer.Serializer;
import org.net4care.utility.Constants;
import org.net4care.utility.Net4CareException;
import org.net4care.utility.QueryKeys;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

/** 
 * Provides a default HTTP servlet-based observation server. The server observes the following
 * protocol:
 * 
 * <pre>
 *  POST observation
 *  &lt;JSON observation data&gt;
 * </pre>
 * 
 * Posts an observation on the server. There is no response data
 * 
 * <pre>
 *  GET observation
 * </pre>
 * 
 * Returns the latest POSTed observation in JSON format (see comments for doGet
 * for details)
 * 
 * @author Klaus Marius Hansen, klausmh@diku.dk
 * 
 */
import com.apkc.archtype.quals.*;
@ArchTypeComponent(
          patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
          ) 
public class StandardHttpServlet extends HttpServlet {
	private static final String DEFAULT_PATH = "/observation";
	private static final long serialVersionUID = 1L;

	private Serializer serializer = null;
	private ObservationReceiver receiver = null;
	private HttpService http;
	private Logger logger = Logger.getLogger(StandardHttpServlet.class);
	private static String version;	

	public StandardHttpServlet() {}
	
	public StandardHttpServlet( Serializer serializer, ObservationReceiver observationReceiver ) {
	  this.serializer = serializer;
	  this.receiver = observationReceiver;
	}
	
	public static final  String getWelcomeMsg() {
		String result;
		result = "This is the Net4Care server version " + version + "\n"+
				"The server accepts REST-based POST and GET requests,\n" +
				"that allow uploading clinical observations from a home-device\n"+
				"and retrieving observations (raw or as graphs) based on queries.\n"+
				"However, use the Java classes instead that provide a high-level API.\n"+
				"--- Please visit www.net4care.org for more information and download.\n"+
				"                         The Net4Care Team...";
		return result;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Receive and store latest observation
		StringBuffer data = new StringBuffer();
		for (String line; (line = req.getReader().readLine()) != null;) {
			data.append(line);
		}
		logger.info("Received payload: " + data.toString());

		try {
			receiver.observationMessageReceived(data.toString());
			logger.info("Processed payload");
		} catch (Net4CareException e) {
			if(e.getErrorCode() == Constants.ERROR_UNKNOWN_CPR) {
				logger.error("CPR exception", e);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
			} else {
				logger.error("Net4Care exception", e);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
			}
		}

	}

	/**
	 * Protocol
	 * 
	 * GET /observation?cpr=<cpr>&intervalstart=<timestamp>&codelist<codelist>&
	 * codesystem=<codesystem>
	 * 
	 * where <timestamp> is in a UNIX timestamp in milliseconds , e.g, 123414701
	 * and where <codelist> is in the JSON format |c1|c2|...|cn| where ci is a
	 * valid code according to the codesystem. and where <codesystem> could be
	 * UIPAC or another supported system. This return a JSON array on the form
	 * 
	 * [ ['<timestamp>' <c1 value>, <c2 value>, ... <cn value>], * ]
	 * 
	 * where <timestamp> is in the format <year>-<month>-<day> <hours>:<minutes>
	 * 
	 * Set the accept header to application/json to get JSON and
	 * application/graph to get graph format.
	 * 
	 * @precondition the clinical quantities requested must have the same unit
	 *               if graph data is to be returned.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String data = "";

		// Retrieve the parameters from the GET request and store them in
		// queryMap
		Map<String, String> queryMap = new HashMap<String, String>();
		for (String key : (Iterable<String>) req.getParameterMap().keySet()) {
			queryMap.put(key, ((String[]) req.getParameterMap().get(key))[0]);
		}

		//System.out.println("V2: Received query: " + queryMap.toString());
		logger.info("Received query: " + queryMap.toString());

		// Compose the result to return to client
		String responseFormat = "";
		String result = "";
		try {
			// The result string to return to client
			result = "";
			// Assume that the client wants the raw JSON strings back
			responseFormat = QueryKeys.ACCEPT_JSON_DATA;
			if (queryMap.isEmpty()) {
				// This is just at raw "/observation" request without
				// parameters,
				// consider it a 'ping' and return some sensible info
				result = getWelcomeMsg();
				logger.info(result);
			} else {

				// Removed - instead of switching on the header accept field
				// I use the query parameter as it is under 'framework code
				// control' whereas the HTTP header information is particular
				// to the HTTP connector implementation and thus not
				// general enough to handle other implemenations. -HBC
				//queryMap.put(QueryKeys.FORMAT_KEY, req.getHeader("accept"));

				// Ask the receiver object to retrieve all observations objects
				// (as JSON strings) from the underlying database tier that
				// match the query parameters
				data = receiver.observationQueryReceived(queryMap);

				// Test that the requested data format is graph format and
				// not raw JSON
				String dataTypeRequested = queryMap.get(QueryKeys.FORMAT_KEY);
				//System.out.println("V2: The requested returned data format is:"+dataTypeRequested);
				if (dataTypeRequested.equals(QueryKeys.ACCEPT_GRAPH_DATA)) {
					// OK the requested data is graph format, so deserialize the
					// objects and use the graph generator to produce the
					// graph data.
					List<StandardTeleObservation> stoList = serializer.deserializeList(data);
					result = GraphGenerator.generateGoogleGraphFromListOfObservations(stoList);
					responseFormat = QueryKeys.ACCEPT_GRAPH_DATA;
				} else if ( dataTypeRequested.equals(QueryKeys.ACCEPT_JSON_DATA)){
					// OK, it was the raw JSON that was wanted, just
					// make that the result.
					responseFormat = QueryKeys.ACCEPT_JSON_DATA;
					result = data;
				} else if ( dataTypeRequested.equals(QueryKeys.ACCEPT_XML_DATA)) {
					//System.out.println( "Requested XML DATA!");
					responseFormat = QueryKeys.ACCEPT_XML_DATA;
					result = data;
				}
			}


			resp.setStatus(HttpServletResponse.SC_OK);


		} catch (Net4CareException e) {
			if(e.getErrorCode() == Constants.ERROR_UNKNOWN_CPR) {
				logger.error("CPR exception", e);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
				
			} else {
				logger.error("Query GET failed", e);
				for (Object key : req.getParameterMap().keySet()) {
					logger.error(key + " = " + queryMap.get(key));
				}
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
			}
		}

		resp.setContentType(responseFormat);
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.getOutputStream().write(result.getBytes());
		resp.flushBuffer();
	}

	/*
	 * Called when component is completely instantiated
	 */
	protected void activate(ComponentContext context) {
		version = (String) context.getBundleContext().getBundle().getHeaders().get("Bundle-Version");

		String path = DEFAULT_PATH;
		logger.info("Net4Care observation server started on " + path);
		try {
			http.registerServlet(path, this, null, new BasicAuthHttpContext());
			if (System.getProperty("org.net4care.test.isIntegrationTesting").equals("true")) {
				new RMIServer(this);
			}
		} catch (Exception e) {
			logger.error("Error in registering servlet", e);
		}
	}

	protected void setHttp(HttpService http) {
		this.http = http;
	}

	protected void unsetHttp(HttpService http) {
		this.http = null;
	}

	protected void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	protected void unsetSerializer(Serializer serializer) {
		this.serializer = null;
	}

	protected void setReceiver(ObservationReceiver receiver) {
		this.receiver = receiver;
	}

	protected void unsetReceiver(ObservationReceiver receiver) {
		this.receiver = null;
	}

	public void kill() {
		logger.info("Shutting down server by RMI kill!");
		unsetHttp(http);
		unsetReceiver(receiver);
		unsetSerializer(serializer);
		this.destroy();
		System.exit(0);
	}

}
