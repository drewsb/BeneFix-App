package components;

import components.Main.Carrier;

public class ProductName {

	Metal metal;
	
	Network network;
	
	Carrier carrier;
	
	
	enum Metal {
		Bronze, Silver, Gold, Platinum
	}
	
	enum Network {
		Bronze, Silver, Gold, Platinum
	}

	public ProductName(Metal metal, Network network) {
		super();
		this.metal = metal;
		this.network = network;
	}
	
	
	
	
	
	
}
