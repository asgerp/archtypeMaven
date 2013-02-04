package com.apkc.archtype;

import com.apkc.archtype.alloy.AlloyTest;
import com.apkc.archtype.processors.ComponentRepresentation;
import com.apkc.archtype.processors.ProcessorUtils;
import edu.mit.csail.sdg.alloy4.Err;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;


public class App
{
    final static Logger log = Logger.getLogger(App.class.getName());

    public static void main( String[] args ) throws NoSuchFieldException
    {
        App.processFromFile();

    }

    public static void processFromFile(){
        File f = new File("components.ser");
        HashMap<String, ArrayList<ComponentRepresentation>> allComponents = ProcessorUtils.readFromFile(f);
        ArrayList<String> models = ProcessorUtils.generateAlloyModelsStr(allComponents);
        for (String model : models) {
            try {
                // should return a data structure that encapsulates whether the check passed or not and a message
                //AlloyTest.passToAlloy(model);
                AlloyTest.passStrToAlloy(model);
                //AlloyTest.passToAlloy("alloy_test.als");
            } catch (Err ex) {
                log.error(ex);
            }
        }

    }
}
