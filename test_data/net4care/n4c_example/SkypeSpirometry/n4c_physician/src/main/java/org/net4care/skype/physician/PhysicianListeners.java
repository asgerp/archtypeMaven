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
 
import org.net4care.skype.api.Net4CareSession; 
import org.net4care.skype.physician.PhysicianApplication; 
import org.net4care.skype.util.Net4CareListeners; 
 
import com.skype.api.Conversation; 
import com.skype.api.Message; 
import com.skype.api.Participant; 
import com.skype.api.Skype; 
 
 
public class PhysicianListeners extends Net4CareListeners { 
	 
	private Net4CareSession mySession; 
 
 
	public PhysicianListeners(Net4CareSession mySession) { 
		super(mySession); 
		this.mySession = mySession; 
	} 
 
	public void registerAllListeners() { 
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
 
	public void unRegisterAllListeners() { 
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
 
 
 
	public void onMessage(com.skype.api.Conversation obj, Message message) { 
 
		Message.Type msgType = message.getType(); 
		if (msgType == Message.Type.POSTED_TEXT) { 
			String conversationID = message.getConvoGuid(); 
			mySession.mySkype.getConversationByIdentity(conversationID); 
			String msgAuthor = message.getAuthor(); 
			String msgBody = message.getBodyXml(); 
			if (!msgAuthor.equals(mySession.myAccountName)) { 
				if (PhysicianApplication.getPatientAcc().length() == 0) { 
					PhysicianApplication.setPatientAcc(msgAuthor); 
					PhysicianApplication 
							.setApplicationState(PhysicianApplication.applicationState.READYCALL); 
				}  
				 
				if (PhysicianApplication.getPatientAcc().equals( 
						msgAuthor)) { 
					if (msgBody.contains("[") && msgBody.contains(":") 
							&& msgBody.contains("]")) { 
						int[] markers = new int[] { msgBody.indexOf("["), 
								msgBody.indexOf(":"), msgBody.indexOf("]") }; 
						String name = msgBody.substring(markers[0]+1, markers[1]); 
						String CPR = msgBody.substring(markers[1]+1, markers[2]); 
						PhysicianApplication.setCurrentPatient(name, CPR); 
					} 
					else { 
						PhysicianApplication.setTextAreaText(msgAuthor 
								+ " wrote: \n" + msgBody); 
					} 
					PhysicianApplication.enableButton(true); 
					 
				} else { 
					Net4CareSession.myConsole.printf("ignore message"); 
					// ignore message 
				} 
			} else { 
				Net4CareSession.myConsole 
						.printf("%s: Ignoring ConversationListener.OnMessage of type %s%n", 
								mySession.myClassTag, msgType.toString()); 
			} 
 
		} 
 
	} 
 
 
	public void onMessage(Skype obj, Message message, 
			boolean changesInboxTimestamp, Message supersedesHistoryMessage, 
			Conversation conversation) { 
		Message.Type msgType = message.getType(); 
		if (msgType == Message.Type.POSTED_TEXT) { 
			String conversationID = message.getConvoGuid(); 
			mySession.mySkype.getConversationByIdentity(conversationID); 
			String msgAuthor = message.getAuthor(); 
			String msgBody = message.getBodyXml(); 
			if (!msgAuthor.equals(mySession.myAccountName)) { 
				if (PhysicianApplication.getPatientAcc().length() == 0) { 
					PhysicianApplication.setPatientAcc(msgAuthor); 
					PhysicianApplication 
							.setApplicationState(PhysicianApplication.applicationState.READYCALL); 
				}  
				 
				if (PhysicianApplication.getPatientAcc().equals( 
						msgAuthor)) { 
					if (msgBody.contains("[") && msgBody.contains(":") 
							&& msgBody.contains("]")) { 
						int[] markers = new int[] { msgBody.indexOf("["), 
								msgBody.indexOf(":"), msgBody.indexOf("]") }; 
						String name = msgBody.substring(markers[0]+1, markers[1]); 
						String CPR = msgBody.substring(markers[1]+1, markers[2]); 
						PhysicianApplication.setCurrentPatient(name, CPR); 
					} 
					else { 
						PhysicianApplication.setTextAreaText(msgAuthor 
								+ " wrote: \n" + msgBody); 
					} 
					PhysicianApplication.enableButton(true); 
					 
				} else { 
					Net4CareSession.myConsole.printf("ignore message"); 
					// ignore message 
				} 
			} else { 
				Net4CareSession.myConsole 
						.printf("%s: Ignoring ConversationListener.OnMessage of type %s%n", 
								mySession.myClassTag, msgType.toString()); 
			} 
 
		} 
	} 
 
} 
