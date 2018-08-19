package com.techouse.tcp.fileserver;

import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
* Copyright: Copyright (c) 2018 www.techouse.top
* 
* @ClassName: FileServerDispatcher.java
* @Description: 
*			文件服务器调度器
* @version: v1.0.0
* @author: 许宝众
* @date: 2018年8月19日 下午7:41:38 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月19日       许宝众          			v1.0.0              	 首次添加
 */
public class FileServerDispatcher extends ChannelInboundHandlerAdapter{
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String) {
			int length = ((String)(msg)).length();
//			System.out.println("接收到文本请求："+length);
			if(length!=121544) {
				System.out.println("解析异常");
			}
		}else if(msg instanceof TransBinaryData) {
			System.out.println("接收到二进制数据请求：");
		}else {
			System.out.println("?????-->未知类型的请求-->msg-->"+msg);
		}
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}
}
