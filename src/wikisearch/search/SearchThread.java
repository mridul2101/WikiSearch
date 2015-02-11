package wikisearch.search;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import wikisearch.main.SecondIndex;
import wikisearch.wikipage.FrequencyCount;

public class SearchThread extends Thread{
	
	private int flagop;
	private List<SecondIndex> secondindex;
	private Map<String, Double> tfidmap;
	private double N;
	private RandomAccessFile raf;
	private String keystr;
	public SearchThread(){		
	}
	
	public SearchThread(String str, List<SecondIndex> si, Map<String, Double> sd, String uri, int f){
		
		keystr = str;
		N = Math.log(14041179);
		secondindex = si;
		tfidmap = sd;
		flagop = f;		
		try {
			raf = new RandomAccessFile(uri+"/finalindex", "r");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		//System.out.println("Start...");
		try{
		if(keystr.length() != 0)
			operateKeyword(keystr);
		}
		catch (Exception e ){
			return;
		}
		//System.out.println("End... "+keystr);
	}
	
	public void operateKeyword(String str) throws Exception {
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
			//value = new byte[(int)(7393304691-7393303762-1)];
			//value = new byte[(int) (8549375495-8549363918-1)];
			//value = new byte[(int) (621-1)];
			value = new byte[(int) (929-1)];
		try {
			raf.seek(index[0]);
			//System.out.println("chk1"+str);
			if(index[1] != -1)
				raf.read(value, 0, (int)(index[1]-index[0]));
			else
				raf.read(value);
			//System.out.println("chk2"+str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ans = new String(value);
		
		//if(str.compareTo("cricket")==0)
		//System.out.println(ans);
	//	double startTime = System.currentTimeMillis();
		try{
			parseString1(str, ans);
		}
		catch(Exception e)
		{
			return;
		}
//		double endTime   = System.currentTimeMillis();
//		double totalTime = endTime - startTime;
//		System.out.println("SEARCH TIME : "+ totalTime/1000 + " sec");
		
	}	
	private void parseString1(String str, String ans1) throws Exception{
		String[] strarr = ans1.split("\n");
		for(String ans : strarr){
			int index = ans.indexOf(':');
			String keystr = ans.substring(0, index);
			if(keystr.compareToIgnoreCase(str) > 0)
				break;
			int i = index+1;
			if(keystr.matches(str)){
				int prev = 0;
				String docid = "";
				int numdoc = 0;
				
				index = ans.indexOf(':', i);
				numdoc = Integer.parseInt(ans.substring(i, index));
				i = index+1;
				int pc = 0;
				for(int k = i;k<ans.length() && pc <= 789900;k++){
					pc++;
					index = ans.indexOf('#', k);
					docid = String.valueOf(Integer.parseInt(ans.substring(k, index))+prev);
					prev = Integer.parseInt(docid);
					FrequencyCount fc = new FrequencyCount(docid);
					
					k = index+1;
					
					while(ans.charAt(k) != '|'){
						if(k+1 == ans.length()){
							break;
						}
						if(ans.charAt(k) == '#') {k++;continue;}
						int flagop1 = 0;
						
						switch(ans.charAt(k)){
						
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
						
						if(flagop1 != 0){
							index = Math.min(ans.indexOf('#', k), ans.indexOf('|', k));
							if(index == -1){
								index = ans.indexOf('|', k);
						}
						try{
							int count = Integer.parseInt(ans.substring(k+1, index));
							//System.out.print(count +" ");
							fc.incrementCounterByValue(flagop1, count);
							k = index;
						}
						catch(Exception e){e.printStackTrace();
							System.out.println("EXCEPTION : "+e.getMessage());
						}
						}
					}
				
				addToTempMap(str,docid, fc, numdoc);
				}//System.out.println(pc);
				break;
			}
			
		}
		
	}
//	private void parseString(String str, String ans)  throws Exception {
//		// TODO Auto-generated method stub
//		
//		
//		StringBuilder key = new StringBuilder();
//		int len = ans.length();
//		for(int i = 0;i<len;){
//			 key.setLength(0); ;
//			if(ans.charAt(i) == '\n')
//				break;
//			//System.out.println("TRUE");
//			while(ans.charAt(i) != ':'){
//				key.append(ans.charAt(i));
//				i++;		
//				if(i == len){
//					//System.out.println(str +"  :  NOT FOUND IN INDEX");
//					
//					return;
//				}
//			}
//			i++;
//			if(i == len){
//				//System.out.println(str +"  :  NOT FOUND IN INDEX");
//				return;
//			}
//			if(str.matches(key.toString())){
//				//System.out.print(key.toString() +"  :  ");
//				int prev = 0;
//				String docid = "";
//				int numdoc = 0;
//				
//				int indexi = ans.indexOf(':', i);
//				numdoc = Integer.parseInt(ans.substring(i, indexi));
//				i = indexi+1;
//				
//			//	double startTime = System.currentTimeMillis();
//				//int indexi = ans.indexOf('\n', i);
//				//numdoc = (indexi - i)/15;
////				int k = i;
////				while(ans.charAt(k) != '\n'){
////					if(ans.charAt(k) == '|')
////						numdoc++;
////					k++;
////				}
//				
////				double endTime   = System.currentTimeMillis();
////				double totalTime = endTime - startTime;
////				System.out.println("SEARCH TIME'''' : "+ totalTime/1000 + " sec");
////				//System.out.println("NO of Doc : "+ numdoc);
//				//Map<String, Double> tfidtemp = new HashMap<String, Double>();
//				
//				while(ans.charAt(i) != '\n'){
//				//	System.out.print(ans.charAt(i));
//					docid = "";
//					indexi = ans.indexOf('#', i);
//					//docid = ans.substring(i, indexi);
//					
////					while(ans.charAt(i) != '#'){
////						docid += ans.charAt(i);						
////						i++;
////					}
//					docid = String.valueOf(Integer.parseInt(ans.substring(i, indexi))+prev);
//					prev = Integer.parseInt(docid);
//					FrequencyCount fc = new FrequencyCount(docid);
//					i = indexi;
//					i++;
//					int flagop1 = -1;
//					StringBuffer count = new StringBuffer();
//					while(ans.charAt(i) != '|'){
//						
//						switch(ans.charAt(i)){
//							case 't' : flagop1 = 1;
//										break;
//							case 'i' : flagop1 = 2;
//										break;
//							case 'b' : flagop1 = 3;
//							 			break;
//							case 'r' : flagop1 = 6;
//										break;
//							case 'L' : flagop1 = 5;
//										break;
//							case 'c' : flagop1 = 4;
//										break;
//							case 'l' : flagop1 = 7;
//										break;
//							default : flagop1 = 0;
//						}
//						i++;
//						count.setLength(0);
//						while(ans.charAt(i) != '#' && ans.charAt(i) != '|'){
//							count.append(ans.charAt(i));// ans.charAt(i);						
//							i++;
//						}
//						fc.incrementCounterByValue(flagop1, Integer.parseInt(count.toString()));
//						if(ans.charAt(i) == '#')
//							i++;
//					}
//					addToTempMap(str,docid, fc, numdoc);
//					//addToTempMap1(str, docid, fc, tfidtemp);
//					//numdoc++;
//					
//					
//					i++;
//					if(i == len){
//						break;
//					}
//				}
//				//addToMap(tfidtemp, numdoc);
//				//System.out.println();
//				return;
//			}
//			else{
//				i = ans.indexOf('\n', i);
//				//i = index;
////				while(ans.charAt(i) != '\n'){
////					i++;
////					if(i == ans.length()){
////						//System.out.println(str +"  :  NOT FOUND IN INDEX");
////						return;
////					}
////				}
//				i++;
//			}			
//			
//		}
//		
//	}
//	
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

	private void findIndex(String str, long[] index)  throws Exception {
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
	

	
}
