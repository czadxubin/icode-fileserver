package com.techouse.tcp.fileserver.dto.trans;

/**
 * 
* Copyright: Copyright (c) 2018 www.techouse.top
* 
* @ClassName: TechouseTransData.java
* @Description: 
*				基础传输数据
* @version: v1.0.0
* @author: 许宝众
* @date: 2018年8月19日 下午3:30:55 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月19日       许宝众          			v1.0.0              	 首次添加
 */
public interface TechouseTransData {
	/**数据字段类型：文本JSON**/
	public final short DATA_TYPE_JSON = 0;
	/**数据字段类型：数据**/
	public final short DATA_TYPE_BINARY = 1;
	
	/**分块类型：-1：无分块**/
	public final byte CHUNK_TYPE_NO_CHUNKED = -1;
	/**分块类型：0：分块开始**/
	public final byte CHUNK_TYPE_START = 0;
	/**分块类型：1：分块中间**/
	public final byte CHUNK_TYPE_CONTIUNE = 1;
	/**分块类型：2：分块结束**/
	public final byte CHUNK_TYPE_END = 2;
	
	/**数据长度字段标识的字节数**/
	public final int LENGTH_FILED_LENGTH = 4;
	/**数据类型字段标识字节数**/
	public final int TYPE_FIELD_LENGTH = 2;
	/**数据分块字段标识字节数**/
	public final int CHUNK_FIELD_LENGTH = 1;
	/**数据分块大小**/
	public final int CHUNKED_SIZE = 8*1024;
	
	public TechouseTransDataType getTransDataType();
}
