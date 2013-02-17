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
 * Copyright (C) 2010, Skype Limited 
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
 
import com.skype.api.Account; 
 
/** 
 * Collect common sign-in/sign-out fields as members and encapsulate common code as methods. 
 *  
 * @author Andrea Drane 
 *  
 * @since 1.0 
 */ 
public class SignInMgr { 
	/** 
	 * Info/Debug console output message prefix/identifier tag. 
	 * Corresponds to class name. 
	 *  
	 * @since 1.0 
	 */ 
    public static final String MY_CLASS_TAG = "SignInMgr"; 
 
    /** 
  	 * Request polling interval (milliseconds). 
  	 *  
  	 * @since 1.0 
  	 */ 
    public static final int DELAY_INTERVAL = 1000;   // Equivalent to 1 second. 
 
    /** 
  	 * Request polling limit (iterations). 
  	 * Results in a maximum total delay of <code>DELAY_CNT * DELAY_INTERVAL</code> 
  	 * <em>milliseconds</em> before giving up and failing. 
  	 *  
  	 * @since 1.0 
  	 */ 
    public static final int DELAY_CNT = 45; 
     
    /** 
  	 * Delay interval prior to logout (milliseconds). 
  	 * <br /><br /> 
  	 * Timing issue w/ some early versions of SkypeKit runtime - "immediate" logout 
  	 * frequently causes the runtime to reflect an erroneous logout reason of 
  	 * Account.LOGOUTREASON.APP_ID_FAILURE, so wait a few seconds...  
  	 *  
  	 * @since 1.0 
  	 */ 
    public static final int LOGOUT_DELAY = (1 * 1000);   // Equivalent to 1 second. 
 
