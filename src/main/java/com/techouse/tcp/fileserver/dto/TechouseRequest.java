package com.techouse.tcp.fileserver.dto;

import com.techouse.tcp.fileserver.dto.trans.TechouseTransData;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;

public class TechouseRequest<T> implements TechouseTransData {
	private TechouseRequestHeader req_h;
	private T req_b;

	public TechouseRequest(TechouseRequestHeader req_h, T req_b) {
		this.req_h = req_h;
		this.req_b = req_b;
	}

	public TechouseRequest(TechouseRequestHeader req_h) {
		this(req_h,null);
	}

	public TechouseRequestHeader getReq_h() {
		return req_h;
	}

	public void setReq_h(TechouseRequestHeader req_h) {
		this.req_h = req_h;
	}

	public T getReq_b() {
		return req_b;
	}

	public void setReq_b(T req_b) {
		this.req_b = req_b;
	}

	@Override
	public TechouseTransDataType whatTransDataType() {
		return TechouseTransDataType.JSON;
	}
}
