/*
 * Copyright 2012 Net4Care, www.net4care.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
package org.net4care.skype.patient; 
 
import java.awt.BorderLayout; 
import java.awt.Button; 
import java.awt.Dialog; 
import java.awt.Dimension; 
import java.awt.FlowLayout; 
import java.awt.Frame; 
import java.awt.Label; 
import java.awt.Panel; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
 
public class MsgboxDialog extends Dialog implements ActionListener { 
    private Button ok,can; 
    public boolean isOk = false; 
 
    /* 
     * @param frame   parent frame  
     * @param msg     message to be displayed 
     * @param okcan   true : ok cancel buttons, false : ok button only  
     */ 
    MsgboxDialog(Frame frame, String msg, boolean okcan){ 
        super(frame, "Message", true); 
        setLayout(new BorderLayout()); 
        add("Center",new Label(msg)); 
        addOKCancelPanel(okcan); 
        createFrame(); 
        pack(); 
        setVisible(true); 
    } 
     
    MsgboxDialog(Frame frame, String msg){ 
        this(frame, msg, false); 
    } 
     
    void addOKCancelPanel( boolean okcan ) { 
        Panel p = new Panel(); 
        p.setLayout(new FlowLayout()); 
        createOKButton( p ); 
        if (okcan == true) 
            createCancelButton( p ); 
        add("South",p); 
    } 
 
    void createOKButton(Panel p) { 
        p.add(ok = new Button("OK")); 
        ok.addActionListener(this);  
    } 
 
    void createCancelButton(Panel p) { 
        p.add(can = new Button("Cancel")); 
        can.addActionListener(this); 
    } 
 
    void createFrame() { 
        Dimension d = getToolkit().getScreenSize(); 
        setLocation(d.width/3,d.height/3); 
    } 
 
    public void actionPerformed(ActionEvent ae){ 
        if(ae.getSource() == ok) { 
            isOk = true; 
            setVisible(false); 
        } 
        else if(ae.getSource() == can) { 
            setVisible(false); 
        } 
    } 
 
}