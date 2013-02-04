/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.testpatterns;

import com.apkc.archtype.quals.*;
/**
 *
 * @author asger
 */
@ArchTypeComponent(
        patterns = {@Pattern(name="testMvc",kind = "MVC", role="Controller")}
        )
public class TestController3 {
    TestController testControlControl;
    TestController testViewControl;
    TestModel testModelControl;


    public TestController3() {
    }

    TestController getController(){
        return testControlControl;
    }

}
