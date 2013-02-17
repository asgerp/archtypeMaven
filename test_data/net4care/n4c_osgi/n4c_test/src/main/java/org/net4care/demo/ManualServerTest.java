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
 
package org.net4care.demo; 
 
import java.io.*; 
 
import javax.swing.*; 
import javax.swing.border.*; 
 
import java.awt.*; 
import java.awt.event.*; 
import java.net.MalformedURLException; 
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.*; 
import java.util.List; 
 
import org.net4care.forwarder.*; 
import org.net4care.serializer.Serializer; 
import org.net4care.serializer.delegate.ServerJSONSerializer; 
import org.net4care.forwarder.delegate.*; 
import org.net4care.forwarder.query.QueryPersonTimeInterval; 
import org.net4care.graph.ObservationGraph; 
import org.net4care.graph.delegate.StandardObservationGraph; 
import org.net4care.graph.delegate.StandardObservationGraphWithFilter; 
import org.net4care.observation.*; 
import org.net4care.utility.*; 
 
import com.smb.homeapp.Spirometry; 
 
/** 
 * Test program - provides various means of manual testing upload and query 
 * between a home client (this instance) and a running server. Make sure a 
 * server is running (go to the n4c_receiver folder and issue a 'mvn 
 * pax:provision' and wait until Felix Gogo writes the g! prompt). 
 *  
 * @author Henrik Baerbak Christensen, Aarhus University 
 *  
 */ 
 
public class ManualServerTest extends JFrame { 
 
	private static final long serialVersionUID = 1L; 
 
	public static void main(String[] args) throws IOException { 
		new ManualServerTest(); 
	} 
 
	String[] ptDatabase = new String[] { 
			"Nancy Berggren", "251248-4916", 
			"Jens Hansen", "120753-2355", 
			"Birgitte Roenholt", "030167-1648", 
			"Bahar Soomekh", "250255-1234", 
			"Ole Dalgaard Lauritzen", "161134-1341", 
			"Lise Lone Lohmann", "010189-1626", 
			"Mohammed Al Tyhmäh", "120575-4343", 
			"Ibrahim Fathcie Gugemiy", "190964-9735", 
			"Jane Line Hansson", "301212-1642", 
			"Laila Niggie", "092987-1264", 
			"(T) Erik Lademann Hollstein", "2007333995", 
			"(T) Lisbeth Araracus",    "1602389995" 
	}; 
 
	private DataUploader dataUploader; 
	private Serializer serializer; 
 
	private JTextField fvcField, fev1Field; 
	private double fvc = 0.0, fev1 = 0.0; 
	private String ptName; 
	private String ptCPR; 
	private int ptToggle; 
 
	private JLabel nameLabel, cprLabel; 
	 
	private Container cpane = null; 
	 
	// this is the container that contains either text or graph 
	private JPanel outputContainer = new JPanel(); 
	 
	ObservationGraph graph = new StandardObservationGraphWithFilter(new StandardObservationGraph()); 
	 
	// For local testing 
	private String localServerAddress = "http://localhost:8080/observation"; 
	private String membraneServerAddress = "http://localhost:8079/observation"; 
	private String csServerAddress = "http://www.net4care.org/observation"; 
	private String myServerAddress = "http://localhost:8082/observation"; 
 
	private String serverAddressToUse; 
 
	private Border raisedetched; 
 
	public String formatToUse; 
 
	public ManualServerTest() throws IOException { 
		super("Manual Server testing of Net4Care Server"); 
 
		ptToggle = 0; 
 
		createGUI(); 
 
		serverAddressToUse = localServerAddress; 
		formatToUse = QueryKeys.ACCEPT_JSON_DATA; 
 
		serializer = new ServerJSONSerializer(); 
		//By using the ServerSerializer (in combination with the DefaultObservationSpecifics) 
		//we should be able to avoid the deserializing problems. 
		try { 
			dataUploader = new StandardDataUploader(serializer, 
					new HttpConnector(serverAddressToUse)); 
		} catch (MalformedURLException e) { 
			e.printStackTrace(); 
			System.exit(1); 
		} 
 
	} 
 
