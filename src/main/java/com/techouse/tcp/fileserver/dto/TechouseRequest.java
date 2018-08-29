package com.techouse.tcp.fileserver.dto;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.techouse.tcp.fileserver.dto.trans.ITechouseTransData;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadReqBody;

public class TechouseRequest<T> implements ITechouseTransData{
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
		return TechouseTransDataType.TEXT_JSON_DATA;
	}
}
