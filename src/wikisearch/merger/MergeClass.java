package wikisearch.merger;

import java.util.ArrayList;
import java.util.List;

import wikisearch.wikipage.FrequencyCount;

public class MergeClass {
	public List<FrequencyCount> listfc;
	public List<Integer> file;
	public int counter;
	
	MergeClass(){
		listfc = new ArrayList<FrequencyCount>();
		file = new ArrayList<Integer>();
	}
	public void setCounter(String countc){
		counter += Integer.parseInt(countc);
	}

	public int getCounter(){
		return counter;
	}

}
