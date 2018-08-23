package com.techouse.tcp.fileserver.vo.file_upload;
/**
 * 文件上传请求VO
 * @author xiaobao
 *
 */
public class FileUploadReqBody {
	private String file_name ;
	private String dir_path;
	private long file_size;
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getDir_path() {
		return dir_path;
	}
	public void setDir_path(String dir_path) {
		this.dir_path = dir_path;
	}
	public long getFile_size() {
		return file_size;
	}
	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}
}
