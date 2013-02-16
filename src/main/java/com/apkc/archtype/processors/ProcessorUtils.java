/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import com.apkc.archtype.models.Model;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author asger
 */
public class ProcessorUtils {
    final static Logger log = Logger.getLogger(ComponentProcessor.class.getName());

    /**
     *
     * @param references
     * @param components
     */
    public static void setUpReferences(HashMap references, HashMap components) {
        Iterator iterator = references.entrySet().iterator();
        // Iterate over each annotated class/type
        while (iterator.hasNext()) {
            Map.Entry next = (Map.Entry) iterator.next();
            // The annotated class
            String anClass = (String) next.getKey();
            // List of potential references
            ArrayList<String> unAnnotatedRefs = (ArrayList<String>) next.getValue();

            Iterator componentIterator = components.entrySet().iterator();
            // Iterate over each pattern(ComponentRepresentation)
            while (componentIterator.hasNext()) {
                Map.Entry pattern = (Map.Entry) componentIterator.next();
                // Get the list of component representations in the pattern
                ArrayList<ComponentRepresentation> componentRepresentations =
                        (ArrayList<ComponentRepresentation>) pattern.getValue();
                Iterator<ComponentRepresentation> ite =
                        componentRepresentations.iterator();
                boolean inPattern = false;
                ComponentRepresentation anClassCr = null;
                // is the annotated class in pattern?
                while (ite.hasNext() && !inPattern) {
                    ComponentRepresentation c = ite.next();
                    if (c.getComponentName().equals(anClass)) {
                        inPattern = true;
                        anClassCr = c;
                    }
                }
                if(inPattern){
                    ite = componentRepresentations.iterator();
                    while (ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        // extend the annotated class's references with other
                        // annotated classes found in class
                        if (unAnnotatedRefs.contains(c.getComponentName())) {
                            anClassCr.extendReferences(c.getComponentName());
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param components
     */
    public static ArrayList<String> generateAlloyModelsStr(HashMap components) {
        ArrayList<String> models = new ArrayList<>();
        if (components.isEmpty()) {
            return models;
        }

        Iterator componentIterator = components.entrySet().iterator();
        // Iterate over each pattern and write alloy model
        while (componentIterator.hasNext()) {
            Map.Entry next = (Map.Entry) componentIterator.next();
            String patternName = (String) next.getKey();
            ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) next.getValue();
            StringBuilder finalStr = new StringBuilder("");
            String pat = "";
            StringBuilder contains = new StringBuilder("\telements = ");
            if (componentRepresentation != null) {
                Iterator<ComponentRepresentation> it = componentRepresentation.iterator();
                while (it.hasNext()) {
                    ComponentRepresentation c = it.next();
                    contains.append(c.getComponentName());
                    pat = c.getPattern();
                    if (it.hasNext()) {
                        contains.append(" + ");
                    } else {
                        contains.append("\n}\n");
                    }
                }
            }
            Model model = new Model();
            finalStr.append(model.getModel(pat.toLowerCase(), Boolean.TRUE));
            //finalStr.append("open " + pat.toLowerCase() + "\n");
            finalStr.append("\none sig ").append(patternName).append(" extends Configuration { } {\n");
            finalStr.append(contains.toString());

            if (componentRepresentation != null) {
                Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    finalStr.append(c.toString());
                }
                ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    writeAssertsStr(finalStr, pat, patternName, c.getRole(), c.getComponentName());

                }
                ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    writeCommandsStr(finalStr, pat, c.getComponentName());
                }
            }
            models.add(finalStr.toString());
        }
        return models;
    }


    /**
     * Should analyze which pattern is used and write the appropriate commands
     * for that pattern, using the writer.
     *
     * @param bw
     * @param pattern
     */
    private static void writeCommandsStr(StringBuilder sb, String pattern, String componentName) {
        sb.append("check ")
                .append(componentName.toLowerCase())
                .append(" for 8 but 1 Configuration\n");
    }



    /**
     *
     * @param bw
     * @param pattern
     * @param patternName
     * @throws IOException
     */
    private static void writeAssertsStr(StringBuilder sb, String pattern, String patternName, String role, String componentName) {
        sb.append("assert ")
                .append(componentName.toLowerCase())
                .append(" {\n\t")
                .append(pattern.toLowerCase())
                .append("_")
                .append(role.toLowerCase())
                .append("_style[")
                .append(patternName)
                .append("]\n}\n");
    }
    public static void writeRefsTofile(HashMap<String,ArrayList<String>> references, File f){
        int size = references.size();
        log.debug(size);
        try {
            f.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            Iterator iterator = references.entrySet().iterator();
            oos.writeObject(size);
            while(iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                String patternName = (String) next.getKey();
                oos.writeObject(patternName);
                oos.writeObject(next.getValue());
            }
            oos.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public static void writeTofile(HashMap<String,ArrayList<ComponentRepresentation>> components, File f){
        int size = components.size();
        log.debug(size);
        try {
            f.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            Iterator iterator = components.entrySet().iterator();
            oos.writeObject(size);
            while(iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                String patternName = (String) next.getKey();
                oos.writeObject(patternName);
                oos.writeObject(next.getValue());
            }
            oos.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    public static HashMap<String, ArrayList<String>> readRefsFromFile(File f){
        HashMap<String,ArrayList<String>> references =
                new HashMap<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            int reads = 0;
            int objects = (int) ois.readObject();
            while(reads < objects){
                String refClass = (String)ois.readObject();
                ArrayList<String> refs = (ArrayList<String>) ois.readObject();
                references.put(refClass,refs);
                reads++;
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.error(ex);
        }
        return references;
    }

    public static HashMap<String, ArrayList<ComponentRepresentation>> readFromFile(File f){
        HashMap<String,ArrayList<ComponentRepresentation>> readComponents =
                new HashMap<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            int reads = 0;
            int objects = (int) ois.readObject();
            while(reads < objects){
                String ptn = (String)ois.readObject();
                ArrayList<ComponentRepresentation> acr = (ArrayList<ComponentRepresentation>) ois.readObject();
                readComponents.put(ptn,acr);
                reads++;
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.error(ex);
        }
        return readComponents;
    }

    public static void cleanUpFiles(File fCom, File fRef){
        fCom.delete();
        fRef.delete();
    }
}
