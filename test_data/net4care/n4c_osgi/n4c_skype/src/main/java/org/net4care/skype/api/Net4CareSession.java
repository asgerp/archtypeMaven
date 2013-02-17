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
 
/** 
 * Copyright (C) 2010-2012 Skype 
 * 
 * All intellectual property rights, including but not limited to copyrights, 
 * trademarks and patents, as well as know how and trade secrets contained in, 
 * relating to, or arising from the internet telephony software of 
 * Skype Limited (including its affiliates, "Skype"), including without 
 * limitation this source code, Skype API and related material of such 
 * software proprietary to Skype and/or its licensors ("IP Rights") are and 
 * shall remain the exclusive property of Skype and/or its licensors. 
 * The recipient hereby acknowledges and agrees that any unauthorized use of 
 * the IP Rights is a violation of intellectual property laws. 
 * 
 * Skype reserves all rights and may take legal action against infringers of 
 * IP Rights. 
 * 
 * The recipient agrees not to remove, obscure, make illegible or alter any 
 * notices or indications of the IP Rights and/or Skype's rights and 
 * ownership thereof. 
 */ 
 
package org.net4care.skype.api; 
 
import java.io.PrintStream; 
import java.util.HashMap; 
import java.util.Map; 
 
import org.net4care.skype.interfaces.HandleCallbackResult; 
import org.net4care.skype.util.Net4CareListeners; 
import org.net4care.skype.util.ParseSkypeKitVersion; 
 
import com.skype.api.Account; 
import com.skype.api.Contact; 
import com.skype.api.ContactGroup; 
import com.skype.api.ContactSearch; 
import com.skype.api.Conversation; 
import com.skype.api.Participant; 
import com.skype.api.Skype; 
import com.skype.api.Skype.GetAvailableVideoDevicesResponse; 
import com.skype.ipc.ClientConfiguration; 
import com.skype.ipc.ConnectionListener; 
 
/** 
 * Session Object. 
 *  
 * Encapsulates common aspects of the SkypeKit-based Java s. These include: 
 * <ul> 
 * <li>target account name</li> 
 * <li>console PrintStream (based off <code>System.out</code>) that specifies 
 * <code>autoFlush</code> as true</li> 
 * <li>activity indicators, such as the associated Account's login status and 
 * whether a call is in progress</li> 
 * <li>transport-related members: 
 * <ul> 
 * <li>SkypeKit runtime IP address</li> 
 * <li>SkypeKit runtime port number</li> 
 * <li>com.skype.ipc.ClientConfiguration instance</li> 
 * </ul> 
 * </li> 
 * <li>{@link com.skype.api.Skype} instance</li> 
 * <li>{@link com.skype.api.Account} instance</li> 
 * <li>{@link SignInMgr} instance</li> 
 * <li>{@link PhysicianListeners} instance (s-specific "helper" class instance 
 * that implements the various "listener" interfaces)</li> 
 * <li>{@link #setupAudioDevices(int, int)} method to commonize assignment of 
 * microphone/speaker devices for calls</li> 
 * </ul> 
 *  
 * Furthermore custom methods have been added by net4care, these are: 
 *  
 * @author Andrea Drane (ported/refactored from existing C++ code) 
 *  
 * @since 1.0 
 */ 
public class Net4CareSession { 
 
	/** 
	 * Info/Debug console output message prefix/identifier tag. Corresponds to 
	 * the class name. 
	 *  
	 * @since 1.0 
	 */ 
	public String myClassTag; 
 
	/** 
	 * Console PrintStream. <br /> 
	 * <br /> 
	 * Based off <code>System.out</code>, but specifies <code>autoFlush</code> 
	 * as true to ensure that console output does not intermingle since both the 
	 * code and the event handlers write to the console. 
	 *  
	 * @since 1.0 
	 */ 
	public static PrintStream myConsole = new PrintStream(System.out, true); 
 
	/** 
	 * Name of the target Skype account, which is actually the Skype Name of the 
	 * user that created it. 
	 *  
	 * @since 1.0 
	 */ 
	public String myAccountName; 
 
	public SignInMgr mySignInMgr = new SignInMgr(); 
 
	/** 
	 * Skype instance for this session. 
	 *  
	 * @see com.skype.api.Skype 
	 *  
	 * @since 1.0 
	 */ 
	public Skype mySkype = null; 
 
