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


public class ParsingXML1 {

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
		parentmap = uriparent + "//mappIdTitle1";
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
		 
				if (qName.equalsIgnoreCase("MEDIAWIKI")) {
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
					if(num % 5000 == 0)
					{
						//writeFile();
					}
					flagop = 0;
					flag = false;
					fpage = false;
					//System.out.println("ID : " + docid);
				}
				
				if(qName.equalsIgnoreCase("TITLE")){
					ftitle = false;
				}
				if(qName.equalsIgnoreCase("ID") && flag == false){
					docid = new String(sid.toString());
					flag = true;
					fid = false;
					try{
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
					
				}
				if(qName.equalsIgnoreCase("TEXT")){
					
					ftitle = true;
					ftitle = false;
					
					
					ftext = false;
					
				}
				if(qName.equalsIgnoreCase("MEDIAWIKI")){
					
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
					
					
					System.out.println("Num of pages : "+num);	
										
				}
				//System.out.println("End Element :" + qName);
		 
			}
			
			public void characters(char ch[], int start, int length) throws SAXException {
				 
				
				if (ftitle) {
					stitle.append(ch,start,length);
				}
		 
				if (fid) {
					sid.append(ch,start,length);
				}
		 
				
				if (ftext) {
						//stext.append(ch,start,length);
				//		internalParse(ch, start,length);
						//internalParse(stext);
						//stext.setLength(0);
			
						return;
					
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


