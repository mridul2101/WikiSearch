package wikisearch.search;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import wikisearch.main.SecondIndex;
import wikisearch.stemmer.Stemmer;
import wikisearch.stopwords.StopWords;
import wikisearch.wikipage.FrequencyCount;

public class Phase2Search {
	
	public RandomAccessFile raf,rafmap;
	private StopWords stopwords;
	private Stemmer stem;
	public int flagop;
	public List<SecondIndex> secondindex,secondmapindex;
	public Map<String, Double> tfidmap;
	private String urin;
	private double N;
	
	public Phase2Search() {
		// TODO Auto-generated constructor stub
	}
	public Phase2Search(String uri) throws Exception{
	
		N = Math.log(14041179);
		flagop = -1;
		urin = uri;
		secondindex = new ArrayList<SecondIndex>();
		secondmapindex = new ArrayList<SecondIndex>();
		
		loadSecondaryIndex(uri);
		stopwords = new StopWords();
		stem = new Stemmer();
		try {
			//raf = new RandomAccessFile(uri+"/finalindex", "r");
			rafmap = new RandomAccessFile(uri+"/mappIdTitle", "r");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		raf.close();
		rafmap.close();
	}
	public void searchFunc(String query){
		// TODO Auto-generated method stub
		tfidmap = new ConcurrentHashMap<String, Double>();
		List<SearchThread> stlist = new ArrayList<SearchThread>();
		SearchThread st;
		String []qsplit=query.split(" ");
		for(String str : qsplit){
			//System.out.println(str);
			flagop = 0;
			int index = str.indexOf(':');
			if(index != -1){
				//char ch = str.charAt(0);
				switch(str.charAt(0)){				
					case 't' : flagop = 1;
								break;
					case 'b' : flagop = 3;
								break;
					case 'i' : flagop = 2;
								break;
					case 'l' : flagop = 5;
								break;
					case 'c' : flagop = 4;
								break;
					case 'r' : flagop = 6;
								break;
					default  : flagop = 0;					
				}
				str = str.substring(index+1);
			}
			str = str.toLowerCase();
			if(!stopwords.checkNotStopWords(str)){
				continue;
			}
			stem.add(str.toCharArray(), str.length());
			stem.stem();
			str = stem.toString();
			if(!stopwords.checkNotStopWords(str)){
				continue;
			}
			st = new SearchThread(str,secondindex, tfidmap, urin, flagop);
			st.start();
			stlist.add(st);
			//st.operateKeyword(str);
//			operateKeyword(str.substring(index+1));
			
		}
		try {
			for(SearchThread st1 : stlist){
				//System.out.println("wait");
				//if(st1.isAlive())
					st1.join();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
	
		//double startTime = System.currentTimeMillis();
		long size = tfidmap.size();
		if(size == 0){
			System.out.println("Your Search doesn't match any result");
		}
		
		for(int i = 0;(i<10 && i<size);i++){
			Double max = 0.0;
			String docid = "";
			Set<Entry<String, Double>> set = tfidmap.entrySet();
			
			for(Map.Entry<String, Double> e : set){				
				if(max < e.getValue()){
					max = e.getValue();
					docid = e.getKey();
				}				
			}
			try {
				printDoc(docid);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			tfidmap.remove(docid);
		}
		
//		Set<Entry<String, Double>> set = tfidmap.entrySet();
//		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
//        
//		sortHashMapByValue(list);
//		
//		for(int i = 0;(i<10 && i<list.size());i++){
//			printDoc(list.get(i));
//		}
//		double endTime   = System.currentTimeMillis();
//		double totalTime = endTime - startTime;
//		System.out.println("SEARCH TIME : "+ totalTime/1000 + " sec");
//		
		//System.out.println();
		tfidmap.clear();
	}
	
	private void printDoc(String entry) throws Exception {
		// TODO Auto-generated method stub
		
		String str = entry;
		long[] index = new long[2];
		index[0] = index[1] = -1; 
		findMapIndex(str, index);
		//System.out.println(str+ "  "+flagop+"   START : " + index[0] +"   "+"END :  "+ index[1]);
		
		byte[] value;
		if(index[1] != -1)
			value = new byte[(int)(index[1]-index[0]+1)];
		else 
			value = new byte[(int)(424621417-index[0]-1)];
			//value = new byte[(int)(474115825-index[0]-1)];
		
		try {
			rafmap.seek((int)index[0]);
			if(index[1] != -1)
				rafmap.read(value, 0, (int)(index[1]-index[0]));
			else
				rafmap.read(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ans = new String(value);
		
		parseMapString(str,ans);
		
		//System.out.println(ans);
		
	}
	private void printDoc(Entry<String, Double> entry) throws Exception {
		// TODO Auto-generated method stub
		
		String str = entry.getKey();
		long[] index = new long[2];
		index[0] = index[1] = -1; 
		findMapIndex(str, index);
		//System.out.println(str+ "  "+flagop+"   START : " + index[0] +"   "+"END :  "+ index[1]);
		
		byte[] value;
		if(index[1] != -1)
			value = new byte[(int)(index[1]-index[0]+1)];
		else 
			//value = new byte[(int)(408847-index[0]-1)];
			value = new byte[(int)(474115825-index[0]-1)];
		
		try {
			rafmap.seek((int)index[0]);
			if(index[1] != -1)
				rafmap.read(value, 0, (int)(index[1]-index[0]));
			else
				rafmap.read(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ans = new String(value);
		
		parseMapString(str,ans);
		
		//System.out.println(ans);
		
	}
	private void operateKeyword(String str) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(str+ "  "+flagop);
		long[] index = new long[2];
		index[0] = index[1] = -1; 
		findIndex(str, index);
		//System.out.println(str+ "  "+flagop+"   START : " + index[0] +"   "+"END :  "+ index[1]);
		
		byte[] value;
		int diffval = (int) (index[1]-index[0]);
		if(index[1] != -1)
			value = new byte[(int)(diffval+1)];
		else 
			//value = new byte[(int)(18922449-18920844-1)];
			//value = new byte[(int) (8549375495-8549363918-1)];
			value = new byte[(int) (621-1)];
		try {
			raf.seek(index[0]);
			if(index[1] != -1)
				raf.read(value, 0, (int)(index[1]-index[0]));
			else
				raf.read(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ans = new String(value);
	//	System.out.println(ans);
	//	double startTime = System.currentTimeMillis();
		
		parseString(str, ans);
		
//		double endTime   = System.currentTimeMillis();
//		double totalTime = endTime - startTime;
//		System.out.println("SEARCH TIME : "+ totalTime/1000 + " sec");
		
	}
	
	private void parseMapString(String str, String ans) throws Exception{
		// TODO Auto-generated method stub
		String key;
		int len = ans.length();
		for(int i = 0;i<len;){
			 //key = new StringBuilder();
			if(ans.charAt(i) == '\n')
				break;
		
			int index = ans.indexOf(':', i);
			key = ans.substring(i, index);
			i = index+1;
//			while(ans.charAt(i) != ':'){
//				key.append(ans.charAt(i));
//				i++;		
//				if(i == len){
//					//System.out.println(str +"  :  NOT FOUND IN INDEX");
//					
//					return;
//				}
//			}
		//	i++;
//			if(i == len){
//				//System.out.println(str +"  :  NOT FOUND IN INDEX");
//				return;
//			}
			if(str.matches(key.toString())){
				System.out.print(str +"  :  ");
				index = ans.indexOf('\n', i);
				String output = ans.substring(i, index);
//				while(ans.charAt(i) != '\n'){
//					System.out.print(ans.charAt(i));
//					i++;
//					if(i == len)
//						break;
//				}
				i = index;
				System.out.println(output);
				return;
			}
			else{
				while(ans.charAt(i) != '\n'){
					i++;
					if(i == len){
						//System.out.println(str +"  :  NOT FOUND IN INDEX");
						return;
					}
				}
				i++;
			}			
			
		}
		
	}
	

	
	private void parseString(String str, String ans) throws Exception {
		// TODO Auto-generated method stub
		
		
		StringBuilder key;
		int len = ans.length();
		for(int i = 0;i<len;){
			 key = new StringBuilder();
			if(ans.charAt(i) == '\n')
				break;
		
			while(ans.charAt(i) != ':'){
				key.append(ans.charAt(i));
				i++;		
				if(i == len){
					//System.out.println(str +"  :  NOT FOUND IN INDEX");
					
					return;
				}
			}
			i++;
//			if(i == len){
//				//System.out.println(str +"  :  NOT FOUND IN INDEX");
//				return;
//			}
			if(str.matches(key.toString())){
				//System.out.print(key.toString() +"  :  ");
				int prev = 0;
				String docid = "";
				int numdoc = 0;
				
			//	double startTime = System.currentTimeMillis();
				int indexi = ans.indexOf('\n', i);
				numdoc = (indexi - i)/15;
//				int k = i;
//				while(ans.charAt(k) != '\n'){
//					if(ans.charAt(k) == '|')
//						numdoc++;
//					k++;
//				}
				
//				double endTime   = System.currentTimeMillis();
//				double totalTime = endTime - startTime;
//				System.out.println("SEARCH TIME'''' : "+ totalTime/1000 + " sec");
//				//System.out.println("NO of Doc : "+ numdoc);
				//Map<String, Double> tfidtemp = new HashMap<String, Double>();
				
				while(ans.charAt(i) != '\n'){
				//	System.out.print(ans.charAt(i));
					docid = "";
					indexi = ans.indexOf('#', i);
					//docid = ans.substring(i, indexi);
					
//					while(ans.charAt(i) != '#'){
//						docid += ans.charAt(i);						
//						i++;
//					}
					docid = String.valueOf(Integer.parseInt(ans.substring(i, indexi))+prev);
					prev = Integer.parseInt(docid);
					FrequencyCount fc = new FrequencyCount(docid);
					i = indexi;
					i++;
					int flagop1 = -1;
					StringBuffer count = new StringBuffer();
					while(ans.charAt(i) != '|'){
						
						switch(ans.charAt(i)){
							case 't' : flagop1 = 1;
										break;
							case 'i' : flagop1 = 2;
										break;
							case 'b' : flagop1 = 3;
							 			break;
							case 'r' : flagop1 = 6;
										break;
							case 'L' : flagop1 = 5;
										break;
							case 'c' : flagop1 = 4;
										break;
							case 'l' : flagop1 = 7;
										break;
							default : flagop1 = 0;
						}
						i++;
						count.setLength(0);
						while(ans.charAt(i) != '#' && ans.charAt(i) != '|'){
							count.append(ans.charAt(i));// ans.charAt(i);						
							i++;
						}
						fc.incrementCounterByValue(flagop1, Integer.parseInt(count.toString()));
						if(ans.charAt(i) == '#')
							i++;
					}
					addToTempMap(str,docid, fc, numdoc);
					//addToTempMap1(str, docid, fc, tfidtemp);
					//numdoc++;
					
					
					i++;
					if(i == len){
						break;
					}
				}
				//addToMap(tfidtemp, numdoc);
				//System.out.println();
				return;
			}
			else{
				i = ans.indexOf('\n', i);
				//i = index;
//				while(ans.charAt(i) != '\n'){
//					i++;
//					if(i == ans.length()){
//						//System.out.println(str +"  :  NOT FOUND IN INDEX");
//						return;
//					}
//				}
				i++;
			}			
			
		}
		
	}

	private void addToTempMap(String str, String docid, FrequencyCount fc,
			int numdoc) throws Exception {
		// TODO Auto-generated method stub
		
		Double count = 0.0;
		
		switch(flagop){
		
			case 1 : {
						count += (fc.getTitle());			
						break;
			}
			case 2 : {
						count += (fc.getInfo());
						break;
			}
			case 3 : {
						count += fc.getBody();
						break;
			}
			case 4 : {
						count += fc.getCategory();
						break;
			}
			case 5 : {
						count += (fc.getLink()+fc.getinlinks());
						break;
			}
			case 6 : {	
						count += fc.getreferences();
						break;
			}
			case 0 : {
						count += fc.getScore();	
						break;
			}
		}
		if(count == 0.0) return;
		
		//Double idf;
		//idf = (N - Math.log(numdoc));
		Double value = (Double)count*(N - Math.log(numdoc));
			
		if(!tfidmap.containsKey(docid)){
			tfidmap.put(docid, value);
		}
		else{ 
				tfidmap.put(docid, tfidmap.get(docid) * value);
			}
				
			
		return;
		
		
	}
	private void addToMap( Map<String, Double> tfidtemp, int numdoc) {
		// TODO Auto-generated method stub
		int N = 14041179;
		Double idf;
		idf = Math.log(N) - Math.log(numdoc);
		Set<Entry<String, Double>> set = tfidtemp.entrySet();
		for(Entry<String, Double> e1 : set){
			Double value = (Double)e1.getValue()*idf;
			
			if(value != 0){
				
				String str = (String)e1.getKey();
				if(!tfidmap.containsKey(str)){
					tfidmap.put(str, value);
				}
				else{
					Double temp = tfidmap.get(str) * value ; 
					tfidmap.put(str, temp);
				}
				
			}
			
		}		
		return;		
	}
	
	
	
	private void addToTempMap1(String str, String docid, FrequencyCount fc, Map<String, Double> tfidtemp) {
		// TODO Auto-generated method stub
		Double count = 0.0;
		
		switch(flagop){
		
			case 1 : {
						count += (fc.getTitle());			
						break;
			}
			case 2 : {
						count += (fc.getInfo());
						break;
			}
			case 3 : {
						count += fc.getBody();
						break;
			}
			case 4 : {
						count += fc.getCategory();
						break;
			}
			case 5 : {
						count += (fc.getLink()+fc.getinlinks());
						break;
			}
			case 6 : {	
						count += fc.getreferences();
						break;
			}
			case 0 : {
						count += fc.getScore();	
						break;
			}
		}
		if(count!= 0)
			tfidtemp.put(docid, count);
			
		//System.out.println(docid+" : "+tfidmap.get(docid));
		return;		
		
	}
	
	
	private void findIndex(String str, long[] index) throws Exception {
		// TODO Auto-generated method stub
		
		int start, end;
		start = 0;
		end = secondindex.size()-1;
		int mid;
		mid = (start+end)/2;
		while(start < (end-1)){
			if(str.compareTo(secondindex.get(mid).getKey()) < 0){
				end = mid;
			}
			else if(str.compareTo(secondindex.get(mid).getKey()) > 0){
				start = mid;
			}
			else{
				index[0] = secondindex.get(mid).getOffset();
				index[1] = secondindex.get(mid+1).getOffset();
				return;
			}
			mid = (start+end)/2;
		}
			
		if(str.compareTo(secondindex.get(secondindex.size()-1).getKey()) < 0){
			index[0] = secondindex.get(start).getOffset();			
			index[1] = secondindex.get(start+1).getOffset();
		}
		else{
			index[0] = secondindex.get(start+1).getOffset();			
			index[1] = -1;
		}
		
	return;		
	}
	
	private void findMapIndex(String str, long[] index) throws Exception {
		// TODO Auto-generated method stub
		
		int start, end;
		start = 0;
		end = secondmapindex.size()-1;
		int mid;
		mid = (start+end)/2;
		while(start < (end-1)){
			//if(str.compareTo(secondmapindex.get(mid).getKey()) < 0){
			if(Integer.parseInt(str) < Integer.parseInt(secondmapindex.get(mid).getKey())){
				end = mid;
			}
			else // if(str.compareTo(secondmapindex.get(mid).getKey()) > 0){
				if(Integer.parseInt(str) > Integer.parseInt(secondmapindex.get(mid).getKey())){
				start = mid;
			}
			else{
				index[0] = secondmapindex.get(mid).getOffset();
				index[1] = secondmapindex.get(mid+1).getOffset();
				return;
			}
			mid = (start+end)/2;
		}
		
		//if(str.compareTo(secondmapindex.get(secondmapindex.size()-1).getKey()) < 0){
		if(Integer.parseInt(str) < Integer.parseInt(secondmapindex.get(secondmapindex.size()-1).getKey())){
			index[0] = secondmapindex.get(start).getOffset();			
			index[1] = secondmapindex.get(start+1).getOffset();
		}
		else{
			index[0] = secondmapindex.get(start+1).getOffset();			
			index[1] = -1;
		}
		
	return;		
	}

	private void sortHashMapByValue(List<Entry<String, Double>> list) throws Exception {
		// TODO Auto-generated method stub
		
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
       // System.out.println();
		
	}

	
	private void loadSecondaryIndex(String uri) throws Exception{
		FileInputStream fstream;
		DataInputStream in;
		BufferedReader br;
		
		try{
			fstream = new FileInputStream(uri+"/secondindex");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			String str;
			while((str = br.readLine())!=null){
				String key;
				long offset;
				int index;
				index = str.indexOf(':');
				key = str.substring(0, index);
				offset = Long.parseLong(str.substring(index+1));
				SecondIndex temp = new SecondIndex(key, offset);
				secondindex.add(temp);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			fstream = new FileInputStream(uri+"/secondmapindex");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			String str;
			while((str = br.readLine())!=null){
				String key;
				long offset;
				int index;
				index = str.indexOf(':');
				key = str.substring(0, index);
				offset = Long.parseLong(str.substring(index+1));
				SecondIndex temp = new SecondIndex(key, offset);
				secondmapindex.add(temp);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	

}
