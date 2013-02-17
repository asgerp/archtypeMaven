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
 
package org.net4care.storage.delegate; 
 
import org.net4care.storage.AddressData; 
import org.net4care.storage.ExternalDataSource; 
import org.net4care.storage.PersonData; 
import org.net4care.storage.TreatmentData; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
 
/** A fake object implementation of the ExternalDataSource 
 * that provides a very limited set of known CPR and 
 * a very limited set of treatments. 
 *  
 * @author Henrik Baerbak Christensen and Morten Larsson, Aarhus University 
 * 
 */ 
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{3}")}
	           ) 
public class FakeObjectExternalDataSource implements ExternalDataSource { 
 
	// Patients 
	public static final String NANCY_CPR = "251248-4916"; 
	public static final String JENS_CPR = "120753-2355"; 
	public static final String BIRGITTE_CPR = "030167-1648"; 
	public static final String OLE_CPR = "161134-1341"; 
	public static final String LISE_CPR = "010189-1626"; 
	public static final String MOHAMMED_CPR = "120575-4343"; 
	public static final String IBRAHIM_CPR = "190964-9735"; 
	public static final String BAHAR_CPR = "250255-1234"; 
	public static final String JANE_CPR = "301212-1642"; 
	public static final String LAILA_CPR = "092987-1264"; 
 
	//Patients from Telekat data. 
	public static final String TELEKAT_1 = "2007333995"; 
	public static final String TELEKAT_2 = "1602389995"; 
	public static final String TELEKAT_3 = "604449989"; 
	public static final String TELEKAT_4 = "2905469995"; 
	public static final String TELEKAT_5 = "1910323995"; 
	public static final String TELEKAT_6 = "905439989"; 
	public static final String TELEKAT_7 = "1708429995"; 
	public static final String TELEKAT_8 = "1805449995"; 
	public static final String TELEKAT_9 = "0903499995"; 
	public static final String TELEKAT_10 = "0905439989"; 
	public static final String TELEKAT_11 = "2201479995"; 
	public static final String TELEKAT_12 = "2712569995"; 
	public static final String TELEKAT_13 = "3105343995"; 
	public static final String TELEKAT_14 = "2409509995"; 
	public static final String TELEKAT_15 = "2302343995"; 
	public static final String TELEKAT_16 = "0604449989"; 
 
	// Physicains 
	public static final String CARSTEN_CPR = "040156-1505"; 
	public static final String ANNE_VINHOLT = "161167-1254"; 
	public static final String DONALD_DOC = "121212-1111"; 
 
 
	private static final  AddressData skejby =  
			new AddressData("Brendstrupgaardsvej 100","8200", 
					"Aarhus N","Danmark", "78450000" ); 
 
