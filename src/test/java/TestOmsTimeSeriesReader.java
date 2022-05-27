import org.junit.Test;

import it.geoframe.blogspot.variabletransformation.timeseries.OmsTimeSeriesReader;
import it.geoframe.blogspot.variabletransformation.timeseries.OmsTimeSeriesWriter;

/**
 * Test the {@link TestBrooksCorey} module.
 * 
 * @author Niccolo' Tubini
 */
public class TestOmsTimeSeriesReader {
	
	@Test
	public void Test() throws Exception {
		
		OmsTimeSeriesReader r = new OmsTimeSeriesReader();
		r.file = "resources/timeseries.csv";
		r.read();
		
		for(Integer id : r.idList) {
			System.out.println(id);
		}
		
		OmsTimeSeriesWriter w = new OmsTimeSeriesWriter();
		w.file = "resources/prova.csv";
		w.inData = r.outData;
		w.idList = r.idList;
		w.write();
		w.close();
		
	}

}
