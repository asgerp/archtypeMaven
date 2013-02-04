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
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
        )
public class Layer2 {
    Layer1 layer1;
    //Layer3 layer3;

    public Layer2() {
    }

    public Layer1 getLayer1() {
        return layer1;
    }

    public void setLayer1(Layer1 layer1, Layer3 l3) {
        this.layer1 = layer1;
    }

    public void getLayer3() {
        //return layer3;
    }

    //public void setLayer3(Layer3 layer3) {
        //this.layer3 = layer3;
    //}

    

}
