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

package it.geoframe.blogspot.zscore;

import org.junit.Test;

import it.geoframe.blogspot.variabletransformation.timeseries.*;
import it.geoframe.blogspot.variabletransformation.zscore.ZScore;

/**
 * Test the {@link TestZcoreScore} module.
 * 
 * 
 * @author Niccolo' Tubini
 */
public class TestZScore {
	
	@Test
	public void Test() throws Exception {
		
		OmsTimeSeriesReader reader = new OmsTimeSeriesReader();
		reader.file = "resources/temperature.csv";
		reader.fileNovalue = "-9999";
		
		reader.read();
		
		ZScore zScore = new ZScore();
		zScore.inData = reader.outData;
//		zScore.inValuesToSetToNoValue = new String[] {"0.0"};
		zScore.run();
		
		OmsTimeSeriesWriter writer = new OmsTimeSeriesWriter();
		writer.file = "resources/z_score.csv";
		writer.inData = zScore.outData;
		writer.idList = reader.idList;
		writer.write();
		writer.close();

	}
}
