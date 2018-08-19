package com.techouse.tcp.fileserver.dto;

import com.techouse.tcp.fileserver.dto.trans.TechouseTransData;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;

public class TechouseResponse<T> implements TechouseTransData{
	private TechouseResponseHeader res_q;
	private T res_b;
	public TechouseResponseHeader getRes_q() {
		return res_q;
	}
	public void setRes_q(TechouseResponseHeader res_q) {
		this.res_q = res_q;
	}
	public T getRes_b() {
		return res_b;
	}
	public void setRes_b(T res_b) {
		this.res_b = res_b;
	}
	@Override
	public TechouseTransDataType getTransDataType() {
		return TechouseTransDataType.BINARY;
	}
}
