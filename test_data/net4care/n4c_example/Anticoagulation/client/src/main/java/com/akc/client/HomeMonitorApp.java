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
 
package com.akc.client; 
 
import java.awt.BorderLayout; 
import java.awt.Container; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.io.IOException; 
import java.net.MalformedURLException; 
 
import java.util.List; 
 
import javax.swing.Box; 
import javax.swing.BoxLayout; 
import javax.swing.JButton; 
import javax.swing.JComponent; 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JOptionPane; 
import javax.swing.JTextField; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.ServerJSONSerializer; 
import org.net4care.forwarder.DataUploader; 
import org.net4care.forwarder.FutureResult; 
import org.net4care.forwarder.Query; 
import org.net4care.forwarder.QueryResult; 
import org.net4care.forwarder.delegate.HttpConnector; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.forwarder.query.QueryPersonTimeInterval; 
import org.net4care.observation.Codes; 
import org.net4care.observation.DeviceDescription; 
import org.net4care.observation.StandardTeleObservation; 
 
import org.net4care.graph.*; 
import org.net4care.graph.delegate.*; 
 
import com.akc.data.Anticoagulation; 
 
 
/**  
 * Stand alone Home Monitoring demo application with graphical feedback.<br /> 
 * Designed for the displaying and reporting data for chronic diseases 
 * where the patient receives anticoagulating medicine.  <br /> 
 *   
 * Author: Morten Larsson (MLN), AU 
 */ 
 
public class HomeMonitorApp extends JFrame { 
 
  private static final long serialVersionUID = 1L; 
 
  public static void main(String[] args) { 
    new HomeMonitorApp(); 
  } 
 
  String[] ptDatabase = new String[] { 
      "Nancy Berggren", "251248-4916", 
      "Jens Hansen", "120753-2355", 
      "Birgitte Roenholt", "030167-1648", 
  }; 
 
  private DataUploader dataUploader; 
  private Serializer serializer; 
 
  private JTextField INRTextField, NumOfTablesTextField, timestampTextField; 
  private double INR = 0.0, numOfTablets = 0.0; 
  private long timestamp = 0L; 
  private String ptName; 
  private String ptCPR; 
  private int ptToggle; 
  private int counter = 0; 
 
  private JLabel nameLabel, cprLabel; 
   
  private StandardObservationGraph outputGraph; 
   
  public HomeMonitorApp() { 
    super("Home Monitoring / Net4Care" ); 
 
    outputGraph = new StandardObservationGraph(); 
     
    JFrame.setDefaultLookAndFeelDecorated(true); 
    setLocation( 100, 20 ); 
 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
 
    Container cpane = getContentPane();  
    cpane.setLayout( new BorderLayout() ); 
    cpane.add( createInfoPanel(), BorderLayout.NORTH ); 
    cpane.add( createCoagDataPanel(), BorderLayout.CENTER ); 
    cpane.add( createUploadDataPanel(), BorderLayout.SOUTH ); 
    ptToggle = 0; 
    setPttData(ptToggle); 
 
    serializer = new ServerJSONSerializer(); 
    try { 
      dataUploader = new StandardDataUploader(serializer, new HttpConnector("http://localhost:8080/observation")); 
    } catch (MalformedURLException e) { 
      e.printStackTrace(); 
    } 
 
    pack(); 
    setVisible(true); 
  } 
   
  private void updateValues() { 
    timestamp = Long.valueOf(timestampTextField.getText()); 
    INR = Double.valueOf(INRTextField.getText()); 
    numOfTablets = Double.valueOf(NumOfTablesTextField.getText()); 
  } 
   
  private void updateFields() { 
    INRTextField.setText(""+INR); 
    NumOfTablesTextField.setText(""+numOfTablets); 
    timestampTextField.setText(timestamp+""); 
  } 
 
  private void setPttData(int i) { 
    ptName = ptDatabase[2*i]; ptCPR = ptDatabase[2*i+1]; 
    nameLabel.setText(ptName); 
    cprLabel.setText(ptCPR); 
  } 
 