	@Override 
	public PersonData getPersonData(String cpr) throws Net4CareException { 
		PersonData pd = null; 
		if ( cpr.equals( NANCY_CPR )) { 
			AddressData addr = new AddressData("Skovvejen 12","8010", 
					"Aarhus N","Danmark", "86121824"); 
			pd = new PersonData(cpr, "Nancy","Berggren","F","19481225","86121824", addr); 
		} else if ( cpr.equals( JENS_CPR )){ 
			AddressData addr = new AddressData("Havvej 234","8520", 
					"Lystrup","Danmark", "86875623"); 
			pd = new PersonData(cpr, "Jens","Olsen","M","19562503","86875623", addr); 
		} else if ( cpr.equals( BIRGITTE_CPR )){ 
			AddressData addr = new AddressData("Hovmarken 15","9000", 
					"Aalborg","Danmark", "98121824"); 
			pd = new PersonData(cpr, "Birgitte","Pedersen","F","19870105","98121824", addr); 
		} else if ( cpr.equals( OLE_CPR )){ 
			AddressData addr = new AddressData("Lille Tornbjergvej 149","5220", 
					"Odense SØ","Danmark", "65931600"); 
			pd = new PersonData(cpr, "Ole","Dalgaard Lauritzen","M","19340612","65931600", addr); 
		} else if ( cpr.equals( LISE_CPR )){ 
			AddressData addr = new AddressData("Karmen 1","6623", 
					"Vorbasse","Danmark", "64294829"); 
			pd = new PersonData(cpr, "Lise","Lone Lohmann","F","19891648","64294829", addr); 
		} else if ( cpr.equals( MOHAMMED_CPR )){ 
			AddressData addr = new AddressData("Overgade 16A","6760", 
					"Ribe","Danmark", "54393834"); 
			pd = new PersonData(cpr, "Mohammed","Al Tyhmäh","M","19752359","54393834", addr); 
		} else if ( cpr.equals( IBRAHIM_CPR )){ 
			AddressData addr = new AddressData("Matadorstrædet 97 4. th.","6900", 
					"Skjern","Danmark", "20215613"); 
			pd = new PersonData(cpr, "Ibrahim","Fathcie Gugemiy","M","19641404","20215613", addr); 
		} else if ( cpr.equals( BAHAR_CPR )){ 
			AddressData addr = new AddressData("Vestergade 68 3. mf.","5000", 
					"Odense C","Danmark", "32102912"); 
			pd = new PersonData(cpr, "Bahar","Soomekh","F","19551941","32102912", addr); 
		} else if ( cpr.equals( JANE_CPR )){ 
			AddressData addr = new AddressData("Stengade 30","2100", 
					"København Ø","Danmark", "65293821"); 
			pd = new PersonData(cpr, "Jane","Line Hansson","F","19121242","65293821", addr); 
		} else if ( cpr.equals( LAILA_CPR )){ 
			AddressData addr = new AddressData("Kvisten 1","2750", 
					"Ballerup","Danmark", "89172481"); 
			pd = new PersonData(cpr, "Laila","Niggie","F","19870154","89172481", addr); 
		} else if ( cpr.equals( CARSTEN_CPR )){ 
			pd = new PersonData( cpr, "Carsten", "Nieboehl", "M", "19560104", "78450000", skejby ); 
		} else if ( cpr.equals( TELEKAT_1 )){ 
			AddressData addr = new AddressData("Damgårdsvej 122C","4891", 
					"Toreby Lolland","Danmark", "24943714"); 
			pd = new PersonData(cpr, "Erik","Lademann Hollstein","M","19330720","24943714", addr); 
		} else if ( cpr.equals( TELEKAT_2 )){ 
			AddressData addr = new AddressData("NederHoltevej 2","6534", 
					"Agerskov","Danmark", "40493827"); 
			pd = new PersonData(cpr, "Lisbeth","Araracus","F","19380216","40493827", addr); 
		} else if ( cpr.equals( TELEKAT_3 )){ 
			AddressData addr = new AddressData("Vanglet 530, 1. tv.","6980", 
					"Tim","Danmark", "32984719"); 
			pd = new PersonData(cpr, "Marethe","Hans-Nielsen","F","19494460","32984719", addr); 
		} else if ( cpr.equals( TELEKAT_4 )){ 
			AddressData addr = new AddressData("Kongensgade 57, 4. th.","5000", 
					"Odense C","Danmark", "26140292"); 
			pd = new PersonData(cpr, "Trine","Tornbjerg","F","19460529","26140292", addr); 
		} else if ( cpr.equals( TELEKAT_5 )){ 
			AddressData addr = new AddressData("Vestergade 12, 1.","5000", 
					"Odense C","Danmark", "65931405"); 
			pd = new PersonData(cpr, "Hannas","Bananas","F","19321019","65931405", addr); 
		} else if ( cpr.equals( TELEKAT_6 )){ 
			AddressData addr = new AddressData("Erisgade 1","7080", 
					"Børkop","Danmark", "32103958"); 
			pd = new PersonData(cpr, "Karsten","Knækhæg","M","19430509","32103958", addr); 
		} else if ( cpr.equals( TELEKAT_7 )){ 
			AddressData addr = new AddressData("Matanansen 65","7620", 
					"Lemvig","Danmark", "31491845"); 
			pd = new PersonData(cpr, "Dr. Speedy","Gonzalez","F","19420817","31491845", addr); 
		} else if ( cpr.equals( TELEKAT_8 )){ 
			AddressData addr = new AddressData("Leverstrupvej 85","3330", 
					"Gørløse","Danmark", "21029583"); 
			pd = new PersonData(cpr, "Vixi","Bird","M","19440518","21029583", addr); 
		} else if ( cpr.equals( TELEKAT_9 )){ 
			AddressData addr = new AddressData("Skorstensfejergade 11","2700", 
					"Brønshøj","Danmark", "54304932"); 
			pd = new PersonData(cpr, "Niels","Fabrisius Hansen","M","19490309","54304932", addr); 
		} else if ( cpr.equals( TELEKAT_10 )){ 
			AddressData addr = new AddressData("Spanien 2","8000", 
					"Aarhus C","Danmark", "49204820"); 
			pd = new PersonData(cpr, "Arne","Nøjegaard Iversen","M","19430509","49204820", addr); 
		} else if ( cpr.equals( TELEKAT_11 )){ 
			AddressData addr = new AddressData("Lillebjergsted 61","2000", 
					"Frederiksberg","Danmark", "65395017"); 
			pd = new PersonData(cpr, "Holger","Danske","M","19470122","65395017", addr); 
		} else if ( cpr.equals( TELEKAT_12 )){ 
			AddressData addr = new AddressData("Berlinervej 431, 4. mf.","1790", 
					"København V","Danmark", "22059433"); 
			pd = new PersonData(cpr, "Erik","Den Røde","M","19561227","22059433", addr); 
		} else if ( cpr.equals( TELEKAT_13 )){ 
			AddressData addr = new AddressData("Højstrupgaardsgade 7","8963", 
					"Auning","Danmark", "54032928"); 
			pd = new PersonData(cpr, "Welter","Lonich","M","19340531","54032928", addr); 
		} else if ( cpr.equals( TELEKAT_14 )){ 
			AddressData addr = new AddressData("Nielsbors Allé 124","5220", 
					"Odense SØ","Danmark", "65382918"); 
			pd = new PersonData(cpr, "Lonni","Niggiesen","F","19340531","65382918", addr); 
		} else if ( cpr.equals( TELEKAT_15 )){ 
			AddressData addr = new AddressData("Maltevej 55","9430", 
					"Vadum","Danmark", "44938137"); 
			pd = new PersonData(cpr, "Henrik","Popolonias","M","19340223","44938137", addr); 
		} else if ( cpr.equals( TELEKAT_16 )){ 
			AddressData addr = new AddressData("Simondachstrasse 13","9460", 
					"Brovst","Danmark", "24104932"); 
			pd = new PersonData(cpr, "Elter","Langegaard","M","19440406","24104932", addr); 
		}  
		else { 
			throw new Net4CareException(Constants.ERROR_UNKNOWN_CPR,"The CPR nr: '" + cpr + "' does not exist in the system."); 
		} 
		return pd; 
	} 
 
	@Override 
	public TreatmentData getTreatmentData(String treatmentID) { 
		return new TreatmentData(){ 
			@Override 
			public String getAuthorCPR() { 
				return CARSTEN_CPR; 
			} 
 
			@Override 
			public String getStewardOrganizationName() { 
				return "Region Midt / Skeyby Sygehus"; 
			} 
 
			@Override 
			public AddressData getCustodianAddr() { 
				return skejby; 
			}}; 
	} 
 
} 
