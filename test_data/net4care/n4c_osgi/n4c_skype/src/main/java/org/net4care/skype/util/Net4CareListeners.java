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
 
 
package org.net4care.skype.util; 
 
import java.text.DateFormat; 
import java.util.Calendar; 
import java.util.Date; 
 
import org.net4care.skype.api.Net4CareSession; 
import org.net4care.skype.interfaces.RegisterListeners; 
 
import com.skype.api.Account; 
import com.skype.api.AccountListener; 
import com.skype.api.Contact; 
import com.skype.api.ContactGroup; 
import com.skype.api.ContactGroupListener; 
import com.skype.api.ContactListener; 
import com.skype.api.ContactSearchListener; 
import com.skype.api.Conversation; 
import com.skype.api.ConversationListener; 
import com.skype.api.Message; 
import com.skype.api.MessageListener; 
import com.skype.api.Participant; 
import com.skype.api.ParticipantListener; 
import com.skype.api.Skype; 
import com.skype.api.SkypeListener; 
import com.skype.api.SmsListener; 
import com.skype.api.TransferListener; 
import com.skype.api.VideoListener; 
import com.skype.api.VoicemailListener; 
import com.skype.ipc.ConnectionListener; 
import com.skype.util.Log; 
 
 
/** 
 * 
 */ 
