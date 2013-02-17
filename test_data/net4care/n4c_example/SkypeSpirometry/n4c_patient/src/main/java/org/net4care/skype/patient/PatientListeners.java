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
 
import java.text.DateFormat; 
import java.util.Calendar; 
import java.util.Date; 
 
import org.net4care.skype.api.Net4CareSession; 
import org.net4care.skype.util.Net4CareListeners; 
 
 
import com.skype.api.Conversation; 
import com.skype.api.Message; 
import com.skype.api.Participant; 
import com.skype.api.Skype; 
 
 
public class PatientListeners extends Net4CareListeners { 
	 
 
    private Net4CareSession mySession; 
 
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
 
	 
	public PatientListeners (Net4CareSession mySession) { 
		super(mySession); 
		this.mySession = mySession; 
	} 
	 
    public void registerAllListeners() 
    { 
        mySession.mySkype.registerConnectionListener(this); 
        mySession.mySkype.registerSkypeListener(this); 
        mySession.mySkype.registerAccountListener(this); 
        mySession.mySkype.registerContactListener(this); 
        mySession.mySkype.registerContactGroupListener(this); 
        mySession.mySkype.registerContactSearchListener(this); 
        mySession.mySkype.registerConversationListener(this); 
        mySession.mySkype.registerMessageListener(this); 
        mySession.mySkype.registerParticipantListener(this); 
        mySession.mySkype.registerVideoListener(this); 
    } 
 
    public void unRegisterAllListeners() 
    { 
        mySession.mySkype.unRegisterSkypeListener(this); 
        mySession.mySkype.unRegisterAccountListener(this); 
        mySession.mySkype.unRegisterContactListener(this); 
        mySession.mySkype.unRegisterContactGroupListener(this); 
        mySession.mySkype.unRegisterContactSearchListener(this); 
        mySession.mySkype.unRegisterConversationListener(this); 
        mySession.mySkype.unRegisterMessageListener(this); 
        mySession.mySkype.unRegisterParticipantListener(this); 
        mySession.mySkype.unRegisterVideoListener(this); 
        mySession.mySkype.unRegisterConnectionListener(this); 
    } 
 
	 
	boolean callTookPlace = false; 
	 
 
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
					callTookPlace = true; 
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
				System.out.println("call took place: " + callTookPlace); 
				if (callTookPlace) { 
					PatientApplication.setTextAreaText("Call ended\nThe program terminates"); 
					PatientApplication.teardown(); 
					callTookPlace = false; 
				} 
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
 
 
	public void onMessage(com.skype.api.Conversation obj, Message message) { 
 
		Message.Type msgType = message.getType(); 
		if (msgType == Message.Type.POSTED_TEXT) { 
		   String conversationID = message.getConvoGuid(); 
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
            	Calendar.getInstance(); 
            	//conversation.postText((targetDateFmt.format(targetDate.getTime()) + ": This is an automated reply"), false); 
            	PatientApplication.setTextAreaText("The hospital replied:\n" + msgBody); 
            } 
        } 
		else { 
			Net4CareSession.myConsole.printf("%s: Ignoring ConversationListener.onMessage of type %s%n", 
				mySession.myClassTag, msgType.toString()); 
		}	 
	} 
 
 
 
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
            	Calendar.getInstance(); 
            	PatientApplication.setTextAreaText("The hospital replied:\n" + msgBody); 
            } 
        } 
		else { 
			Net4CareSession.myConsole.printf("%s: Ignoring SkypeListener.onMessage of type %s%n", 
					mySession.myClassTag, msgType.toString()); 
		}	 
	} 
 
 
	public boolean doPickUpCall() { 
		Conversation[] liveConversations = mySession.mySkype.getConversationList(Conversation.ListType.LIVE_CONVERSATIONS); 
		if (liveConversations.length == 0) { 
			Net4CareSession.myConsole.printf("%s: No live conversations to pick up!%n", mySession.myClassTag); 
			return (false); 
		} 
		 
		PatientApplication.setTextAreaText("The hospital is calling"); 
		PatientApplication.buttonToggleEnabled(true); 
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
			PatientApplication.setTextAreaText("You are now in contact with the hospital"); 
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
