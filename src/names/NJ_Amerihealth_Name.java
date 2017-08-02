package names;

import java.util.HashMap;
import java.util.Map;

import components.Main.Carrier;
import names.Product_Name.Metal;

public class NJ_Amerihealth_Name extends Product_Name{
	
	String lower_name = original_name.toLowerCase();
	String[] tokens = lower_name.split("\\s");

	public final Plan_Attribute att;
	

	public NJ_Amerihealth_Name(String original_name) {
		super(original_name);
		this.carrier = getCarrier();
		this.state = getState();
		this.metal = getMetal();
		this.plan = getPlan();
		this.rx_copay = getRxCopay();
		this.deductible = getDeductible();
		this.coinsurance = getCoinsurance();
		this.isPlusPlan = hasPlusAttribute();
		this.att = getPlanAttribute();
	}
	
	public final HashMap<Plan_Attribute, String[]> planTypeAbbrevMap = new HashMap<Plan_Attribute, String[]>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Plan_Attribute.National, new String[] {"ntl"});
			put(Plan_Attribute.Preferred, new String[] {"pfd, prefd"});
			put(Plan_Attribute.Value, new String[] {"val"});
		}
	};
	
	public enum Plan_Attribute {
		Preferred, Value, National, AH, None
	}
	
	
	public Carrier getCarrier(){
		for(Carrier c : carriers){
			if(lower_name.contains(c.toString().toLowerCase())){
				return c;
			}
		}
		return Carrier.NONE;
	}
	
	public State getState(){
		return null;
	}
	
	public String getRxCopay(){
		for(String s : tokens){
			if(s.contains("rx")){
				
			}
				
		}
		return "";
	}
	
	public String getDeductible(){
		return "";
	}
	
	public String getCoinsurance(){
		return "";
	}
	
	public boolean hasPlusAttribute(){
		if(lower_name.contains("+") || lower_name.contains("plus")){
			return true;
		}
		return false;
	}
	
	public boolean hasHSAAttribute(){
		String[] tokens = lower_name.split("\\s");
		for(String s : tokens){
			if(s.equals("hsa")){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasAdvantageAttribute(){
		for(String s : tokens){
			if(s.equals("hsa")){
				return true;
			}
		}
		return false;
	}
	
	public Plan_Attribute getPlanAttribute(){
		for(String s : tokens){
			for(Plan_Attribute plan_att : Plan_Attribute.values()){
				if(s.equals(plan_att.toString().toLowerCase())){
					return plan_att;
				}
				for(String abbrev : planTypeAbbrevMap.get(plan_att)){
					if(s.equals(abbrev)){
						return plan_att;
					}
				}
			}
		}
		return Plan_Attribute.None;
	}
	

}
