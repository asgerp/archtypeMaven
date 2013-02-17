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

import java.awt.Dimension;
import java.util.List;
import java.util.Map.Entry;
import org.jfree.chart.plot.XYPlot;
import java.util.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.net4care.graph.ObservationGraph;
import org.net4care.observation.*;

/** 
 * Standard class for using a JFreeChart to show StandardTeleObservations graphically. <br />
 * Role: Adaptor/Container for list of Net4Care StandardTeleObservations and a JFreeChart. <br />  
 * Author: Morten Larsson, AU
 */
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
	           ) 
public class StandardObservationGraph implements ObservationGraph {

	private final String graphTitle;
	private final String xAxisTitle;
	private final String yAxisTitle;
	private List<String> listOfCodes;

	private ChartPanel cPanel;
	private XYPlot plotter;

	/**
	 * Standard constructor for the this class, using default naming of the graph and 
	 * plotting all possible ClinicalQuantities in the input list of StandardTeleObservations.
	 */
	public StandardObservationGraph() {
		this("N4C Observation Graph", "Time", "Value(s)");
	}

	/**
	 * @param graphTitle  The title of the graph.
	 * @param xAxisTitle  The title of the horizontal axis.
	 * @param yAxisTitle  The title of the vertical axis.
	 */
	public StandardObservationGraph(String graphTitle, String xAxisTitle, String yAxisTitle) {
		this.graphTitle = graphTitle;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		cPanel = new ChartPanel(createChart());
	}

	/**
	 * @param graphTitle  The title of the graph.
	 * @param xAxisTitle  The title of the horizontal axis.
	 * @param yAxisTitle  The title of the vertical axis.
	 * @param listOfCodes  The list of codes to be shown on the graph. I.e. used for filtering. 
	 */
	public StandardObservationGraph(String graphTitle, String xAxisTitle, String yAxisTitle, List<String> listOfCodes) {
		this(graphTitle, xAxisTitle, yAxisTitle);
		this.listOfCodes = listOfCodes;
	}

	/**
	 * @param listOfCodes  The list of codes to be shown on the graph. I.e. used for filtering. 
	 */
	public StandardObservationGraph(List<String> listOfCodes) {
		this();
		this.listOfCodes = listOfCodes;
	}

	public void plotData(List<StandardTeleObservation> observationList) {

		//Avoid crashing if null.
		if(observationList == null) {
			plotter.setDataset(0, null);
			return;
		}
		//Make sure that all timestamps are different!
		avoidCollidingObservationTimes(observationList);

		//Creates the observations series.
		Map<String,TimeSeries> measurementSeries = new HashMap<String,TimeSeries>();
		for(StandardTeleObservation sto : observationList) {      
			Date stoDate = new Date(sto.getTime());
			Iterator<ClinicalQuantity> iter = sto.getObservationSpecifics().iterator();
			ClinicalQuantity quantity;
			while(iter.hasNext()) {
				quantity = iter.next();        
				String quantityCode = quantity.getCode();
				if(listOfCodes != null && !listOfCodes.contains(quantityCode))
					continue;
				if(!measurementSeries.containsKey(quantityCode)) {
					measurementSeries.put(quantityCode, new TimeSeries(quantity.getDisplayName() + " ("+quantity.getUnit()+")"));
				}
				TimeSeries currentTimeSeries = measurementSeries.get(quantityCode);
				currentTimeSeries.add(new Millisecond(stoDate),quantity.getValue());
			}
		}

		//Insert the series.
		TimeSeriesCollection resultingSeries = new TimeSeriesCollection();
		Iterator<Entry<String, TimeSeries>> iter = measurementSeries.entrySet().iterator();
		while(iter.hasNext()) {
			resultingSeries.addSeries(iter.next().getValue());
		}
		addDataset(resultingSeries);
	}

	public void setListofCodes(List<String> listOfCodes) {
		this.listOfCodes = listOfCodes;
	}

	public JComponent getGraphComponent() {
		return cPanel;
	}

	public void setGraphSize(int width, int height) {
		cPanel.setPreferredSize(new Dimension(width, height));
	}

	private void addDataset(XYDataset dataset) {
		plotter.setDataset(0, dataset);
	}

	private JFreeChart createChart() {
		JFreeChart result = ChartFactory.createTimeSeriesChart(
				graphTitle,
				xAxisTitle,
				yAxisTitle,
				null,
				true,
				true,
				false); 
		plotter = (XYPlot) result.getPlot();
		XYItemRenderer r = plotter.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}     
		return result;
	}

	private void avoidCollidingObservationTimes(List<StandardTeleObservation> observationList) {
		if(observationList == null) 
			return;
		ArrayList<Long> stampsRegistered = new ArrayList<Long>();
		int offset = 1;
		long temp;
		for(StandardTeleObservation s : observationList) { 
			temp = s.getTime();
			if( stampsRegistered.contains(temp) ) { 
				s.setTime(temp+offset);
				offset++;
			}
			stampsRegistered.add(temp);

		}
	}
}