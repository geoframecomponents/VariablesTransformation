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
 * The source code in https://github.com/TheHortonMachine/hortonmachine/blob/master/gears/src/main/java/org/hortonmachine/gears/io/timeseries/OmsTimeSeriesReader.java
 * has been modified by Niccolò Tubini in order to get as output the list of the header of the columns
 * 
 *   @Out
 *   public List<Integer> idList;
 * 
 */

package it.geoframe.blogspot.variabletransformation.timeseries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.hortonmachine.gears.libs.exceptions.ModelsIllegalargumentException;
import org.hortonmachine.gears.libs.modules.HMConstants;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import oms3.annotations.Execute;
import oms3.annotations.Finalize;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.io.CSTable;
import oms3.io.DataIO;
import oms3.io.TableIterator;

public class OmsTimeSeriesReader {

	@In
	public String file = null;
	
	@In
	public String fileNovalue = "-9999";
	
    @In
    public String idfield = "ID";
	
    @In
    public double novalue = HMConstants.doubleNovalue;
	
    @Out
    public HashMap<DateTime, double[]> outData;
    
    @Out
    public List<Integer> idList;
    
    private TableIterator<String[]> rowsIterator;

    private CSTable table;

    private DateTimeFormatter formatter = HMConstants.utcDateFormatterYYYYMMDDHHMM;
    
    
    private void ensureOpen() throws IOException {
        if (table == null) {
            table = DataIO.table(new File(file), null);
            rowsIterator = (TableIterator<String[]>) table.rows().iterator();
            outData = new LinkedHashMap<DateTime, double[]>();
        }
    }

    @Execute
    public void read() throws IOException {
        ensureOpen();
        
        /*
         * Get columns id
         */
        int columnCount = table.getColumnCount();
        idList = new ArrayList<Integer>();

        for( int i = 2; i <= columnCount; i++ ) {
        	
            String id = table.getColumnInfo(i).get(idfield);
            try {
            	
                Integer idInteger = Integer.valueOf(id);
                idList.add(idInteger);
                
            } catch (Exception e) {
            	
                throw new ModelsIllegalargumentException("The id value doesn't seem to be an integer.", this.getClass()
                        .getSimpleName());
                
            }
            
        }
        
        /*
         * Get the values
         */
        while( rowsIterator.hasNext() ) {
            String[] row = rowsIterator.next();
            double[] record = new double[row.length - 2];
            for( int i = 2; i < row.length; i++ ) {
                double value = -1;
                if (row[i] == null || row[i].length() == 0) {
                    value = novalue;
                } else {
                    String valueStr = row[i];
                    if (valueStr.trim().equals(fileNovalue)) {
                        value = novalue;
                    } else {
                        value = Double.parseDouble(valueStr);
                    }
                }
                record[i - 2] = value;
            }

            outData.put(formatter.parseDateTime(row[1]), record);
        }
    }

    @Finalize
    public void close() throws IOException {
        rowsIterator.close();
    }
	
}
