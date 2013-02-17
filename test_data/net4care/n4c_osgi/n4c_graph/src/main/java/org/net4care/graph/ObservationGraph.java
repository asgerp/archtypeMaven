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
 
package org.net4care.graph;

import java.util.List;

import javax.swing.JComponent;

import org.net4care.observation.StandardTeleObservation;

/** 
 * Interface for defining a types that  StandardTeleObservations graphically. <br />
 * Role: Adaptor/Container for list of Net4Care StandardTeleObservations and a JFreeChart. <br />  
 * Author: Morten Larsson, AU
 */

public interface ObservationGraph {

	/**
	 * @param observationList  The list of StandardTeleObservations to plot on the graph. 
	 * Depending on whether a listOfCodes was given in the constructor, this method plots all
	 * available ClinicalQuantities in the STO list or only the ones specified en the listOfCodes list. 
	 */
	public void plotData(List<StandardTeleObservation> observationList);
	
	/**
	 * Used to filter out which clinical quantities you want on the graph.
	 * If it is not set, then all quantities in the list of STO's will be shown.
	 * @param listOfCodes List of clinical codes that you want to be visible on the graph.
	 */
	public void setListofCodes(List<String> listOfCodes);

	/**
	 * Method for getting the component containing the graph.a
	 * @return The JComponent containing the graph.
	 */
	public JComponent getGraphComponent();

	/**
	 * Set the size of the graph. 
	 * @param width of the graph.
	 * @param height height of the graph. 
	 */
	public void setGraphSize(int width, int height);
}