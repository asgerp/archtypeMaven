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
 
package org.net4care.skype.physician; 
 
import java.awt.Button; 
import java.awt.FlowLayout; 
import java.awt.Frame; 
import java.awt.TextArea; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.net.MalformedURLException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.JacksonJSONSerializer; 
import org.net4care.forwarder.DataUploader; 
import org.net4care.forwarder.Query; 
import org.net4care.forwarder.QueryResult; 
import org.net4care.forwarder.delegate.HttpConnector; 
import org.net4care.forwarder.delegate.StandardDataUploader; 
import org.net4care.forwarder.query.QueryPersonTimeInterval; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.skype.api.Net4CareSession; 
import org.net4care.skype.api.SignInMgr; 
import org.net4care.skype.appkeypair.AppKeyPairMgr; 
 
import com.skype.api.Conversation; 
import com.skype.api.Participant; 
import com.smb.data.Spirometry; 
 
public class PhysicianApplication { 
 
	public static final String MY_CLASS_TAG = "Physician"; 
 
	private static AppKeyPairMgr myAppKeyPairMgr = new AppKeyPairMgr(); 
 
	private static PhysicianApplication myPhysician = new PhysicianApplication(); 
	private static Net4CareSession mySession = new Net4CareSession(); 
 
	private static String ACC_NAME = ""; 
	private static String ACC_PWD = ""; 
	private static String KEYPAIR_PATH = ""; 
 
	private static String CURRENT_PATIENT_ACC = ""; 
 
	private DataUploader dataUploader; 
	private Serializer serializer; 
 
	private static String phName = "Anders Jakobsen"; 
 
	private static String ptName = ""; 
	private static String ptCPR = ""; 
 
	public PhysicianApplication() { 
		serializer = new JacksonJSONSerializer(); 
		try { 
	    	/** 
	    	 * NOTICE: The in case http://cyrixmorten.net:8080/observation is not available run an instance on your 
	    	 * own server with a public accessible server. If you do not have such a server take a look at the 
	    	 * net4care site section describing how to make a cloud deployment using Amazon EC2 
	    	 */ 
			dataUploader = new StandardDataUploader(serializer, 
					new HttpConnector("http://cyrixmorten.net:8080/observation")); 
		} catch (MalformedURLException e) { 
			e.printStackTrace(); 
			System.exit(1); 
		} 
	} 
 
	public static void main(String[] args) { 
		 
		if (args.length < 3) { 
			Net4CareSession.myConsole 
					.printf("Usage is: accountName accountPassword pathToKeypair%n%n"); 
		} 
 
		ACC_NAME = args[0]; 
		ACC_PWD = args[1]; 
		KEYPAIR_PATH = args[2]; 
 
		if ((!myAppKeyPairMgr.resolveAppKeyPairPath(KEYPAIR_PATH)) 
				|| (!myAppKeyPairMgr.isValidCertificate())) { 
			return; 
		} 
 
		myPhysician.createGUI(); 
 
		Net4CareSession.myConsole.printf( 
				"%s: main - Creating session - Account = %s%n", MY_CLASS_TAG, 
				ACC_NAME); 
 
		PhysicianApplication 
				.setTextAreaText("Starting Skype\nIf the system hangs it might be because the Skype runtime is not started"); 
		if (mySession.doCreateSession(MY_CLASS_TAG, ACC_NAME, 
				myAppKeyPairMgr.getPemFilePathname(), new PhysicianListeners(mySession))) { 
			PhysicianApplication 
					.setTextAreaText("Connected to skype, now loggin on"); 
		} else { 
			System.exit(1); 
		} 
 
		Net4CareSession.myConsole.printf("%s: main - Logging in w/ password %s%n", 
				MY_CLASS_TAG, ACC_PWD); 
		if (mySession.mySignInMgr.Login(MY_CLASS_TAG, mySession, ACC_PWD)) { 
			PhysicianApplication 
					.setApplicationState(PhysicianApplication.currentState); 
			mySession.doGetAuthorizationtRequests(); 
		} else { 
			PhysicianApplication.setTextAreaText("Unable to log in!"); 
			Net4CareSession.myConsole.printf("%s: Account for %s does not exist.%n", 
					MY_CLASS_TAG, mySession.myAccountName); 
			myPhysician.teardown(); 
			System.exit(1); 
		} 
 
	} 
 
	public static void setCurrentPatient(String name, String CPR) { 
		System.out.println("Setting patientname and cpr: " + name + "  " + CPR); 
		ptCPR = CPR; 
		ptName = name; 
	} 
 
	void teardown() { 
		Net4CareSession.myConsole.printf("%s: Cleaning up...%n", MY_CLASS_TAG); 
		if (mySession != null) { 
			mySession.mySignInMgr.Logout(MY_CLASS_TAG, mySession); 
			while (SignInMgr.isLoggedIn(mySession.myAccount)) { 
				// wait 
			} 
			mySession.doTearDownSession(); 
		} 
		Net4CareSession.myConsole.printf("%s: Done!%n", MY_CLASS_TAG); 
	} 
 
	private static TextArea textarea = new TextArea("", 4, 35, 
			TextArea.SCROLLBARS_VERTICAL_ONLY); 
	private static Button button = new Button("Loading.."); 
	private final static Frame frame = new Frame("Physician"); 
 
	public static void setTextAreaText(String text) { 
		textarea.setText(text); 
	} 
 
	public static boolean performingMeasurement; 
 
	public static enum applicationState { 
		INIT, READYCALL, MAKECALL, VIEWHISTORY_RETRY, VIEWHISTORY, ENDCALL 
	}; 
 
