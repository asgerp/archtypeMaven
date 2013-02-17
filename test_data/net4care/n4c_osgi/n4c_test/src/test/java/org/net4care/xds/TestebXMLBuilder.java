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
 
package org.net4care.xds; 
 
import static org.junit.Assert.*; 
 
import java.io.*; 
import java.util.*; 
 
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
 
import org.apache.axis.message.*; 
import org.junit.*; 
 
//import org.custommonkey.xmlunit.*; 
 
import org.net4care.common.*; 
import org.net4care.storage.*; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
import org.net4care.utility.QueryKeys; 
import org.net4care.xdsproxy.*; 
 
import org.w3c.dom.*; 
import org.xml.sax.SAXException; 
 
import com.microsoft.schemas.Message.MessageBody; 
 
/** Test the builder that convert N4C metadata into 
 * Soap messages acceptable by the MS Codeplex XDS.b 
 * implementation. 
 *  
 * Some commented code reflects failed experiments 
 * with using XMLUnit as testing framework. 
 *  
 * @author Net4Care, Henrik Baerbak Christensen, AU  
 * 
 */ 
public class TestebXMLBuilder { 
  private RegistryEntry meta_nancy1; 
  private EbXMLBuilder ebxmlBuilder; 
 
  private long time1Nancy; 
  
  private DocumentBuilderFactory factory;  
  private DocumentBuilder builder; 
   
  private Transformer transformer; 
 
  @Before 
  public void setup()  
      throws ParserConfigurationException,  
        TransformerConfigurationException,  
          TransformerFactoryConfigurationError { 
    factory = 
        DocumentBuilderFactory.newInstance(); 
    // important! http://www.kdgregory.com/index.php?page=xml.parsing 
    factory.setNamespaceAware(true); 
     
    builder = factory.newDocumentBuilder(); 
     
    transformer = TransformerFactory.newInstance().newTransformer(); 
    //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
    transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );  
 
    // make sure XMLUnit uses the same parser 
    /* 
    XMLUnit.setTestParser( factory.getClass().getName() ); 
    XMLUnit.setControlParser( factory.getClass().getName() ); 
    */ 
    final Calendar cal = Calendar.getInstance(); 
    // System.out.println( "Date  = "+cal.getTime()); 
    cal.set(2012,5,1,7,30,00);  
    time1Nancy = cal.getTimeInMillis(); 
  
