package com.techouse.tcp.fileserver.handler;

import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class BaseFileServerHanlder<T> extends ChannelInboundHandlerAdapter{
	private TypeParameterMatcher matcher;
	public BaseFileServerHanlder() {
		matcher = TypeParameterMatcher.find(this, BaseFileServerHanlder.class, "T");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(isHandled(msg)) {
			TechouseRequestHeader reqHeader = ctx.channel().attr(ConstantsUtils.FileServerAttr.RREQUEST_HEADER_ATTR_KEY).get();
			if(reqHeader!=null) {
				doHandleRequest(ctx,(T)msg,reqHeader);
			}else {
				System.out.println("当前请求不能处理：未访问到请求头信息，请检查！");
			}
		}else {
			doNotHandled(ctx,msg);
		}
	}
	/**
	 * 消息类型不属于泛型类型，预留子类覆盖，默认向下传递消息
	 * @param ctx
	 * @param msg
	 */
	protected void doNotHandled(ChannelHandlerContext ctx, Object msg) {
		ctx.fireChannelRead(msg);
	}
	/**
	 * 处理请求
	 * @param ctx
	 * @param msg
	 */
	protected abstract void doHandleRequest(ChannelHandlerContext ctx, T reqBody,TechouseRequestHeader reqHeader) throws Exception;
	/**
	 * 消息时候可以能处理
	 * @param msg
	 * @return
	 */
	protected boolean isHandled(Object msg) {
		return matcher.match(msg);
	}
}
