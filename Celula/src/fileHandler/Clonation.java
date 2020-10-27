package fileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Clonation {
		
		public static File validateClone( String baseString, String iterationString ) {
			String fullStr = baseString; //"C:/temp/Services/Add/Add-Clone.jar";
			String iterationStr = iterationString; //"C:/temp/Services/Add/Add-Clone";
			File temp = new File(fullStr);
			int i = 1;
	    	while(temp.exists()) {
				String tempStr = iterationStr + i + ".jar";
				temp = new File(tempStr);
				i++;
			}
			return temp;
		}

		public static void copy(File src, File dest) throws IOException { 
			InputStream is = null; 
			OutputStream os = null; 
			try { 
				is = new FileInputStream(src); 
				os = new FileOutputStream(dest);
				// buffer size 1K 
				byte[] buf = new byte[1024]; 
				int bytesRead; 
				while ((bytesRead = is.read(buf)) > 0) { 
					os.write(buf, 0, bytesRead); 
					} 
			} 
			
			finally {
				System.out.println("Created " + dest + " through a marvelous clonation process.");
				is.close(); 
				os.close(); 
			} 
		}

}	
