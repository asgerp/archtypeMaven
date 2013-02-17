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
 
package org.net4care.phmr; 
 
import java.io.*; 
import java.util.Calendar; 
 
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
 
import org.junit.*; 
import static org.junit.Assert.*; 
 
import org.w3c.dom.Document; 
import org.xml.sax.SAXException; 
 
import org.net4care.cda.PHMRBuilder; 
import org.net4care.common.*; 
import org.net4care.observation.Codes; 
import org.net4care.observation.DeviceDescription; 
import org.net4care.observation.StandardTeleObservation; 
import org.net4care.storage.RegistryEntry; 
import org.net4care.storage.delegate.FakeObjectExternalDataSource; 
import org.net4care.utility.*; 
 
import com.smb.homeapp.*; 
 
import org.net4care.helper.*; 
 
import org.apache.log4j.Logger; 
 
 
/** Testing the PHMR output.  
 *  
 * There are some smoketests as well as a comprehensive test 
 * that compares the full PHMR document with a validated 
 * XML stored in the ExpectedValue folder. 
 *  
 * An improved test (in the future) would be to run it through 
 * a schema validation as well. You can find one in src/AP20 
 * in the repository. The process I have used is to 
 * a) enable the system.println statement in one 
 * of the test cases 
 * b) copy the output from the console into an 
 * example xml file in the 'resource' folder in 
 * the AP20 project 
 * c) update the AP20 program to read in this 
 * xml file and validate it against the schema. 
 *  
 * Henrik Baerbak Christensen, Net4Care, AU 
 *  */ 
public class TestPHMR { 
  private final String cpr = FakeObjectExternalDataSource.NANCY_CPR; 
  private final String organizationOID = Codes.DK_REGION_MIDT_SKEJBY_SYGEHUS;  
  private final String treatment_id = "EPJ-AU-1266-2"; 
 
  private StandardTeleObservation spiro_sto, bloodpressure_sto; 
  private Spirometry spiro; 
   
  private Document phmr; 
  private RegistryEntry metadata; 
 
  @Before  
  public void setup() throws Net4CareException { 
     
    // preset a time for the STO 
    final Calendar cal = Calendar.getInstance(); 
    cal.set(2012,5,1,7,30,00);  
 
    // Create a spirometry observation for Nancy 
    DeviceDescription device = 
        new DeviceDescription("Spirometer", "SpiroCraft-II",  
                              "MyCompany", "584216", "69854", "2.1", "1.1"); 
    spiro = new Spirometry(3.7, 3.42); 
    spiro_sto =  
      new StandardTeleObservation(cpr, organizationOID, treatment_id,  
                                  Codes.LOINC_OID, device, spiro ); 
    spiro_sto.setTime(cal.getTimeInMillis()); 
     
    bloodpressure_sto = HelperMethods.defineBloodPressureObservation(cpr, 143.0, 79.0, organizationOID, treatment_id); 
     
    UUIDStrategy uuidStrategy = new UUIDStrategy() {   
      public String generateUUID() { 
        return "FixedUUID"; 
      } 
    }; 
 
    // Convert it into a HL7 PHMR document 
    PHMRBuilder phmrForObservation =  
      new PHMRBuilder( new FakeObjectExternalDataSource(), spiro_sto,  
          uuidStrategy); 
      phmr = phmrForObservation.buildDocument(); 
 
    metadata = phmrForObservation.buildRegistryEntry(); 
  } 
   
  private DocumentBuilderFactory factory;  
  private DocumentBuilder builder; 
 
 
  @Test public void shouldMatchExpectedValue() throws ParserConfigurationException, SAXException, IOException, Net4CareException { 
 
    // testing something completely different? code by KMH 
    // Logger logger = Logger.getLogger(TestPHMR.class); 
    // logger.error("Dette er en test"); 
     
    factory = 
        DocumentBuilderFactory.newInstance(); 
    // important! http://www.kdgregory.com/index.php?page=xml.parsing 
    factory.setNamespaceAware(true);     
    builder = factory.newDocumentBuilder(); 
     
    Document expected; 
    expected = builder.parse( "ExpectedValues/PHMR_Nancy.xml"); 
 
    String exp, com; 
    exp = org.net4care.utility.Utility.convertXMLDocumentToString(expected); 
    com = org.net4care.utility.Utility.convertXMLDocumentToString(phmr); 
    assertEquals(exp,com); 
  } 
   
