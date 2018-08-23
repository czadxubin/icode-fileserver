package com.techouse.tcp.fileserver.vo.client_auth;

public class ClientAuthReqBody {
	private String client_id;
	private String client_secret;
	private String root_path;
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	public String getRoot_path() {
		return root_path;
	}
	public void setRoot_path(String root_path) {
		this.root_path = root_path;
	}
	@Override
	public String toString() {
		return "ClientAuthReqBody [client_id=" + client_id + ", client_secret=" + client_secret + ", root_path="
				+ root_path + "]";
	}
	
}
