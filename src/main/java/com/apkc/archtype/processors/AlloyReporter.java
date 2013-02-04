/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

/**
 * This class represents whether the a pattern has passed alloy check.
 * @author asger
 */
public class AlloyReporter {
    public boolean passed;
    public String message;
    
    public AlloyReporter(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
    }


}
