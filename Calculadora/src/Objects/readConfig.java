package Objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class readConfig {
    public int [] loadConfig() throws FileNotFoundException {
    	
    	Scanner scanner = new Scanner(new File("C:/temp/requirements.txt"));
        scanner.useDelimiter(",");
        
        int [] initialConfig = new int[4];
        int i = 0;
        
        while(scanner.hasNext()){
    		
        	initialConfig[i] =  Integer.parseInt(scanner.next());
        	i++;
        }
        
        scanner.close();
        return initialConfig;
    }
}
