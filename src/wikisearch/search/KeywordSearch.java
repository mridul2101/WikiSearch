package wikisearch.search;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import wikisearch.stemmer.Stemmer;
import wikisearch.stopwords.StopWords;

public class KeywordSearch {

	private FileInputStream fstream;
	private DataInputStream in;
	private BufferedReader br;
	private StopWords stopwords;
	private Stemmer stem;
	
	public void Search(String uri, String str) {
		// TODO Auto-generated method stub
		stopwords = new StopWords();
		stem = new Stemmer();
		
		str = str.toLowerCase();
		if(!stopwords.checkNotStopWords(str)){
			return;
		}
		stem.add(str.toCharArray(), str.length());
		stem.stem();
		str = stem.toString();
		if(!stopwords.checkNotStopWords(str)){
			return;
		}
		
		try{
			fstream =new FileInputStream(new File(uri));
			in=new DataInputStream(fstream) ;
			br = new BufferedReader(new InputStreamReader(in));
		}
		catch(IOException e){
			System.out.println("EXCEPTION : "+e.getMessage());
		}
		
		String input;
		
		try{
			boolean flag = false;
			while((input = br.readLine()) != null){
				
				if(traverseLine(input, str) == true){
					flag = true;
					break;
				}
				
			}
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		
	}
	
	
	private boolean traverseLine(String input, String str) {
		// TODO Auto-generated method stub
		
		int indexid;
		indexid = input.indexOf(':');
		if(input.substring(0, indexid).matches(str) ){
			//System.out.println("FOUND : " + str);
			Set<String> outId = new TreeSet<String>();
			
			int prev = 0;
			int k = indexid+1;
			String docid;
			while(k < input.length()){
				indexid = input.indexOf('#', k);
				docid = String.valueOf(Integer.parseInt(input.substring(k, indexid))+prev);
				prev = Integer.parseInt(docid);
				//System.out.println(docid);
				outId.add(docid);
				indexid = input.indexOf('|', k);
				k = indexid+1;
			}	
			printDocId(outId);
			return true;
		}
		return false;
	}


	private void printDocId(Set<String> outId) {
		// TODO Auto-generated method stub
		Iterator<String> it;
		it = outId.iterator();
		boolean f = false;
		
		while(it.hasNext()){
			if(f == false)
				{
					System.out.print(it.next());
					f = true;
				}
			else
				System.out.print("," + it.next());
		}
		//System.out.println();
		
	}


//	public void main(String args[]){
//	
//		String uri = "E:\\Dropbox\\dump\\sample\\finalindex";
//		int t;
//		t = Integer.parseInt(args[1]);
//		
//		String str;
//		for(int i = 0;i<t;i++){
//				if(i!=0){
//					System.out.println();
//				}
//				str = args[i+2];
//				Search(uri,str);		
//		}
//	}	

}