  @Test public void shouldReferProperPatient() throws Net4CareException { 
    //System.out.println( Utility.convertXMLDocumentToString(phmr)); 
    assertEquals( "19481225", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, "birthTime", "patient", phmr));   
  } 
 
  @Test public void shouldReferProperNet4CareOIDs() { 
    assertEquals( Codes.NET4CARE_ROOT_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("root", 0, "id", "ClinicalDocument", phmr)); 
    // AsOtherID is disabled 
    // assertEquals( Codes.DK_CPR_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("root", 0, "id", "asOtherIDs", phmr)); 
 // assertEquals( cpr, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension", 0, "id", "asOtherIDs", phmr)); 
    assertEquals( cpr, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("extension", 0, "id", "patientRole", phmr)); 
    assertEquals( Codes.DK_CPR_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("root", 0, "id", "patientRole", phmr)); 
     
    // TODO: Get the custodian in place later... 
    assertEquals( Codes.DK_REGION_MIDT_SKEJBY_SYGEHUS, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("root", 0, "id", "representedCustodianOrganization", phmr)); 
     
  } 
   
  @Test public void shouldContainFVCandFEV1() { 
    // validate FVC correctly stored 
    assertEquals( "19868-9", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 0, "code", "observation", phmr)); 
    assertEquals( Codes.LOINC_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("codeSystem", 0, "code", "observation", phmr)); 
    assertEquals( "FVC", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 0, "code", "observation", phmr)); 
    assertEquals( "L", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 0, "value", "observation", phmr)); 
    assertEquals( "3.7", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, "value", "observation", phmr)); 
     
     
    // and FEV1 
    assertEquals( "20150-9", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 1, "code", "observation", phmr)); 
    assertEquals( Codes.LOINC_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("codeSystem", 1, "code", "observation", phmr)); 
    assertEquals( "FEV1", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 1, "code", "observation", phmr)); 
    assertEquals( "L", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 1, "value", "observation", phmr)); 
    assertEquals( "3.42", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 1, "value", "observation", phmr)); 
  } 
 
  @Test public void shouldContainSysAndDia() throws Net4CareException { 
    // Convert blood pressure into a HL7 PHMR document 
    PHMRBuilder phmrForBlood =  
      new PHMRBuilder( new FakeObjectExternalDataSource(), bloodpressure_sto); 
      phmr = phmrForBlood.buildDocument(); 
  
    metadata = phmrForBlood.buildRegistryEntry(); 
    // System.out.println( Utility.convertXMLDocumentToString(phmr)); 
 
    assertEquals( "MSC88019", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 0, "code", "observation", phmr)); 
    assertEquals( Codes.UIPAC_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("codeSystem", 0, "code", "observation", phmr)); 
    assertEquals( "Systolisk BT", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 0, "code", "observation", phmr)); 
    assertEquals( "mm(Hg)", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 0, "value", "observation", phmr)); 
    assertEquals( "143.0", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 0, "value", "observation", phmr)); 
 
     
    assertEquals( "MSC88020", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("code", 1, "code", "observation", phmr)); 
    assertEquals( Codes.UIPAC_OID, HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("codeSystem", 1, "code", "observation", phmr)); 
    assertEquals( "Diastolisk BT", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("displayName", 1, "code", "observation", phmr)); 
    assertEquals( "mm(Hg)", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("unit", 1, "value", "observation", phmr)); 
    assertEquals( "79.0", HelperMethods.getValueOfAttrNamedInNodeIndexNamedEnclosedInNodeInDoc("value", 1, "value", "observation", phmr)); 
  } 
   
  @Test public void shouldBuildProperMetaData() { 
    assertNotNull( metadata ); 
     
    assertEquals( FakeObjectExternalDataSource.NANCY_CPR, metadata.getCpr() ); 
    assertEquals( spiro_sto.getTime(), metadata.getTimestamp() ); 
    assertEquals( Codes.LOINC_OID, metadata.getCodeSystem() ); 
 
    String[] codes = metadata.getCodesOfValuesMeasured(); 
    assertEquals( 2, codes.length ); 
    // Unfortunately this assert fails if the order of codes 
    // are permuated - HBC 
    assertArrayEquals( new String[] { "19868-9", "20150-9" }, 
                       codes ); 
  } 
 
   
  /** Generate expected value.  
   * DO NOT REENABLE THIS UNLESS YOU HAVE TESTED THAT 
   * THE OUTPUT IS CORRECT PHMR - CURRENTLY THIS IS 
   * A MANUAL PROCESS, USE THE VALIDATOR IN AP20 
   * IN THE REPOSITORY. 
   * @throws TransformerFactoryConfigurationError  
   * @throws TransformerException  
   */ 
  @Ignore 
  @Test public void generateExpectedValue() throws TransformerFactoryConfigurationError, TransformerException { 
    DOMSource source = new DOMSource(phmr); 
    StreamResult result = new StreamResult(new File("ExpectedValues/PHMR_Nancy.xml")); 
  
    // Output to console for testing 
    //StreamResult result = new StreamResult(System.out); 
  
    Transformer transformer; 
    transformer = TransformerFactory.newInstance().newTransformer(); 
    //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );  
 
    transformer.transform(source, result); 
  } 
 
} 
