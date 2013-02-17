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
 
import java.awt.Button; 
import java.awt.FlowLayout; 
import java.awt.Frame; 
import java.awt.TextArea; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.io.IOException; 
import java.net.MalformedURLException; 
import java.text.DateFormat; 
import java.util.Calendar; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.forwarder.DataUploader; 
import org.net4care.forwarder.FutureResult; 
import org.net4care.forwarder.delegate.HttpConnector; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.observation.Codes; 
import org.net4care.observation.DeviceDescription; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.skype.api.Net4CareSession; 
import org.net4care.skype.api.SignInMgr; 
import org.net4care.skype.appkeypair.AppKeyPairMgr; 
 
import com.skype.api.Conversation; 
import com.smb.data.Spirometry; 
 
 
public class PatientApplication { 
 
	public static final String MY_CLASS_TAG = "Patient"; 
 
	private static AppKeyPairMgr myAppKeyPairMgr = new AppKeyPairMgr(); 
 
	private static PatientApplication myPatient = new PatientApplication(); 
	private static Net4CareSession mySession = new Net4CareSession(); 
	 
	private static String ACC_NAME = ""; 
	private static String ACC_PWD = ""; 
	private static String KEYPAIR_PATH = ""; 
	 
	private static String ACC_PHYSICIAN = ""; 
	 
    private DataUploader dataUploader; 
    private Serializer serializer; 
     
    /* 
     * Can be one of the following: 
     *  
     * "Nancy Berggren", "251248-4916", 
     * "Jens Hansen", "120753-2355", 
     * "Birgitte Roenholt", "030167-1648", 
     * "Morten Larsson", "210688-1111", 
     */ 
    private String ptName = "Jens Hansen"; 
    private String ptCPR = "120753-2355"; 
 
     
    public PatientApplication() { 
	    serializer = new JacksonJSONSerializer(); 
	    try { 
	    	/** 
	    	 * NOTICE: The in case http://cyrixmorten.net:8080/observation is not available run an instance on your 
	    	 * own server with a public accessible server. If you do not have such a server take a look at the 
	    	 * net4care site section describing how to make a cloud deployment using Amazon EC2 
	    	 */ 
	      dataUploader = new StandardDataUploader(serializer, new HttpConnector("http://cyrixmorten.net:8080/observation")); 
	    } catch (MalformedURLException e) { 
	      e.printStackTrace(); 
	      System.exit(1); 
	    } 
    } 
 
	public static void main(String[] args) { 
		 
		if (args.length < 4) { 
			Net4CareSession.myConsole.printf("Usage is: accountName accountPassword physicianAccountName pathToKeypair%n%n"); 
		} 
		 
		ACC_NAME = args[0]; 
		ACC_PWD = args[1]; 
		ACC_PHYSICIAN = args[2]; 
		KEYPAIR_PATH = args[3]; 
		 
 
		if ((!myAppKeyPairMgr.resolveAppKeyPairPath(KEYPAIR_PATH)) || 
				(!myAppKeyPairMgr.isValidCertificate())) { 
				return; 
			} 
 
		myPatient.createGUI(); 
 
		Net4CareSession.myConsole.printf( 
				"%s: main - Creating session - Account = %s%n", MY_CLASS_TAG, 
				ACC_NAME); 
		 
		PatientApplication.setTextAreaText("Starting Skype\nIf the system hangs it might be because the Skype runtime is not started"); 
		if (mySession.doCreateSession(MY_CLASS_TAG, ACC_NAME, myAppKeyPairMgr.getPemFilePathname(), new PatientListeners(mySession))) { 
			PatientApplication.setTextAreaText("Connected to skype, now loggin on");	 
		} 
		else { 
			System.exit(1); 
		} 
		 
		 
		Net4CareSession.myConsole.printf("%s: main - Logging in w/ password %s%n", 
				MY_CLASS_TAG, ACC_PWD); 
		if (mySession.mySignInMgr.Login(MY_CLASS_TAG, mySession, ACC_PWD)) { 
			mySession.addAccountToContacts(ACC_PHYSICIAN); 
			PatientApplication.setTextAreaText("The system is ready!"); 
			PatientApplication.buttonToggleEnabled(true); 
			mySession.doGetAuthorizationtRequests(); 
			mySession.doAcceptCalls(); 
 
		} else { 
			PatientApplication.setTextAreaText("Unable to log in!"); 
			Net4CareSession.myConsole.printf("%s: Account for %s does not exist.%n", 
					MY_CLASS_TAG, mySession.myAccountName); 
			PatientApplication.teardown(); 
			System.exit(1); 
		} 
	 
	} 
 
	 
	public static void teardown() { 
		Net4CareSession.myConsole.printf("%s: Cleaning up...%n", MY_CLASS_TAG); 
		if (mySession != null) { 
			textarea.setText("Logging out of Skype\nThe program terminates in a few seconds..."); 
			mySession.mySignInMgr.Logout(MY_CLASS_TAG, mySession); 
			while (SignInMgr.isLoggedIn(mySession.myAccount)) { 
				// wait 
			} 
			mySession.doTearDownSession(); 
		} 
		Net4CareSession.myConsole.printf("%s: Done!%n", MY_CLASS_TAG); 
		System.exit(0); 
	} 
 
