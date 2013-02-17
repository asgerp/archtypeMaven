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
 
package com.smb.client; 
 
import java.awt.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.MalformedURLException; 
import java.security.KeyManagementException; 
import java.security.KeyStoreException; 
import java.security.NoSuchAlgorithmException; 
import java.security.cert.CertificateException; 
import java.sql.Date; 
import java.text.SimpleDateFormat; 
import java.util.List; 
 
import javax.swing.*; 
 
import org.net4care.forwarder.*; 
import org.net4care.forwarder.delegate.*; 
import org.net4care.forwarder.query.QueryPersonTimeInterval; 
import org.net4care.observation.*; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
 
import com.smb.data.Spirometry; 
 
/**  
 * Stand alone Home Monitoring demo application 
 
    Author: Henrik Baerbak Christensen 2011, Net4Care 
 */ 
 
public class HomeMonitorApp extends JFrame { 
  /** 
   *  
   */ 
  private static final long serialVersionUID = 1L; 
 
  public static void main(String[] args) throws IOException { 
    new HomeMonitorApp(); 
     
  } 
 
  String[] ptDatabase = new String[] { 
      "Nancy Berggren", "251248-4916", 
      "Jens Hansen", "120753-2355", 
      "Birgitte Roenholt", "030167-1648", 
      "Bahar Soomekh", "250255-1234", 
  }; 
 
 
  private DataUploader dataUploader; 
  private Serializer serializer; 
 
  private JTextField fvcField, fev1Field; 
  private double fvc = 0.0, fev1 = 0.0; 
  private String ptName; 
  private String ptCPR; 
  private int ptToggle; 
 
  private JLabel nameLabel, cprLabel; 
   
  private JTextArea output; 
 
  // For local testing, change this address if you 
  // deploy on some remote host. 
  private String serverAddress = "http://localhost:8080/observation"; 
 
  public HomeMonitorApp() throws IOException { 
    super("Home Monitoring / Net4Care" ); 
 
    JFrame.setDefaultLookAndFeelDecorated(true); 
    setLocation( 100, 20 ); 
 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
 
    Container cpane = getContentPane();  
    cpane.setLayout( new BorderLayout() ); 
 
    cpane.add( createInfoPanel(), BorderLayout.NORTH ); 
    cpane.add( createSpiroDataPanel(), BorderLayout.CENTER ); 
    cpane.add( createUploadDataPanel(), BorderLayout.SOUTH ); 
 
    ptToggle = 0; 
    setPttData(ptToggle); 
 
    serializer = new JacksonJSONSerializer();     
    try { 
      dataUploader = new StandardDataUploader(serializer, new HttpConnector( serverAddress )); 
    } catch (MalformedURLException e) { 
	      e.printStackTrace(); 
	      System.exit(1); 
    } 
 
    pack(); 
    setVisible(true); 
  } 
 
  private void setPttData(int i) { 
    ptName = ptDatabase[2*i]; ptCPR = ptDatabase[2*i+1]; 
    nameLabel.setText(ptName); 
    cprLabel.setText(ptCPR); 
  } 
 
