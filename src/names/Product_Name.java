package names;

import components.Main.Carrier;

public class Product_Name {
	
	public final String original_name;
	public State state;
	public Metal metal;
	public Plan plan;
	public Carrier carrier;

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
		Choice_Plus, EPO, PPO, HMO, POS, HSA, QPOS, Savings_Plus, Wellspan_HNOption, LVHN_HNOption, None
	}
	
	@Override
	public String toString(){
		return original_name;
	}
	
	public static Metal getMetal(String s) {
		String str = s.toLowerCase().replaceAll("\\s", "");
		if (str.contains("bronze")) {
			return Metal.Bronze;
		} else if (str.contains("silver")) {
			return Metal.Silver;
		} else if (str.contains("gold")) {
			return Metal.Gold;
		} else if (str.contains("platinum")) {
			return Metal.Platinum;
		} else {
			return Metal.None;
		}
	}
}
