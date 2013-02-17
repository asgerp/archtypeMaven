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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Abstract superclass for EC2 mojos. 
 * 
 * Defines appropriate parameters
 */
public abstract class EC2Mojo extends AbstractMojo {
  /**
   * @parameter 
   *  expression="${aws.region}" 
   *  default-value="eu-west-1"
   */
  protected String region;

  /**
   * @parameter 
   *  expression="${ec2.imageid}" 
   */
  protected String imageId;

  /**
   * @parameter 
   *  expression="${ec2.instancetype}"
   *  default-value="t1.micro"
   */
  protected String instanceType;

  /**
   * @parameter 
   *  expression="${ec2.securitygroup}"
   *  default-value="net4care-server"
   */
  protected String securityGroup;

  /**
   * @parameter 
   *  expression="${ec2.keyname}"
   *  default-value="net4care-key"
   */
  protected String keyName;
  protected String shellExecute(String command) throws Exception {
    getLog().debug("Executing:\n  " + command);
    StringBuffer result = new StringBuffer();

    Process process = Runtime.getRuntime().exec(command);
    process.waitFor();

    BufferedReader reader;
    if (process.exitValue() == 0) {
      reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    } else {
      reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    String line = "";
    while ((line = reader.readLine()) != null) {
      result.append(line);
      result.append('\n');
    }
    getLog().debug(String.format(" Result is:\n  %s", result));

    if (process.exitValue() != 0) {
      throw new Exception(String.format("Error executing command (%s)", result)); 
    }

    return result.toString();
  }
}
