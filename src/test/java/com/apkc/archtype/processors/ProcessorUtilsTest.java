/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author asger
 */
public class ProcessorUtilsTest extends TestCase {

    public ProcessorUtilsTest(String testName) {
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
     * Test of generateAlloyModelsStr method, of class ProcessorUtils.
     */
    public void testGenerateAlloyModelsStr() {
        System.out.println("generateAlloyModelsStr");
        HashMap components = null;
        ArrayList expResult = null;
        ArrayList result = ProcessorUtils.generateAlloyModelsStr(components);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeTofile method, of class ProcessorUtils.
     */
    public void testWriteTofile() {
        System.out.println("writeTofile");
        HashMap<String, ArrayList<ComponentRepresentation>> components = null;
        File f = null;
        ProcessorUtils.writeTofile(components, f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readFromFile method, of class ProcessorUtils.
     */
    public void testReadFromFile() {
        System.out.println("readFromFile");
        File f = null;
        HashMap expResult = null;
        HashMap result = ProcessorUtils.readFromFile(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
