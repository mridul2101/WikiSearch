package wikisearch.wikipage;

public class FrequencyCount {
	
	private String docId;
	private int ctitle, cinfo, cbody, ccategory, clink, creferences, cinlinks, total;
	
	public FrequencyCount(String id)
	{
		docId = id;
		ctitle = cinfo = cbody = ccategory = clink = creferences = cinlinks = total = 0;
	}
	public String getId(){
		return docId;
	}
	
	public void incrementCounter(int flag){
		total++;
		switch(flag){
		
			case 1 : ctitle++;
					 break;
			case 2 : cinfo++;
					 break;
			case 3 : cbody++;
			 		 break;
			case 4 : ccategory++;
					 break;
			case 5 : clink++;
			 		 break;
			case 6 : creferences++;
			 		 break;
			case 7 : cinlinks++;
	 		 		 break;
		}		
	}
	public void incrementCounterByValue(int flag, int value){
		
		total += value;
		switch(flag){
		
			case 1 : ctitle += value;
					 break;
			case 2 : cinfo += value;
					 break;
			case 3 : //cbody += value;
					cbody++;
			 		 break;
			case 4 : ccategory += value;
					 break;
			case 5 : clink += value;
			 		 break;
			case 6 : creferences += value;
			 		 break;
			case 7 : cinlinks += value;
	 		 		 break;
		}		
	}
	
	public Double getScore(){
		return (ctitle*18.0 + cinfo*5.0 + total) ;
		//return (ctitle*(1.5) + cinfo*(0.8) + cbody*(0.8) + ccategory*(0.45) + (cinlinks+clink)*(0.4) + creferences*(0.2));
	}
	
	public int getTitle(){
		return ctitle;
	}
	public int getInfo(){
		return cinfo;
	}
	public int getBody(){
		return cbody;
	}
	public int getCategory(){
		return ccategory;
	}
	public int getLink(){
		return clink;
	}
	public int getreferences(){
		return creferences;
	}
	public int getinlinks(){
		return cinlinks;
	}
	public int gettotal(){
		return total;
	}
	
}