	/** 
	 * SkypeKit configuration instance for this session. Contains transport 
	 * port/IP address and certificate file data. 
	 *  
	 * @since 2.0 
	 */ 
	public ClientConfiguration myClientConfiguration = null; 
 
	/** 
	 * SkypeKit version number parse instance for this session. <br /> 
	 * <br /> 
	 * Do <em>not</em> attempt to instantiate this instance until 
	 * <em>after intializing</em> {@link #mySkype}! 
	 *  
	 * @see com.skype..util.ParseSkypeKitVersion 
	 *  
	 * @since 1.0 
	 */ 
	public ParseSkypeKitVersion myParseSkypeKitVersion = null; 
 
	/** 
	 * Account instance for this session. Set on successful login, <i>not</i> 
	 * during session creation! 
	 *  
	 * @see com.skype.api.Account 
	 *  
	 * @since 1.0 
	 */ 
	public Account myAccount = null; 
 
	/** 
	 * Whether we are currently in a call. <br /> 
	 * <br /> 
	 * Set to true when a Conversation goes live ( 
	 * <code>Conversation.LOCAL_LIVEStatus.RINGING_FOR_ME</code>) after a 
	 * successful <code>Conversation.join</code> or 
	 * <code>Conversation.JoinLiveSession</code>; set to false when a 
	 * Conversation goes non-live ( 
	 * <code>Conversation.LOCAL_LIVEStatus.RECENTLY_LIVE</code> or 
	 * <code>Conversation.LOCAL_LIVEStatus.NONE</code>). 
	 *  
	 * @see org.net4care.skype.Net4CareListeners.PhysicianListeners.util.Listeners#onPropertyChange(com.skype.api.Conversation, 
	 *      com.skype.api.Conversation.Property, int, String) 
	 * @see org.net4care.skype.Net4CareListeners.PhysicianListeners.util.Listeners#onConversationListChange(Skype, 
	 *      com.skype.api.Conversation, com.skype.api.Conversation.ListType, 
	 *      boolean) 
	 *  
	 * @since 1.0 
	 */ 
	public boolean callActive = false; 
 
	/** 
	 * Cached status of this session's associated Account. <br /> 
	 * <br /> 
	 * Initialized to <code>Account.Status.LOGGED_OUT</code>; updated by Account 
	 * onPropertyChange handler. 
	 *  
	 * @see org.net4care.skype.Net4CareListeners.PhysicianListeners.util.Listeners#onPropertyChange(com.skype.api.Account, 
	 *      com.skype.api.Account.Property, int, String) 
	 * @see com.skype.api.Account 
	 *  
	 * @since 1.1 
	 */ 
	public Account.Status loginStatus = Account.Status.LOGGED_OUT; 
 
	/** 
	 * Datagram stream ID, used by 11. 
	 *  
	 * @since 1.0 
	 */ 
	public String streamName = new String(""); 
 
	/** 
	 * Callbacks/event handlers for this session. 
	 *  
	 * @since 1.0 
	 */ 
	public Net4CareListeners myListeners = null; 
 
	/** 
	 * Server IP Address. 
	 *  
	 * @since 1.0 
	 */ 
	public static final String IP_ADDR = "127.0.0.1"; 
 
	/** 
	 * Server Port. <br /> 
	 * <br /> 
	 * If you modify this compiled-in default, you will need to start the 
	 * matching SkypeKit runtime with option:<br /> 
	 * &nbsp;&nbsp;&nbsp;&nbsp;<code>-p <em>9999</em></code><br /> 
	 * where <code>-p <em>9999</em></code> reflects this value. 
	 *  
	 * @since 1.0 
	 */ 
	public static final int PORT_NUM = 8963; 
 
