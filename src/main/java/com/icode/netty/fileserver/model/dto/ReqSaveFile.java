package com.icode.netty.fileserver.model.dto;

public class ReqSaveFile {
	/** 文件名称 **/
	private String fileName;
	/** 文件存储路径 **/
	private String filePath;
	/** 是否覆盖 **/
	private boolean isOverwrite;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public boolean isOverwrite() {
		return isOverwrite;
	}
	public void setOverwrite(boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}
}
