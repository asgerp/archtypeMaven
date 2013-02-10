/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 *
 * @author asger
 */
public class ComponentRepresentationTest extends TestCase {

    public ComponentRepresentationTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getMetaData method, of class ComponentRepresentation.
     */
    public void testGetMetaData() {
        System.out.println("getMetaData");
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        String[] expResult = {"1"};
        String[] result = instance.getMetaData();
        assertTrue(Arrays.equals(result, expResult));
        
        
    }

    /**
     * Test of getComponentName method, of class ComponentRepresentation.
     */
    public void testGetComponentName() {
        System.out.println("getComponentName");
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        String expResult = "layertest";
        String result = instance.getComponentName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPattern method, of class ComponentRepresentation.
     */
    public void testGetPattern() {
        System.out.println("getPattern");
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        String expResult = "Layered";
        String result = instance.getPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRole method, of class ComponentRepresentation.
     */
    public void testGetRole() {
        System.out.println("getRole");
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        String expResult = "Layer";
        String result = instance.getRole();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRefreferences method, of class ComponentRepresentation.
     */
    public void testGetRefreferences() {
        System.out.println("getRefreferences");
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        ArrayList expResult = new ArrayList();
        ArrayList result = instance.getRefreferences();
        assertEquals(expResult, result);
    }

    /**
     * Test of setComponentName method, of class ComponentRepresentation.
     */
    public void testSetComponentName() {
        System.out.println("setComponentName");
        String componentName = "";
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        instance.setComponentName(componentName);
        
    }

    /**
     * Test of setPattern method, of class ComponentRepresentation.
     */
    public void testSetPattern() {
        System.out.println("setPattern");
        String pattern = "";
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        instance.setPattern(pattern);
    }

    /**
     * Test of setRole method, of class ComponentRepresentation.
     */
    public void testSetRole() {
        System.out.println("setRole");
        String role = "";
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        instance.setRole(role);
    }

    /**
     * Test of setRefererences method, of class ComponentRepresentation.
     */
    public void testSetRefererences() {
        System.out.println("setRefererences");
        ArrayList<String> refererences = null;
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        instance.setRefererences(refererences);
    }

    /**
     * Test of extendReferences method, of class ComponentRepresentation.
     */
    public void testExtendReferences_ArrayList() {
        System.out.println("extendReferences");
        ArrayList<String> extraRefs = new ArrayList<>();
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        instance.extendReferences(extraRefs);
    }

    /**
     * Test of extendReferences method, of class ComponentRepresentation.
     */
    public void testExtendReferences_String() {
        System.out.println("extendReferences");
        String extraRef = "";
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        instance.extendReferences(extraRef);
    }

    /**
     * Test of toString method, of class ComponentRepresentation.
     */
    public void testToString() {
        System.out.println("toString");
        ComponentRepresentation instance = new ComponentRepresentation("layertest", "Layered", "Layer{1}");
        String expResult = "one sig layertest extends Layer { } {\n" +
        "	references = 	meta = 1\n" +
        "}\n";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}
