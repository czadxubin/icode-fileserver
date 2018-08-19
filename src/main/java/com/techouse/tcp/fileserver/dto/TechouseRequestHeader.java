package com.techouse.tcp.fileserver.dto;

public class TechouseRequestHeader {
	private String req_id;
	private String req_type;
	public String getReq_id() {
		return req_id;
	}
	public void setReq_id(String req_id) {
		this.req_id = req_id;
	}
	public String getReq_type() {
		return req_type;
	}
	public void setReq_type(String req_type) {
		this.req_type = req_type;
	}
}