	/** 
	 * Creates <em>most</em> everything needed for a session; the Account 
	 * instance is populated during sign-in. 
	 *  
	 * @param classTag 
	 *            The 's class name. If null or the empty string, default it to 
	 *            <code>T_TAG_DFLT</code>. 
	 * @param accountName 
	 *            The <em>name</em> of the account to use for this . If null or 
	 *            the empty string, <em>fail</em> by throwing a RuntimeException 
	 *            indicating that fact. 
	 * @param pathName 
	 *            Pathname of the certificate file, which should be a PEM file. 
	 *  
	 * @return <ul> 
	 *         <li>true: session initialized</li> 
	 *         <li>false: session initialization failed due to: 
	 *         <ul> 
	 *         <li>no or empty account name</li> 
	 *         <li>com.skype.api.Skype.Init failed - most likely from an invalid 
	 *         AppKeyPair</li> 
	 *         <li>could not obtain an Account instance</li> 
	 *         </ul> 
	 *         </li> 
	 *         </ul> 
	 *  
	 * @see com.skype..util.SignInMgr 
	 *  
	 * @since 1.0 
	 */ 
	/* 
	 * public boolean doCreateSession(String Tag, String accountName, 
	 * AppKeyPairMgr myAppKeyPairMgr) { 
	 */ 
	public boolean doCreateSession(String classTag, String accountName, 
			String pathName, Net4CareListeners listeners) { 
 
		myClassTag = new String(classTag); 
		 
 
		if ((accountName != null) && (accountName.length() != 0)) { 
			myAccountName = new String(accountName); // All s minimally require 
														// an account name 
		} else { 
			throw new RuntimeException( 
					(myClassTag + ": Cannot initialize session instance - no account name!")); 
		} 
	 
 
		// Set up our session with the SkypeKit runtime... 
		// Note that most of the Skype methods - including static methods and 
		// GetVersionString - will 
		// fail and/or throw an exception if invoked prior to successful 
		// initialization! 
		mySkype = new Skype(); 
		myClientConfiguration = new ClientConfiguration(); 
		myClientConfiguration.setTcpTransport(IP_ADDR, PORT_NUM); 
		myClientConfiguration.setCertificate(pathName); 
 
		myListeners = listeners; 
		Net4CareSession.myConsole.println("\tRegistering the listeners..."); 
		listeners.registerAllListeners(); 
		 
		myConsole 
				.printf("%s: Instantiated Skype, ClientConfiguration, and Listeners instances...%n", 
						myClassTag); 
		mySkype.init(myClientConfiguration, myListeners); 
		if (!mySkype.start()) { // You must invoke start --immediately-- after 
								// invoking init! 
			// success 
			return false; 
		} 
 
		myParseSkypeKitVersion = new ParseSkypeKitVersion(mySkype); 
		myConsole.printf( 
				"%s: Initialized MySkype instance - version = %s (%d.%d.%d)%n", 
				myClassTag, myParseSkypeKitVersion.getVersionStr(), 
				myParseSkypeKitVersion.getMajorVersion(), 
				myParseSkypeKitVersion.getMinorVersion(), 
				myParseSkypeKitVersion.getPatchVersion()); 
 
		// Get the Account 
		if ((myAccount = mySkype.getAccount(myAccountName)) == null) { 
			myConsole.printf("%s: Could not get Account for %s!%n", myClassTag, 
					myAccountName); 
			myConsole 
					.printf("%s: Session initialization failed!%n", myClassTag); 
			return (false); 
		} 
 
		myConsole.printf("%s: Got Account for %s%n", myClassTag, myAccountName); 
		myConsole.printf("%s: Initialized session!%n", myClassTag); 
 
		return (true); 
	} 
 
	/** 
	 * Tears down a session. <br /> 
	 * <br /> 
	 * Specifically, this involves: 
	 * <ol> 
	 * <li>Un-registering the listeners</li> 
	 * <li>Disconnecting the transport</li> 
	 * <li>"Closing" our Skype instance, which terminates the SkypeKit runtime</li> 
	 * </ol> 
	 *  
	 * @see PhysicianListeners#unRegisterAllListeners() 
	 *  
	 * @since 1.0 
	 */ 
	public void doTearDownSession() { 
 
		if (myListeners != null) { 
			myListeners.unRegisterAllListeners(); 
			myListeners = null; 
		} 
		// Closing Skype also disconnects the transport 
		if (mySkype != null) { 
			mySkype.stop(); 
			mySkype = null; 
		} 
 
		myConsole.printf("%s: Tore down session instance%n", myClassTag); 
	} 
 
	/** 
	 * Retrieves the current login status of this session's Account. 
	 *  
	 * @return Cached login status of this session's Account. 
	 *  
	 * @see org.net4care.skype.Net4CareListeners.PhysicianListeners.util.Listeners#onPropertyChange(com.skype.api.Account, 
	 *      com.skype.api.Account.Property, int, String) 
	 *  
	 * @since 1.0 
	 */ 
	public Account.Status getLoginStatus() { 
 
		return (this.loginStatus); 
	} 
 
