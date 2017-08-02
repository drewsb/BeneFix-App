package names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import components.Main.Carrier;

public class Product_Name {
	
	public final String original_name;
	public final String[] name_tokens;
	public State state;
	public Metal metal;
	public Plan plan;
	public Carrier carrier;
	public String rx_copay;
	public String deductible;
	public String coinsurance;
	
	public boolean isHSAPlan;
	public boolean isPlusPlan;
	public boolean isAdvantagePlan;
	public boolean isOffExchangePlan;

	public final static ArrayList<State> states = new ArrayList<State>(Arrays.asList(State.values()));
	public final static ArrayList<Plan> plans = new ArrayList<Plan>(Arrays.asList(Plan.values()));
	public final static ArrayList<Metal> metals = new ArrayList<Metal>(Arrays.asList(Metal.values()));
	public final static ArrayList<Carrier> carriers = new ArrayList<Carrier>(Arrays.asList(Carrier.values()));
	
	
	public final HashMap<Metal, String[]> metalAbbrevMap = new HashMap<Metal, String[]>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Metal.Bronze, new String[] {"brz"});
			put(Metal.Silver, new String[] {"slv"});
			put(Metal.Gold, new String[] {"gld"});
			put(Metal.Platinum, new String[] {"plt"});
		}
	};
	
	
	public Product_Name(String original_name) {
		super();
		this.original_name = original_name;
		this.name_tokens = original_name.toLowerCase().split("\\s");
	}

	public enum State {
		PA, OH, NJ, CA
	}
	
	public enum Metal {
		Bronze, Silver, Gold, Platinum, None
	}
	
	public enum Plan {
		Choice_Plus, EPO, PPO, HMO, POS, HSA, QPOS, Savings_Plus, Wellspan_HNOption, LVHN_HNOption, None
	}
	
	@Override
	public String toString(){
		return original_name;
	}
	
	public Metal getMetal() {
		for(String t : name_tokens){
			for(Metal m : metals){
				if(t.equals(m.toString().toLowerCase())){
					return m;
				}
				for(String abbrev : metalAbbrevMap.get(m)){
					if(t.equals(abbrev)){
						return m;
					}
				}
			}
		}
		return Metal.None;
	}
	
	public Plan getPlan() {
		String str = original_name.toLowerCase().replaceAll("[\\s_]", "");
		for(Plan p : plans){
			if(str.contains(p.toString().toLowerCase())){
				return p;
			}
		}
		return Plan.None;
	}
	
	
	
}