  private JComponent createInfoPanel() { 
    Box p = new Box( BoxLayout.Y_AXIS ); 
	p.add( new JLabel("Server URL = "+serverAddress ) ); 
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
 
    JButton measurebutton =  new JButton("Take spirometry measurement..."); 
    p.add( measurebutton ); 
    measurebutton.addActionListener( new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        makeSpirometerMeasurementFromUSBAttachedInstrument(); 
        fev1Field.setText(""+fev1); 
        fvcField.setText(""+fvc); 
      } 
 
    }); 
     
     
    return p; 
  } 
 
  private JComponent createSpiroDataPanel() { 
    Box p = new Box( BoxLayout.X_AXIS ); 
    p.add( new JLabel("FVC:") ); 
    p.add( fvcField = new JTextField(""+fvc) ); 
    p.add( new JLabel("FEV1:") ); 
    p.add( fev1Field = new JTextField(""+fev1) ); 
    return p; 
  } 
 
  private JComponent createUploadDataPanel() { 
    Box panel = new Box( BoxLayout.Y_AXIS ); 
     
    JButton uploadButton = new JButton("Transfer to Net4Care server" ); 
    uploadButton.addActionListener( new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        Spirometry sp = new Spirometry( fvc, fev1 ); 
        DeviceDescription devdesc = new DeviceDescription("Spirometry", "MODEL1", "Manufac1", "1", "1", "1.0", "1.0"); 
        StandardTeleObservation sto = new StandardTeleObservation(ptCPR, "MyOrgID", "myTreatmentId", Codes.LOINC_OID, devdesc, sp ); 
        FutureResult result; 
        try { 
          result = dataUploader.upload(sto); 
          result.awaitUninterruptibly(); 
        } catch (IOException e1) { 
          output.setText("Currently unable to connect to server"); 
        } 
      } 
    });  
     
    JLabel outputlabel = new JLabel("Retrieved Data from Server"); 
    output = new JTextArea(20,60); 
    output.setText("No data yet"); 
     
    JButton downloadButton = new JButton("Retrieve data from server"); 
    downloadButton.addActionListener( new ActionListener() { 
       
      @Override 
      public void actionPerformed(ActionEvent arg0) { 
    	  try { 
    		  queryServerAndPutIntoOutputField(); 
    	  } catch (Net4CareException e) { 
    		  if(e.getErrorCode() == Constants.ERROR_UNKNOWN_CPR) 
    			  output.setText("Unknown CPR"); 
    		  else { 
    			  output.setText("NET4CARE exception."); 
    		  } 
    	  } 
      } 
    }); 
     
    panel.add(uploadButton); 
    panel.add(outputlabel); 
    panel.add(output); 
    panel.add(downloadButton); 
     
    return panel; 
  } 
 
  private void makeSpirometerMeasurementFromUSBAttachedInstrument() { 
    double fev1delta = (int) (10* Math.random()) / 10.0; 
    double fvcdelta = (int) (10* Math.random()) / 10.0; 
    fvc = 3.4+fvcdelta; fev1 = 3.0+fev1delta; 
  } 
   
  private void queryServerAndPutIntoOutputField() throws Net4CareException { 
    Query query; QueryResult res = null; 
    List<StandardTeleObservation> obslist; 
 
    // Query for the last 10 days 
    long now = System.currentTimeMillis(); 
    long tendaysago = now - 1000L* 3600L * 24L; 
    query = new QueryPersonTimeInterval(ptCPR, tendaysago, now); 
    // System.out.println("Formulated query: "+query); 
    
    try { 
			res = dataUploader.query(query); 
			res.awaitUninterruptibly(); 
		} catch (IOException e) { 
			 output.setText("Currently unable to connect to server"); 
			e.printStackTrace(); 
		} catch (Net4CareException e) { 
			output.setText("Encountered a Net4Care exception \n\n Please inspect the console for details"); 
			e.printStackTrace(); 
		} 
         
  
    obslist = res.getObservationList(); 
    // System.out.println("The obs list length = "+obslist.size()); 
 
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
     
    int i = 0; 
    String result = ""; 
    for ( StandardTeleObservation sto : obslist ) { 
      long timestamp = sto.getTime();  
      //System.out.println(" stamp = "+timestamp); 
      Date d = new Date(timestamp); 
      Spirometry spiro = (Spirometry) sto.getObservationSpecifics(); 
      String nicedate = sdf.format( d ); 
      result += nicedate+" FVC="+spiro.getFvc().getValue()+spiro.getFvc().getUnit()+ 
          " FEV1="+spiro.getFev1().getValue()+spiro.getFev1().getUnit()+"\n"; 
      i++; 
      if ( i > 38 ) break; 
    } 
    output.setText(result); 
 
  } 
 
}