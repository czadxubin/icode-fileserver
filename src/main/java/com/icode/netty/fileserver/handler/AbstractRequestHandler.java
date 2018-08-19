package com.icode.netty.fileserver.handler;

import com.icode.netty.fileserver.exception.RequestHandleException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

public abstract class AbstractRequestHandler {
	public abstract void doHandleHttpRequest(ChannelHandlerContext ctx,HttpRequest httpRequest) throws RequestHandleException;
	public abstract void doHandleHttpContent(ChannelHandlerContext ctx,HttpContent httpContent) throws RequestHandleException;
}
