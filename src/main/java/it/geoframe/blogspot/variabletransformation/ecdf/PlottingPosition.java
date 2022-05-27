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

package it.geoframe.blogspot.variabletransformation.ecdf;

import java.util.LinkedHashMap;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Niccolo' Tubini", contact = "tubini.niccolo@gmail.com")
@License("General Public License Version 3 (GPLv3)")
public class PlottingPosition {
	
	private double a;
	
	public PlottingPosition(double a) {
		this.a = a;
	}

	public LinkedHashMap<Double,Double> computeECDF(int count, LinkedHashMap<Double, Double> valuesRank){

		LinkedHashMap<Double, Double> ecdf = new LinkedHashMap<Double, Double>(valuesRank.size());
		
		for(Double value : valuesRank.keySet()) {
			
			ecdf.put(value, ((valuesRank.get(value)-this.a)/((double)count+1.0-2*this.a)));
		}
		
		return ecdf;
	}
	
}
