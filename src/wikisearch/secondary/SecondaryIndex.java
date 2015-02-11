package wikisearch.secondary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SecondaryIndex {

	private FileInputStream fstream;
	private DataInputStream in;
	private BufferedReader br;
	
	private FileWriter fw;
	private Path myDir,writeFile;
	private BufferedWriter bw;
		
	
	public void createSecIndex(String uri, int flag){
		
		File file;
		if(flag == 1)
			file = new File(uri+"/secondindex");
		else
			file = new File(uri+"/secondmapindex");
		try{
			if(flag == 1)
				fstream = new FileInputStream(uri+"/finalindex");
			else
				fstream = new FileInputStream(uri+"/mappIdTitle");
			
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			fw = new FileWriter(file.getAbsoluteFile());
		
			myDir = Paths.get(file.getParentFile().toURI());
		
			writeFile = myDir.resolve(file.getName());
			bw = Files.newBufferedWriter(writeFile,               
                Charset.forName("UTF-8"), 
                new OpenOption[] {StandardOpenOption.WRITE});
		}
		catch(IOException e){e.printStackTrace();
			System.out.println("EXCEPTION : "+e.getMessage());
		}
		
		final int diff = 19;
		
		int count = 0;
		String str,word;
		long offset = 0;
		StringBuilder content = new StringBuilder("");
		try{
			while((str = br.readLine())!=null){
			
				if(count == 0){
					int indexid;
					indexid = str.indexOf(':');
					word = str.substring(0, indexid);
					content = content.append(word+":"+offset+"\n");
					if(content.length() > 500){
		            	 bw.write(content.toString());
		            	 content = new StringBuilder("");		            	 
		             }
					count = diff;
					
				}
				else{
					count--;
				}
				offset += str.getBytes().length;
				offset++;
				
				
			}
			if(content.length() != 0){
				 bw.write(content.toString());
			}
			System.out.println("OFFSET : "+offset);
			bw.close();
			br.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
}
