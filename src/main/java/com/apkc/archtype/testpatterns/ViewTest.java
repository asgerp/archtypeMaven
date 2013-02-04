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
        patterns = {@Pattern(name="testMvc", kind = "MVC", role="View")}
        )
public class ViewTest {
    TestController testControlModel;

    public ViewTest() {
    }


}
