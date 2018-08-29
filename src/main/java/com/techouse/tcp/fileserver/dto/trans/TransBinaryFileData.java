package com.techouse.tcp.fileserver.dto.trans;

import java.io.File;

/**
 *
* Copyright: Copyright (c) 2018 www.techouse.top
* 
* @ClassName: TransBinaryData.java
* @Description: 
* 			二进制传输数据
* @version: v1.0.0
* @author: 许宝众
* @date: 2018年8月19日 下午6:54:48 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月19日       许宝众          			v1.0.0              	 首次添加
 */
public class TransBinaryFileData implements ITechouseTransData{
	/**将要上传的文件对象**/
	private File file;
	public TransBinaryFileData(File file) {
		this.file = file;
	}
	@Override
	public TechouseTransDataType whatTransDataType() {
		return TechouseTransDataType.BINARY_FILE_DATA;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
