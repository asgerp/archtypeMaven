/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import com.apkc.archtype.App;
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        log.debug(keep_processing);
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

        
        setUpReferences(references, components);
        File f = new File(temp_file_path + "components.ser");
        log.debug(f.exists());
        if(claimed ){
            HashMap<String, ArrayList<ComponentRepresentation>> allComponents = new HashMap<>();
            if(f.exists()) {
                HashMap<String, ArrayList<ComponentRepresentation>> readComponents = ProcessorUtils.readFromFile(f);
                allComponents.putAll(readComponents);
            }
            allComponents.putAll(components);
            ProcessorUtils.writeTofile(allComponents, f);
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
                    if (type.contains(")")) {
                        String[] params = StringUtils.split(type.substring(type.lastIndexOf(".") + 1, type.lastIndexOf(")") + 1), ',');
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

    /**
     *
     * @param references
     * @param components
     */
    private void setUpReferences(HashMap references, HashMap components) {
        Iterator iterator = references.entrySet().iterator();
        // Iterate over each annotated class/type
        while (iterator.hasNext()) {
            Map.Entry next = (Map.Entry) iterator.next();
            // The annotated class
            String anClass = (String) next.getKey();
            Iterator compIt = components.entrySet().iterator();
            // List of perhaps unannotated references
            ArrayList<String> annotatedRefs = (ArrayList<String>) next.getValue();
            // Iterate over each pattern(ComponentRepresentation)
            while (compIt.hasNext()) {
                // The pattern
                Map.Entry pattern = (Map.Entry) compIt.next();
                // Get the list of component representations in the pattern
                ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) pattern.getValue();
                Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                boolean inPattern = false;
                ComponentRepresentation anClassCr = null;
                // is the annotated class in pattern?
                // each class in pattern
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    if (c.getComponentName().equals(anClass)) {
                        inPattern = true;
                        anClassCr = c;
                    }
                }
                ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    // extend the annotated class's references with other annotated classes found in class
                    if (annotatedRefs.contains(c.getComponentName()) && inPattern) {
                        anClassCr.extendReferences(c.getComponentName());
                    }
                }
            }
        }
    }
}
