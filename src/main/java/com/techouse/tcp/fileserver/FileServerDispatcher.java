package com.techouse.tcp.fileserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.handler.FileClientAuthHandler;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.client_auth.ClientAuthReqBody;

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
		System.out.println("客户端连接进入...安装认证授权Handler");
		//添加 认证Handler
		ctx.pipeline().addAfter("fileServerDispatcher", "fileClientAuthHandler", new FileClientAuthHandler());
		super.channelActive(ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String) {
			System.out.println("接收到文本数据："+msg);
			Object obj = null;
			boolean isHandleable = false;
			try {
				JSONObject reqJson = JSON.parseObject((String)msg);
				TechouseRequestHeader reqHeader = reqJson.getObject(ConstantsUtils.REQUEST_HEADER_KEY,TechouseRequestHeader.class);
				if(reqHeader!=null) {
					Class<?> jsonClass = ConstantsUtils.REQUEST_TYPE_MAP.get(reqHeader.getReq_type());
					if(jsonClass!=null) {
						String bodyString = reqJson.getString(ConstantsUtils.REQUEST_BODY_KEY);
						obj = JSON.parseObject(bodyString, jsonClass);
						//reqHeader放入ctx
						ctx.channel().attr(ConstantsUtils.FileServerAttr.RREQUEST_HEADER_ATTR_KEY).set(reqHeader);
					}
				}
				//处理后的请求可转换为POJO即为可处理请求
				isHandleable = obj!=null;
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("无法理解的客户请求");
			}finally {
				if(isHandleable) {
					ctx.fireChannelRead(obj);
				}
			}
		}else if(msg instanceof TransBinaryData) {
			System.out.println("接收到二进制数据请求："+msg);
			ctx.fireChannelRead(msg);
		}else {
			System.out.println("?????-->未知类型的请求-->msg-->"+msg);
		}
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}
}
