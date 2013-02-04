/* Alloy Analyzer 4 -- Copyright (c) 2006-2009, Felix Chang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.apkc.archtype.alloy;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4.Util;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4viz.VizGUI;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class demonstrates how to access Alloy4 via the API.
 */
public final class AlloyTest {

    // patterns we'll be supporting, extend as necessary
    private enum Pattern {
        MVC, NOTMVC
    }

    /**
     * @param filename - the file to pass to alloy for interpreting
     */
    public static void passToAlloy(String filename) {
        String pattern = filename.substring(0,filename.indexOf("_"));
        System.out.println(pattern);
        InputStream input = AlloyTest.class.getResourceAsStream("/alloy_models/" + pattern.toLowerCase() + ".als");

        // boilerplate alloy4 reporter code
        A4Reporter rep = new A4Reporter() {
            // here we choose to display each "warning" by printing it to System.out
            @Override
            public void warning(ErrorWarning msg) {
                System.out.print("Relevance Warning:\n" + (msg.toString().trim()) + "\n\n");
                System.out.flush();
            }
        };
        // Parse+typecheck the model
        System.out.println("=========== Parsing+Typechecking " + filename + " ===========");
        Module world = null;
        try {
            world = CompUtil.parseEverything_fromFile(rep, null, filename);
        } catch (Err ex) {
            Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Choose some default options for how you want to execute the commands
        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;
        // FIXME: figure out folders and shit
        //options.tempDirectory
        for (Command command : world.getAllCommands()) {
            // Execute the command
            System.out.println("=========== Command " + command + ": ===========");
            A4Solution ans = null;
            try {
                ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);
            } catch (Err ex) {
                Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Print the outcome


            if(ans.satisfiable()){
                System.out.println(command + " failed");

            } else {
                System.out.println(command + " passed");
            }
        }
    }

    /**
     * @param model - the model to pass to alloy for interpreting
     */
    public static void passStrToAlloy(String model) throws Err {
        String pattern = model.substring(7,model.indexOf('\n'));
        //InputStream input = AlloyTest.class.getResourceAsStream("/alloy_models/" + pattern.toLowerCase() + ".als");
        File f = null;
        try {
            f = File.createTempFile(pattern, ".als");
        } catch (IOException ex) {
            Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Util.writeAll(f.getAbsolutePath(), model);
        // boilerplate alloy4 reporter code
        A4Reporter rep = new A4Reporter() {
            // here we choose to display each "warning" by printing it to System.out
            @Override
            public void warning(ErrorWarning msg) {
                System.out.print("Relevance Warning:\n" + (msg.toString().trim()) + "\n\n");
                System.out.flush();
            }
        };
        // Parse+typecheck the model
        System.out.println("=========== Parsing+Typechecking " + pattern + " ===========");
        Module world = null;
        try {
            world = CompUtil.parseEverything_fromFile(rep, null, f.getAbsolutePath());
            //world = CompUtil.parseOneModule(model);
        } catch (Err ex) {
            Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Choose some default options for how you want to execute the commands
        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;
        options.skolemDepth = 1;
        // FIXME: figure out folders and shit
        //options.tempDirectory
        for (Command command : world.getAllCommands()) {
            // Execute the command
            System.out.println("=========== Command " + command + ": ===========");
            A4Solution ans = null;
            try {
                ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);
            } catch (Err ex) {
                Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Print the outcome

            if(ans == null){
                System.out.println(command + " superfailed");
            }else{
                if(ans.satisfiable()){
                    System.out.println(command + " failed");

                } else {
                    System.out.println(command + " passed");
                }
            }
        }
    }


    public static void main(String[] args) throws Err {
        // quick test
        String path = null;
        try {
            path = new java.io.File(".").getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        VizGUI vg = new VizGUI(true,path+"/result.xml",null);
        vg.doShowViz();
        //String filename = "alloy_models/testMvc_configuration.als";
        //passToAlloy(filename);
    }
}
