/*
 * This file is part of HortonMachine (http://www.hortonmachine.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * The HortonMachine is free software: you can redistribute it and/or modify
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

/*
 * The source code in https://github.com/TheHortonMachine/hortonmachine/blob/master/gears/src/main/java/org/hortonmachine/gears/io/timeseries/OmsTimeSeriesWriter.java
 * has been modified by Niccolò Tubini in order to specify as input the list of the header of the columns.
 * 
 *   @In
 *   public List<Integer> idList;
 * 
 */
package it.geoframe.blogspot.variabletransformation.timeseries;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hortonmachine.gears.libs.exceptions.ModelsIllegalargumentException;
import org.hortonmachine.gears.libs.modules.HMConstants;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import oms3.annotations.Execute;
import oms3.annotations.Finalize;
import oms3.annotations.In;
import oms3.io.DataIO;
import oms3.io.MemoryTable;

public class OmsTimeSeriesWriter {

	@In
    public String file = null;
	
    @In
    public String tablename = "table";
    
    @In
    public HashMap<DateTime, double[]> inData;
    
    @In
    public String fileNovalue = "-9999";
    
    @In
    public List<Integer> idList;
    
    private MemoryTable memoryTable;

    private DateTimeFormatter formatter = HMConstants.utcDateFormatterYYYYMMDDHHMM;
    private String formatterPattern = HMConstants.utcDateFormatterYYYYMMDDHHMM_string;
    
    private void ensureOpen() throws IOException {
        if (memoryTable == null) {
            memoryTable = new MemoryTable();
            memoryTable.setName(tablename);
            memoryTable.getInfo().put("Created", new DateTime().toString(formatter));
            memoryTable.getInfo().put("Author", "HortonMachine");
        }
    }
    
    @Execute
    public void write() throws IOException {
        ensureOpen();

        Set<Entry<DateTime, double[]>> entrySet = inData.entrySet();
        if (entrySet.isEmpty()) {
            throw new ModelsIllegalargumentException("The data to write are empty.", this);
        }

        String[] colNames = new String[idList.size()+1];
        colNames[0] = "timestamp";
        for( int i = 0; i < idList.size(); i++ ) {
        	colNames[i+1] = "value_" + idList.get(i);
        }
        memoryTable.setColumns(colNames);
        
        
        // Line 5 of the csv
        memoryTable.getColumnInfo(1).put("ID", "");

        int k = 0;
        for( Integer id : idList ) {
            memoryTable.getColumnInfo(k + 1 + 1).put("ID", String.valueOf(id));
            k++;
        }
        
        // Line 6 of the csv
        memoryTable.getColumnInfo(1).put("Type", "Date");
        for( int i = 0; i < idList.size(); i++ ) {
        	memoryTable.getColumnInfo(i+2).put("Type", "double");
        }
        
        // Line 7 of the csv
        memoryTable.getColumnInfo(1).put("Format", formatterPattern);
        for( int i = 0; i < idList.size(); i++ ) {
        	memoryTable.getColumnInfo(i+2).put("Format", "");
        }
        
        
        for( Entry<DateTime, double[]> entry : entrySet ) {
            Object[] valuesRow = new Object[idList.size()+1];

            DateTime dateTime = entry.getKey();
            valuesRow[0] = dateTime.toString(formatter);
            
            double[] valuesArray = entry.getValue();
            for( int j = 0; j < valuesArray.length; j++ ) {
            	if(HMConstants.isNovalue(valuesArray[j])) {
            		valuesRow[j + 1] = fileNovalue;
            	} else {
            		valuesRow[j + 1] = String.valueOf(valuesArray[j]);
            	}
            }
            memoryTable.addRow(valuesRow);
        }
    }
    
    @Finalize
    public void close() throws IOException {
        DataIO.print(memoryTable, new PrintWriter(new File(file)));
    }
}

