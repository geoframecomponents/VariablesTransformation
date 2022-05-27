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
import java.util.LinkedHashSet;
import java.util.List;

import org.joda.time.DateTime;

import it.geoframe.blogspot.variabletransformation.cdfinterpolation.HyperbolicModel;
import it.geoframe.blogspot.variabletransformation.cdfinterpolation.PowerModel;
import it.geoframe.blogspot.variabletransformation.ecdf.PlottingPosition;
import it.geoframe.blogspot.variabletransformation.rank.Ranking;
import it.geoframe.blogspot.variabletransformation.rank.SimpleFactoryRanking;
import it.geoframe.blogspot.variabletransformation.utils.Utils;

import org.apache.commons.math3.distribution.NormalDistribution;

import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Out;

public class InverseNormalScore {

	@Description("Data set of observed variable")
	@In
	public HashMap<DateTime, double[]> inData;
	
	@Description("Data set of normal score variable to transformed with the inverse normal score")
	@In
	public HashMap<DateTime, double[]> inDataNS;
	
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
	
	@Description("Value of the exponent of the Power model to interpolate within ties. Goovaerts, P. (1997), p 280."
			+ "1 corresponds to the linear model (uniform distribution)"
			+ "<1 for distribution with positive skewed"
			+ ">1 for distribution with negative skewed")
	@In
	public double exponentPowerModelMiddleValues = 1.0; 
	
	@Description("Value of the exponent of the Power model to interpolate the values in the lower tail. Goovaerts, P. (1997), p 280."
			+ ">1 for distribution with negative skewed")
	@In
	public double exponentPowerModelLowerTail = 5.0; 
	
	@Description("Minimum z-value fixed by the user. Goovaerts, P. (1997), p 280.")
	@In
	public double minimumFixedValue = 0.0; 
	
	@Description("Value of the exponent of the Hyperbolic model to interpolate the values in the upper tail. Goovaerts, P. (1997), p 280."
			+ "Practice has shown that hyperbolic upper tail distribution with 1.5 yields acceptable results.")
	@In
	public double exponentPowerModelUpperTail = 1.5; 
	
	@Description("Data set of physical varible.")
	@Out
	public HashMap<DateTime, double[]> outData;
	
	
	private ArrayList<Double> values;
	private List<Double> valuesToSetToNoValue;
	private LinkedHashMap<Double, Double> valuesRank;
	private LinkedHashMap<Double, Double> ecdf;
	
	private ArrayList<Double> valuesNS;
	private ArrayList<Double> valuesNSUnique;
	private LinkedHashMap<Double, Double> cdf;
	
	private List<Double> lowerTailValuesNS;
	private List<Double> lowerTailCdf;
	private List<Double> middleValuesNS;
	private List<Double> middleCdf;
	private List<Double> upperTailValuesNS;
	private List<Double> upperTailCdf;
	
	private LinkedHashMap<Double, Double> transformedValues;


	private Ranking ranking;
	private SimpleFactoryRanking simpleFactoryRanking;
	private PlottingPosition weibullECDF;
	private Utils utils;
	private NormalDistribution normalDistribution;
	private PowerModel powerModel;
	private HyperbolicModel hyperbolicModel;

	private double[] tmpValuesNS;
	private double[] tmpCdf;
	private double[] tmpValues;
	private double[] tmpEcdf;
	
	private int count;
	private double tmp;
	private double ecdfMin;
	private	double ecdfMax; 

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

		/*
		 * Move physical data values in ArrayList
		 */
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
		 * Move NS values in ArrayList
		 */
		valuesNS = utils.flattenHashMap(inDataNS, null); 
		Collections.sort(valuesNS);
		valuesNSUnique = new ArrayList<>(new LinkedHashSet<>(valuesNS));
		
		cdf = new LinkedHashMap<Double, Double>(valuesNS.size());
		normalDistribution = new NormalDistribution();
		for(Double value : valuesNSUnique) {
			
			cdf.put(value, normalDistribution.cumulativeProbability(value));
			
		}
	
		/*
		 * Divide NS values in lower tail, middle values, and upper tail.
		 */
		lowerTailValuesNS = new ArrayList<Double>();
		lowerTailCdf = new ArrayList<Double>();

		middleValuesNS = new ArrayList<Double>();
		middleCdf = new ArrayList<Double>();
		
		upperTailValuesNS = new ArrayList<Double>();
		upperTailCdf = new ArrayList<Double>();
		
		ecdfMin = ecdf.get(ecdf.keySet().toArray()[0]);
		ecdfMax = ecdf.get(ecdf.keySet().toArray()[ecdf.size()-1]);
		
