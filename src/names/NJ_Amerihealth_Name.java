package names;

import components.Main.Carrier;

public class NJ_Amerihealth_Name extends Product_Name{
	

	public NJ_Amerihealth_Name(String original_name) {
		super(original_name);
		this.carrier = getCarrier();
		this.state = getState();
		this.metal = getMetal();
		this.plan = getPlan();
		this.rx_copay = getRxCopay();
		this.deductible = getDeductible();
		this.coinsurance = getCoinsurance();
	}
	
	
	public Carrier getCarrier(){
		for(Carrier c : carriers){
			if(original_name.contains(c.toString())){
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
	

}