	private static TextArea textarea = new TextArea("", 4, 30, TextArea.SCROLLBARS_VERTICAL_ONLY); 
	private static Button button = new Button("Contact the hospital"); 
	private final Frame frame = new Frame("Patient"); 
	 
	public static void setTextAreaText(String text) { 
		textarea.setText(text); 
	} 
	 
	public static void buttonToggleEnabled(boolean enable) { 
		button.setEnabled(enable); 
	} 
	 
	public boolean performingMeasurement; 
 
	void createGUI() { 
		textarea.setEditable(false); 
		button.setEnabled(false); 
		performingMeasurement = false; 
		button.addActionListener(new ActionListener() { 
 
			@Override 
			public void actionPerformed(ActionEvent arg0) { 
				if (!performingMeasurement) { 
					performingMeasurement = true; 
					   Conversation conversation = 
						   mySession.mySkype.getConversationByParticipants(new String[] {ACC_PHYSICIAN}, true, false); 
					    
	            	conversation.postText((" [" + ptName + ":" + ptCPR + "] wishes to perform measurement"), false); 
	            	textarea.setText("Contacting hospital, please wait..."); 
	            	button.setLabel("Do measurement"); 
	            	buttonToggleEnabled(false); 
				} 
				else { 
					 
					SpirometryDialog sd = new SpirometryDialog(frame); 
					sd.pack(); 
					sd.setVisible(true); 
					Double[] measurements = sd.getValidatedText(); 
					if (measurements != null) { 
						performingMeasurement = false; 
						   Conversation conversation = 
								   mySession.mySkype.getConversationByParticipants(new String[] {ACC_PHYSICIAN}, true, false); 
						    
					        Spirometry sp = new Spirometry( measurements[0], measurements[1]); 
					        DeviceDescription devdesc = new DeviceDescription("Spirometry", "MODEL1", "Manufac1", "1", "1", "1.0", "1.0"); 
					        StandardTeleObservation sto = new StandardTeleObservation(ptCPR, "MyOrgID", "myTreatmentId", Codes.LOINC_OID, devdesc, sp ); 
					        FutureResult result; 
					        try { 
					          textarea.setText("Sending the measurements ..."); 
					          result = dataUploader.upload(sto); 
					          result.awaitUninterruptibly(); 
						      conversation.postText("Spirometry measurements: \n FVC: " + measurements[0] + " \n FEV1: " +  measurements[1], true); 
			            	  textarea.setText("Measurements are now sent to hospital \n FVC: " + measurements[0] + " \n FEV1: " +  measurements[1]); 
			            	  buttonToggleEnabled(false); 
					        } catch (IOException e1) { 
					          performingMeasurement = true; 
					          textarea.setText("Sending failed, try again"); 
					          MsgboxDialog msg = new MsgboxDialog(frame, "Failed to connect to server!"); 
					          msg.dispose(); 
					        } 
					         
					} 
					else { 
						// values not valid or operation canceled, do nothing 
					} 
				} 
			} 
		}); 
 
		frame.add(button); 
	 
		frame.add(textarea); 
		frame.setLayout(new FlowLayout()); 
		frame.setSize(200, 200); 
		frame.pack(); 
		frame.setVisible(true); 
		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) { 
 
				PatientApplication.teardown(); 
			} 
		}); 
	 
	} 
	 
 
} 
