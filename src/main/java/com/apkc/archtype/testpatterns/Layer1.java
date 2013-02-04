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
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{1}")}
        )
public class Layer1 {
    Layer2 layer2;

    public Layer1() {
    }

    public Layer2 getLayer2() {
        return layer2;
    }

    public void setLayer2(Layer2 layer2) {
        this.layer2 = layer2;
    }
    
}