	public static void setPatientAcc(String acc) { 
		System.out.println("Setting patient account: " + acc); 
		CURRENT_PATIENT_ACC = acc; 
	} 
 
	public static String getPatientAcc() { 
		return CURRENT_PATIENT_ACC; 
	} 
 
	private static applicationState currentState = applicationState.INIT; 
 
	public static void setApplicationState(applicationState state) { 
 
		button.setEnabled(false); 
		 
		currentState = state; 
 
		Conversation conversation = null; 
 
		if (state == applicationState.READYCALL 
				|| state == applicationState.MAKECALL) { 
			conversation = mySession.mySkype.getConversationByParticipants( 
					new String[] { CURRENT_PATIENT_ACC }, true, true); 
		} 
 
		switch (state) { 
		case INIT: 
			setTextAreaText("The system is ready\n\nNo patients currently waiting"); 
			updateButtonLabel("Call patient"); 
			break; 
 
		case READYCALL: 
			textarea.setText("Patient " 
					+ ptName 
					+ " is awaiting assitance\n\nThe patient is informed that he/she will be contacted within 2 minutes"); 
			updateButtonLabel("Call patient"); 
			enableButton(true); 
			performingMeasurement = true; 
			conversation.postText(("You will be contacted within 2 minutes"), 
					false); 
			break; 
 
		case MAKECALL: 
			updateButtonLabel("History"); 
			conversation.postText( 
					"You are currently in contact with physician " + phName, 
					false); 
			textarea.setText("The system is waiting for measurements"); 
			 
			activecall = mySession.doMakeCall( 
					PhysicianApplication.getPatientAcc(), true); 
 
			break; 
		case VIEWHISTORY_RETRY: 
			updateButtonLabel("History"); 
			button.setEnabled(true); 
			textarea.setText("Unable to connect to server"); 
			break; 
		case VIEWHISTORY: 
			 
			updateButtonLabel("End conversation"); 
			myPhysician.queryServerAndPutIntoOutputField(); 
			break; 
 
		case ENDCALL: 
			 
			// hang up and reset 
			activecall.hangup(); 
			activecall = null; 
			mySession.callActive = false; 
			CURRENT_PATIENT_ACC = ""; 
			setApplicationState(applicationState.INIT); 
			break; 
 
		default: 
			break; 
		} 
	} 
 
	private static void updateButtonLabel(String text) { 
		button.setLabel(text); 
		frame.pack(); 
	} 
	 
	public static void enableButton(boolean enable) { 
		button.setEnabled(enable); 
	} 
 
	void createGUI() { 
		// button.setSize(150, button.getHeight()); 
		button.setEnabled(false); 
		textarea.setEditable(false); 
 
		performingMeasurement = false; 
		button.addActionListener(new ActionListener() { 
 
			@Override 
			public void actionPerformed(ActionEvent arg0) { 
				// determine next state 
				int nextOrdinal = (currentState.ordinal() + 1) 
						% applicationState.values().length; 
				if (applicationState.VIEWHISTORY_RETRY.ordinal() == nextOrdinal) { 
					nextOrdinal++; // skip this state 
				} 
				// find matching state and move to it 
				for (applicationState state : applicationState.values()) { 
					if (state.ordinal() == nextOrdinal) 
						setApplicationState(state); 
				} 
			} 
		}); 
 
		frame.add(button); 
		frame.add(textarea); 
		frame.setLayout(new FlowLayout()); 
		frame.pack(); 
		frame.setVisible(true); 
		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) { 
				textarea.setText("Login out of Skype\nThe program terminates in a few seconds..."); 
				mySession.mySignInMgr.Logout(MY_CLASS_TAG, mySession); 
				myPhysician.teardown(); 
				while (mySession.isLoggedIn()) { 
					// stay alive 
				} 
				System.exit(0); 
			} 
		}); 
	} 
 
	private static Participant activecall = null; 
 
	private void queryServerAndPutIntoOutputField() { 
		button.setEnabled(false); 
		textarea.setText("Loading..."); 
		Query query; 
		QueryResult res = null; 
		java.util.List<StandardTeleObservation> obslist; 
 
		// Query for the last 10 days 
		long now = System.currentTimeMillis(); 
		long tendaysago = now - 1000L * 3600L * 24L; 
		query = new QueryPersonTimeInterval(ptCPR, tendaysago, now); 
		// System.out.println("Formulated query: "+query); 
		try { 
			res = dataUploader.query(query); 
			res.awaitUninterruptibly(); 
		} catch (Exception e) { 
			setApplicationState(applicationState.VIEWHISTORY_RETRY); 
			e.printStackTrace(); 
			return; 
		} 
		obslist = res.getObservationList(); 
		// System.out.println("The obs list length = "+obslist.size()); 
 
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
 
		int i = 0; 
		String result = ""; 
		for (StandardTeleObservation sto : obslist) { 
			long timestamp = sto.getTime(); 
			// System.out.println(" stamp = "+timestamp); 
			Date d = new Date(timestamp); 
			Spirometry spiro = (Spirometry) sto.getObservationSpecifics(); 
			String nicedate = sdf.format(d); 
			result += nicedate + " FVC=" + spiro.getFvc().getValue() 
					+ spiro.getFvc().getUnit() + " FEV1=" 
					+ spiro.getFev1().getValue() + spiro.getFev1().getUnit() 
					+ "\n"; 
			i++; 
			if (i > 38) 
				break; 
		} 
		textarea.setText(result); 
		button.setEnabled(true); 
	} 
 
} 
