package pa;

import java.util.HashMap;
import java.util.Map;


/**
 * Page class for a PDF page. Holds necessary data to populate an excel sheet. 
 */
public class PA_UPMC_Page {
	String start_date;
	String end_date;
	String pharmacy_rider;
	String rating_area_id;
	String plan_code;
	String group_rating_area;
	String plan_name;
	String state;
	HashMap<String,Double> non_tobacco_dict;
	HashMap<String,Double> tobacco_dict;
	int page;
	
	public PA_UPMC_Page(String s_date, String e_date, String rider, String code, int p, String rating, String name, String s, HashMap<String,Double> non_tobacco_rates,  HashMap<String,Double> tobacco_rates){
		start_date = s_date;
		end_date = e_date;
		pharmacy_rider = rider;
		plan_code = code;
		group_rating_area = rating;
		plan_name = name;
		state = s;
		non_tobacco_dict = non_tobacco_rates;
		tobacco_dict = tobacco_rates;
		page = p;
	}
	
	/*
	 * Print method used for debugging purposes. 
	 */
	public void printPage(){
		System.out.printf("Start date: %s\n", this.start_date);
		System.out.printf("End date: %s\n", this.end_date);
		System.out.printf("Page no.: %d\n", this.page);
		System.out.printf("Rating Area: %s\n", this.group_rating_area);
		System.out.printf("Plan Code: %s\n", this.plan_code);
		System.out.printf("Plan Name: %s\n", this.plan_name);
		System.out.printf("Pharmacy Rider: %s\n", this.pharmacy_rider);
		System.out.printf("State: %s\n", this.state);
		System.out.printf("Non-Tobacco Rates\n");
		for( Map.Entry<String, Double> key : this.non_tobacco_dict.entrySet()){
			System.out.printf("Key: %s  |  Value: %.2f\n", key.getKey(), key.getValue());
		}
		System.out.printf("Tobacco Rates\n");
		for( Map.Entry<String, Double> key : this.tobacco_dict.entrySet()){
			System.out.printf("Key: %s  |  Value: %.2f\n", key.getKey(), key.getValue());
		}


	}
}
