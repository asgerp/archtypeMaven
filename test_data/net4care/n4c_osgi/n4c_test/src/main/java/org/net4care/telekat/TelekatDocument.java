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

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.net4care.observation.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class TelekatDocument {

	private Document doc = null;
	private StandardTeleObservation sto = null;

	private long docTime = 0;

	private String patientCPR = "";
	private String organisationOID = "";
	private String treatmentID = "";
	private String codeSystem = "";
	private String devModel = "";
	private String devType = "";
	private String devManufacturer = "";
	private String devSerialId = "";
	private String devPartNr = "";
	private String devSoftwareRev = "";
	private String devHardwareRev = "";

	public TelekatDocument(String filePath) throws ParserConfigurationException, SAXException, IOException {
		this(new File(filePath));
	}	

	public TelekatDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
	}	

	public StandardTeleObservation getStandardTeleObservation() {		

		List<ClinicalQuantity> quantities = null;

		// Dummy values are used for these vars, because the telekat documents are lacking these informations.
		treatmentID = "dummy";	
		devManufacturer = "dummy";
		devType = "dummy";
		devPartNr = "dummy";
		devSoftwareRev = "dummy";
		devHardwareRev = "dummy";

		try {
			//Get time value for doc and convert it to timestamp.
			DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = format.parse(getPathValue("//effectiveTime/attribute::value"));
			docTime = date.getTime();


			// Values are fetched from the XML-doc using XPath exps.
			patientCPR = getPathValue("//recordTarget/patientRole/id/attribute::extension");
			organisationOID = getPathValue("//author/assignedAuthor/id/attribute::root");
			codeSystem = getPathValue("//component/structuredBody/component/section/entry/organizer/component/observation/code/attribute::codeSystem");
			devModel = getPathValue("//component/structuredBody/component/section/entry/organizer/participant/participantRole/playingDevice/manufacturerModelName");
			devSerialId = getPathValue("//component/structuredBody/component/section/text");
			devSerialId = devSerialId.substring(devSerialId.length()-16);

			// The clinical quantities in the XML documents are converted to ClinicalQuantity objects.
			quantities = getClinicalQuantities();

			// TODO: Reenable later HBC
			//ObservationSpecifics obs = getObservationSpecifics();

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//System.out.println("--- New STO ---");

		// ObscervationSpecifics and DeviceDescription are created from the fetched values.
		ObservationSpecifics bp = new DefaultObservationSpecifics(quantities);
		DeviceDescription device = new DeviceDescription(devType, devModel, devManufacturer, devSerialId, devPartNr, devHardwareRev, devSoftwareRev);

		if(sto == null) 
			sto = new StandardTeleObservation( patientCPR, organisationOID, treatmentID, codeSystem, device, bp );
		sto.setTime(docTime);
		return sto;
	}

	// Unfinished code HBC
	private ObservationSpecifics getObservationSpecifics() throws XPathExpressionException {
		ObservationSpecifics obs = null;

		// Make xPath expression that filters out the observations contained in the XML document.
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression exp = xpath.compile("//component/structuredBody[child::component[child::section[title!='Medical Equipment']]]/component/section/entry/organizer/component/observation");

		//Evaluation of xPath expression and casting to a nodelist of the resulting observations.
		NodeList nList = ((NodeList) exp.evaluate(doc, XPathConstants.NODESET));

		//The algorithm below finds the values (i.e. the measured value, the unit, the clinical code and the display name) that constitutes a ClinicalQuantity. 
		String value, unit, code, displayName;
		for(int i = 0 ; i < nList.getLength(); i ++) {
			value = "N/A"; unit = "N/A"; code = "N/A"; displayName = "N/A";
			NodeList obsNodes = nList.item(i).getChildNodes();
			for(int j = 0; j < obsNodes.getLength(); j ++) {
				Node n = obsNodes.item(j);
				if(n.getNodeName().equals("code")) {
					NamedNodeMap atts = n.getAttributes();
					code = atts.getNamedItem("code").getTextContent();
					displayName = atts.getNamedItem("displayName").getTextContent();
				}
				if(n.getNodeName().equals("value")) {
					NamedNodeMap atts = n.getAttributes();
					unit = atts.getNamedItem("unit").getTextContent();
					value = atts.getNamedItem("value").getTextContent().replace(',', '.');
				}
			}
			// TODO: Convert to specific types of ObsSpecifics (HBC)
			System.out.println(" "+displayName + " / "+ code+ " / "+ unit + " / " + value);
		}
		return obs;
	}

	/**
	 * 
	 * @param xPath  xPath expression to evaluate.
	 * @return The resulting value of evaluating the xPath expression.
	 * @throws XPathExpressionException
	 */
	private String getPathValue(String xPath) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression exp = xpath.compile(xPath);
		Object result = exp.evaluate(doc, XPathConstants.NODESET);
		return ((NodeList)result).item(0).getTextContent();
	}

	/**
	 * 
	 * @return A list of the ClinicalQuantities contained in the XML document.
	 * @throws XPathExpressionException
	 */
	private List<ClinicalQuantity> getClinicalQuantities() throws XPathExpressionException {

		// Make container for the result.
		List<ClinicalQuantity> resultList = new ArrayList<ClinicalQuantity>();

		// Make xPath expression that filters out the observations contained in the XML document.
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression exp = xpath.compile("//component/structuredBody[child::component[child::section[title!='Medical Equipment']]]/component/section/entry/organizer/component/observation");

		//Evaluation of xPath expression and casting to a nodelist of the resulting observations.
		NodeList nList = ((NodeList) exp.evaluate(doc, XPathConstants.NODESET));

		//The algorithm below finds the values (i.e. the measured value, the unit, the clinical code and the display name) that constitutes a ClinicalQuantity. 
		String value, unit, code, displayName;
		for(int i = 0 ; i < nList.getLength(); i ++) {
			value = "N/A"; unit = "N/A"; code = "N/A"; displayName = "N/A";
			NodeList obsNodes = nList.item(i).getChildNodes();
			for(int j = 0; j < obsNodes.getLength(); j ++) {
				Node n = obsNodes.item(j);
				if(n.getNodeName().equals("code")) {
					NamedNodeMap atts = n.getAttributes();
					code = atts.getNamedItem("code").getTextContent();
					displayName = atts.getNamedItem("displayName").getTextContent();
				}
				if(n.getNodeName().equals("value")) {
					NamedNodeMap atts = n.getAttributes();
					unit = atts.getNamedItem("unit").getTextContent();
					value = atts.getNamedItem("value").getTextContent().replace(',', '.');
				}
			}
			value = correctTelekatValueOnSpirometry(value,code);
			resultList.add(new ClinicalQuantity(new Double(value),unit,code,displayName));
		}		
		return resultList;
	}

	private String correctTelekatValueOnSpirometry(String v, String code) {
		String result = v;
		if(code.equals("MCS88015") || code.equals("MCS88016")) {
			double value = Double.valueOf(v);
			if(value > 5 ) {
				value = value / 1000;
			}
			DecimalFormat df = new DecimalFormat("#.##");
			result = df.format(value);
		}
		return result;
	}
}
