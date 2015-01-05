import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataImportUtility {
	public static ArrayList<String[]> readCSVFile(String fileName) throws IOException {
		@SuppressWarnings("resource")
		BufferedReader myReader = new BufferedReader(new FileReader(fileName));
		int columns = myReader.readLine().split(",").length;
		ArrayList<String[]> data = new ArrayList<String[]>();
		String line;
        while ((line = myReader.readLine()) != null) { 
            String[] rowData = new String[columns];
            for (int i = 0; i < columns; i++) { 
                String[] value = line.split(",", columns);                
                rowData[i] = value[i];
            }
            data.add(rowData);
        }
        
        return data;		
	}
}