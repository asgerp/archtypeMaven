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
 
package org.net4care.security.delegate;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * This class creates a logger using the log4j that create
 * the audit trace.
 * The name and path of the log file are currently hardcoded 
 * in the code.
 * 
 * @author kmanikas@diku.dk
 *
 */
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{4}")}
	           ) 
public class AuditLogger {
	private static Logger logger = null;
	
	public static Logger getLogger() {
		return logger;
	}
	
	protected void setConfigAdmin(ConfigurationAdmin confman) throws IOException {
		String PID = "org.apache.sling.commons.log.LogManager.factory.config";
		Configuration conf 
			= confman.createFactoryConfiguration(PID, null);
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put("pid", getClass().getName());
		properties.put("org.apache.sling.commons.log.level", "INFO");
		properties.put("org.apache.sling.commons.log.file", "audit.log");
		properties.put("org.apache.sling.commons.log.names", getClass().getName());
		conf.update(properties);
		logger = Logger.getLogger(AuditLogger.class);
	}
	
	protected void unsetConfigAdmin(ConfigurationAdmin confman){
		//ignored
	}
}
