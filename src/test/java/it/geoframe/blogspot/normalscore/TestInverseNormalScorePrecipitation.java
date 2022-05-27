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

package it.geoframe.blogspot.normalscore;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import it.geoframe.blogspot.variabletransformation.normalscore.InverseNormalScore;

import it.geoframe.blogspot.variabletransformation.timeseries.*;

/**
 * Test the {@link TestInverse NormaScore} module.
 * 
 * 
 * @author Niccolo' Tubini
 */
public class TestInverseNormalScorePrecipitation {
	
	@Test
	public void Test() throws Exception {
		
		OmsTimeSeriesReader reader = new OmsTimeSeriesReader();
		reader.file = "resources/precipitation.csv";
		reader.fileNovalue = "-9999";
		
		reader.read();
		
		OmsTimeSeriesReader readerNS = new OmsTimeSeriesReader();
		readerNS.file = "resources/normal_score_precipitation.csv";
		readerNS.fileNovalue = "-9999";
		
		readerNS.read();
		
		InverseNormalScore ins = new InverseNormalScore();
		ins.inData = reader.outData;
		ins.inDataNS = readerNS.outData;
		ins.inValuesToSetToNoValue = new String[] {"0.0"};
		ins.exponentPowerModelLowerTail = 5.0;
		ins.exponentPowerModelMiddleValues = 1.0;
		ins.exponentPowerModelUpperTail = 1.5;
		ins.minimumFixedValue = 0.0;
		ins.rankingMethod = "average";
		
		ins.run();
		
		OmsTimeSeriesWriter writer = new OmsTimeSeriesWriter();
		writer.file = "resources/inverse_normal_score_precipitation.csv";
		writer.inData = ins.outData;
		writer.idList = reader.idList;
		
		writer.write();
		writer.close();

	}
}
