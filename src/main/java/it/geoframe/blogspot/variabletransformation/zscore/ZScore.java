/*
 * GNU GPL v3 License
 *
 * Copyright 2021  Niccolo` Tubini
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geoframe.blogspot.variabletransformation.zscore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;

import it.geoframe.blogspot.variabletransformation.utils.Utils;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.License;
import oms3.annotations.Out;

@Description("Transform a variable with z-score.")
@Documentation("")
@Author(name = "Niccolo' Tubini, Giuseppe Formetta, and Riccardo Rigon", contact = "tubini.niccolo@gmail.com")
@Keywords("Hydrology, z-score")
//@Label()
//@Name()
//@Status()
@License("General Public License Version 3 (GPLv3)")
public class ZScore {

	@Description("Data set of observed variable to transformed with z-score")
	@In
	public HashMap<DateTime, double[]> inData;
	
	@Description("Values to set to NaN")
	@In
	public String[] inValuesToSetToNoValue = null;
	
	@Description("Data set of z-score variable")
	@Out
	public HashMap<DateTime, double[]> outData;
	
	private ArrayList<Double> values;
	private List<Double> valuesToSetToNoValue;

	private double mean;
	private double standardDeviation;
	private double[] tmp1;
	private double[] tmp2;
	
	private Utils utils;
	private DescriptiveStatistics ds;
	
	@Execute
	public void run() {
		
		utils = new Utils();
		ds = new DescriptiveStatistics();
		
		/*
		 * Move values in ArrayList
		 */
		valuesToSetToNoValue = new ArrayList<Double>();
		valuesToSetToNoValue.add((double) -9999);
		if(inValuesToSetToNoValue!=null) {
			for(String s : inValuesToSetToNoValue) {
				valuesToSetToNoValue.add(Double.parseDouble(s));
			}
		}
		
		values = utils.flattenHashMap(inData, valuesToSetToNoValue);
		
		for(Double value : values) {
			
			ds.addValue(value);

		}
		
		mean = ds.getMean();
		standardDeviation = Math.sqrt(ds.getVariance());
		
		outData = new LinkedHashMap<DateTime, double[]>(inData.size());
		
		for(DateTime dateTime : inData.keySet()) {
			
			tmp1 = inData.get(dateTime);
			tmp2 = new double[tmp1.length];

			for(int i=0; i<tmp1.length; i++) {
				
				if(!Double.isNaN(tmp1[i])) {
					tmp2[i] = (tmp1[i] - mean)/standardDeviation;

				} else {
					tmp2[i] = Double.NaN;
				}
				
			}
			
			outData.put(dateTime,tmp2);
		}
	
	}
	
}
