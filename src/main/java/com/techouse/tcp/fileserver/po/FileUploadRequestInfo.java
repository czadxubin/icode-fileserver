package com.techouse.tcp.fileserver.po;

import java.io.File;

public class FileUploadRequestInfo {
	/**文件上传编号**/
	private Integer upload_id;
	/**上传文件对象信息**/
	private File file;
	/**锁文件对象信息**/
	private File lockFile;
	public Integer getUpload_id() {
		return upload_id;
	}
	public void setUpload_id(Integer upload_id) {
		this.upload_id = upload_id;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public File getLockFile() {
		return lockFile;
	}
	public void setLockFile(File lockFile) {
		this.lockFile = lockFile;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (upload_id ^ (upload_id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileUploadRequestInfo other = (FileUploadRequestInfo) obj;
		if (upload_id != other.upload_id)
			return false;
		return true;
	}
}
