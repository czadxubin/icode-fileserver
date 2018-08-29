package com.techouse.tcp.fileserver;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.TechouseResponse;
import com.techouse.tcp.fileserver.dto.TechouseResponseHeader;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.event.ClientOfflineNotifyEvent;
import com.techouse.tcp.fileserver.event.CloseConnectionEvent;
import com.techouse.tcp.fileserver.event.FileRecvFinishedEvent;
import com.techouse.tcp.fileserver.handler.FileClientAuthHandler;
import com.techouse.tcp.fileserver.utils.CommonUtils;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.server_notify.ServerNotifyEvent;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateEvent;

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
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event == IdleStateEvent.READER_IDLE_STATE_EVENT||
				event == IdleStateEvent.WRITER_IDLE_STATE_EVENT||
				event == IdleStateEvent.ALL_IDLE_STATE_EVENT) {
				TechouseResponse<?> respone = CommonUtils.generateGenericReponse(UUID.randomUUID().toString(), ConstantsUtils.ReqTypeConstants.SERVER_NOTIFY_NO_REPLY, "0", "客户端连接超时",ServerNotifyEvent.CLOSE_CONNECTION_EVENT);
				ctx.writeAndFlush(respone).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						System.out.println("数据发送完毕后30s服务器主动关闭连接");
						//数据发送完毕后30s服务器主动关闭连接
						future.channel().eventLoop().schedule(new Runnable() {
							@Override
							public void run() {
								future.channel().close();
							}
						}, 30, TimeUnit.SECONDS);
					}
				});
			}
			//服务器要求关闭客户端连接，并会在30秒后主动关闭
		}else if(evt instanceof CloseConnectionEvent){
			ctx.channel().close();
			//客户端接收到文件传输完毕事件
		}else if(evt instanceof FileRecvFinishedEvent){	
			ClientOfflineNotifyEvent resBody = new ClientOfflineNotifyEvent();
			TechouseResponse<ClientOfflineNotifyEvent> response = CommonUtils.generateGenericReponse(UUID.randomUUID().toString(), ConstantsUtils.ReqTypeConstants.SERVER_NOTIFY_NO_REPLY, "1", "下线通知", resBody );
			ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						Date finishedTime = new Date();
						Date oneLineTime = future.channel().pipeline().channel().attr(ConstantsUtils.FileServerAttr.CLIENT_ONLINE_TIME).get();
						System.out.println(finishedTime.getTime()-oneLineTime.getTime()+"ms");
						future.channel().close();
					}else {
						future.channel().close();
					}
				}
			});
		}else{
			super.userEventTriggered(ctx, evt);
		}
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//添加 认证Handler
		ChannelPipeline pipeline = ctx.pipeline();
		pipeline.channel().attr(ConstantsUtils.FileServerAttr.CLIENT_ONLINE_TIME).set(new Date());
		pipeline.addAfter("fileServerDispatcher", "fileClientAuthHandler", new FileClientAuthHandler());
		super.channelActive(ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String) {
//			System.out.println("接收到文本数据："+msg);
			Object obj = null;
			boolean isHandleable = false;
			try {
				JSONObject reqJson = JSON.parseObject((String)msg);
				boolean isReq = reqJson.getString(ConstantsUtils.REQUEST_HEADER_KEY)!=null;
				if(isReq) {
					TechouseRequestHeader reqHeader = reqJson.getObject(ConstantsUtils.REQUEST_HEADER_KEY,TechouseRequestHeader.class);
					if(reqHeader!=null) {
						String req_type = reqHeader.getReq_type();
						Class<?> jsonClass = ConstantsUtils.REQUEST_TYPE_MAP.get(req_type);
						if(jsonClass!=null) {
							String bodyString = reqJson.getString(ConstantsUtils.REQUEST_BODY_KEY);
							obj = JSON.parseObject(bodyString, jsonClass);
							//reqHeader放入ctx
							ctx.channel().attr(ConstantsUtils.FileServerAttr.RREQUEST_HEADER_ATTR_KEY).set(reqHeader);
						}
					}
					//处理后的请求可转换为POJO即为可处理请求
					isHandleable = obj!=null;
				}else {
					boolean isRes = reqJson.getString(ConstantsUtils.RESPONSE_HEADER_KEY)!=null;
					if(isRes) {
						TechouseResponseHeader resHeader = reqJson.getObject(ConstantsUtils.RESPONSE_HEADER_KEY,TechouseResponseHeader.class);
						System.out.println(resHeader.getRes_msg());
						String res_type = resHeader.getRes_type();
						if(ConstantsUtils.ReqTypeConstants.SERVER_NOTIFY_NO_REPLY.equals(res_type)) {
							String bodyString = reqJson.getString(ConstantsUtils.RESPONSE_BODY_KEY);
							ServerNotifyEvent serverNotifyEvent = JSON.parseObject(bodyString, ServerNotifyEvent.class);
							if(serverNotifyEvent!=null) {
								String eventClass = serverNotifyEvent.getEventClass();
								if(StringUtils.isNotBlank(eventClass)) {
									try {
										Class<?> clazz = Class.forName(eventClass);
										Object evt = clazz.newInstance();
										//触发自身事件
										this.userEventTriggered(ctx, evt);
									}catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}else {
							ctx.fireChannelRead(msg);
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				System.err.println("无法理解的客户请求");
			}finally {
				if(isHandleable) {
					ctx.fireChannelRead(obj);
				}
			}
		}else if(msg instanceof TransBinaryData) {
//			System.out.println("接收到二进制数据请求："+msg);
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
