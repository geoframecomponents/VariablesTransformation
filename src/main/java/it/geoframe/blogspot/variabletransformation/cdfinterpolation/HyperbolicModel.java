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

package it.geoframe.blogspot.variabletransformation.cdfinterpolation;

import oms3.annotations.Author;
import oms3.annotations.License;

@Author(name = "Niccolo' Tubini", contact = "tubini.niccolo@gmail.com")
@License("General Public License Version 3 (GPLv3)")
public class HyperbolicModel {

	private double lambda;
	
	public double interpolate(double G, double omega) {
		
//		return 1 - lambda/Math.pow(z, omega);
		return Math.pow(-lambda/(G-1), 1/omega);
		
	}
	
	
	
	public void computeLambda(double z, double F, double omega) {
		
		lambda = Math.pow(z, omega)*(1-F);
		
	}
	
	public double getLambda() {
		
		return lambda;
		
	}
	
}
