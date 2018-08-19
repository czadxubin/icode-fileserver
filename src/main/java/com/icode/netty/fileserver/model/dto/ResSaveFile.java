package com.icode.netty.fileserver.model.dto;

public class ResSaveFile {
	/**100成功，否则失败**/
	private String resCode;
	/**错误消息**/
	private String errMsg;
	/**保存成功时返回真实保存路径**/
	private String realPath;
	
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getRealPath() {
		return realPath;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
}