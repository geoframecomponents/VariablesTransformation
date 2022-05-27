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

package it.geoframe.blogspot.variabletransformation.normalscore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.joda.time.DateTime;

import it.geoframe.blogspot.variabletransformation.ecdf.PlottingPosition;
import it.geoframe.blogspot.variabletransformation.rank.Ranking;
import it.geoframe.blogspot.variabletransformation.rank.SimpleFactoryRanking;
import it.geoframe.blogspot.variabletransformation.utils.Utils;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.License;
import oms3.annotations.Out;

@Description("Transform a variable with a Normal score technique.")
@Documentation("Goovaerts, P. (1997). Geostatistics for natural resources evaluation. Oxford University Press. p 266")
@Author(name = "Niccolo' Tubini", contact = "tubini.niccolo@gmail.com")
@Keywords("Hydrology, Normal score")
//@Label()
//@Name()
//@Status()
@License("General Public License Version 3 (GPLv3)")
public class NormalScore {
	
	@Description("Data set of observed variable to transformed with normal score")
	@In
	public HashMap<DateTime, double[]> inData;
	
	@Description("Values to set to NaN")
	@In
	public String[] inValuesToSetToNoValue = null;
	
	@Description("Method to compute the rank of measurements. Available average and maximum")
	@In
	public String rankingMethod = "average";
	
	@Description("Parameter of the plotting position function"
			+ "a=0 Weibull plotting position, default"
			+ "a=0.5 Hazen plotting position" )
	@In
	public double a = 0.0;
	
	@Description("Data set of normal score variable")
	@Out
	public HashMap<DateTime, double[]> outData;
	
	
	private ArrayList<Double> values;
	private List<Double> valuesToSetToNoValue;
	private LinkedHashMap<Double, Double> valuesRank;
	private LinkedHashMap<Double, Double> ecdf;
	private LinkedHashMap<Double, Double> transformedValues;
	
	private Ranking ranking;
	private SimpleFactoryRanking simpleFactoryRanking;
	private PlottingPosition weibullECDF;
	private Utils utils;
	private int count;
	
	@Execute
	public void run() {
		
		utils = new Utils();
		
		simpleFactoryRanking = new SimpleFactoryRanking();
		ranking = simpleFactoryRanking.create(rankingMethod);
		
		weibullECDF = new PlottingPosition(a);

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
		count = values.size();
		
		/*
		 * Sort values
		 */
		Collections.sort(values);
		
		/*
		 * Compute rank
		 */
		valuesRank = ranking.rank(values);
//		for(double value : valuesRank.keySet()) {
//			System.out.println(value +" "+ valuesRank.get(value));
//	    }
		
		/*
		 * Compute ECDF
		 */
		ecdf = weibullECDF.computeECDF(count, valuesRank);
//		for(double value : ecdf.keySet()) {
//			System.out.println(value +" "+ ecdf.get(value));
//		}
				
		/*
		 *  Compute transformed variable
		 */
		transformedValues = computeTransformedValues(ecdf);

		/*
		 * Create the output file
		 */
		outData = utils.substitueValues(inData, transformedValues);
		
	}
	
	
	private LinkedHashMap<Double,Double> computeTransformedValues(LinkedHashMap<Double, Double> ecdf){

		LinkedHashMap<Double, Double> transformedValues = new LinkedHashMap<Double, Double>(ecdf.size());
		NormalDistribution normalDistribution = new NormalDistribution();

		for(Double value : ecdf.keySet()) {
			
			transformedValues.put(value, normalDistribution.inverseCumulativeProbability(ecdf.get(value)));
			
		}
		
		return transformedValues;
	}
	
}
