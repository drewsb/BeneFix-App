package names;

import components.Main.Carrier;

public class Product_Name {
	
	public final String original_name;
	public final State state;
	public final Metal metal;
	public final Plan plan;
	public final Carrier carrier;

	public Product_Name(String original_name, State state, Metal metal, Plan plan, Carrier carrier) {
		super();
		this.original_name = original_name;
		this.state = state;
		this.metal = metal;
		this.plan = plan;
		this.carrier = carrier;
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
	
	public static Plan getPlan(String s) {
		String str = s.toLowerCase().replaceAll("[\\s_]", "");
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
