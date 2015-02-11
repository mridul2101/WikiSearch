package wikisearch.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore.LoadStoreParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import wikisearch.search.KeywordSearch;
import wikisearch.search.Phase2Search;

public class SearchMain {

	private static Scanner in;
		public static void main(String args[]){
		
		//String uri = "E:\\Dropbox\\dump\\sample\\finalindex";
		String uri;
		try{
			uri = args[0];
			System.out.print("LOADING....");
			Phase2Search search = new Phase2Search(uri);
			System.out.println("COMPLETED\n");
			
			String query;
			boolean flag = false;
			do{
			 in = new Scanner(System.in);
			 System.out.print("ENTER QUERY : ");
			 query = in.nextLine();
			//query = "roann indiana";
			//query = "imdb";
			 //query = "aalburg";
			 System.out.println("QUERY: " + query);
			double startTime = System.currentTimeMillis();
				search.searchFunc(query);
			double endTime   = System.currentTimeMillis();
			double totalTime = endTime - startTime;
			System.out.println("SEARCH TIME : "+ totalTime/1000 + " sec");
			
			 System.out.print("Want another Query (1 for YES) : ");
			 if(!(in.nextLine()).equalsIgnoreCase("1")){
				 flag = true;
			 }
			}while(flag == false);
//			
			System.out.println();
			}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("EXCEPTION : Command Line Arguments are Missing.");
		}
		catch(Exception e){
			System.out.println("EXCEPTION : Exception Occur.");
		}
		
	}	

}