	/** 
	 * Establishes the login status of this session's Account. 
	 *  
	 * @param loginStatus 
	 *            Reported login status of this session's Account. 
	 *  
	 * @see org.net4care.skype.Net4CareListeners.PhysicianListeners.util.Listeners#onPropertyChange(com.skype.api.Account, 
	 *      com.skype.api.Account.Property, int, String) 
	 *  
	 * @since 1.0 
	 */ 
	public void setLoginStatus(Account.Status loginStatus) { 
 
		this.loginStatus = loginStatus; 
 
		Net4CareSession.myConsole.printf(myClassTag + ": " 
				+ "setting loginStatus to %s%n", loginStatus.toString()); 
		return; 
	} 
 
	/** 
	 * Determines if an Account is signed in. <br /> 
	 * <br /> 
	 * Specifically, this involves examining the last cached value for the 
	 * associated Account's status property. Essentially, <em>only</em> a 
	 * current status of <code>Account.Status.LOGGED_IN</code> returns true <br /> 
	 * <br /> 
	 * Caching the status avoids having to query the DB. For mobile devices, 
	 * WiFi-connected laptops running on battery power, and so forth this 
	 * typically avoids expending battery charge to transmit the server request. 
	 *  
	 * @return <ul> 
	 *         <li>true: currently signed in</li> 
	 *         <li>false: currently signed out <em>or</em> target Account is 
	 *         null</li> 
	 *         </ul> 
	 *  
	 * @see com.skype..util.SignInMgr#isLoggedIn(Account) 
	 *  
	 * @since 1.0 
	 */ 
	public boolean isLoggedIn() { 
 
		if (this.loginStatus == Account.Status.LOGGED_IN) { 
			return (true); 
		} 
		return (false); 
	} 
 
	/** 
	 * Assigns active input and output devices from among those available. 
	 * Notifies user regarding the name of the selected devices or whether the 
	 * request failed. <em>Both</em> devices must exist for the request to 
	 * succeed. 
	 *  
	 * @param micIdx 
	 *            Index into the array of available recording devices of the 
	 *            requested input device. 
	 * @param spkrIdx 
	 *            Index into the array of available playback devices of the 
	 *            requested output device. 
	 *  
	 * @return <ul> 
	 *         <li>true: success</li> 
	 *         <li>false: failure</li> 
	 *         </ul> 
	 *  
	 * @see com.skype.api.Skype#getAvailableRecordingDevices() 
	 * @see com.skype.api.Skype#getAvailableOutputDevices() 
	 *  
	 * @since 2.0 
	 */ 
	public boolean setupAudioDevices(int micIdx, int spkrIdx) { 
		boolean passFail = true; // Ever the optimist, assume success! 
 
		Skype.GetAvailableRecordingDevicesResponse inputDevices = mySkype 
				.getAvailableRecordingDevices(); 
		Skype.GetAvailableOutputDevicesResponse outputDevices = mySkype 
				.getAvailableOutputDevices(); 
 
		if (micIdx > (inputDevices.handleList.length + 1)) { 
			Net4CareSession.myConsole.printf( 
					"%s: Invalid mic device no. (%d) passed!%n", myClassTag, 
					micIdx); 
			passFail = false; 
		} 
 
		if (spkrIdx > (outputDevices.handleList.length + 1)) { 
			Net4CareSession.myConsole.printf( 
					"%s: Invalid speaker device no. (%d) passed!%n", 
					myClassTag, spkrIdx); 
			passFail = false; 
		} 
 
		if (passFail) { 
			Net4CareSession.myConsole.printf("%s: Setting mic to %s (%s)%n", 
					myClassTag, inputDevices.nameList[micIdx], 
					inputDevices.productIdList[micIdx]); 
			Net4CareSession.myConsole.printf( 
					"%s: Setting speakers to %s  (%s)%n", myClassTag, 
					outputDevices.nameList[spkrIdx], 
					outputDevices.productIdList[spkrIdx]); 
			mySkype.selectSoundDevices(inputDevices.handleList[micIdx], 
					outputDevices.handleList[spkrIdx], 
					outputDevices.handleList[spkrIdx]); 
			mySkype.setSpeakerVolume(100); 
		} 
 
		return (passFail); 
	} 
 
