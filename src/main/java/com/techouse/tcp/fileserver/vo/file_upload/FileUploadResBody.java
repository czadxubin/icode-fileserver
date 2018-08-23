package com.techouse.tcp.fileserver.vo.file_upload;
/**
 * 文件上传请求VO
 * @author xiaobao
 *
 */
public class FileUploadResBody {
	/**响应客户端一个上传ID**/
	private Integer upload_id;

	public Integer getUpload_id() {
		return upload_id;
	}

	public void setUpload_id(Integer upload_id) {
		this.upload_id = upload_id;
	}
}
