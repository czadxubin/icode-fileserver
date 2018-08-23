package com.techouse.tcp.fileserver.dto;

import com.techouse.tcp.fileserver.dto.trans.TechouseTransData;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;

public class TechouseResponse<T> implements TechouseTransData{
	private TechouseResponseHeader res_h;
	private T res_b;
	public TechouseResponseHeader getRes_h() {
		return res_h;
	}
	public void setRes_h(TechouseResponseHeader res_h) {
		this.res_h = res_h;
	}
	public T getRes_b() {
		return res_b;
	}
	public void setRes_b(T res_b) {
		this.res_b = res_b;
	}

	@Override
	public TechouseTransDataType whatTransDataType() {
		return TechouseTransDataType.JSON;
	}
}
