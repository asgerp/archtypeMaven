/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;




/**
 *
 * @author asger
 */
public class ComponentRepresentation implements Serializable{
    private String componentName;
    private String pattern;
    private String role;
    private ArrayList<String> references;

    /**
     *
     * @param componentName
     * @param pattern
     * @param role
     * @param references
     */
    public ComponentRepresentation(String componentName, String pattern, String role) {
        this.componentName = componentName;
        this.pattern = pattern;
        this.role = role;
        this.references = new ArrayList<>();
    }

    public String[] getMetaData(){
        String name = role;
        if(name.contains("{")){
            String metas = StringUtils.substringBetween(name, "{","}");
            return StringUtils.split(metas,"_");
        }
        return null;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getPattern() {
        return pattern;
    }

    public String getRole() {
        if(role.contains("{")){
            return StringUtils.substringBefore(role, "{");
        }
        return role;
    }

    public ArrayList<String> getRefreferences() {
        return references;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRefererences(ArrayList<String> refererences) {
        this.references = refererences;
    }

    public void extendReferences(ArrayList<String> extraRefs){
        for (String ref : extraRefs) {
            if(!references.contains(ref)){
                references.add(ref);
            }
        }
    }
    public void extendReferences(String extraRef){
        if(!references.contains(extraRef)){
            references.add(extraRef);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("one sig ").append(componentName).append(" extends ").append(getRole()).append(" { } {\n");
        sb.append("\treferences = ");
        Iterator<String> it = references.iterator();
        while( it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(" + ");
            } else{
                sb.append("\n");
            }
        }
        String[] meta = getMetaData();
        if(meta != null){
            sb.append("\tmeta = ");
            for (int i = 0; i < meta.length; i++) {
                sb.append(meta[i]);
            }
        }
        sb.append("\n");
        sb.append("}\n");
        return sb.toString();
        //return "ComponentRepresentation{" + "componentName=" + componentName + ", pattern=" + pattern + ", role=" + role + ", refreferences=" + references + '}';
    }
}
