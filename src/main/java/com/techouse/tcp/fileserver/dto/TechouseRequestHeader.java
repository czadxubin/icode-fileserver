package com.techouse.tcp.fileserver.dto;

import java.util.Date;

public class TechouseRequestHeader {
	private String req_id;
	private String req_type;
	private Date flow_time;
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
	public Date getFlow_time() {
		return flow_time;
	}
	public void setFlow_time(Date flow_time) {
		this.flow_time = flow_time;
	}
}
