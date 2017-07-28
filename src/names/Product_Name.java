package names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

import components.Main.Carrier;

public class Product_Name {
	
	public final String original_name;
	public State state;
	public Metal metal;
	public Plan plan;
	public Carrier carrier;
	public String rx_copay;
	public String deductible;
	public String coinsurance;
	public boolean isPlusPlan;

	public final static ArrayList<State> states = new ArrayList<State>(Arrays.asList(State.values()));
	public final static ArrayList<Plan> plans = new ArrayList<Plan>(Arrays.asList(Plan.values()));
	public final static ArrayList<Metal> metals = new ArrayList<Metal>(Arrays.asList(Metal.values()));
	public final static ArrayList<Carrier> carriers = new ArrayList<Carrier>(Arrays.asList(Carrier.values()));
	
	
	public final HashMap<String[], Metal> metalAbbrevMap = new HashMap<String[], Metal>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(new String[] {"brz"}, Metal.Bronze);
			put(new String[] {"slv"}, Metal.Silver);
			put(new String[] {"gld"}, Metal.Gold);
			put(new String[] {"plt"}, Metal.Platinum);
		}
	};
	
	
	public Product_Name(String original_name) {
		super();
		this.original_name = original_name;
	}

	public enum State {
		PA, OH, NJ, CA
	}
	
	public enum Metal {
		Bronze, Silver, Gold, Platinum, None
	}
	
	public enum Plan {
		Choice_Plus, EPO, PPO, HMO, POS, HSA, QPOS, Savings_Plus, Wekkspan_HNOption, LVHN_HNOption, None
	}
	
	@Override
	public String toString(){
		return original_name;
	}
	
	public Metal getMetal() {
		String str = original_name.toLowerCase().replaceAll("\\s", "");
		for(Metal m : metals){
			if(str.contains(m.toString())){
				return m;
			}
		}
		for(Map.Entry<String[], Metal> entry : metalAbbrevMap.entrySet()){
			for(String abb : entry.getKey()){
				if(str.contains(abb)){
					return entry.getValue();
				}
			}
		}
		return Metal.None;
	}
	
	public Plan getPlan() {
		String str = original_name.toLowerCase().replaceAll("[\\s_]", "");
		if (str.contains("choiceplus")) {
			return Plan.Choice_Plus;
		} else if (str.contains("epo")) {
			return Plan.EPO;
		} else if (str.contains("ppo")) {
			return Plan.PPO;
		} else if (str.contains("hmo")) {
			return Plan.HMO;
		} else if (str.contains("hsa")) {
			return Plan.HSA;
		} else if (str.contains("qpos")) {
			return Plan.QPOS;
		} else if (str.contains("savingsplus")) {
			return Plan.Savings_Plus;
		} else if (str.contains("wekkspanhnoption")) {
			return Plan.Wekkspan_HNOption;
		} else if (str.contains("lvhnhnoption")) { //Add more to this
			return Plan.LVHN_HNOption;
		} else {
			return Plan.None;
		}
	}
	
}
