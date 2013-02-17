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
 
package org.net4care.telekat; 
 
import org.net4care.cda.PHMRBuilder; 
import org.net4care.observation.*; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
import org.net4care.utility.*; 
import org.w3c.dom.Document; 
 
import com.smb.homeapp.*; 
 
/** Demo of Telekat data in phmr format. 
 * Creates a PHMR document that mimics some data 
 * we received from the Telekat project. 
 *  
 * This program was only used for creating a PHMR 
 * document to show to KMD as guide for how to convert. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU 
 */ 
public class ConvertTelekat { 
   
  public static void main(String[] args) throws Net4CareException { 
 
    String cpr = FakeObjectExternalDataSource.NANCY_CPR; 
    String organizationOID = Codes.DK_REGION_MIDT_SKEJBY_SYGEHUS;  
    String treatment_id = "EPJ-AU-1266-2"; 
 
    StandardTeleObservation sto; 
     
    Document phmr; 
 
    // Create a blood pressure observation for Nancy 
    DeviceDescription device = 
        new DeviceDescription("Bloodpressure", "model.name", "Tunstall", "00A0962997E7:221",  
		                       "part.id", "hw.rev.id","sw.rev.id"); 
    BloodPressure bp = new BloodPressure(107.0, 81.0); 
    sto = 
        new StandardTeleObservation(cpr, organizationOID, treatment_id,  
            Codes.UIPAC_OID, device, bp ); 
 
    // Convert it into a HL7 PHMR document 
    PHMRBuilder phmrForObservation =  
        new PHMRBuilder( new FakeObjectExternalDataSource(), sto); 
    phmr = phmrForObservation.buildDocument();     
   
    System.out.println( Utility.convertXMLDocumentToString(phmr)); 
  } 
} 
