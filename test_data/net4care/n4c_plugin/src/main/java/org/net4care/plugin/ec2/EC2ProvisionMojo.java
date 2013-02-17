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
 
package org.net4care.plugin.ec2;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Deploys a Net4Care project on an instance launch using 
 * the net4care:ec2launch goal. 
 * 
 * The following steps are taken
 * 
 * - The pom.xml file from the local directory is copied to EC2
 * - "mvn pax:provision" is executed remotely based on the copied pom.xml file 
 * 
 * @author Klaus Marius Hansen, klausmh@diku.dk
 * 
 * @execute phase="package"
 * @goal ec2provision
 * @requiresProject true
 */

public class EC2ProvisionMojo extends EC2Mojo {
  /**
   * @parameter 
   *  expression="${ec2.dns}" 
   */
  protected String dnsName;

  /**
   * @parameter 
   *  expression="${ec2.key}" 
   */
  protected String key;

	public void execute() throws MojoExecutionException {
	  if (dnsName == null) {
	    readProperties();
	  }
    getLog().info("Amazon key is: " + key);
    getLog().info("Amazon DNS name is: " + dnsName);

    getLog().info("(Copying POM)");
    try {
      String command = 
        String.format("scp -i %s pom.xml ec2-user@%s:/home/ec2-user/", key, dnsName);
      shellExecute(command);
    } catch (Exception e) {
      throw new MojoExecutionException("Error copying POM", e);
    }
    getLog().info("(Executing POM)");

    try {
      String command = 
        String.format("ssh -i %s ec2-user@%s mvn pax:provision", key, dnsName);
      shellExecute(command);
    } catch (Exception e) {
      throw new MojoExecutionException("Error executing POM", e);
    }
	}

  private void readProperties() {
    /* Try reading properties files (maven-properties-plugin does not work
     * when calling a plugin, so we have to do this ourselves
     */
     getLog().info("(Loading ec2.properties)");  
     Properties properties = new Properties();
     try {
      properties.load(new FileInputStream("ec2.properties"));
      dnsName = properties.getProperty("ec2.dns");
    } catch (Exception e) {
      // Ignore
      e.printStackTrace();
    }
  }
}