		for(Double value : cdf.keySet()) {
			
			if(cdf.get(value)<ecdfMin) {
				
				lowerTailValuesNS.add(value);
				lowerTailCdf.add(cdf.get(value));
				
			} else if(cdf.get(value)>ecdfMax) {
				
				upperTailValuesNS.add(value);
				upperTailCdf.add(cdf.get(value));
				
			} else {
				
				middleValuesNS.add(value);
				middleCdf.add(cdf.get(value));
				
			}
			
		}
		
		/*
		 * For each value of the NS variable we associate the corrisponding value of the physical variable
		 */
		transformedValues = new LinkedHashMap<Double, Double>();

		
		/*
		 * Inverse normal score middle values
		 */
		tmpValuesNS = new double[middleValuesNS.size()];
		tmpCdf = new double[middleCdf.size()];
		tmpValues = new double[ecdf.size()];
		tmpEcdf = new double[ecdf.size()];
		
		for(int i=0; i<middleValuesNS.size(); i++) {
			tmpValuesNS[i] = middleValuesNS.get(i);
			tmpCdf[i] = middleCdf.get(i);
		}
		
		int j = 0;
		for(Double value : ecdf.keySet()) {
			tmpValues[j] = value;
			tmpEcdf[j] = ecdf.get(value);
			j++;
		}
		
//		LinearInterpolator li = new LinearInterpolator();
//		PolynomialSplineFunction psf = li.interpolate(tmpEcdf, tmpValues);
//		for(int i=0; i<tmpValuesNS.length; i++) {
//			tmp = psf.value(tmpCdf[i]);
//			System.out.println(tmpValuesNS[i] +" "+ tmp);
//			transformedValues.put(tmpValuesNS[i], tmp);
//		}
		
		powerModel = new PowerModel();
		
		for(int i=0; i<tmpCdf.length; i++) {
//			System.out.println(tmpCdf[i]);
			for(int k=1; k<tmpValues.length; k++) {
			
				if(tmpCdf[i]<=tmpEcdf[k] && tmpCdf[i]>=tmpEcdf[k-1]) {
					double z_p = tmpValues[k];
					double z_m = tmpValues[k-1];
//					System.out.println("\tz_m: "+z_m+"\tz_p: "+z_p);
					double F_m = tmpEcdf[k-1];
					double F_p = tmpEcdf[k];
//					System.out.println("\tF_m: "+F_m+"\tF_p: "+F_p);
					tmp = powerModel.interpolate(tmpCdf[i], F_m, F_p, z_m, z_p, exponentPowerModelMiddleValues);
//					System.out.println(tmpValuesNS[i] +" "+ tmp);
					transformedValues.put(tmpValuesNS[i], tmp);
					break;

				}
			}
		}
		
		/*
		 * lower tail
		 */
		tmpValuesNS = new double[lowerTailValuesNS.size()];
		tmpCdf = new double[lowerTailCdf.size()];
		for(int i=0; i<lowerTailValuesNS.size(); i++) {
			tmpValuesNS[i] = lowerTailValuesNS.get(i);
			tmpCdf[i] = lowerTailCdf.get(i);
		}
		
		for(int i=0; i<tmpCdf.length; i++) {

			tmp = powerModel.interpolate(tmpCdf[i], 0.0, tmpEcdf[0], minimumFixedValue, tmpValues[0], exponentPowerModelLowerTail);
//			System.out.println(tmpValuesNS[i] +" "+ tmp);
			transformedValues.put(tmpValuesNS[i], tmp);

		}


		/*
		 * upper tail
		 */
		tmpValuesNS = new double[upperTailValuesNS.size()];
		tmpCdf = new double[upperTailCdf.size()];
		for(int i=0; i<upperTailValuesNS.size(); i++) {
			tmpValuesNS[i] = upperTailValuesNS.get(i);
			tmpCdf[i] = upperTailCdf.get(i);
		}
		
		hyperbolicModel = new HyperbolicModel();
		hyperbolicModel.computeLambda(tmpValues[tmpValues.length-1], tmpEcdf[tmpEcdf.length-1], exponentPowerModelUpperTail);
		
		for(int i=0; i<tmpCdf.length; i++) {

			tmp = hyperbolicModel.interpolate(tmpCdf[i], 1.5);
//			System.out.println(tmpValuesNS[i] +" "+ tmp);
			transformedValues.put(tmpValuesNS[i], tmp);

		}
		
		/*
		 * Create the output file
		 */
		outData = utils.substitueValues(inDataNS, transformedValues);
//		for(double value : transformedValues.keySet()) {
//		System.out.println(value +" "+ transformedValues.get(value));
//	}
		
	}
	
}
