package com.techouse.tcp.fileserver.dto;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.techouse.tcp.fileserver.dto.trans.TransTextData;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadReqBody;

public class TechouseRequest<T> extends TransTextData{
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
	
	
	
	public static void main(String[] args) {
		TechouseRequestHeader reqHeader = new TechouseRequestHeader();
		reqHeader.setFlow_time(new Date());
		TechouseRequest<FileUploadReqBody> request = new TechouseRequest<FileUploadReqBody>(reqHeader);
		FileUploadReqBody reqBody = new FileUploadReqBody();
		reqBody.setDir_path("/222//333");
		request.setReq_b(reqBody );
		System.out.println(JSON.toJSONString(request));
	}
	
	
}
