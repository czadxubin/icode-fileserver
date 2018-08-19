package com.techouse.tcp.fileserver.codec;

import com.techouse.tcp.fileserver.codec.decoder.FilerServerDataDecoder;
import com.techouse.tcp.fileserver.codec.encoder.FilerServerDataEncoder;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * 
* Copyright: Copyright (c) 2018 www.techouse.top
* 
* @ClassName: TechouseFileServerCodec.java
* @Description: 
*			文件服务器数据编解码器
* @version: v1.0.0
* @author: 许宝众
* @date: 2018年8月19日 下午3:09:20 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月19日       许宝众          			v1.0.0              	 首次添加
 */
public class TechouseFileServerCodec extends CombinedChannelDuplexHandler<FilerServerDataDecoder,FilerServerDataEncoder>{
	public TechouseFileServerCodec() {
		super(new FilerServerDataDecoder(), new FilerServerDataEncoder());
	}
}
