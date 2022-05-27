/*
  * GNU GPL v3 License
 *
 * Copyright 2021 Niccolo` Tubini
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

package it.geoframe.blogspot.variabletransformation.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.joda.time.DateTime;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Niccolo' Tubini", contact = "tubini.niccolo@gmail.com")
@License("General Public License Version 3 (GPLv3)")
public class Utils {

	
	
	public ArrayList<Double> flattenHashMap(HashMap<DateTime, double[]> inData, List<Double> valuesToSetToNoValue){
		
		ArrayList<Double> values = new ArrayList<Double>();
		
		int count = 0;
		double[] tmp;
		
		for(DateTime dateTime : inData.keySet()) {

			tmp = inData.get(dateTime);
			
			for(int i=0; i<tmp.length; i++) {
				
				if(valuesToSetToNoValue!=null) {
					
					if(valuesToSetToNoValue.contains(tmp[i])) {
						tmp[i] = Double.NaN;
					}
					
				}

				if(!Double.isNaN(tmp[i])) {
					values.add(tmp[i]);
					count++;
				}

			}
			
		}
		
		return values;
		
	}
	
	
		
	public HashMap<DateTime, double[]> substitueValues(HashMap<DateTime, double[]> inData, LinkedHashMap<Double, Double> valuesDictionary){
		
		HashMap<DateTime, double[]> outData = new LinkedHashMap<DateTime, double[]>(inData.size());
		double[] tmp1;
		double[] tmp2;
		
		for(DateTime dateTime : inData.keySet()) {
			
			tmp1 = inData.get(dateTime);
			tmp2 = new double[tmp1.length];
			
			for(int i=0; i<tmp1.length; i++) {
				
//				if(!Double.isNaN(tmp1[i])) {
				if(tmp1[i]!=-9999.0 && !Double.isNaN(tmp1[i])) {
					
					tmp2[i] = valuesDictionary.get(tmp1[i]);

				} else {
					
					tmp2[i] = tmp1[i];
					
				}

			}
			
			outData.put(dateTime, tmp2);
		}
		
		return outData;
	}
	
	
}
