/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;
/**
 * returns the model supplied as argument as a string
 * @author mundane
 */
public class Model {   
    final static Logger log = Logger.getLogger(Model.class);
    
    public String getModel(String str, Boolean verbose) {
        if(verbose){
            log.info("Opening model file: " + str + "\n");
        }
        StringBuilder fStr = new StringBuilder();
        try {
            InputStream stream = getClass().getResourceAsStream("/"+str+".als");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = "";
            while(line != null) {
                fStr.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
        } catch (Exception ioe) {
            log.error(ioe);
        }
        return fStr.toString().trim();
    }
}
