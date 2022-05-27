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

package it.geoframe.blogspot.variabletransformation.rank;

import java.util.LinkedHashMap;
import java.util.List;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Niccolo' Tubini", contact = "tubini.niccolo@gmail.com")
@License("General Public License Version 3 (GPLv3)")
public abstract class Ranking {
	
	protected LinkedHashMap<Double, Integer> valuesOccurrences;
	
	
	public abstract LinkedHashMap<Double,Double> rank(List<Double> sortedValues);

	
	protected void countOccurrences(List<Double> sortedValues){
		
		valuesOccurrences = new LinkedHashMap<Double, Integer>();
		int length = sortedValues.size();
		int occurrences = 1;
		
		for(int i=0; i<length; i++) {

			if(i<length-1) {
				
				if(sortedValues.get(i).compareTo(sortedValues.get(i+1))==0) {
					occurrences++;
				} 
				
			} else {
					
				valuesOccurrences.put(sortedValues.get(i), occurrences);
				
			}
			
			if(i<length-1 && sortedValues.get(i).compareTo(sortedValues.get(i+1))!=0) {

				valuesOccurrences.put(sortedValues.get(i), occurrences);
				occurrences = 1;
				
			}
			
		}
				
	}
	
}
