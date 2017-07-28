package components;

public class DentalPage implements Page{
	public String group;
	public String carrier;
	public int carrier_id;
	public String product_name;
	public String sic_level;
	public String start_date;
	public String end_date;
	public String states;
	public String group_rating_areas;
	public String zip_codes;
	public String contribution_type;
	public String minimum_enrolled;
	public String minimum_participation;
	public String class_I_diagnostic_preventive;
	public String class_II_basic;
	public String class_III_major;
	public String endodonitcs;
	public String periodontics;
	public String annual_max;
	public String office_visit_copay;
	public String deductible_ind_fam;
	public String orthodontics;
	public String orthodonitics_lifetime_maximum;
	public String waiting_period;
	public String rc_mac;
	public String one_tier;
	public String two_tier_e;
	public String two_tier_f;
	public String three_tier_e;
	public String three_tier_ed;
	public String three_tier_f;
	public String four_tier_e;
	public String four_tier_ea;
	public String four_tier_ec;
	public String four_tier_f;
	
	public DentalPage() {
		this.group = "";
		this.carrier = "";
		this.carrier_id = 0;
		product_name = "";
		sic_level = "";
		start_date = "";
		end_date = "";
		states = "";
		group_rating_areas = "";
		zip_codes = "";
		contribution_type = "";
		minimum_enrolled = "";
		minimum_participation = "";
		class_I_diagnostic_preventive = "";
		class_II_basic = "";
		class_III_major = "";
		endodonitcs = "";
		periodontics = "";
		annual_max = "";
		office_visit_copay = "0";
		deductible_ind_fam = "";
		orthodontics = "";
		orthodonitics_lifetime_maximum = "";
		waiting_period = "";
		rc_mac = "";
		one_tier = "";
		two_tier_e = "";
		two_tier_f = "";
		three_tier_e = "";
		three_tier_ed = "";
		three_tier_f = "";
		four_tier_e = "";
		four_tier_ea = "";
		four_tier_ec = "";
		four_tier_f = "";
	}
	
	@Override
	public void printPage() {
		System.out.println("Group: " + this.group);
		System.out.println("Carrier: " + this.carrier);
		System.out.println("Carrier ID: " + this.carrier_id);
		System.out.println("Product name: " + this.product_name);
		System.out.println("Sic level: " + this.sic_level);
		System.out.println("Start date: " + this.start_date);
		System.out.println("End date: " + this.end_date);
		System.out.println("States: " + this.states);
		System.out.println("Group rating areas: " + this.group_rating_areas);
		System.out.println("Zip codes: " + this.zip_codes);
		System.out.println("Contribution type: " + this.contribution_type);
		System.out.println("Minimum enrolled: " + this.minimum_enrolled);
		System.out.println("Minimum participation: " + this.minimum_participation);
		System.out.println("Class I Diagnostic & Preventative: " + this.class_I_diagnostic_preventive);
		System.out.println("Class II Basic: " + this.class_II_basic);
		System.out.println("Class II Major: " + this.class_III_major);
		System.out.println("Endodonitcs: " + this.endodonitcs);
		System.out.println("Periodontics: " + this.periodontics);
		System.out.println("Annual max: " + this.annual_max);
		System.out.println("Office visit copay: " + this.office_visit_copay);
		System.out.println("Deductible Individual/Family: " + this.deductible_ind_fam);
		System.out.println("Orthodontics: " + this.orthodontics);
		System.out.println("Orthodontics lifetime maximum: " + this.orthodonitics_lifetime_maximum);
		System.out.println("Waiting period: " + this.waiting_period);
		System.out.println("R&C/MAC: " + this.rc_mac);
		System.out.println("One Tier: " + this.one_tier);
		System.out.println("Two Tier E: " + this.two_tier_e);
		System.out.println("Two Tier F: " + this.two_tier_f);
		System.out.println("Three Tier E: " + this.three_tier_e);
		System.out.println("Three Tier ED: " + this.three_tier_ed);
		System.out.println("Three Tier F: " + this.three_tier_f);
		System.out.println("Four Tier E: " + this.four_tier_e);
		System.out.println("Four Tier EA: " + this.four_tier_ea);
		System.out.println("Four Tier EC: " + this.four_tier_ec);
		System.out.println("Four Tier F: " + this.four_tier_f);
	}
	
	public void format(){
		
	}
}
