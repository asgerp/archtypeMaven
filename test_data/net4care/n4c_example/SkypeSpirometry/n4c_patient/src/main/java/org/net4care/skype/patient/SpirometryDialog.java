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
 
import javax.swing.JLabel; 
import javax.swing.JOptionPane; 
import javax.swing.JDialog; 
import javax.swing.JTextField; 
import java.beans.*; //property change stuff 
import java.awt.*; 
import java.awt.event.*; 
  
 
class SpirometryDialog extends JDialog 
                   implements ActionListener, 
                              PropertyChangeListener { 
	 
    /** 
	 *  
	 */ 
	private static final long serialVersionUID = 1487349202908712285L; 
 
	private boolean validated = false; 
     
    private Double fvc = 0.0; 
    private Double fev1 = 0.0; 
     
    private JTextField textFieldfvc; 
    private JTextField textFieldfev1; 
  
    private JOptionPane optionPane; 
  
    private String btnString1 = "Send"; 
    private String btnString2 = "Cancel"; 
  
    /** 
     * Returns null if the typed string was invalid; 
     * otherwise, returns the string as the user entered it. 
     */ 
    public Double[] getValidatedText() { 
    	if (validated) { 
    		return new Double[]{fvc, fev1}; 
    	} 
    	return null; 
    } 
  
    /** Creates the reusable dialog. */ 
    public SpirometryDialog(Frame aFrame) { 
        super(aFrame, true); 
 
        setTitle("Spirometry measurement"); 
  
        textFieldfvc = new JTextField(3); 
        textFieldfev1 = new JTextField(3); 
  
        //Create an array of the text and components to be displayed. 
        String msgString1 = "Type in measured values"; 
 
        Object[] array = {msgString1, new JLabel("FVC:"), textFieldfvc, new JLabel("FEV1:"), textFieldfev1}; 
  
        //Create an array specifying the number of dialog buttons 
        //and their text. 
        Object[] options = {btnString1, btnString2}; 
  
        //Create the JOptionPane. 
        optionPane = new JOptionPane(array, 
                                    JOptionPane.QUESTION_MESSAGE, 
                                    JOptionPane.YES_NO_OPTION, 
                                    null, 
                                    options, 
                                    options[0]); 
  
        //Make this dialog display it. 
        setContentPane(optionPane); 
  
        //Handle window closing correctly. 
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
        addWindowListener(new WindowAdapter() { 
                public void windowClosing(WindowEvent we) { 
                /* 
                 * Instead of directly closing the window, 
                 * we're going to change the JOptionPane's 
                 * value property. 
                 */ 
                    optionPane.setValue(new Integer( 
                                        JOptionPane.CLOSED_OPTION)); 
            } 
        }); 
  
        //Ensure the text field always gets the first focus. 
        addComponentListener(new ComponentAdapter() { 
            public void componentShown(ComponentEvent ce) { 
            	textFieldfvc.requestFocusInWindow(); 
            } 
        }); 
  
        //Register an event handler that puts the text into the option pane. 
        textFieldfvc.addActionListener(this); 
  
        //Register an event handler that reacts to option pane state changes. 
        optionPane.addPropertyChangeListener(this); 
    } 
  
    /** This method handles events for the text field. */ 
    public void actionPerformed(ActionEvent e) { 
        optionPane.setValue(btnString1); 
    } 
  
    /** This method reacts to state changes in the option pane. */ 
    public void propertyChange(PropertyChangeEvent e) { 
        String prop = e.getPropertyName(); 
  
        if (isVisible() 
         && (e.getSource() == optionPane) 
         && (JOptionPane.VALUE_PROPERTY.equals(prop) || 
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) { 
            Object value = optionPane.getValue(); 
  
            if (value == JOptionPane.UNINITIALIZED_VALUE) { 
                //ignore reset 
                return; 
            } 
  
            //Reset the JOptionPane's value. 
            //If you don't do this, then if the user 
            //presses the same button next time, no 
            //property change event will be fired. 
            optionPane.setValue( 
                    JOptionPane.UNINITIALIZED_VALUE); 
  
            StringBuilder errors = new StringBuilder(); 
             
            if (btnString1.equals(value)) { 
                 
            	try { 
					fev1 = Double.parseDouble(textFieldfev1.getText()); 
					if (!(fev1 > 1.00 && fev1 < 10.00)) { 
						errors.append("fev1 must be in the range 1.00 to 10.00 \n"); 
					} 
				} catch (Exception e2) { 
					errors.append("fev1 must be a numeric value \n"); 
				} 
            	 
            	try { 
					fvc = Double.parseDouble(textFieldfvc.getText()); 
					if (!(fvc > 1.00 && fvc < 10.00)) { 
						errors.append("fvc must be in the range 1.00 to 10.00 \n"); 
					} 
				} catch (Exception e2) { 
					errors.append("fvc must be a numeric value \n"); 
				} 
                 
                if (errors.toString().equals("")) { 
                    //we're done; clear and dismiss the dialog 
                	validated = true; 
                    clearAndHide(); 
                } else { 
                    //text was invalid 
                	textFieldfvc.selectAll(); 
         
                    JOptionPane.showMessageDialog( 
                    		SpirometryDialog.this, 
                                    errors.toString(), 
                                    "Try again", 
                                    JOptionPane.ERROR_MESSAGE); 
                    validated = false; 
                    textFieldfvc.requestFocusInWindow(); 
                } 
            } else { //user closed dialog or clicked cancel 
            	validated = false; 
                clearAndHide(); 
            } 
        } 
    } 
  
    /** This method clears the dialog and hides it. */ 
    public void clearAndHide() { 
    	textFieldfvc.setText(null); 
    	textFieldfev1.setText(null); 
        setVisible(false); 
    } 
}