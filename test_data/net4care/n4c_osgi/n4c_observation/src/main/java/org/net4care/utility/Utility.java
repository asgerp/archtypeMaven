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
 
package org.net4care.utility; 
 
import java.io.*; 
import java.util.StringTokenizer; 
 
import javax.xml.parsers.*; 
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
 
import org.apache.log4j.Logger; 
import org.w3c.dom.Document; 
import org.w3c.dom.Node; 
import org.xml.sax.*; 
 
/** Various utility functions.  
 *  
 * Contains: 
 * 
 * Functions to convert from XML Document to nicely formatted XML string; 
 * and back again. 
 *  
 * Functions to convert between code lists and its bar separated format. 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 * 
 */ 
 
public class Utility { 
 
  private static Logger logger = Logger.getLogger(Utility.class); 
 
  /** convert an XML document to a human readable string with 
   * proper indentation. 
   * @param doc the XML document to convert 
   * @return the string representation of the document. 
   */ 
  public static String convertXMLDocumentToString(Node doc)  throws Net4CareException { 
 
    Transformer trans = null; 
    try { 
      trans = transfac.newTransformer(); 
    } catch ( TransformerException e ) { 
      logger.error("Error in creating Transformer instance.", e); 
      throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, "Error in creating Transformer instance."); 
    } 
    //trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
    trans.setOutputProperty(OutputKeys.INDENT, "yes"); 
    trans.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );  
 
    //create string from xml tree 
    StringWriter sw = new StringWriter(); 
    StreamResult result = new StreamResult(sw); 
    DOMSource source = new DOMSource(doc); 
    try { 
      trans.transform(source, result); 
    } catch ( TransformerException e ) { 
      logger.error("Error in transforming XML-document to string.", e); 
      throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, "Error in transforming XML-document to string."); 
    } 
    //sw.close(); 
 
    String xmlString = sw.toString(); 
 
    return xmlString; 
  } 
   
 
  private static TransformerFactory transfac = TransformerFactory.newInstance(); 
  private static DocumentBuilderFactory factory = 
      DocumentBuilderFactory.newInstance(); 
 
  /** convert a valid XML string into the equivalent Document object 
   *  
   * @param xml well formed XML string 
   * @return corresponding Document instance or null of failed 
   * @throws Net4CareException  
   */ 
  public static Document convertXMLStringToDocument(String xml) throws Net4CareException { 
    // Convert the XML string to w3c Document 
    Document doc = null; 
    try { 
      DocumentBuilder builder = factory.newDocumentBuilder(); 
      doc = builder.parse(new InputSource(new StringReader(xml))); 
    } catch (IOException e) { 
      logger.error("IO error while parsing XML string.", e); 
      throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, "IO error while parsing XML string."); 
    } 
    catch ( Exception e ) { 
      logger.error("Parse error on XML string.", e); 
      throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, "Parse error on XML string."); 
    } 
    return doc; 
  } 
 
  public static String convertCodeListToBarSeparatedString(String[] codelist) { 
    String concatenatedCodes = "|"; 
    for (int i = 0; i< codelist.length; i++) { 
      concatenatedCodes += codelist[i]+"|"; 
    } 
  
    return concatenatedCodes; 
  } 
 
  public static String[] convertBarSeparatedStringToCodeList( 
      String barPackedCodeList) { 
    StringTokenizer st = new StringTokenizer(barPackedCodeList, "|"); 
    String[] codelist = new String[st.countTokens()]; 
    int i = 0; 
    while ( st.hasMoreTokens() ) { 
      codelist[i++] = st.nextToken(); 
    } 
    return codelist; 
  } 
 
} 
