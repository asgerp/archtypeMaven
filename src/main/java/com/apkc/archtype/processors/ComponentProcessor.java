/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import com.apkc.archtype.App;
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.apache.commons.lang3.*;
import org.apache.log4j.Logger;


/**
 * ArchTypeComponent processor. Process
 *
 * @ArchTypeComponent annotations and writes alloy models to files
 * @author asger
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.apkc.archtype.quals.ArchTypeComponent"})
public class ComponentProcessor extends AbstractProcessor {
    final static Logger log = Logger.getLogger(ComponentProcessor.class.getName());
    String temp_file_path;
    String keep_processing;
    /**
     *
     * @param annotations annotations - contains the set of annotations that we
     * are interested in processing. The annotations in the set should
     * correspond with the list of annotations that we specify in the
     * @SupportedAnnotationTypes annotation
     * @param roundEnv - this is the processing environment
     * @return boolean - a boolean value is to indicate whether we have claimed
     * ownership of the set of annotations passed by the processing environment
     * in elements. "Claiming ownership" means that the set of annotations in
     * elements are ours, and we have processed it. Return true if you claim
     * ownership of them; false otherwise.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException ex) {
            log.error(ex);
        }
        temp_file_path = prop.getProperty("path","");
        keep_processing = prop.getProperty("continue");
        //processingEnv is a predefined member in AbstractProcessor class
        //Messager allows the processor to output messages to the environment
        Messager messager = processingEnv.getMessager();
        HashMap<String,ArrayList<ComponentRepresentation>> components = new HashMap<>();
        HashMap<String,ArrayList<String>> references = new HashMap<>();
        ArrayList<String> annotatedClasses = new ArrayList<>();
        boolean claimed = false;
        for (TypeElement te : annotations) {
            //Get the members that are annotated with Option
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                claimed = true;
                annotatedClasses.add(e.getSimpleName().toString());
                processAnnotation(e, messager, components, references);
            }
        }
        //roundEnv.processingOver();
        
        File fComp = new File(temp_file_path + "components.ser");
        File fRef = new File(temp_file_path + "references.ser");
        if(claimed ){
            HashMap<String, ArrayList<ComponentRepresentation>> allComponents = new HashMap<>();
            HashMap<String, ArrayList<String>> allRefs = new HashMap<>();
            if(fComp.exists()) {
                HashMap<String, ArrayList<ComponentRepresentation>> readComponents = ProcessorUtils.readFromFile(fComp);
                allComponents.putAll(readComponents);
            }
            if(fRef.exists()){
                HashMap<String, ArrayList<String>> readRefs = ProcessorUtils.readRefsFromFile(fRef);
                allRefs.putAll(readRefs);
            }
            Iterator<Entry<String, ArrayList<ComponentRepresentation>>> iterator = allComponents.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String, ArrayList<ComponentRepresentation>> next = iterator.next();
                String key = next.getKey();
                ArrayList<ComponentRepresentation> value = next.getValue();
                if(components.containsKey(key)){
                    ArrayList<ComponentRepresentation> getList = components.get(key);
                    getList.addAll(value);
                } else{
                    components.put(key, value);
                }
            }
            Iterator<Entry<String, ArrayList<String>>> iterator1 = allRefs.entrySet().iterator();
            while(iterator1.hasNext()){
                Entry<String, ArrayList<String>> next = iterator1.next();
                String key = next.getKey();
                ArrayList<String> value = next.getValue();
                if(references.containsKey(key)){
                    ArrayList<String> getList = references.get(key);
                    getList.addAll(value);
                } else{
                    references.put(key, value);
                }
            }
            ProcessorUtils.writeRefsTofile(references, fRef);
            ProcessorUtils.writeTofile(components, fComp);
        }
        if(keep_processing.equals("yes")){
            App.processFromFile();
        }
        return claimed;
    }

    /**
     *
     * @param e - the element currently being worked on
     * @param m - the messager used for errors, notes and the like
     * @param components - a hashmap containing all components, stored under the
     * pattern(s) they are part of
     */
    private void processAnnotation(Element e, Messager m, HashMap components, HashMap refs) {
        ArchTypeComponent com = e.getAnnotation(ArchTypeComponent.class);
        // TODO do some analisys of enclosed contra p.references, do they
        List<? extends Element> enclosedElements = e.getEnclosedElements();
        ArrayList<String> stringRefs = new ArrayList<>();
        for (Element el : enclosedElements) {
            String type = el.asType().toString();
            if (type.contains(".")) {
                // is return value
                if (type.contains("()")) {
                    stringRefs.add(type.substring(type.lastIndexOf(".") + 1));
                } else // is parameter check for more than one param
                    if (type.contains(")") && type.contains(",")) {
                        String[] split = StringUtils.split(type, ")");
                        String[] params = StringUtils.split(split[0], ',');
                        for (int i = 0; i < params.length; i++) {
                            params[i] = params[i].substring(params[i].lastIndexOf(".") + 1);
                            log.info(params[i]);

                        }
                        split[1] = split[1].substring(split[1].lastIndexOf(".") + 1);
                        StringUtils.strip(split[1], "(>");
                        stringRefs.add(split[1]);
                        stringRefs.addAll(Arrays.asList(params));
                    } // is field
                    else {
                        stringRefs.add(type.substring(type.lastIndexOf(".") + 1));
                    }
            }
        }
        refs.put(e.getSimpleName().toString(), stringRefs);
        ArrayList<ComponentRepresentation> cr;
        for (Pattern p : com.patterns()) {
            ComponentRepresentation comRep = new ComponentRepresentation(e.getSimpleName().toString(), p.kind(), p.role());
            // check if the hashmap contains pattern p, if is does check if the list allready
            // contains the the component and extend the components references
            // else just add component
            if (components.containsKey(p.name())) {
                cr = (ArrayList<ComponentRepresentation>) components.get(p.name());
                boolean wasThere = false;
                /**
                 * for (ComponentRepresentation crIns : cr) {
                 * if(crIns.getComponentName().equals(comRep.getComponentName())){
                 * crIns.extendReferences(comRep.getRefreferences()); wasThere =
                 * true; } }
                 *
                 */
                if (!wasThere) {
                    cr.add(comRep);
                }
            } else {
                cr = new ArrayList<>();
                components.put(p.name(), cr);
                cr.add(comRep);
            }
        }

    }


    /**
     *
     * @param com
     * @param e
     */
    private void debugComponent(ArchTypeComponent com, Element e){
        log.debug("{");
        log.debug("Component: ");
        log.debug("\tname: " + e.getSimpleName());
        log.debug( "\tfileName: " + e.getSimpleName());
        for(Pattern p: com.patterns()){
            log.debug("\t\t{");
            log.debug("\t\t\tPattern:");
            log.debug("\t\t\t\tname: " + p.name());
            log.debug("\t\t\t\tkind: " + p.kind());
            log.debug("\t\t\t\trole: " + p.role());
            log.debug("\t\t\t\trefs: {" );
            log.debug("\t\t\t\t}");
            log.debug("\t\t}");
        }
        log.debug("}");
    }
}
