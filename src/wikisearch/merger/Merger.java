package wikisearch.merger;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import wikisearch.wikipage.FrequencyCount;

public class Merger {
	
	private List<FrequencyCount> fc;
	private Map<String, MergeClass> t_map;
	private Map<Integer, String> filemap;
	
	private String word, counter;
	private StringBuilder content;
	private FileWriter fw;
	private Path myDir,writeFile;
	private BufferedWriter bw;
	private long lines;
	
	private FileInputStream []fstream;
	private DataInputStream []in;
	private BufferedReader []br;
	
	public void mergingFunc(String uri, long fnum){
		
		lines = 0;
		int c_merge = 1;
		String name;
		filemap = new HashMap<Integer, String>();
		fstream =new FileInputStream[(int) fnum];
		in=new DataInputStream[(int) fnum] ;
		br = new BufferedReader[(int) fnum] ;
		
		File file = new File(uri + "/finalindex");
		if(!file.exists()){
			try {
				file.createNewFile();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EXCEPTION : "+e.getMessage());
			}
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
		
        uri = uri + "/file_";
		t_map = new TreeMap<String, MergeClass>();
		
		content = new StringBuilder();
		fc = new ArrayList<FrequencyCount>();
		word = new String();
		
		try
	    {
		for(int i=0;i<fnum;i++)
		{
			name = uri + c_merge;
			filemap.put(i, name);
			c_merge++;
			fstream[i] = new FileInputStream(name);
			in[i] = new DataInputStream(fstream[i]);
			br[i] = new BufferedReader(new InputStreamReader(in[i]));
			
			String str;
			if((str = br[i].readLine())!=null)
			{
				//String temp = "";
				word = "";
				try{
				traverseLine(str);
				if(t_map.containsKey(word)){
					t_map.get(word).file.add(i);
					t_map.get(word).setCounter(counter);
					for(int k = 0;k<fc.size();k++){
						t_map.get(word).listfc.add(fc.get(k));
					}
				}
				else
				{
					//t_map.put(word.toString(), new MergeClass());
					MergeClass mc = new MergeClass();
					mc.file.add(i);
					mc.setCounter(counter);
					for(int k = 0;k<fc.size();k++){
						
						mc.listfc.add(fc.get(k));						
					}	
					t_map.put(word,mc);
					
				}
				}
				catch(Exception e){e.printStackTrace();
					System.out.println("EXCEPTION : "+e.getMessage());
				}
				fc.clear();
			}
					
		  }
	    }
		catch(Exception e){e.printStackTrace();
			System.out.println("EXCEPTION : "+e.getMessage());
		}
		
		while(t_map.size() != 0){
			List<Integer>filefc = null;
			try {
				filefc = printTopValue();
			for(Integer i : filefc){
				String str;
				if((str = br[i].readLine())!=null)
				{
					word = "";
					traverseLine(str);
					if(t_map.containsKey(word)){
						t_map.get(word).file.add(i);
						t_map.get(word).setCounter(counter);
						for(int k = 0;k<fc.size();k++){
								t_map.get(word).listfc.add(fc.get(k));
							
						}
					}
					else
					{
						//t_map.put(word.toString(), new MergeClass());
						MergeClass mc = new MergeClass();
						mc.file.add(i);
						mc.setCounter(counter);
						for(int k = 0;k<fc.size();k++){
							
							mc.listfc.add(fc.get(k));						
						}	
						t_map.put(word,mc);
						
					}
				}
				else
				{
					br[i].close();
					System.out.println("Delete file "+filemap.get(i));
					Files.deleteIfExists(Paths.get(filemap.get(i)));
				}
				fc.clear();
			   }
			}
			catch(Exception e){e.printStackTrace();
				System.out.println("EXCEPTION : "+e.getMessage());
			}
		}
			
		try {
				 if(content.length() != 0)
					 bw.write(content.toString());
				 bw.close();
			 }
			 catch (IOException e) {e.printStackTrace();
				// TODO Auto-generated catch block
				 System.out.println("EXCEPTION : "+e.getMessage());
			}	
		System.out.println("Index File Created of line : "+lines);
	}
	
	private List<Integer> printTopValue() throws IOException {
		// TODO Auto-generated method stub
		 lines++;
		 Iterator<Entry<String, MergeClass>> it = t_map.entrySet().iterator();
		 Entry<String, MergeClass> me = it.next();
         content.append(me.getKey() + ":");
         content.append(me.getValue().getCounter() + ":");
         //System.out.print(me.getKey()+":");
         int previd = 0;
         MergeClass mc = me.getValue();
         List<FrequencyCount> fc = mc.listfc;
         List<Integer> filefc = mc.file;
         for(FrequencyCount f : fc){
        	 content.append(Integer.parseInt(f.getId())-previd);
        	 previd = Integer.parseInt(f.getId());
        	 if(f.getTitle() != 0){
        		 content.append("#t"+f.getTitle());
        	 }
        	 if(f.getInfo() != 0){
        		 content.append("#i"+f.getInfo());
        	 }
        	 if(f.getBody() != 0){
        		 content.append("#b"+f.getBody());
        	 }
        	 if(f.getreferences() != 0){
        		 content.append("#r"+f.getreferences());
        	 }
        	 if(f.getLink() != 0){
        		 content.append("#L"+f.getLink());
        	 }
        	 if(f.getCategory() != 0){
        		 content.append("#c"+f.getCategory());
        	 }
        	 if(f.getinlinks() != 0){
        		 content.append("#l"+f.getinlinks());
        	 }
        	 content.append("|");
        	 if(content.length() > 500){
            	 bw.write(content.toString());
            	 content = new StringBuilder("");
            	 
             }
        	 
        	 //System.out.print(f.getId() + " -  T " + f.getTitle() + " |  I " + f.getInfo()+" | B " + f.getBody()+" | R " + f.getreferences()
        	//		 +" | L " + f.getLink() +" | C " + f.getCategory() + " #### ");				        	
         }
         content.append("\n");
         //System.out.println();
         if(content.length() > 500){
        	 bw.write(content.toString());
        	 content = new StringBuilder("");
        	 
         }	
         t_map.remove(me.getKey());
         return filefc;
	}

	private void traverseLine(String str) {
		// TODO Auto-generated method stub
		FrequencyCount f ;
		int prev = 0;
		String docid = "";
		int flag = 0,flag1 = 0;
		for(int k = 0;k<str.length();k++)
		{
			//System.out.print(str.charAt(k));
			if(str.charAt(k) == ':'){
				flag = 1;k++;
			}
			
			if(flag == 1){
				int indexid;
				if(flag1 == 0){
					counter = "";
					indexid = str.indexOf(':', k);
					counter = str.substring(k, indexid);
					k = indexid+1;
					flag1 = 1;
				}
				indexid = str.indexOf('#', k);
				docid = String.valueOf(Integer.parseInt(str.substring(k, indexid))+prev);
				prev = Integer.parseInt(docid);
				
				f = new FrequencyCount(docid);
				k = indexid+1;
				while(str.charAt(k) != '|'){
					if(k+1 == str.length()){
						return;
					}
					if(str.charAt(k) == '#') {k++;continue;}
					int flagop = 0;
					
					switch(str.charAt(k)){
					
						case 't' : flagop = 1;
									 break;
						case 'i' : flagop = 2;
									break;
						case 'b' : flagop = 3;
				 		 			break;
						case 'r' : flagop = 6;
									break;
						case 'L' : flagop = 5;
									break;
						case 'c' : flagop = 4;
									break;
						case 'l' : flagop = 7;
									break;
						default : flagop = 0;
					}
					
					if(flagop != 0){
						indexid = Math.min(str.indexOf('#', k), str.indexOf('|', k));
						if(indexid == -1){
							indexid = str.indexOf('|', k);
						}
						try{
						int count = Integer.parseInt(str.substring(k+1, indexid));
						//System.out.print(count +" ");
						for(int j = 0;j<count;j++)
							f.incrementCounter(flagop);
						k = indexid;
						}
						catch(Exception e){e.printStackTrace();
							System.out.println("EXCEPTION : "+e.getMessage());
						}
					}
				}
				fc.add(f);
			}
			else{
				word = word+str.charAt(k);
			}
			
		}
	}

//	public void main(String args[]){
//		String uri = "E:\\Dropbox\\dump\\sample";
//		
//		long fnum = 3;
//		mergingFunc(uri, fnum);
//		
//		
//	}
	
}

