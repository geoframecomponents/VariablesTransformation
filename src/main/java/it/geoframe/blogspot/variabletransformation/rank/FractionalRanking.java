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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import oms3.annotations.Author;
import oms3.annotations.License;


@Author(name = "Niccolo' Tubini", contact = "tubini.niccolo@gmail.com")
@License("General Public License Version 3 (GPLv3)")

public class FractionalRanking extends Ranking{

	public LinkedHashMap<Double,Double> rank(List<Double> sortedValues){

		super.countOccurrences(sortedValues);
		
		LinkedHashMap<Double, Double> valuesRank = new LinkedHashMap<Double, Double>();
		int tmp = 0;
		
		for(double value : valuesOccurrences.keySet()) {

			valuesRank.put(value, (double)(1 + 2*tmp + super.valuesOccurrences.get(value))/2.0);
			tmp += super.valuesOccurrences.get(value);
		
		}

		return valuesRank;

	}

	public static void main(String[] args) {
		
		Ranking fractionalRanking = new FractionalRanking();
		List<Double> sortedValues = new ArrayList<Double>(Arrays.asList(0.0, 1.0, 1.0, 4.0, 4.0, 4.0, 5.0, 7.0, 7.0, 7.0, 7.0, 7.0, 8.0, 8.0, 9.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0));
		
		LinkedHashMap<Double, Double> valuesRank;
		valuesRank = fractionalRanking.rank(sortedValues);
		
		for(Double value : valuesRank.keySet()) {
			
			System.out.println(value +" "+valuesRank.get(value));
			
		}
		
	}
}