	/** 
	 * Common SkypeKit tutorial login processing. 
	 * <ul> 
	 *   <li>populates the session's Account instance</li> 
	 *   <li>writes message to the console indicating success/failure/timeout</li> 
	 *   <li>writes stack trace if I/O error setting up the transport!</li> 
	 * </ul> 
	 *  
	 * @param TAG 
	 * 	Invoker's {@link #MY_CLASS_TAG}. 
	 * @param mySession 
	 *	Partially initialized session instance providing access to this sessions's Skype object. 
	 *  
	 * @return 
	 *   <ul> 
	 * 	   <li>true: success; {@link org.net4care.skype.api.Net4CareSession.util.MySession#myAccount} populated</li> 
	 *	   <li>false: failure</li> 
	 *   </ul> 
	 *  
	 * @since 1.0 
	 */ 
	public boolean Login(String TAG, Net4CareSession mySession, String myAccountPword) { 
 
		if (mySession.isLoggedIn()) { 
			// Already logged in... 
			Net4CareSession.myConsole.printf("%s: %s already logged in! (IP Addr %s:%d)%n", 
								TAG, mySession.myAccountName, 
								Net4CareSession.IP_ADDR, Net4CareSession.PORT_NUM); 
			return (true); 
		} 
 
		// Issue login request 
		Net4CareSession.myConsole.printf("%s: Issuing login request%n", TAG); 
		mySession.myAccount.loginWithPassword(myAccountPword, false, true); 
			 
		// Loop until AccountListener shows we are logged in or time-out... 
		Net4CareSession.myConsole.printf("%s: Waiting for login to complete...%n", TAG); 
		int i = 0; 
		while ((i < SignInMgr.DELAY_CNT) && (!mySession.isLoggedIn())) { 
			try { 
				Thread.sleep(SignInMgr.DELAY_INTERVAL); 
			} 
			catch (InterruptedException e) { 
				// TODO Auto-generated catch block 
				e.printStackTrace(); 
				return (false); 
			} 
			Net4CareSession.myConsole.printf("\t %d...%n", i++); 
		} 
 
		if (i < SignInMgr.DELAY_CNT) { 
			// Successful Login 
			Net4CareSession.myConsole.printf("%s: %s Logged In (IP Addr %s:%d)%n", 
								TAG, mySession.myAccountName, 
								Net4CareSession.IP_ADDR, Net4CareSession.PORT_NUM); 
			return (true); 
		} 
		else { 
			Net4CareSession.myConsole.printf("%s: Login timed out for %s! (IP Addr %s:%d)%n", 
								TAG, mySession.myAccountName, 
								Net4CareSession.IP_ADDR, Net4CareSession.PORT_NUM); 
			return (false); 
		} 
	} 
	 
 
	/** 
	 * Common SkypeKit tutorial logout processing. 
	 * <ul> 
	 *   <li>writes message to the console indicating success/failure/timeout</li> 
	 *   <li>writes stack trace if I/O error setting up the transport!</li> 
	 * </ul> 
	 *  
	 * Delays the logout by a second or so to ensure that the SkypeKit runtime has fully settled in 
	 * if the interval between sign-in and sign-out is really, really short (such as exists in 
	 * {@link com.skype.tutorial.step1.Tutorial_1}). We don't want to see 
  	 * Account.LOGOUTREASON.APP_ID_FAILURE unless our AppToken is truly bogus!  
	 *  
	 * @param myTutorialTag 
	 * 	Invoker's {@link #MY_CLASS_TAG}. 
	 * @param mySession 
	 * 	Populated session object providing access to the invoker's 
	 *  Skype and Account objects. 
	 *   
	 * @see #LOGOUT_DELAY 
	 *  
	 * @since 1.0 
	 */ 
	public void Logout(String myTutorialTag, Net4CareSession mySession) { 
		 
		// Give the runtime a chance to catch its breath if it needs to... 
		try { 
			Thread.sleep(SignInMgr.LOGOUT_DELAY); 
		} 
		catch (InterruptedException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
 
		if (!mySession.isLoggedIn()) { 
			// Already logged out... 
			Net4CareSession.myConsole.printf("%s: %s already logged out! (IP Addr %s:%d)%n", 
								myTutorialTag, mySession.myAccountName, 
								Net4CareSession.IP_ADDR, Net4CareSession.PORT_NUM); 
			return; 
		} 
 
		// Issue logout request 
		mySession.myAccount.logout(false); 
 
		// Loop until AccountListener shows we are logged out or we time-out... 
		Net4CareSession.myConsole.printf("%s: Waiting for logout to complete...%n", myTutorialTag); 
		int i = 0; 
/* 
 * 		while ((i < SignInMgr.DELAY_CNT) && (SignInMgr.isLoggedIn(mySession.myAccount))) { 
 */ 
		while ((i < SignInMgr.DELAY_CNT) && (mySession.isLoggedIn())) { 
			try { 
				Thread.sleep(SignInMgr.DELAY_INTERVAL); 
			} 
			catch (InterruptedException e) { 
				// TODO Auto-generated catch block# 
				e.printStackTrace(); 
				return; 
			} 
			Net4CareSession.myConsole.printf("\t%d...%n", i++); 
		} 
 
		if (i < SignInMgr.DELAY_CNT) { 
			// Successful Logout 
			Net4CareSession.myConsole.printf("%s: %s logged out (IP Addr %s:%d)%n", 
								myTutorialTag, mySession.myAccountName, 
								Net4CareSession.IP_ADDR, Net4CareSession.PORT_NUM); 
		} 
		else { 
			Net4CareSession.myConsole.printf("%s: Logout timed out for %s! (IP Addr %s:%d)%n", 
								myTutorialTag, mySession.myAccountName, 
								Net4CareSession.IP_ADDR, Net4CareSession.PORT_NUM); 
		} 
	} 
 
 
	/** 
	 * <em>Dynamically</em> determines if an Account is signed in. 
	 * <br /><br /> 
	 * Specifically, this involves querying the DB to determine if the 
	 * Account's status property reflects Account.STATUS.LOGGED_IN. 
	 * For mobile devices, such activity can adversely affect battery life.  
	 *  
	 * @param myAccount 
	 *  The target Account.  
	 *   
	 * @return 
	 * <ul> 
	 *   <li>true: currently signed in</li> 
	 *   <li>false: currently signed out <em>or</em> target Account is null</li> 
	 * </ul> 
	 *  
	 * @see org.net4care.skype.api.Net4CareSession.util.MySession#isLoggedIn() 
	 *  
	 * @since 1.0 
	 */ 
	public static boolean isLoggedIn(Account myAccount) { 
 
		Net4CareSession.myConsole.println("Dynamically determining if Account is logged in..."); 
		 
		if (myAccount != null) { 
			if (myAccount.getStatusWithProgress().status == Account.Status.LOGGED_IN) { 
				return (true); 
			} 
		} 
		return (false); 
	} 
} 
