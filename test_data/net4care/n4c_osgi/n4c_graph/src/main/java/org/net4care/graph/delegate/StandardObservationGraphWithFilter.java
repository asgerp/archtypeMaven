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
 
package org.net4care.graph.delegate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.*;

import javax.swing.*;

import org.net4care.graph.ObservationGraph;
import org.net4care.observation.ClinicalQuantity;
import org.net4care.observation.StandardTeleObservation;

/** 
 * Decorator class for using ObservationGraph objects. <br />
 * Role: Adding panel for filtering of which clinical quantities to show <br />  
 * Author: Morten Larsson, AU
 */
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
	           ) 
public class StandardObservationGraphWithFilter implements ObservationGraph {

	private static final int DEFAULT_PANEL_WIDTH = 150;
	
	//The last list of STO's received
	private List<StandardTeleObservation> obsList;
	
	//Diff. lists for keeping track of the graphpanel
	private String[] listOfCodesSelected;
	private String[] listOfCodes;
	private DefaultListModel displayName;

	
	//The main container, containing the panel and the graph
	private JPanel container;
	
	//The graphpanel
	private JList graphPanel;
	
	//The delegate graph
	private ObservationGraph graph;
	
	private int containerWidth;
	private int containerHeight;
	
	public StandardObservationGraphWithFilter(ObservationGraph graph) {
		this.graph = graph;
		initiateLists();
		initiateContainer();
	}
	
	@Override
	public void plotData(List<StandardTeleObservation> observationList) {
		obsList = observationList;
		updateListOfCodes();
		
		//Set the first clinical quantity in the list to the default selected.
		if(displayName.size()  > 0)
			graphPanel.setSelectedIndex(0);
		
		graph.plotData(observationList);
	}

	@Override
	public void setListofCodes(List<String> listOfCodes) {
		listOfCodes.toArray(listOfCodesSelected);	
		graph.setListofCodes(listOfCodes);
	}

	@Override
	public JComponent getGraphComponent() {
		return container;
	}

	@Override
	public void setGraphSize(int width, int height) {
		containerWidth = width;
		containerHeight = height;
		container.setPreferredSize(new Dimension(containerWidth, containerHeight));
		graph.setGraphSize((int)(width*0.75-1), height);
		graphPanel.setPreferredSize(new Dimension((int)(width*0.25-1), height));

	}

	private JList createGraphPanel() {	
		updateListOfCodes();
		JList list = new JList(displayName);
		list.setBackground(new Color(0xc0c0c0));
		list.setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, containerHeight));
		list.setBorder(BorderFactory.createLineBorder(
				Color.black, 1));
		list.setSelectionModel(new DefaultListSelectionModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setSelectionInterval(int index0, int index1) {
				if(super.isSelectedIndex(index0)) {
					super.removeSelectionInterval(index0, index1);
					listOfCodesSelected[index0] = null;
					updatePlot();
				}
				else {
					super.addSelectionInterval(index0, index1);
					listOfCodesSelected[index0] = listOfCodes[index0];
					updatePlot();
				}
			}
		});
		return list;	
	}
	
	private void updatePlot() {
		graph.setListofCodes(Arrays.asList(listOfCodesSelected));
		graph.plotData(obsList);
	}
	
	private void initiateContainer() {
		container = new JPanel(new GridBagLayout());
		graphPanel = createGraphPanel();
		
		// Setting placement of the panel in the container
		GridBagConstraints graphPanelConstrainer = new GridBagConstraints();
		graphPanelConstrainer.fill = GridBagConstraints.BOTH;
		graphPanelConstrainer.anchor = GridBagConstraints.FIRST_LINE_START;
		graphPanelConstrainer.gridx = 0;
		graphPanelConstrainer.gridy = 0;
		
		//Setting placement of the graph in the container
		GridBagConstraints graphConstrainer = new GridBagConstraints();
		graphConstrainer.fill = GridBagConstraints.BOTH;
		graphConstrainer.anchor = GridBagConstraints.FIRST_LINE_START;
		graphConstrainer.gridx = 1;
		graphConstrainer.gridy = 0;
		
		container.add(graphPanel, graphPanelConstrainer);
		container.add(graph.getGraphComponent(), graphConstrainer);
	}

	private void clearLists() {
		listOfCodesSelected = new String[512];
		listOfCodes = new String[512];
		displayName.clear();
	}
	
	private void initiateLists() {
		listOfCodesSelected = new String[512];
		listOfCodes = new String[512];
		displayName = new DefaultListModel();
	}

	private void updateListOfCodes() {
		clearLists();
		int counter = 0;
		if(obsList == null)
			return;
		for(StandardTeleObservation s: obsList) {
			Iterator<ClinicalQuantity> clis = s.getObservationSpecifics().iterator();
			while(clis.hasNext())  {
				ClinicalQuantity cli = clis.next();
				String code = cli.getCode();
				String disName = cli.getDisplayName();

				if(!Arrays.asList(listOfCodes).contains((code))) {
					listOfCodes[counter] = code;
					displayName.addElement(disName);
					counter++;
				}
			}
		}
	}
}