	private void createGUI() { 
		JFrame.setDefaultLookAndFeelDecorated(true); 
 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
 
		raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED); 
 
		if (cpane != null) { 
			cpane.removeAll(); 
		} 
 
		cpane = getContentPane(); 
 
		cpane.setLayout(new BoxLayout(cpane, BoxLayout.Y_AXIS)); 
 
		cpane.add(createServerSelectPanel()); 
		cpane.add(createPatientSelectPanel()); 
		cpane.add(createMakeMeasurementPanel()); 
		cpane.add(createQueryPanel()); 
 
		useTextOutput("No data yet"); 
 
		// Make the whole output area white. 
		outputContainer.setOpaque(true); 
		outputContainer.setBackground(Color.white); 
 
 
		JScrollPane scrollPane = new JScrollPane(outputContainer); 
 
		//Set the output panels height so that it fits with a graph of dim(600,375). 
		scrollPane.setPreferredSize(new Dimension( 
				(int) cpane.getMinimumSize().getWidth(), 
				400)); 
		cpane.add(scrollPane); 
 
		setPttData(ptToggle); 
 
		pack(); 
		setVisible(true); 
		cpane.repaint(); 
 
	} 
 
	private void useTextOutput(String text) { 
		JTextArea textArea = new JTextArea(24,70); 
		textArea.setText(text); 
		outputContainer.removeAll(); 
		outputContainer.add(textArea); 
		outputContainer.updateUI(); 
 
	} 
 
	private void useGraphOutput(List<StandardTeleObservation> obslist) { 
	 outputContainer.removeAll(); 
 
		graph.plotData(obslist); 
		outputContainer.add(graph.getGraphComponent()); 
		graph.setGraphSize(600, 375); 
		outputContainer.updateUI(); 
	} 
 
	private void setPttData(int i) { 
		ptName = ptDatabase[2 * i]; 
		ptCPR = ptDatabase[2 * i + 1]; 
		nameLabel.setText("Name: " + ptName); 
		cprLabel.setText("ID: " + ptCPR); 
	} 
 
	private JComponent createPatientSelectPanel() { 
		Box p = new Box(BoxLayout.Y_AXIS); 
 
		// Make the name and cpr group 
		JPanel nameGroup = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		nameGroup.add(nameLabel = new JLabel("(Patientname)")); 
		nameGroup.add(cprLabel = new JLabel("(Patient id)")); 
 
		JButton toggleButton = new JButton("Toogle Active Patient"); 
		toggleButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				ptToggle = (ptToggle + 1) % (ptDatabase.length / 2); 
				setPttData(ptToggle); 
			} 
		}); 
		nameGroup.add(toggleButton); 
 
		p.add(nameGroup); 
		return p; 
	} 
 
	private JComponent createMakeMeasurementPanel() { 
		JPanel pa = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		JButton measurebutton = new JButton( 
				"Simulate spirometry measurement..."); 
		pa.add(measurebutton); 
		measurebutton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				makeSpirometerMeasurementFromUSBAttachedInstrument(); 
				fev1Field.setText("" + fev1); 
				fvcField.setText("" + fvc); 
			} 
		}); 
 
		Box p = new Box(BoxLayout.X_AXIS); 
		p.add(new JLabel("FVC: ")); 
		p.add(fvcField = new JTextField("(measurement)")); 
		p.add(new JLabel("FEV1: ")); 
		p.add(fev1Field = new JTextField("(measurement)")); 
 
		pa.add(p); 
 
		JButton uploadButton = new JButton("Upload this to Net4Care server"); 
		uploadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
 
				useTextOutput("Uploading..."); 
 
				Spirometry sp = new Spirometry(fvc, fev1); 
				DeviceDescription devdesc = new DeviceDescription("Spirometry", 
						"MODEL1", "Manufac1", "1", "1", "1.0", "1.0"); 
				StandardTeleObservation sto = new StandardTeleObservation( 
						ptCPR, "MyOrgID", "myTreatmentId", Codes.LOINC_OID, 
						devdesc, sp); 
				FutureResult result; 
				try { 
					result = dataUploader.upload(sto); 
					result.awaitUninterruptibly(); 
					if (!result.isSuccess()) { 
						useTextOutput(result.result()); 
					} else { 
						useTextOutput("Successfully uploaded measurement"); 
					} 
 
				} catch (IOException e1) { 
					useTextOutput("Currently unable to connect to server"); 
				}  
			} 
		}); 
 
		pa.add(uploadButton); 
 
		pa.setBorder(raisedetched); 
		return pa; 
	} 
 
	private JPanel createServerSelectPanel() { 
		JRadioButton localServerButton = new JRadioButton("Local server at: " 
				+ localServerAddress); 
		localServerButton.setActionCommand(localServerAddress); 
		localServerButton.addActionListener(new ServerActionListener()); 
 
		JRadioButton membraneServerButton = new JRadioButton( 
				"Membrane redirected server at: " + membraneServerAddress 
				+ "  - (let membrane redirect to port 8080)"); 
		membraneServerButton.setActionCommand(membraneServerAddress); 
		membraneServerButton.addActionListener(new ServerActionListener()); 
 
		JRadioButton csServerButton = new JRadioButton("AU CS test server at: " 
				+ csServerAddress); 
		csServerButton.setActionCommand(csServerAddress); 
		csServerButton.addActionListener(new ServerActionListener()); 
 
		JRadioButton myServerButton = new JRadioButton("(run-server.bat)  at: " 
				+ myServerAddress); 
		myServerButton.setActionCommand(myServerAddress); 
		myServerButton.addActionListener(new ServerActionListener()); 
 
		if (serverAddressToUse == null || serverAddressToUse.equals(localServerAddress)) { 
			localServerButton.setSelected(true); 
		} else if (serverAddressToUse.equals(membraneServerAddress)) { 
			membraneServerButton.setSelected(true); 
		} else if (serverAddressToUse.equals(csServerAddress)) { 
			csServerButton.setSelected(true); 
		} else if (serverAddressToUse.equals(myServerAddress)) { 
			myServerButton.setSelected(true); 
		} 
 
		ButtonGroup group = new ButtonGroup(); 
		group.add(localServerButton); 
		group.add(membraneServerButton); 
		group.add(csServerButton); 
		group.add(myServerButton); 
 
		JPanel serverPanel = new JPanel(new GridLayout(0, 1)); 
		serverPanel.add(localServerButton); 
		serverPanel.add(membraneServerButton); 
		serverPanel.add(csServerButton); 
		serverPanel.add(myServerButton); 
 
		serverPanel.setBorder(raisedetched); 
		return serverPanel; 
	} 
 
	Date now = null; 
	Date before = null; 
 
	private void updateDefaultQueryDates() { 
		Calendar calendar = Calendar.getInstance(); 
 
		calendar.add(Calendar.MINUTE, 5); 
		now = calendar.getTime(); 
		calendar.add(Calendar.DATE, -1); 
		before = calendar.getTime(); 
	} 
 
	private JComponent createQueryPanel() { 
		Box mainPanel = new Box(BoxLayout.Y_AXIS); 
 
		updateDefaultQueryDates(); 
 
		SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM/dd"); 
		SimpleDateFormat hourMinutesFormat = new SimpleDateFormat("HH:mm"); 
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy"); 
 
		final SimpleDateFormat fullyQualifiedDate = new SimpleDateFormat( 
				"MM/ddHH:mmyyyy"); 
 
 
		JPanel retrivePanel = new JPanel(); 
 
		JLabel startLabel = new JLabel("Interval Start (MM/dd HH:mm yyyy)"); 
		retrivePanel.add(startLabel); 
 
		JPanel startDatePanel = new JPanel( 
				new FlowLayout(FlowLayout.LEFT, 0, 0)); 
 
		final JTextField startMonthDayField = new JTextField( 
				monthDayFormat.format(before)); 
		startMonthDayField.setColumns(5); 
		startDatePanel.add(startMonthDayField); 
		startDatePanel.add(new Label(" ")); 
		final JTextField startHourMinutesField = new JTextField( 
				hourMinutesFormat.format(before)); 
		startHourMinutesField.setColumns(5); 
		startDatePanel.add(startHourMinutesField); 
		final JTextField startYearField = new JTextField( 
				yearFormat.format(before)); 
		startYearField.setColumns(5); 
		startDatePanel.add(startYearField); 
 
		retrivePanel.add(startDatePanel); 
 
		JLabel endLabel = new JLabel("End "); 
		retrivePanel.add(endLabel); 
 
		JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
 
		final JTextField endMonthDayField = new JTextField( 
				monthDayFormat.format(now)); 
		endMonthDayField.setColumns(4); 
		endDatePanel.add(endMonthDayField); 
		endDatePanel.add(new JLabel(" ")); 
		final JTextField endHourMinutesField = new JTextField( 
				hourMinutesFormat.format(now)); 
		endHourMinutesField.setColumns(4); 
		endDatePanel.add(endHourMinutesField); 
		final JTextField endYearField = new JTextField( 
				yearFormat.format(now)); 
		endYearField.setColumns(5); 
		endDatePanel.add(endYearField); 
 
		retrivePanel.add(endDatePanel); 
 
		JButton downloadButton = new JButton("Retrieve data from server"); 
		downloadButton.addActionListener(new ActionListener() { 
			@Override 
			public void actionPerformed(ActionEvent arg0) { 
				Date startDate = null; 
				Date endDate = null; 
 
				String startMonthDay = startMonthDayField.getText(); 
				String startHourMinutes = startHourMinutesField.getText(); 
				String startYear = startYearField.getText(); 
 
				try { 
					startDate = fullyQualifiedDate.parse(startMonthDay 
							+ startHourMinutes + startYear); 
				} catch (ParseException e) { 
					useTextOutput("Invalid start time!\n\nIt must be of format: 'MM/dd HH:mm yyyy' like 07/12 12:55 2012"); 
				} 
				String endMonthDay = endMonthDayField.getText(); 
				String endHourMinutes = endHourMinutesField.getText(); 
				String endYear = endYearField.getText(); 
				try { 
					endDate = fullyQualifiedDate.parse(endMonthDay 
							+ endHourMinutes + endYear); 
				} catch (ParseException e) { 
					useTextOutput("Invalid end time!\n\nIt must be of format: 'MM/dd HH:mm yyyy' like 07/12 12:55 2012"); 
				} 
				System.out.println("Querying:\nstart: " + startDate + "\nend: " 
						+ endDate); 
				if (startDate != null && endDate != null) { 
					queryServerAndPutIntoOutputField(startDate, endDate); 
				} 
			} 
		}); 
		retrivePanel.add(downloadButton); 
 
		mainPanel.add(retrivePanel); 
 
		JPanel tweakPanel = new JPanel(); 
 
		JLabel formatLabel = new JLabel("Choose format:"); 
		tweakPanel.add(formatLabel); 
 
		JRadioButton json, graph, phmr; 
 
		json = new JRadioButton("Observation (Processed JSON)"); 
		json.setActionCommand(QueryKeys.ACCEPT_JSON_DATA); 
		json.addActionListener(new FormatActionListener()); 
		graph = new JRadioButton("Google graph"); 
		graph.setActionCommand(QueryKeys.ACCEPT_GRAPH_DATA); 
		graph.addActionListener(new FormatActionListener()); 
		phmr = new JRadioButton("PHMR"); 
		phmr.setActionCommand(QueryKeys.ACCEPT_XML_DATA); 
		phmr.addActionListener(new FormatActionListener()); 
		tweakPanel.add(json); 
		tweakPanel.add(phmr); 
		tweakPanel.add(graph); 
 
		if (formatToUse == null || formatToUse.equals(QueryKeys.ACCEPT_JSON_DATA)) { 
			json.setSelected(true); 
		} else if (formatToUse.equals(QueryKeys.ACCEPT_XML_DATA)) { 
			phmr.setSelected(true); 
		} else if (formatToUse.equals(QueryKeys.ACCEPT_GRAPH_DATA)) { 
			graph.setSelected(true); 
		} 
 
		ButtonGroup group = new ButtonGroup(); 
		group.add(json); 
		group.add(graph); 
		group.add(phmr); 
 
		mainPanel.add(tweakPanel); 
 
		mainPanel.setBorder(raisedetched); 
		return mainPanel; 
	} 
 
	private void makeSpirometerMeasurementFromUSBAttachedInstrument() { 
		double fev1delta = (int) (10 * Math.random()) / 10.0; 
		double fvcdelta = (int) (10 * Math.random()) / 10.0; 
		fvc = 3.4 + fvcdelta; 
		fev1 = 3.0 + fev1delta; 
 
		if (String.valueOf(fvc).length() > 3 || String.valueOf(fev1).length() > 3) { 
			makeSpirometerMeasurementFromUSBAttachedInstrument(); 
		} 
	} 
 
	private void queryServerAndPutIntoOutputField(Date queryStart, Date queryEnd) { 
		Query query; 
		QueryResult res = null; 
		List<StandardTeleObservation> obslist; 
		List<String> phmrList; 
 
		Query.QueryResponseType format = Query.QueryResponseType.STANDARD_TELE_OBSERVATION; 
		if (formatToUse.equals(QueryKeys.ACCEPT_JSON_DATA)) { 
			format = Query.QueryResponseType.STANDARD_TELE_OBSERVATION; 
		} else if (formatToUse.equals(QueryKeys.ACCEPT_XML_DATA)) { 
			format = Query.QueryResponseType.PERSONAL_HEALTH_MONITORING_RECORD; 
		} 
 
		query = new QueryPersonTimeInterval(ptCPR, queryStart.getTime(), 
				queryEnd.getTime()); 
		query.setFormatOfReturnedObservations(format); 
 
		try { 
			res = dataUploader.query(query); 
			res.awaitUninterruptibly(); 
		} catch (IOException e) { 
			useTextOutput("Currently unable to connect to server"); 
			e.printStackTrace(); 
		} catch (Net4CareException e) { 
			if (e.getErrorCode() == Constants.ERROR_UNKNOWN_CPR) { 
				useTextOutput("CPR number is not known on the server"); 
			} else { 
				useTextOutput("Encountered a Net4Care exception \n\n Please inspect the console for details"); 
			} 
			e.printStackTrace(); 
		} 
 
		obslist = res.getObservationList(); 
 
		if (formatToUse.equals("application/graph")) { 
			useGraphOutput(obslist); 
		} else { 
			phmrList = res.getDocumentList(); 
			// System.out.println("The obs list length = "+obslist.size()); 
 
			if (format == Query.QueryResponseType.STANDARD_TELE_OBSERVATION) { 
 
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
 
				int i = 0; 
				String result = ""; 
				for (StandardTeleObservation sto : obslist) { 
					DefaultObservationSpecifics spiro = (DefaultObservationSpecifics) sto 
							.getObservationSpecifics(); 
					result += spiro.toString()+"\n"; 
					i++; 
					if (i > 38) 
						break; 
				} 
				useTextOutput(result); 
			} else { 
				String result = ""; 
				for (String phmr : phmrList) { 
					result += phmr + "\n"; 
				} 
				useTextOutput(result); 
			} 
		} 
	} 
 
	class ServerActionListener implements ActionListener { 
		public void actionPerformed(ActionEvent e) { 
			serverAddressToUse = e.getActionCommand(); 
			try { 
				dataUploader = new StandardDataUploader(serializer, 
						new HttpConnector(serverAddressToUse)); 
				useTextOutput("Server is now set to: " + serverAddressToUse); 
			} catch (MalformedURLException exc) { 
				exc.printStackTrace(); 
				System.exit(1); 
			} 
		} 
	} 
 
	class FormatActionListener implements ActionListener { 
		public void actionPerformed(ActionEvent e) { 
			formatToUse = e.getActionCommand(); 
			if (!formatToUse.equals("application/graph")) { 
				useTextOutput("Format is set to: " + formatToUse); 
			} else { 
				useGraphOutput(null); 
			} 
 
		} 
	} 
 
} 