    meta_nancy1 = new RegistryEntry( NANCY_CPR, time1Nancy, LOINC_OID, 
        new String[] { FVC, GLUCOSE }, org_uid_viewcare, treatmentId_kol);  
 
     
    // Configure the ebXML builder with a strategy that 
    // generates fixed UUID strings to avoid randomness 
    // in output 
    ebxmlBuilder = new EbXMLBuilder(  
        new UUIDStrategy() { 
          @Override 
          public String generateUUID() { 
            return "TestStubFixedUUID"; 
          } 
        }, 
        new TimestampStrategy() { 
           
          @Override 
          public long getCurrentTimeInMillis() { 
            return cal.getTimeInMillis(); 
          } 
        }); 
  } 
 
  /** Validate the ebXML encoded body of the SOAP message. This is done through testing it 
   * against a file-stored properly formatted message. If the contents of the ebxml builder 
   * needs to be changed, you can use the method helperMakeNancy1ExpectedResult to 
   * generate a new expected test case value. 
   * @throws Net4CareException 
   * @throws SAXException 
   * @throws IOException 
   */ 
  @Test 
  public void shouldBuildSOAPContentsForProvideAndRegister() throws Net4CareException, SAXException, IOException { 
    Document expected, computed; 
 
    expected = builder.parse( "ExpectedValues/provideAndRegister_Nancy.xml"); 
 
    computed = ebxmlBuilder.buildSubmitObjectsRequest( meta_nancy1, "test_uniqueID", "test_assocTargetObjID" ); 
        
    //System.out.println( Utility.convertXMLDocumentToString(computed)); 
 
    String exp, com; 
    exp = org.net4care.utility.Utility.convertXMLDocumentToString(expected); 
    com = org.net4care.utility.Utility.convertXMLDocumentToString(computed); 
    assertEquals(exp,com); 
 
    /* Experiements with XMLUnit that failed and not persued due 
     * to timeboxing. HBC. 
    Node node; 
     
    node = expected.getFirstChild(); 
     
    Diff myDiff = new Diff(expected, computed); 
    DetailedDiff dd = new DetailedDiff(myDiff); 
    List list = dd.getAllDifferences(); 
     
    //System.out.println( "xx "+ myDiff ); 
     
     
     
    //assertTrue("pieces of XML are similar " + myDiff, myDiff.similar()); 
    //assertFalse("but are they identical? " + myDiff, myDiff.identical()); 
     *  
     */ 
  } 
 
  /** Validate that the full soap message has the overall correct structure, 
   * in particular sets the proper namespaces for xds-b and ebxml on the 
   * first couple of elements. The actual ebxml body is validated by other 
   * test cases. Other spot tests as well. 
   * @throws Exception 
   */ 
  @Test 
  public void shouldGenerateProperSOAPForProvideAndRegister() throws Exception { 
    SOAPBodyElement provideAndRegisterMsg; 
     
    final String targetID = "test_assocTargetObjID"; 
    provideAndRegisterMsg = ebxmlBuilder.buildProvideAndRegisterDocumentSetRequest(meta_nancy1, "test_uniqueID", targetID ); 
    assertNotNull( provideAndRegisterMsg ); 
     
    Element root = provideAndRegisterMsg.getAsDOM(); 
    assertEquals( "ProvideAndRegisterDocumentSetRequest", root.getNodeName() ); 
    assertEquals( "urn:ihe:iti:xds-b:2007", root.getNamespaceURI() ); 
     
    Element submitObjectRequest = (Element) root.getChildNodes().item(0); 
    assertEquals( "ns1:SubmitObjectsRequest", submitObjectRequest.getNodeName()); 
    assertEquals( "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", submitObjectRequest.getNamespaceURI()); 
  
    Element registryObjectList = (Element) submitObjectRequest.getChildNodes().item(0); 
    assertEquals( "ns1:RegistryObjectList", registryObjectList.getNodeName()); 
    assertEquals( "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", registryObjectList.getNamespaceURI()); 
  
    // The deeper level nodes are all under the xds:lcm namespace, named 'ns1' 
     
    // validate that the target object id is properly set 
    NodeList nl = root.getElementsByTagName("ns1:Association"); 
    assertEquals( 1, nl.getLength() ); 
     
    Element association = (Element) nl.item(0); 
    NamedNodeMap nnm = association.getAttributes(); 
 
    assertEquals( targetID, nnm.getNamedItem("targetObject").getNodeValue() ); 
     
     
    //create string from xml tree 
    StringWriter sw = new StringWriter(); 
    StreamResult result = new StreamResult(sw); 
    DOMSource source = new DOMSource(provideAndRegisterMsg); 
    try { 
      transformer.transform(source, result); 
    } catch ( TransformerException e ) { 
      throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, "Transformation error in test TestebXMLBuilder class."); 
    } 
 
    // System.out.println( sw ); 
 
  } 
   
  @Test 
  public void shouldBuildProperRetrieveDocumentSet() throws Net4CareException { 
    MessageBody msg = ebxmlBuilder.buildRetrieveDocumentSet("theUniqueId"); 
    MessageElement [] me = msg.get_any();   
    assertEquals(1, me.length); 
     
    assertEquals( "RetrieveDocumentSetRequest", me[0].getNodeName()); 
     
    Element dr = (Element) me[0].getChildNodes().item(0); 
    assertEquals( "DocumentRequest", dr.getNodeName()); 
     
    Element dui = (Element) dr.getChildNodes().item(0); 
    assertEquals("DocumentUniqueId", dui.getNodeName() ); 
    assertEquals("theUniqueId", dui.getChildNodes().item(0).toString()); 
    //System.out.println("** "+Utility.convertXMLDocumentToString(me[0])); 
  } 
   
  /** Generate the "expected value" file in case details in the generator are changed. 
   * DO NOT GENERATE A NEW EXPECTED VALUE UNLESS YOU HAVE TESTED MANUALLY AGAINST THE 
   * XDS.b THAT THE GENERATED SOAP MESSAGE IS ACTUALLY ACCEPTED! */ 
  @Ignore 
  @Test public void helperMakeNancy1ExpectedResult() throws TransformerException { 
    Document computed = ebxmlBuilder.buildSubmitObjectsRequest( meta_nancy1, "test_uniqueID", "test_assocTargetObjID" ); 
 
    DOMSource source = new DOMSource(computed); 
    StreamResult result = new StreamResult(new File("ExpectedValues/provideAndRegister_Nancy.xml")); 
  
    // Output to console for testing 
    // StreamResult result = new StreamResult(System.out); 
  
    transformer.transform(source, result); 
  } 
   
  // Patients 
  public static final String NANCY_CPR = "251248-4916"; 
 
  public static final String LOINC_OID = "2.16.840.1.113883.6.1"; 
  public static final String FVC = "19868-9"; 
  public static final String FEV1 = "20150-9"; 
  public static final String GLUCOSE = "30164-6"; 
 
  private final String org_uid_viewcare = "org.net4care.org.careview"; 
  private final String treatmentId_kol = "EPJ-RM-324-12-22"; 
 
} 