public abstract class Net4CareListeners implements RegisterListeners,  
							AccountListener,  
							SkypeListener, 
							ContactListener,  
							ContactGroupListener,  
							ContactSearchListener, 
							ConversationListener,  
							MessageListener,  
							ParticipantListener, 
							SmsListener,  
							TransferListener,  
							VideoListener,  
							VoicemailListener, 
							ConnectionListener { 
	 
	private final String TAG = "N4CListeners"; 
	/** 
	 * Initialized session instance providing access to this sessions's Skype instance, 
	 * Account instance, tutorial tag, and so forth 
	 *  
	 * @since 1.0 
	 */ 
    protected Net4CareSession mySession; 
 
    /** 
	 * Whether a call is in progress: Part I. 
	 * <br /><br /> 
	 * Holds a reference to the affected Conversation instance so it doesn't get garbage-collected 
	 * in the middle of the call. Initialized to null by caller; set to 
	 * non-null by Conversation onPropertyChange and ConversationList onChange handlers. 
	 *  
	 * @see com.skype.api.Conversation 
	 * @see com.skype.api.SkypeListener#onConversationListChange(com.skype.api.Skype, com.skype.api.Conversation, com.skype.api.Conversation.ListType, boolean) 
	 *  
	 * @since 1.0 
	 */ 
    Conversation activeConversation = null; 
 
    /** 
	 * Whether a call is in progress: Part II. 
	 * <br /><br /> 
	 * Holds a reference to <em>Participants</em> of the affected Conversation instance so they 
	 * don't get garbage-collected in the middle of the call. Initialized to null by caller; set to 
	 * non-null by Conversation onPropertyChange and ConversationList onChange handlers. 
	 *  
	 * @see com.skype.api.Conversation 
	 * @see com.skype.api.SkypeListener#onConversationListChange(com.skype.api.Skype, com.skype.api.Conversation, com.skype.api.Conversation.ListType, boolean) 
	 *  
	 * @since 1.0 
	 */ 
    Participant[] activeConversationParticipants = null; 
 
    /** 
	 * Indicates whether onApp2AppStreamListChange was ever fired 
	 * with a non-zero stream count. 
	 * <br /> 
	 * Basically, this is for "prettiness" - we'll only display the 
	 * "connection" message <em>once</em> per session. 
	 *  
	 * @since 1.0 
	 */ 
	public boolean appConnected = false; 
	 
	public Net4CareListeners (Net4CareSession mySession) { 
		this.mySession = mySession; 
	} 
	 
 
	/** 
	 * VoicemailListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.VoicemailListener#onPropertyChange(com.skype.api.Voicemail, com.skype.api.Voicemail.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Voicemail obj, com.skype.api.Voicemail.Property prop, int value, String svalue) { 
	} 
 
	/** 
	 * VideoListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.VideoListener#onPropertyChange(com.skype.api.Video, com.skype.api.Video.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Video obj, com.skype.api.Video.Property prop, int value, String svalue) { 
		System.out.println("video onPropertyChange " + prop.toString() + " : " + value + " | " + svalue); 
	} 
 
	/** 
	 * VideoListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.VideoListener#onCaptureRequestCompleted(com.skype.api.Video, int, boolean, byte[], int, int) 
	 */ 
	public void onCaptureRequestCompleted(com.skype.api.Video obj, int requestId, boolean isSuccessful, 
			byte[] image, int width, int height) { 
		System.out.println("onCaptureRequestCompleted(" + requestId + ", " + isSuccessful + ", image.length:" + image.length 
			  + ", " + width + ", " + height + ")"); 
 
	} 
 
	/** 
	 * TransferListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.TransferListener#onPropertyChange(com.skype.api.Transfer, com.skype.api.Transfer.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Transfer obj, com.skype.api.Transfer.Property prop, int value, String svalue) { 
	} 
 
	/** 
	 * SmsListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SmsListener#onPropertyChange(com.skype.api.Sms, com.skype.api.Sms.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Sms obj, com.skype.api.Sms.Property prop, int value, String svalue) { 
	} 
 
	/** 
	 * ParticipantListener Override: Tutorial Handler - Sound Levels and Voice Status. 
	 * <ul> 
	 *   <li>Tutorial 5 - sound level property changes.</li> 
	 *   <li>Tutorial 6/7 - voice status property changes.</li> 
	 * </ul> 
	 * 
	 * @param obj 
	 * 	The affected Participant. 
	 * @param prop 
	 * 	The Participant property that triggered this event. 
	 * @param value 
	 * 	Ignored. 
	 *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ParticipantListener#onPropertyChange(com.skype.api.Participant, com.skype.api.Participant.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Participant obj, com.skype.api.Participant.Property prop, int value, String svalue) { 
		Participant affectedParticipant = (Participant)obj; 
 
		if (prop == Participant.Property.P_SOUND_LEVEL) 
		{ 
			Net4CareSession.myConsole.printf("Sound level changed to %d for %s%n", 
					affectedParticipant.getSoundLevel(), 
					affectedParticipant.getIdentity()); 
		} 
		else if (prop == Participant.Property.P_VOICE_STATUS) 
		{ 
			Participant.VoiceStatus voiceStatus = affectedParticipant.getVoiceStatus(); 
			Net4CareSession.myConsole.printf("Voice status changed to %s for %s%n", 
					voiceStatus.toString(), 
					affectedParticipant.getIdentity()); 
		} 
    } 
 
	/** 
	 * ParticipantListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ParticipantListener#onIncomingDtmf(com.skype.api.Participant, com.skype.api.Participant.Dtmf) 
	 */ 
	public void onIncomingDtmf(com.skype.api.Participant obj, Participant.Dtmf dtmf) { 
	} 
 
	/** 
	 * MessageListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.MessageListener#onPropertyChange(com.skype.api.Message, com.skype.api.Message.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Message obj, com.skype.api.Message.Property prop, int value, String svalue) { 
	} 
 
	/** 
	 * ConversationListener Override: Tutorial Handler - Conversation "Live Status". 
	 * <br /><br /> 
	 * If it's <em>not</em> a "live status" change, ignore it. Otherwise: 
	 * <ul> 
	 *   <li>display changes to our live status</li> 
	 *   <li>handle answering calls for us</li> 
	 * </ul> 
	 *  
	 * Tutorial 6/7 - Looks for: 
	 * <ul> 
	 *   <li>RINGING_FOR_ME so we can pick up the call</li> 
	 *   <li>IM_LIVE to indicate that a call is in progress</li> 
	 *   <li>RECENTLY_LIVE/NONE to indicate that a call has ended</li> 
	 *  </ul> 
	 *  
	 * @param obj 
	 * 	The affected Conversation. 
	 * @param prop 
	 * 	The Conversation property that triggered this event. 
	 * @param value 
	 * 	Ignored. 
	 *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ConversationListener#onPropertyChange(com.skype.api.Conversation, com.skype.api.Conversation.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Conversation obj, com.skype.api.Conversation.Property prop, int value, String svalue) { 
 
		if (prop == Conversation.Property.P_LOCAL_LIVE_STATUS) { 
 			Conversation affectedConversation = (Conversation)obj; 
			Conversation.LocalLiveStatus liveStatus = affectedConversation.getLocalLiveStatus(); 
			Net4CareSession.myConsole.printf("%s: Live status changed to %s%n", 
								mySession.myClassTag, liveStatus.toString()); 
			switch (liveStatus) { 
			case RINGING_FOR_ME: 
				Net4CareSession.myConsole.println("RING RING..."); 
				if (doPickUpCall()) { 
					Net4CareSession.myConsole.println("Conv: Call answered!"); 
					activeConversation = affectedConversation; 
					activeConversationParticipants = affectedConversation.getParticipants(Conversation.ParticipantFilter.ALL); 
					mySession.callActive = true; 
				} 
				break; 
			case RECENTLY_LIVE: 
			case NONE: 
				activeConversation = null; 
				activeConversationParticipants = null; 
				mySession.callActive = false; 
				Net4CareSession.myConsole.println("Conv: Call has ended/never started."); 
				break; 
			case IM_LIVE: 
				Net4CareSession.myConsole.println("Conv: Live session is up!"); 
				break; 
			default: 
				Net4CareSession.myConsole.println(mySession.myClassTag + ": Conv - Ignoring LiveStatus " + liveStatus.toString()); 
				break; 
			} 
        } 
	} 
 
	/** 
	 * ConversationListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ConversationListener#onParticipantListChange(com.skype.api.Conversation) 
	 */ 
	public void onParticipantListChange(com.skype.api.Conversation obj) { 
	} 
 
	/** 
	 * ConversationListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ConversationListener#onMessage(com.skype.api.Conversation, com.skype.api.Message) 
	 */ 
	public void onMessage(com.skype.api.Conversation obj, Message message) { 
 
		Message.Type msgType = message.getType(); 
		if (msgType == Message.Type.POSTED_TEXT) { 
		   String conversationID = message.getConvoGuid(); 
		   Conversation conversation = 
			   mySession.mySkype.getConversationByIdentity(conversationID); 
            String msgAuthor = message.getAuthor(); 
            String msgBody = message.getBodyXml(); 
            if (!msgAuthor.equals(mySession.myAccountName)) { 
                // Get timestamp -- it's in seconds, and the Date constructor needs milliseconds! 
            	Integer msgTimeStamp = new Integer(message.getTimestamp()); 
                Date dateTimeStamp = new Date((msgTimeStamp.longValue() * 1000L)); 
            	DateFormat targetDateFmt = DateFormat.getDateTimeInstance(); 
            	Net4CareSession.myConsole.printf("%s: [%s] %s posted message%n%s%n", 
            			mySession.myClassTag, targetDateFmt.format(dateTimeStamp), msgAuthor, msgBody); 
            	Calendar targetDate = Calendar.getInstance(); 
            	conversation.postText((targetDateFmt.format(targetDate.getTime()) + ": This is an automated reply"), false); 
            } 
        } 
		else { 
			Net4CareSession.myConsole.printf("%s: Ignoring ConversationListener.onMessage of type %s%n", 
				mySession.myClassTag, msgType.toString()); 
		}	 
	} 
 
	/** 
	 * ConversationListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ConversationListener#onSpawnConference(com.skype.api.Conversation, com.skype.api.Conversation) 
	 */ 
	public void onSpawnConference(com.skype.api.Conversation obj, Conversation spawned) { 
        Log.d(TAG, "onSpawnConference(" + spawned + ")"); 
	} 
 
	/** 
	 * ContactSearchListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ContactSearchListener#onPropertyChange(com.skype.api.ContactSearch, com.skype.api.ContactSearch.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.ContactSearch obj, com.skype.api.ContactSearch.Property prop, int value, String svalue) { 
	} 
 
	/** 
	 * ContactSearchListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ContactSearchListener#onNewResult(com.skype.api.ContactSearch, com.skype.api.Contact, int) 
	 */ 
	public void onNewResult(com.skype.api.ContactSearch obj, Contact contact, int rankValue) { 
			mySession.onNewResult.handleResult(obj, contact, rankValue); 
	} 
 
	/** 
	 * ContactGroupListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ContactGroupListener#onPropertyChange(com.skype.api.ContactGroup, com.skype.api.ContactGroup.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.ContactGroup obj, com.skype.api.ContactGroup.Property prop, int value, String svalue) { 
	} 
 
	/** 
	 * ContactGroup Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ContactGroupListener#onChangeConversation(com.skype.api.ContactGroup, com.skype.api.Conversation) 
	 */ 
	public void onChangeConversation(com.skype.api.ContactGroup obj, Conversation conversation) { 
        Log.d(TAG, "onChangeConversation(" + conversation + ")"); 
	} 
 
	/** 
	 * ContactGroupListener Override: Tutorial Handler - ContactGroup TYPE. 
	 * <br /><br /> 
	 * This handler fires for all ContactGroups. If it's <em>not</em> 
	 * a pending authorization request, log and ignore it. 
	 * <br /><br /> 
     * Tutorial 9 - Process authorization requests 
     *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ContactGroupListener#onChange(com.skype.api.ContactGroup, com.skype.api.Contact) 
	 */ 
	public void onChange(com.skype.api.ContactGroup obj, Contact contact) { 
 
		String contactSkypeName = contact.getSkypeName(); // Find out who it's from 
		String contactDisplayName = contact.getDisplayName(); 
 
		 
		ContactGroup waitingAuth = 
				mySession.mySkype.getHardwiredContactGroup(ContactGroup.Type.CONTACTS_WAITING_MY_AUTHORIZATION); 
		Contact[] waitingAuthMembers = waitingAuth.getContacts(); 
		int waitingAuthMembersCnt = waitingAuthMembers.length; 
		 
		if (waitingAuthMembersCnt == 0) { 
			Net4CareSession.myConsole.printf("%s: Ignoring ContactGroup change for %s (%s); no Contacts awaiting authorization %n", 
					mySession.myClassTag, contactSkypeName, contactDisplayName); 
			return; 
		} 
		 
		 
		if (contact.getReceivedAuthRequest().length() == 0){ 
			Net4CareSession.myConsole.printf("%s: Ignoring ContactGroup change; Contact  %s (%s) is not awaiting authorization %n", 
					mySession.myClassTag, contactSkypeName, contactDisplayName); 
			return; 
		} 
 
		String authRequestText = contact.getReceivedAuthRequest();	// Get any intro text... 
   		if ((authRequestText == null) || (authRequestText.length() == 0)) {						// ...and default it if missing 
   			authRequestText = "-- NO INTRODUCTORY TEXT --"; 
   		} 
		Net4CareSession.myConsole.printf("%s: Authorization request from: %s (%s):%n\t%s", 
				mySession.myClassTag, contactSkypeName, contactDisplayName, authRequestText); 
		contact.setBuddyStatus(true, true); 
/* 
 * SetBuddyStatus should really return a boolean... 
 * If and when it does, replace the above SetBuddyStatus invocation with the following... 
   			if (contact.setBuddyStatus(true, true)) { 
   				N4CSession.myConsole.printf("%s: %s is now authorized%n", mySession.myClassTag, contactSkypeName); 
   			} 
   			else { 
   				N4CSession.myConsole.printf("%s: Authorization failed.%n", mySession.myClassTag); 
   			} 
*/ 
   			Net4CareSession.myConsole.printf("%s: %s is now authorized!%n", mySession.myClassTag, contactSkypeName); 
	} 
 
	/** 
	 * ContactListener Override: Tutorial Handler - Availability. 
	 * <br /><br /> 
     * Maps an availability property ENUM code to a text string, and writes that string to 
     * the console. If it's <em>not</em> an availability change, ignore it. 
     * <br /><br /> 
     * The implementation here follows the C++ pattern - a switch statement with cases for each 
     * defined code. For Java 5.0, the Enum class' <code>toString</code> method eliminates the 
     * need for the entire switch statement. For example, you could code the printf as: 
     * <pre> 
     *     N4CSession.myConsole.printf("%s: Availability of %s is now %s%n", 
	 *                       mySession.myClassTag, 
	 *                       affectedContact.getDisplayName(), 
	 *                       availability.toString()); 
	 * </pre> 
	 * Implementing this functionality as a switch statement, however, 
	 * enables you to perform additional processing for specific cases. 
     *  
	 * @param affectedContact 
	 * 	The affected Contact. 
	 * @param prop 
	 * 	The Contact property that triggered this event. 
	 * @param value 
	 * 	Ignored. 
	 *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.ContactListener#onPropertyChange(com.skype.api.Contact, com.skype.api.Contact.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Contact affectedContact, com.skype.api.Contact.Property prop, int value, String svalue) { 
 
		if (prop == Contact.Property.P_AVAILABILITY) 
		{ 
			String statusAsText; 
			Contact.Availability availability = affectedContact.getAvailability(); 
 
			switch (availability) 
			{ 
			case UNKNOWN: 
				statusAsText = "UNKNOWN"; 
				break; 
			case PENDINGAUTH: 
				statusAsText = "PENDINGAUTH"; 
				break; 
			case BLOCKED: 
				statusAsText = "BLOCKED"; 
				break; 
			case BLOCKED_SKYPEOUT: 
				statusAsText = "BLOCKED_SKYPEOUT"; 
				break; 
			case SKYPEOUT: 
				statusAsText = "SKYPEOUT"; 
				break; 
			case OFFLINE: 
				statusAsText = "OFFLINE"; 
				break; 
			case OFFLINE_BUT_VM_ABLE: 
				statusAsText = "OFFLINE_BUT_VM_ABLE"; 
				break; 
			case OFFLINE_BUT_CF_ABLE: 
				statusAsText = "OFFLINE_BUT_CF_ABLE"; 
				break; 
			case ONLINE: 
				statusAsText = "ONLINE"; 
				break; 
			case AWAY: 
				statusAsText = "AWAY"; 
				break; 
			case NOT_AVAILABLE: 
				statusAsText = "NOT_AVAILABLE"; 
				break; 
			case DO_NOT_DISTURB: 
				statusAsText = "DO_NOT_DISTURB"; 
				break; 
			case SKYPE_ME: 
				statusAsText = "SKYPE_ME"; 
				break; 
			case INVISIBLE: 
				statusAsText = "INVISIBLE"; 
				break; 
			case CONNECTING: 
				statusAsText = "CONNECTING"; 
				break; 
			case ONLINE_FROM_MOBILE: 
				statusAsText = "ONLINE_FROM_MOBILE"; 
				break; 
			case AWAY_FROM_MOBILE: 
				statusAsText = "AWAY_FROM_MOBILE"; 
				break; 
			case NOT_AVAILABLE_FROM_MOBILE: 
				statusAsText = "NOT_AVAILABLE_FROM_MOBILE"; 
				break; 
			case DO_NOT_DISTURB_FROM_MOBILE: 
				statusAsText = "DO_NOT_DISTURB_FROM_MOBILE"; 
				break; 
			case SKYPE_ME_FROM_MOBILE: 
				statusAsText = "SKYPE_ME_FROM_MOBILE"; 
				break; 
			default: 
				statusAsText = "UNKNOWN"; 
				break; 
			} 
			 
			Net4CareSession.myConsole.printf("%s: Availability of %s is now %s (%s)%n", 
					mySession.myClassTag, 
					affectedContact.getDisplayName(), statusAsText, 
					availability.toString()); 
		} 
	} 
	 
	/** 
	 * SkypeListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onNewCustomContactGroup(com.skype.api.Skype, com.skype.api.ContactGroup) 
	 */ 
	public void onNewCustomContactGroup(Skype obj, ContactGroup group) { 
        Log.d(TAG, "onNewCustomContactGroup(" + group + ")"); 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onContactOnlineAppearance(com.skype.api.Skype, com.skype.api.Contact) 
	 */ 
	public void onContactOnlineAppearance(Skype obj, Contact contact) { 
        Log.d(TAG, "onContactOnlineAppearance(" + contact + ")"); 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onContactGoneOffline(com.skype.api.Skype, com.skype.api.Contact) 
	 */ 
	public void onContactGoneOffline(Skype obj, Contact contact) { 
	} 
 
	/** 
	 * SkypeListener Override: Tutorial Handler - Conversation ListType. 
	 * <br /><br /> 
	 * If it's <em>not</em> a LIVE_CONVERSATIONS change, ignore it; if it's not a 
	 * type we're interested in, simply write it to the console. 
	 * <ul> 
	 *   <li>Tutorial 5 - Looks for RINGING_FOR_ME so we can join in.</li> 
	 *   <li>Tutorial 6 - Looks for IM_LIVE or RECENTLY_LIVE/NONE. Former case, indicates 
	 *       that a call is in progress; latter case, indicates that a call has ended.</li> 
	 * </ul> 
	 * 
	 * @param conversation 
	 * 	The affected Conversation. 
	 * @param type 
	 * 	The Conversation list type that triggered this event. 
	 * @param added 
	 * 	Ignored. 
	 *  
	 * @since 1.0 
	 *  
	 */ 
	public void onConversationListChange(Skype obj, Conversation conversation, Conversation.ListType type, boolean added) { 
		 
		Net4CareSession.myConsole.printf("%s: ConversationListChange fired on: %s%n", 
				mySession.myClassTag, conversation.getDisplayName()); 
 
		if (type == Conversation.ListType.LIVE_CONVERSATIONS) { 
			Conversation.LocalLiveStatus liveStatus = conversation.getLocalLiveStatus(); 
			Net4CareSession.myConsole.printf("%s: Live status changed to %s%n", 
								mySession.myClassTag, liveStatus.toString()); 
			switch (liveStatus) { 
			case RINGING_FOR_ME: 
				activeConversation = conversation; 
				activeConversationParticipants = conversation.getParticipants(Conversation.ParticipantFilter.ALL); 
				conversation.join(); 
				mySession.callActive = true; 
				break; 
			case RECENTLY_LIVE: 
			case NONE: 
				Net4CareSession.myConsole.printf("%s: Call finished.%n", mySession.myClassTag); 
				activeConversation = null; 
				activeConversationParticipants = null; 
				mySession.callActive = false; 
				break; 
			case IM_LIVE: 
				Net4CareSession.myConsole.printf("%s: Live session is up.%n", mySession.myClassTag); 
				break; 
			default: 
				Net4CareSession.myConsole.printf("%s: Ignoring Conversation status %s%n", 
									mySession.myClassTag, liveStatus.toString()); 
				break; 
			} 
		} 
	} 
 
	/** 
	 * SkypeListener Override: Tutorial Handler - Posted chat message. 
	 * <br /><br /> 
	 * If it's <em>not</em> a POSTED_TEXT message, ignore it. 
	 * 
	 * @param message 
	 * 	The affected Message. 
	 * @param changesInboxTimestamp 
	 * 	Ignored. 
	 * @param supersedesHistoryMessage 
	 * 	Ignored. 
	 * @param conversation 
	 * 	The affected Conversation. 
	 *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onMessage(com.skype.api.Skype, com.skype.api.Message, boolean, com.skype.api.Message, com.skype.api.Conversation) 
	 */ 
	public void onMessage(Skype obj, Message message, 
			boolean changesInboxTimestamp, Message supersedesHistoryMessage, Conversation conversation) { 
		Message.Type msgType = message.getType(); 
 
        if (msgType == Message.Type.POSTED_TEXT) { 
            String msgAuthor = message.getAuthor(); 
            String msgBody = message.getBodyXml(); 
            if (!msgAuthor.equals(mySession.myAccountName)) { 
                // Get timestamp -- it's in seconds, and the Date constructor needs milliseconds! 
            	Integer msgTimeStamp = new Integer(message.getTimestamp()); 
                Date dateTimeStamp = new Date((msgTimeStamp.longValue() * 1000L)); 
            	DateFormat targetDateFmt = DateFormat.getDateTimeInstance(); 
            	Net4CareSession.myConsole.printf("%s: [%s] %s posted message%n\t%s%n", 
            			mySession.myClassTag, targetDateFmt.format(dateTimeStamp), msgAuthor, msgBody); 
            	Calendar targetDate = Calendar.getInstance(); 
            	conversation.postText((targetDateFmt.format(targetDate.getTime()) + ": This is an automated reply"), false); 
            } 
        } 
		else { 
			Net4CareSession.myConsole.printf("%s: Ignoring SkypeListener.onMessage of type %s%n", 
					mySession.myClassTag, msgType.toString()); 
		}	 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onAvailableVideoDeviceListChange(Skype) 
	 */ 
	public void onAvailableVideoDeviceListChange(Skype obj) { 
        mySession.onAvailableVideoDevices.handleResult(obj); 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onAvailableDeviceListChange(Skype) 
	 */ 
	public void onAvailableDeviceListChange(Skype obj) { 
        Log.d(TAG, "onAvailableDeviceListChange()"); 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onNrgLevelsChange(Skype) 
	 */ 
	public void onNrgLevelsChange(Skype obj) { 
        Log.d(TAG, "onNrgLevelsChange()"); 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 * Fires on P2P connection failure during account login, which (by default) 
	 * attempts direct connection to the network, then falls back to connection via proxy. 
	 *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onProxyAuthFailure(Skype, Skype.ProxyType) 
	 */ 
	public void onProxyAuthFailure(Skype obj, Skype.ProxyType type) { 
        Log.d(TAG, "onProxyAuthFailure()"); 
	} 
 
	/** 
	 * SkypeListener Override. 
	 * 
	 *  
	 * @see com.skype.api.SkypeListener#onH264Activated(Skype) 
	 */ 
    public void onH264Activated(Skype obj) { 
        Log.d(TAG, "onH264Activated()"); 
    } 
 
	 
	/** 
	 * SkypeListener Override: Tutorial Handler - Datagrams. 
	 * <br /><br /> 
	 * In the context of Tutorial 11 datagrams, this event fires when we receive a datagram. 
	 * Writes the name of the affected stream and the actual datagram content to the console. 
	 * 
	 * @param appname 
	 * 	The name of the application associated with this datagram connection. 
	 * @param stream 
	 * 	The name of the affected stream. 
	 * @param data 
	 * 	The payload of this datagram. 
	 *  
	 * @since 1.0 
	 *  
	 * see com.skype.api.SkypeListener#onApp2AppDatagram(com.skype.api.Skype, java.lang.String, java.lang.String, byte[]) 
	 */ 
	public void onApp2AppDatagram(Skype obj, String appname, String stream, byte[] data) { 
		String dataStr = new String(data); // Java 5.0 and up can use data.toString() directly 
 
		Net4CareSession.myConsole.printf("Got datagram in stream %s for app %s: %s%n", stream, appname, dataStr); 
	} 
 
	/** 
	 * SkypeListener Override: Tutorial Handler - Call Quality Information. 
	 * <br /><br /> 
	 * New for SkypeKit 4.1 (Java2Wrapper). 
	 * 
	 * @param object 
	 * 	The... 
	 * @param testType 
	 * 	The... 
	 * @param testResult 
	 * 	The... 
	 * @param withUser 
	 * 	The... 
	 * @param details 
	 * 	The... 
	 * @param xmlDetails 
	 * 	The... 
	 *  
	 * @since 2.0 
	 *  
	 * see com.skype.api.SkypeListener#onQualityTestResult(Skype, Skype.QualityTestType, Skype.QualityTestResult, 
			String, String, String) 
	 */ 
	public void onQualityTestResult(Skype object, Skype.QualityTestType testType, Skype.QualityTestResult testResult, 
			String withUser, String details, String xmlDetails) { 
		 
		Net4CareSession.myConsole.println("Got Quality Test Result"); 
	} 
 
 
 	/** 
	 * SkypeListener Override: Tutorial Handler - App2App Stream List. 
	 * <br /><br /> 
	 * In the context of Tutorial 11 datagrams, this event fires when: 
	 * <ol> 
	 *   <li>Connection is established between two app2app applications. That is, when 
	 *	     both parties have an app up with the same name and <em>both</em> used App2AppConnect 
	 *	     In that case, both parties get this event, with listType ALL_STREAMS</li> 
	 *   <li>When a datagram is sent, the sender will get this event with listType SENDING_STREAMS 
	 *	     Receiver of the datagram will get onApp2AppDatagram event instead.</li> 
	 *   <li>When the remote party drops app2app connection, the local user will get 
	 *	     onApp2AppStreamListChange with listType ALL_STREAMS and streams.length zero, 
	 *	     which is useful for detecting remote drops.</li> 
	 * </ol> 
	 *  
	 * @param appname 
	 * 	The name of the application associated with this datagram connection. 
	 * @param listType 
	 * 	The type of the affected stream(s) - sending, receiving, or "all" 
	 * @param streams 
	 * 	The names of the affected stream(s). In the context of Tutorial 11 datagrams, 
	 *  we assume that there are only 2 participants and so th enumber of streams should be either 
	 *  0 (zero; remote shutdown) or 1 (one; initiated/datagram sent). 
	 *  
	 * @since 1.0 
	 *  
	 * @see com.skype.api.SkypeListener#onApp2AppStreamListChange(Skype, String, Skype.App2AppStreams, String[], int[]) 
	 */ 
	public void onApp2AppStreamListChange(Skype object, String appname, Skype.App2AppStreams listType, String[] streams, int[] receivedSizes) { 
//		int streamsCount = streams.length; 
		 
/* 
		if (streamsCount != 0) { 
	        // Normally the streamCount in this event should be either 1 or 0. 
	        // More streams are possible when there are more than 2 connected 
	        // participants running the same application. For purposes of this 
	        // example, we will assume that there are only 2 participants. 
			int i; 
	        for (i = 0; i < streamsCount; i++) { 
	            N4CSession.myConsole.printf("onApp2AppStreamListChange: %s %s %s%n", 
	            		mySession.mySkype.StreamListType(listType), appname, streams[i]); 
	            mySession.mySkype.streamName = streams[i]; // We're assuming only one stream! 
	            												// If two or more, last one wins! 
	        } 
 
	        if (!appConnected) { 
	            appConnected = true; 
	            N4CSession.myConsole.printf("You can now type and send datagrams to remote instance.%n"); 
	        } 
	    } 
	    else if (listType == Skype.APP2APP_STREAMS.ALL_STREAMS) { 
	            // Remote side dropped connection. 
	            N4CSession.myConsole.printf("No more app2app streams.%n"); 
	            mySession.mySkype.streamName = ""; 
	    } 
*/ 
	} 
	 
	/** 
	 * AccountListener Override: Tutorial Handler - Account Properties. 
	 * <br /><br /> 
	 * Status changes: Specifically looks for/handles login/logout status changes, and reports 
	 * those changes to the associated N4CSession instance. Writes notice of <em>all</em> 
	 * property changes to both the log and the console. 
	 * <br /><br /> 
	 * Logout reasons: Writes reason for logout to both the log and the console. Useful for 
	 * differentiating explicit logout by user (Account.LogoutReason.LOGOUT_CALLED) and 
	 * forced logout by the SkypeKit runtime. 
	 * <br /><br /> 
	 * Other property changes: Writes the name of the changed property to the console. 
	 *  
	 * @param affectedAccount 
	 * 	Ignored - always assumes <em>our</em> account, so references effected through 
	 * 	<code>mySession.myAccount</code>. 
	 * @param prop 
	 * 	The Account property that triggered this event. 
	 * @param value 
	 * 	Ignored. 
	 * 
	 * @since 1.0 
	 *  
	 * @see com.skype.api.AccountListener#onPropertyChange(com.skype.api.Account, com.skype.api.Account.Property, int, String) 
	 */ 
	public void onPropertyChange(com.skype.api.Account affectedAccount, com.skype.api.Account.Property prop, int value, String svalue) { 
		Net4CareSession.myConsole.println(mySession.myClassTag + ": " + "onPropertyChange - Account" ); 
/* 
 * 		N4CSession.myConsole.printf("%s: value = %d; svalue = %s%n", mySession.myClassTag, value, svalue); 
 */ 
 
	    if (prop == Account.Property.P_STATUS) { 
			Account.Status accountStatus = affectedAccount.getStatus(); 
            mySession.setLoginStatus(accountStatus); 
	        if (value == Account.Status.LOGGED_IN_VALUE) { 
	    		Net4CareSession.myConsole.println(mySession.myClassTag + ": " + "Login complete." ); 
	        } 
	        else if ((accountStatus == Account.Status.LOGGED_OUT) || 
	        		(accountStatus == Account.Status.LOGGED_OUT_AND_PWD_SAVED)) { 
	    		Net4CareSession.myConsole.println(mySession.myClassTag + ": " + "Login in progress/Logout complete." ); 
	        } 
	        else { 
	        	String otherStatus = new String("Account Status = " + accountStatus.toString()); 
	        	Net4CareSession.myConsole.printf("%s: %s%n", mySession.myClassTag, otherStatus); 
	        } 
	    } 
	    else if (prop == Account.Property.P_LOGOUT_REASON) { 
 	    	Account.LogoutReason logoutReason = affectedAccount.getLogoutReason(); 
	    	Net4CareSession.myConsole.printf("%s: logoutreason = %s%n", 
	    						mySession.myClassTag, logoutReason.toString()); 
	    } 
	    else { 
	    	String propName = prop.toString(); 
	    	Net4CareSession.myConsole.printf("%s: %s property changed!%n", 
	    						mySession.myClassTag, propName); 
	    } 
	} 
 
 
	/** 
	 * ConnectionListener Override - Disconnect. 
	 * <br /><br /> 
	 * New for SkypeKit 4.1 (Java2Wrapper). 
	 * 
	 * @since 2.0 
	 *  
	 * see com.skype.api.ConnectionListener#sidOnDisconnected(String) 
	 */ 
    public void sidOnDisconnected(String cause) { 
    	Net4CareSession.myConsole.printf("%s: Disconnected; reason = %s%n", 
				mySession.myClassTag, cause); 
    } 
 
    /** 
	 * ConnectionListener Override - Connect. 
	 * <br /><br /> 
	 * New for SkypeKit 4.1 (Java2Wrapper). 
	 * 
	 * @since 2.0 
	 *  
	 * see com.skype.api.ConnectionListener#sidOnDisconnected(String) 
	 */ 
    public void sidOnConnected() { 
    	Net4CareSession.myConsole.printf("%s: Connected!%n", 
				mySession.myClassTag); 
    } 
 
    /** 
	 * ConnectionListener Override - Connect in progress. 
	 * <br /><br /> 
	 * New for SkypeKit 4.1 (Java2Wrapper). 
	 * 
	 * @since 2.0 
	 *  
	 * see com.skype.api.ConnectionListener#sidOnDisconnected(String) 
	 */ 
 
    public void sidOnConnecting() { 
    	Net4CareSession.myConsole.printf("%s: Connecting...%n", 
				mySession.myClassTag); 
    } 
 
	/** 
	 * Business logic for answering a call (Tutorial_5 - Find conversation to join). 
	 * <br /><br /> 
	 * Since this method is invoked from the Conversation event handler 
	 * {@link #onPropertyChange(com.skype.api.Conversation, com.skype.api.Conversation.Property, int, String)}, 
 	 * it's most convenient to place it here in the JavaTutorialListeners class. 
	 *  
	 * @return 
	 * <ul> 
	 *   <li>true: call picked up</li> 
	 *   <li>false: no call to pick up/call not answered/error</li> 
	 * </ul> 
	 *  
	 * @since 1.0 
	 */ 
	public boolean doPickUpCall() { 
		Conversation[] liveConversations = mySession.mySkype.getConversationList(Conversation.ListType.LIVE_CONVERSATIONS); 
		if (liveConversations.length == 0) { 
			Net4CareSession.myConsole.printf("%s: No live conversations to pick up!%n", mySession.myClassTag); 
			return (false); 
		} 
		 
		Conversation targetConversation = liveConversations[0]; 
 
		Participant[] callerList = targetConversation.getParticipants(Conversation.ParticipantFilter.OTHER_CONSUMERS); 
		StringBuffer displayParticipantsStr = new StringBuffer(); 
 
		displayParticipantsStr.setLength(0); 
		int i; 
		int j = callerList.length; 
		for (i = 0; i < j; i++) { 
			displayParticipantsStr.append((" " + callerList[i].getIdentity())); 
		} 
 
		Conversation.LocalLiveStatus liveStatus = targetConversation.getLocalLiveStatus(); 
		switch (liveStatus) { 
		case RINGING_FOR_ME: 
			Net4CareSession.myConsole.println("RING RING..."); 
			Net4CareSession.myConsole.printf("Incoming call from: %s %n", displayParticipantsStr.toString()); 
			targetConversation.joinLiveSession(targetConversation.getJoinBlob()); 
			return (true); 
			// break; 
		case IM_LIVE: 
			Net4CareSession.myConsole.printf("Another call is coming in from : %s %n", displayParticipantsStr.toString()); 
			Net4CareSession.myConsole.println("As we already have a live session up, we will reject it."); 
			targetConversation.leaveLiveSession(true); 
			break; 
		default: 
			Net4CareSession.myConsole.println(mySession.myClassTag + ": Ignoring LiveStatus " + liveStatus.toString()); 
			break; 
		} 
 
		return (false); 
	} 
} 
 
