package wikisearch.main;

public class SecondIndex {
	
	private String key;
	private long offset;
	
	public SecondIndex(){
		key = new String();
		offset = -1;
	}
	public SecondIndex(String str, long off){
		key = str;
		offset = off;
	}
	
	public String getKey(){
		return key;
	}
	public long getOffset(){
		return offset;
	}
	

}