	/** 
	 * enables receiving OnChange events when an incoming authorization request 
	 * occurs, see 
	 * {@link Net4CareListeners#onChange(ContactGroup, com.skype.api.Contact)} 
	 */ 
	public void doGetAuthorizationtRequests() { 
 
		// Here we will get ourselves a ContactGroup object, so that we can get 
		// OnChange events when an incoming authorization request occurs. 
		ContactGroup waitingForAuthList = (ContactGroup) this.mySkype 
				.getHardwiredContactGroup(ContactGroup.Type.CONTACTS_WAITING_MY_AUTHORIZATION); 
 
		// The following may look just like a decoration but in fact, you MUST 
		// read at least one property 
		// of the object (waitingForAuthList) to start getting OnChange events. 
		// We'll randomly pick nrofcontacts 
		// and ignore its value. 
		waitingForAuthList.getContactCount(); 
		 
		myConsole.println("Number of people waiting for auth: " + waitingForAuthList.getContactCount()); 
 
	} 
 
	/** 
	 * Find available input/output devices, then wait for incoming calls.. 
	 * <ol> 
	 * <li>List the available playback and recording devices.</li> 
	 * <li>Set the current devices (input, output, notification) to the first 
	 * device in their respective list.</li> 
	 * <li>Initialize the speaker volume level.</li> 
	 * <li>Wait for in-coming calls.</li> 
	 * </ol> 
	 *  
	 * @param mySession 
	 *            Populated session object 
	 *  
	 * @since 1.0 
	 */ 
	public void doAcceptCalls() { 
 
		int i; 
		int j; 
 
		Skype.GetAvailableOutputDevicesResponse outputDevices = this.mySkype 
				.getAvailableOutputDevices(); 
		Skype.GetAvailableRecordingDevicesResponse inputDevices = this.mySkype 
				.getAvailableRecordingDevices(); 
 
		// Getting a list of audio output devices. 
		Net4CareSession.myConsole.println("** Playback devices:"); 
		j = outputDevices.handleList.length; 
		for (i = 0; i < j; i++) { 
			Net4CareSession.myConsole.printf("\t%d. %s (%s)%n", i, 
					outputDevices.nameList[i], outputDevices.productIdList[i]); 
		} 
		Net4CareSession.myConsole.println(""); 
 
		// Getting a list of audio input devices. 
		Net4CareSession.myConsole.println("** Recording devices:"); 
		j = inputDevices.handleList.length; 
		for (i = 0; i < j; i++) { 
			Net4CareSession.myConsole.printf("\t%d. %s (%s)%n", i, 
					inputDevices.nameList[i], inputDevices.productIdList[i]); 
		} 
		Net4CareSession.myConsole.println(""); 
 
		// Currently setting the sound devices to the first input/output device. 
		// The output and notification are routed through the same device. If 
		// you want more control, 
		// don't invoke SetupAudioDevices -- instead invoke: 
		// mySession.mySkype.SelectSoundDevices(inputDevices.handleList[0], 
		// outputDevices.handleList[0], 
		// outputDevices.handleList[0]); 
		// mySession.mySkype.SetSpeakerVolume(100); 
		// 
		// If your microphone or speakers fail to work, you might want 
		// to change these values. 
 
		if (this.setupAudioDevices(0, 0)) { 
			Net4CareSession.myConsole.printf( 
					"%s: Audio device set-up completed!%n", this.myClassTag); 
		} else { 
			Net4CareSession.myConsole.printf( 
					"%s: Audio device set-up failed - exiting!%n", 
					this.myClassTag); 
			return; 
		} 
 
		if (!hasVideoDevice()) { 
			Net4CareSession.myConsole 
					.println("Now accepting incoming calls without video \n If a webcam is present video should become available in a moment"); 
		} else { 
			Net4CareSession.myConsole 
					.println("Now accepting incoming calls with video"); 
		} 
 
	} 
 
	public HandleCallbackResult onAvailableVideoDevices = new OnAvailableVideoDevices(); 
 
	public class OnAvailableVideoDevices implements HandleCallbackResult { 
 
		public void handleResult(Object... data) { 
			Net4CareSession.myConsole.printf("%s: Found VideoDevices: ", myClassTag); 
			GetAvailableVideoDevicesResponse videodevices = ((Skype)data[0]).getAvailableVideoDevices(); 
			for (String videodevicename: videodevices.deviceNames) { 
				Net4CareSession.myConsole.println(videodevicename);	 
			} 
		} 
 
	} 
 
	/** 
	 * @return boolean indicating whether video device is available 
	 */ 
	private boolean hasVideoDevice() { 
		Skype.GetAvailableVideoDevicesResponse videoDevices = mySkype 
				.getAvailableVideoDevices(); 
		if (videoDevices.count > 0) { 
			Net4CareSession.myConsole.printf("%s: Found video devices: %s%n", 
					this.myClassTag, videoDevices.deviceNames.toString()); 
			return true; 
		} else { 
			Net4CareSession.myConsole.printf("%s: No video devices found%n", 
					this.myClassTag); 
			return false; 
		} 
	} 
 
