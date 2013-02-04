/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.testpatterns;

import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;

/**
 *
 * @author asger
 */
@ArchTypeComponent(
        patterns = {@Pattern(name="testMvc", kind = "MVC", role="Model")}
        )
public class TestModel {
    TestController3 testControlModel;
    

    public TestModel() {
    }

    int getAViewTest(ViewTest vt){
        vt.getClass();
       return 1;
    }

    ViewTest getView(){
        return new ViewTest();
    }

    /**
     * Cannot catch this :(
     */
    void someMethodWithView(){
        ViewTest vt = new ViewTest();
    }

}