  private JComponent createInfoPanel() { 
    Box p = new Box( BoxLayout.Y_AXIS ); 
    p.add( nameLabel = new JLabel("Name: Nancy Berggren") ); 
    p.add( cprLabel = new JLabel("CPR: "+ptCPR ) ); 
 
    JButton toggleButton = new JButton ( "[change ptt]"); 
    p.add(toggleButton); 
    toggleButton.addActionListener( new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        ptToggle = (ptToggle+1) % (ptDatabase.length/2); 
        setPttData(ptToggle); 
      } 
    }); 
 
    JButton measurebutton =  new JButton("Take anticoagulation measurement..."); 
    p.add( measurebutton ); 
    measurebutton.addActionListener( new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        makeCoagulationMeasurementFromUSBAttachedInstrument(); 
        updateFields(); 
      } 
 
    }); 
     
     
    return p; 
  } 
 
  private JComponent createCoagDataPanel() { 
    Box p = new Box( BoxLayout.X_AXIS ); 
    p.add( new JLabel("Anticoagulation:") ); 
    p.add( INRTextField = new JTextField(""+INR) ); 
    p.add( new JLabel("Tablets:") ); 
    p.add( NumOfTablesTextField = new JTextField(""+numOfTablets) ); 
    p.add( new JLabel("Timestamp:") ); 
    p.add( timestampTextField = new JTextField(""+timestamp)); 
    return p; 
  } 
 
  private JComponent createUploadDataPanel() { 
    Box panel = new Box( BoxLayout.Y_AXIS );    
    JButton uploadButton = new JButton("Transfer to Net4Care server" ); 
    uploadButton.addActionListener( new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        updateValues(); 
        Anticoagulation sp = new Anticoagulation( INR, numOfTablets ); 
        DeviceDescription devdesc = new DeviceDescription("Anticoagulation", "CoaguCheck-XS", "Roche", "1", "1", "1.0", "1.0"); 
        StandardTeleObservation sto = new StandardTeleObservation(ptCPR, "MyOrgID", "myTreatmentId", Codes.LOINC_OID, devdesc, sp ); 
        timestamp--; 
        updateFields(); 
        sto.setTime(timestamp); 
        FutureResult result; 
        try { 
          result = dataUploader.upload(sto); 
          result.awaitUninterruptibly(); 
        } catch (IOException e1) { 
        	JOptionPane.showMessageDialog(null , "Currently unable to connect to server"); 
        } 
      } 
    }); 
     
     
    JLabel outputlabel = new JLabel("Retrieved Data from Server"); 
     
    JButton downloadButton = new JButton("Retrieve data from server"); 
    downloadButton.addActionListener( new ActionListener() { 
       
      @Override 
      public void actionPerformed(ActionEvent arg0) { 
        queryServerAndPutIntoOutputField();       
      } 
    }); 
     
    panel.add(uploadButton); 
    panel.add(outputlabel); 
    panel.add(outputGraph.getGraphComponent()); 
    panel.add(downloadButton); 
     
    return panel; 
  } 
 
  private void makeCoagulationMeasurementFromUSBAttachedInstrument() { 
    double coagdelta = (int) (10* Math.random()) / 10.0; 
    double tabletsdelta = (int) (10* Math.random()) / 10.0; 
    INR = 1.5+coagdelta; numOfTablets = 1+tabletsdelta; 
    updateTimestamp(); 
  } 
   
  private void updateTimestamp() { 
    timestamp = System.currentTimeMillis() - counter*1000*60*60*25; 
    counter++; 
  } 
   
  private void queryServerAndPutIntoOutputField() { 
    Query query; QueryResult res = null; 
    List<StandardTeleObservation> obslist; 
 
    // Query for all data. 
    long now = System.currentTimeMillis(); 
    query = new QueryPersonTimeInterval(ptCPR, 0, now); 
    try { 
        res = dataUploader.query(query); 
        res.awaitUninterruptibly(); 
    } catch ( Exception e ) { 
    	JOptionPane.showMessageDialog(null , "Currently unable to connect to server"); 
    } 
    obslist = res.getObservationList(); 
 
    outputGraph.plotData(obslist); 
     
     
  } 
} 