	/** 
	 * Start calling myCallTarget 
	 *  
	 * <ol> 
	 * <li>setupAudioDevices(0, 0)</li> 
	 * <li>initiate conversation</li> 
	 * <li>locate participant matching myCallTarget</li> 
	 * <li>invoke call on myCallTarget</li> 
	 * <li>return {@link Participant} myCallTarget</li> 
	 * </ol> 
	 *  
	 * @param myCallTarget 
	 *            The skype-name of the person you would like to call 
	 * @param videoCall 
	 *            Boolean deciding whether to enable video 
	 * @return {@link Participant} If no audio and/or video (if videoCall is 
	 *         true) is found this method will return null 
	 */ 
	public Participant doMakeCall(String myCallTarget, boolean videoCall) { 
 
		// Get available playback/recording devices; choose first of each 
		if (this.setupAudioDevices(0, 0)) { 
			Net4CareSession.myConsole.printf( 
					"%s: Audio device set-up completed!%n", this.myClassTag); 
		} else { 
			Net4CareSession.myConsole.printf( 
					"%s: Audio device set-up failed - exiting!%n", 
					this.myClassTag); 
			return null; 
		} 
 
		String[] callTargets = { new String(myCallTarget) }; // Create & 
																// initialize 
																// the array in 
																// one step... 
		Conversation myConversation = (Conversation) this.mySkype 
				.getConversationByParticipants(callTargets, true, true); 
 
		Participant[] convParticipantList = myConversation 
				.getParticipants(Conversation.ParticipantFilter.ALL); 
 
		int i; 
		int j = convParticipantList.length; 
		boolean callTargetFound = false; 
		for (i = 0; i < j; i++) { 
			if (convParticipantList[i].getIdentity().equals(myCallTarget)) { 
				callTargetFound = true; 
				break; 
			} 
		} 
 
		if (!callTargetFound) { 
			Net4CareSession.myConsole.printf( 
					"Could not find call target  %s%n", myCallTarget); 
			return null; 
		} 
 
		Net4CareSession.myConsole.printf("Calling %s%n", myCallTarget); // Initiate 
		convParticipantList[i].ring(myCallTarget, videoCall, 0, 10, false, 
				this.myAccount.getSkypeName()); 
		return convParticipantList[i]; 
	} 
 
	public HandleCallbackResult onNewResult = null; 
 
	/** 
	 * Perform a simple search for an account. The first match found (if any) 
	 * receives an auth request if needed and is added as buddy to the owner of 
	 * this session. 
	 *  
	 * @param account 
	 */ 
	public void addAccountToContacts(String account) { 
		onNewResult = new AddToContacts(); 
		ContactSearch cs = this.mySkype.createBasicContactSearch(account); 
		if (cs.isValid()) { 
			cs.submit(); 
		} 
		Net4CareSession.myConsole.printf("%s: Searching for " + account + "%n", 
				this.myClassTag); 
	} 
 
	public class AddToContacts implements HandleCallbackResult { 
 
		public void handleResult(Object... data) { 
			Contact contact = (Contact) data[1]; 
			int rankValue = (Integer) data[2]; 
			if (rankValue == 0) { 
				Contact acontact = (Contact) contact; 
				if (!acontact 
						.isMemberOf(mySkype 
								.getHardwiredContactGroup(ContactGroup.Type.ALL_KNOWN_CONTACTS))) { 
					Net4CareSession.myConsole.printf( 
							"%s: Sending auth request and adding to known contacts" 
									+ "%n", myClassTag); 
					acontact.setBuddyStatus(true, true); 
					acontact.sendAuthRequest("Please add me as contact", 0); 
				} else { 
					System.out.println(acontact.getIdentity() 
							+ " already known"); 
					if (acontact 
							.isMemberOf(mySkype 
									.getHardwiredContactGroup(ContactGroup.Type.UNKNOWN_OR_PENDING_AUTH_BUDDIES))) { 
						Net4CareSession.myConsole.printf( 
								"%s: but has not authed you yet... sending new request" 
										+ "%n", myClassTag); 
						acontact.sendAuthRequest("Please add me as contact", 0); 
					} 
				} 
			} 
		} 
	} 
} 
