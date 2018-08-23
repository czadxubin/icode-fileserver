package com.techouse.tcp.fileserver.dto.trans;

import java.util.Arrays;

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
public class TransBinaryData extends TransData implements TechouseTransData{
	public TransBinaryData(byte chunkType, byte[] data) {
		super(data.length,TechouseTransDataType.BINARY,chunkType,data);
	}

	@Override
	public TechouseTransDataType whatTransDataType() {
		return TechouseTransDataType.BINARY;
	}

	@Override
	public String toString() {
		String type = null;
		if(isFirst()) {
			type="头部";
		}else if(isContinue()) {
			type="中间";
		}else if(isLast()) {
			type="尾部";
		}
		return "[二进制数据包:"+type+"]-[数据长度："+getData().length+"]";
	}
	
}
