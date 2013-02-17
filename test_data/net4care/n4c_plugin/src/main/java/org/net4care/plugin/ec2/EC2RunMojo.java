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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * 
 * Plugin for launching an EC2 instance for deploying Net4Care servers
 * 
 * The following steps are performed:
 * 
 * - A keypair is generated and output in net4care.pem (if a name is not supplied via ec2.keyname)  [TODO: implement]
 * - A security group, net4care, with ports 22, 8080, and 8083 open is created 
 *   (if a name is not supplied via ec2.securitygroup) [TODO: implement]
 * - A suitable Amazon Machine Image (AMI) is found (if not supplied via ec2.imageid)
 * - An instance is launched using the AMI
 * - Waiting until the instance is ready and a DNS name is returned 
 * 
 * @goal ec2run
 * @requiresProject false
 */
public class EC2RunMojo extends EC2Mojo {
  private String instanceId;
  private String dnsName;

  public void execute() throws MojoExecutionException {
    getLog().info("Amazon region is: " + region);
    getLog().info("Amazon instance type is: " + instanceType);
    getLog().info("Amazon security group is: " + securityGroup);
    getLog().info("Amazon key name is: " + keyName);

    // If AMI id is not specified, search for an image
    if (imageId == null) {
      getLog().info("(Finding suitable image)");
      try {
        String command = 
          String.format("ec2-describe-images --region \"%s\" --filter \"name=Net4Care*\" --filter \"is-public=true\"", region);
        String result = shellExecute(command);
        // IMAGE <AMI ID> <owner/AMI Name> <Owner> ...
        imageId = result.split("\t")[1];
      } catch (Exception e) {
        throw new MojoExecutionException("Error finding image", e);
      }
    }
    getLog().info("Amazon image id is: " + imageId);

    getLog().info("(Creating instance)");
    try {
      String command = 
        String.format("ec2-run-instances --region \"%s\" %s -k %s -g %s -t %s", region, imageId, keyName, securityGroup, instanceType);
      String result = shellExecute(command);
      for (String line: result.split("\n")) {
        String[] parts = line.split("\t");
        // INSTANCE <instance id> ...
        if ("INSTANCE".equals(parts[0])) {
          instanceId = parts[1];
        }
      }
    } catch (Exception e) {
      throw new MojoExecutionException("Error creating instance", e);
    }
    getLog().info("Amazon instance id is: " + instanceId);

    while (true) {
      getLog().info("(Waiting for DNS name)");
      try {
        String command = String.format("ec2-describe-instances --region=\"%s\" %s", region, instanceId);
        String result = shellExecute(command);
        for (String line: result.split("\n")) {
          line = line.replaceAll("(\t)+", "\t");
          String[] parts = line.split("\t");
          // INSTANCE <instance id> <AMI id> <status> ...
          if ("INSTANCE".equals(parts[0])) {
            dnsName = parts[3];
            break;
          }
        }
        if (!"pending".equals(dnsName)) {
          if (dnsName.contains("compute.amazonaws.com")) {
            break;
          } else {  
            throw new MojoExecutionException("Error waiting for DNS name (status is not pending and dns is '" + dnsName + "')");
          }
        }
      } catch (Exception e) {
        throw new MojoExecutionException("Error waiting for DNS name", e);
      }
    }
    getLog().info("Amazon DNS name is: " + dnsName);
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("ec2.properties"));
      writer.write(String.format("ec2.dns=%s\n", dnsName));
      writer.close();
    } catch (IOException e) {
      throw new MojoExecutionException("Error creating properties file", e);
    }
  }
}
