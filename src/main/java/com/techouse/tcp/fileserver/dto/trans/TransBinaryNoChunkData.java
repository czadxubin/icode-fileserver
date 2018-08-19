package com.techouse.tcp.fileserver.dto.trans;
import static com.techouse.tcp.fileserver.dto.trans.TechouseTransData.*;
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
public class TransBinaryNoChunkData extends  TransBinaryData{
	public TransBinaryNoChunkData( byte[] data) {
		super(CHUNK_TYPE_NO_CHUNKED,data);
	}
}
