package pa;

import java.util.HashMap;
import java.util.Map;


/**
 * Page class for a PDF page. Holds necessary data to populate an excel sheet. 
 */
public class PA_Aetna_Page {
	String start_date;
	String end_date;
	int page;
	String rating_area;
	String plan_id;
	String plan_name;
	String state;
	HashMap<String,Double> age_dict;
	
	
	public PA_Aetna_Page(String s_date, String e_date, int p, String rating, String id, String name, String s, HashMap<String,Double> dict){
		start_date = s_date;
		end_date = e_date;
		page = p;
		rating_area = rating;
		plan_id = id;
		plan_name = name;
		state = s;
		age_dict = dict;
	}
	
	/*
	 * Print method used for debugging purposes. 
	 */
	public void printPage(){
		System.out.printf("Start date: %s\n", this.start_date);
		System.out.printf("End date: %s\n", this.end_date);
		System.out.printf("Page no.: %d\n", this.page);
		System.out.printf("Rating Area: %s\n", this.rating_area);
		System.out.printf("Plan ID: %s\n", this.plan_id);
		System.out.printf("Plan Name: %s\n", this.plan_name);
		System.out.printf("State: %s\n", this.state);
		for( Map.Entry<String, Double> key : this.age_dict.entrySet()){
			System.out.printf("Key: %s  |  Value: %.2f\n", key.getKey(), key.getValue());
		}



	}
}
