package names;

import components.Main.Carrier;

public class NJ_Amerihealth_Name extends Product_Name{
	
	String lower_name = original_name.toLowerCase();
	

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
	}
	
	public enum Plan_Attribute {
		NTL, Preferred, Value, National, AH
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
	

}
