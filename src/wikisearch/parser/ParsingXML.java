package wikisearch.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import wikisearch.stemmer.Stemmer;
import wikisearch.stopwords.StopWords;
import wikisearch.wikipage.FrequencyCount;


public class ParsingXML {

	public static String uri, uriparent;
	public static StringBuilder mapidtitle;
	private long fnum = 0;
	
	File filemap;
	String parentmap;
	
	
	
	
	//parent = uriparent + "//mappingfile";
	//file = new File(parent);
	
	//FileWriter fwmap;
	Path writeFilemap,myDirmap;
	BufferedWriter brmap;
	
	public long parseMethod(String uriname, String uri1)
	{
		
		uri = 	uriname;	
		uriparent = uri1;
		//uriparent = uriparent + "\\FILE";
		parentmap = uriparent + "//mappIdTitle";
		filemap = new File(parentmap);
		if(!filemap.exists()){
			try {
				filemap.createNewFile();
			} 
			catch (IOException e) {
				e.printStackTrace();
				// TODO Auto-generated catch block
				System.out.println("EXCEPTION : "+e.getMessage());
			}
		}
		
		
		mapidtitle = new StringBuilder();
		
		try{
			
			myDirmap = Paths.get(filemap.getParentFile().toURI());
			
		    writeFilemap = myDirmap.resolve(filemap.getName());
		    brmap = Files.newBufferedWriter(writeFilemap,               
		            Charset.forName("UTF-8"), 
		            new OpenOption[] {StandardOpenOption.WRITE});
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() {
				
				boolean fpage = false;
				boolean ftitle = false;
				boolean fid = false;
				boolean ftext = false;
				boolean fvar = false;

				long num=0;
				boolean flag = false;
				String urifile;
				int flagop;
				
				
				StringBuilder stitle,sid,stext;
				String docid;
				Map<String, ArrayList<FrequencyCount>> index;
				
				StopWords stopWords;
				Stemmer stem;
				
				public void startElement(String uri, String localName,String qName, 
		                 Attributes attributes) throws SAXException {
		 
				if (qName.equalsIgnoreCase("MEDIAWIKI") || qName.equalsIgnoreCase("FILE")) {
					index = new TreeMap<String, ArrayList<FrequencyCount>>();
					
					stopWords = new StopWords();
					stem = new Stemmer();
					
					urifile = ParsingXML.uri;
				}
				if (qName.equalsIgnoreCase("PAGE")) {
					num++;
					fpage = true;
				}
			 		
				if (qName.equalsIgnoreCase("TITLE")) {
					ftitle = true;
					stitle = new StringBuilder();
				}
		 
				if (qName.equalsIgnoreCase("ID")) {
					fid = true;
					sid =  new StringBuilder();
				}
		 
				if (qName.equalsIgnoreCase("TEXT")) {
					ftext = true;
					flagop = 3;
					stext = new StringBuilder();
				}
		 	}
		 
			public void endElement(String uri, String localName,
				String qName) throws SAXException {
				
				if(qName.equalsIgnoreCase("PAGE")){
					//System.out.println("NEW");
					if(num % 8000 == 0)
					{
						writeFile();
					}
					flagop = 0;
					flag = false;
					fpage = false;
					//System.out.println("ID : " + docid);
				}
				
				if(qName.equalsIgnoreCase("TITLE")){
					ftitle = false;
					if(stitle.length() > 10)
						if(stitle.substring(0, 10).equalsIgnoreCase("Wikipedia:")){
							fvar = false;
					}
				}
				if(qName.equalsIgnoreCase("ID") && flag == false){
					docid = new String(sid.toString());
					flag = true;
					fid = false;
					try{
						if(fvar)
							brmap.write(docid+":"+stitle+"\n");
					//mapidtitle.append(docid+":"+stitle+"\n");
					}
					catch(Exception e){
						try {
							brmap.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(mapidtitle.length() == 10000){
						try {
							brmap.write(mapidtitle.toString());
							mapidtitle.setLength(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				if(qName.equalsIgnoreCase("TEXT")){
					
					ftitle = true;
					if(fvar)
						internalParse(stitle);
					ftitle = false;
					
					//internalParse(stext);
					
					ftext = false;
					
				}
				if(qName.equalsIgnoreCase("MEDIAWIKI") || qName.equalsIgnoreCase("FILE")){
					
					if(mapidtitle.length() != 0 ){
						try {
							brmap.write(mapidtitle.toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						brmap.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(!index.isEmpty())
					{
						writeFile();
					}
					System.out.println("Num of pages : "+num);	
										
				}
				//System.out.println("End Element :" + qName);
		 
			}
			
			public void characters(char ch[], int start, int length) throws SAXException {
				 
				
				if (ftitle) {
					stitle.append(ch,start,length);
					fvar = true;
					
				}
		 
				if (fid) {
					sid.append(ch,start,length);
				}
		 
				
				if (ftext && fvar) {
						//stext.append(ch,start,length);
						internalParse(ch, start,length);
						//internalParse(stext);
						//stext.setLength(0);
			
						return;
					
				}	 				
			}
			
			private void internalParse(char[] charr, int start, int length) {
				// TODO Auto-generated method stub
				Stack<Character> brac = new Stack<Character>();
				String temp = new String();
				temp = "";
				char c1='{',c2 = '{';
				String prev = new String();
				prev = "";
				
				if(ftitle == true)
					flagop = 1;
				
				for(int i = start;(i+start)<length;i++){
					
					//System.out.print(stemp.charAt(i));
					char ch = charr[i];
					if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
					{
						temp = temp + Character.toLowerCase(ch);
						
						if(temp.equals("infobox") && (c1=='{') && (c2=='{') )
							flagop = 2;
						//System.out.println(prev + "  :  " + temp + "  :  " + c1 + "  :  " +c2);
						if((i+2 < length)  && (ftitle == false)){
							if(temp.equals("references") && (c1 == '=') && (c2 == '=') && (charr[i+1] == '=') && (charr[i+2] == '='))
							{ temp = "";	flagop = 6;}
							if(temp.equals("links") && prev.equals("external") && (c1 == '=') && (c2 == '=') && (charr[i+1] == '=') && (charr[i+2] == '='))
							{temp = "";	flagop = 5;}
							if(temp.equals("category") && (c1 == '[') && (c2 == '[') && (charr[i+1] == ':'))
							{
								temp = "";	flagop = 4;
							}
							if(temp.length()==2 && (c1 == '[') && (c2 == '[') && (charr[i+1] == ':'))
							{
								temp = "";	flagop = 7;
							}
							
						}
					}
					else{
						if(ch != ' '){
							c2 = c1;
							c1 = ch;
						}
						if(ch == '{'){
							brac.push(ch);							
						}
						if(ch == '}'){
							if(!brac.empty())
								brac.pop();
							if(brac.empty() && flagop == 2){
								flagop = 3;								
							}
						}
						if((flagop == 7 || flagop == 4) && c1 ==']' && c2 ==']'){
							flagop = 3;
						}
						if(flagop == 5 && c1 =='{' && c2 =='{'){
							flagop = 3;
						}
						if(!stopWords.checkNotStopWords(temp)){
							temp = "";
							continue;
						}
						if(!temp.isEmpty()  ){
							prev = temp;
							if(flagop != 0)
								operateKeyword(temp, flagop);
							temp = "";
						}						
					}
					
									
				}
				
				if(!temp.isEmpty() & stopWords.checkNotStopWords(temp) ){
					prev = temp;
					if(flagop != 0)
						operateKeyword(temp, flagop);
					temp = "";
				}

			}

			private void internalParse(StringBuilder stemp) {
				// TODO Auto-generated method stub
				Stack<Character> brac = new Stack<Character>();
				String temp = new String();
				temp = "";
				//flagop = 0;
				char c1='{',c2 = '{';
				String prev = new String();
				prev = "";
				
				if(ftitle == true)
					flagop = 1;
				
				for(int i = 0;i<stemp.length();i++){
					
					//System.out.print(stemp.charAt(i));
					char ch = stemp.charAt(i);
					if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
					{
						temp = temp + Character.toLowerCase(ch);
						
						if(temp.equals("infobox") && (c1=='{') && (c2=='{') )
							flagop = 2;
						//System.out.println(prev + "  :  " + temp + "  :  " + c1 + "  :  " +c2);
						if((i+2 < stemp.length())  && (ftitle == false)){
							if(temp.equals("references") && (c1 == '=') && (c2 == '=') && (stemp.charAt(i+1) == '=') && (stemp.charAt(i+2) == '='))
							{ temp = "";	flagop = 6;}
							if(temp.equals("links") && prev.equals("external") && (c1 == '=') && (c2 == '=') && (stemp.charAt(i+1) == '=') && (stemp.charAt(i+2) == '='))
							{temp = "";	flagop = 5;}
							if(temp.equals("category") && (c1 == '[') && (c2 == '[') && (stemp.charAt(i+1) == ':'))
							{
								temp = "";	flagop = 4;
							}
							if(temp.length()!=0 && (c1 == '[') && (c2 == '[') && (stemp.charAt(i+1) == ':'))
							{
								temp = "";	flagop = 7;
							}
							
						}
					
					}
					else{
					
						if(ch != ' '){
							c2 = c1;
							c1 = ch;
						}
						if(ch == '{'){
							brac.push(ch);							
						}
						if(ch == '}'){
							if(!brac.empty())
								brac.pop();
							if(brac.empty() && flagop == 2){
								flagop = 3;								
							}
						}
						if((flagop == 7 || flagop == 4) && c1 ==']' && c2 ==']'){
							flagop = 3;
						}
						if(flagop == 5 && c1 =='{' && c2 =='{'){
							flagop = 3;
						}
						
						if(!stopWords.checkNotStopWords(temp)){
							temp = "";
							continue;
						}
						if(!temp.isEmpty()  ){
							prev = temp;
							if(flagop != 0)
								operateKeyword(temp, flagop);
							temp = "";
						}						
					}
					
									
				}
				
				if(!temp.isEmpty() & stopWords.checkNotStopWords(temp) ){
					prev = temp;
					if(flagop != 0)
						operateKeyword(temp, flagop);
					temp = "";
				}
			}
		
			

			private void writeFile() {
				// TODO Auto-generated method stub
				
				//System.out.println("Num of pages : "+num);	
				
				
				fnum++;
				File file = new File(uriparent);
//				String parent = new String(file.getParentFile().getAbsolutePath());
				String temp;
//				
//				parent = parent + "\\FILE";
//				file = new File(parent);

				if(!file.exists())
					file.mkdirs();
				
				temp = uriparent + "/file_" + fnum;
				file = new File(temp);
				System.out.print(temp + " : ");
				
				if(!file.exists()){
					try {
						file.createNewFile();
					} 
					catch (IOException e) {
						e.printStackTrace();
						// TODO Auto-generated catch block
						System.out.println("EXCEPTION : "+e.getMessage());
					}
					fileWrite(file);
				}
				
				
				index = new TreeMap<String, ArrayList<FrequencyCount>>();
			}

				private void fileWrite(File file) {
				// TODO Auto-generated method stub
				
				StringBuilder content = new StringBuilder();
				//Set set = index.entrySet();
			    Iterator<Entry<String, ArrayList<FrequencyCount>>> i = index.entrySet().iterator();
			    System.out.println(index.size());
			    
			    try{
			    	FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					
					Path myDir = Paths.get(file.getParentFile().toURI());
					
			        Path writeFile = myDir.resolve(file.getName());
			        BufferedWriter br = Files.newBufferedWriter(writeFile,               
			                Charset.forName("UTF-8"), 
			                new OpenOption[] {StandardOpenOption.WRITE});
			        
					int previd = 0;
					//float size = 0;
			    while(i.hasNext()) {
			 		Entry<String, ArrayList<FrequencyCount>> me = i.next();
			         content.append(me.getKey() + ":");
			         
			         previd = 0;
			         ArrayList<FrequencyCount> fc = me.getValue();
			         content.append(fc.size() + ":");
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
			        	 //System.out.print(f.getId() + " -  T " + f.getTitle() + " |  I " + f.getInfo()+" | B " + f.getBody()+" | R " + f.getreferences()
			        	//		 +" | L " + f.getLink() +" | C " + f.getCategory() + " #### ");				        	
			         }
			         content.append("\n");
			         if(content.length() > 100000){
			        	// size += content.length();
			        	 //bw.write(content.toString());
			        	 br.write(content.toString());
			        	 content = new StringBuilder("");
			        	 
			         }
//			         //System.out.println();
			         
			      }
			        if(content.length() != 0)
			        	//bw.write(content.toString());
			        {
			        	//size += content.length();	 
			        	br.write(content.toString());}
			        	//System.out.println("  :  "+(size/(1024*1024)) + " MB");
			        br.close();
					bw.close();
			      }
			      catch(IOException e){
			    	  e.printStackTrace();
			    	  System.out.println("EXCEPTION : "+e.getMessage());
			      }		
			}
			
			private void operateKeyword(String temp, int flag){
				//if(temp.charAt(0) == '?')return;
				
				stem.add(temp.toCharArray(), temp.length());
				stem.stem();
				temp = stem.toString();
				if(stopWords.checkNotStopWords(temp)){
					//System.out.println(temp);
					if(!index.containsKey(temp)){
						//System.out.println(docid);
						FrequencyCount fc = new FrequencyCount(docid);
						fc.incrementCounter(flag);
						index.put(temp, new ArrayList<FrequencyCount>());
						index.get(temp).add(fc);
					}
					else
					{
						ArrayList<FrequencyCount> tlist = index.get(temp);
						int flag1 = 0;
						int size = tlist.size()-1;
						if(tlist.get(size).getId().equalsIgnoreCase(docid)){
							index.get(temp).get(size).incrementCounter(flag);								
							flag1 = 1;
						}										
						if(flag1 == 0){
							FrequencyCount fc = new FrequencyCount(docid);
							fc.incrementCounter(flag);
							index.get(temp).add(fc);
						}
					}
				}
			}
			
			};
			saxParser.parse(uri, handler);
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
			   System.out.println("EXCEPTION : "+e.getMessage());
		}
		catch(IOException e){
			e.printStackTrace();
			   System.out.println("EXCEPTION : "+e.getMessage());
		}
		catch (SAXParseException e) 
        {
			e.printStackTrace();
           System.out.println("EXCEPTION : "+e.getMessage());
        }
        catch (SAXException e) 
        {
        	e.printStackTrace();
           System.out.println("EXCEPTION : "+e.getMessage());
        }  
		catch(Exception e){
			e.printStackTrace();
			System.out.println("EXCEPTION : "+e.getMessage());
		}
		
		return fnum;
	}


		
}


