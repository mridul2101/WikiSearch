package wikisearch.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import wikisearch.merger.Merger;
import wikisearch.parser.ParsingXML;
import wikisearch.secondary.SecondaryIndex;

public class IndexMain {
	public static void main(String args[])
	{
		//String uri = "E:\\Dropbox\\dump\\sample\\sample.xml";
		//String uri1 = "E:\\Dropbox\\dump\\sample";
		String uri = new String();
		String uri1 = new String();
		try{
			uri = args[0];
			uri1 = args[1];
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("EXCEPTION : Command Line Arguments are Missing.");
			return;
		}
		
		ParsingXML parse = new ParsingXML();
		long fnum = 0;
//		
		double startTime = System.currentTimeMillis();
//		
		fnum = parse.parseMethod(uri,uri1);
		
	//	System.out.println(fnum);
	    //System.out.println("MERGING of " + fnum + " FILES STARTS....");
//		
		if(fnum > 0){
			Merger mg = new Merger();
			mg.mergingFunc(uri1, fnum);
		}
////		
		//SECONDARY INDEX
		System.out.println("Mergin Compeletes.!!!!   Secondary Index starts....");
		SecondaryIndex secInd = new SecondaryIndex();
		secInd.createSecIndex(uri1,1);	//Secondary Index of main Index
		secInd.createSecIndex(uri1,2);	//Secondary Index of mapping file Index
		
		
		double endTime   = System.currentTimeMillis();
		double totalTime = endTime - startTime;
		System.out.println("RUNNING TIME FOR INDEX CREATION : "+ totalTime/1000 + " sec");
		//deleteDirectory(new File(uri1+"\\FILE"));
		//System.out.println(true);
		return;
		
	}
}
