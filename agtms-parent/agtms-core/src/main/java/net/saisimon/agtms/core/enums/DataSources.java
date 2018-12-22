package net.saisimon.agtms.core.enums;

public enum DataSources {

	SESSION("session"), JPA("jpa"), MONGO("mongo"), RPC("rpc");
	
	private String source;
	
	DataSources(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
	
}
