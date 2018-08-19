package com.icode.netty.fileserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.icode.netty.fileserver.channelfuture.CloseChannelFuture;
import com.icode.netty.fileserver.exception.RequestHandleException;
import com.icode.netty.fileserver.handler.AbstractRequestHandler;
import com.icode.netty.fileserver.handler.UploadHandler;
import com.icode.netty.fileserver.utils.FileServerUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class FileServerRequestController extends ChannelInboundHandlerAdapter {
	public static AttributeKey<Long> ACTIVE_TIME = AttributeKey.newInstance("IN_ACTIVE_TIME");
	private Map<Pattern,List<AbstractRequestHandler>> requestHandlerMapping;
	private List<AbstractRequestHandler> handlerList;
	private String uri;
	public FileServerRequestController() {
		System.out.println("new FileServerRequestController");
		Pattern uploadRegex = Pattern.compile("^/upload/.*");
		Pattern downloadRegex = Pattern.compile("^/download/.*");
		requestHandlerMapping = new HashMap<>();
		List<AbstractRequestHandler> uploadRequestHandlers = new ArrayList<AbstractRequestHandler>();
		uploadRequestHandlers.add(new UploadHandler());
		requestHandlerMapping.put(uploadRegex, uploadRequestHandlers);
	}
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			channelIdle(ctx,(IdleStateEvent)evt);
		}else {
			super.userEventTriggered(ctx, evt);
		}
	}
	/**
	 * 处理连接超时
	 * @param ctx
	 * @param evt
	 */
	private void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
		System.out.println("连接超时："+evt);
		ctx.channel().close();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			handleHttpRequest(ctx,(HttpRequest)msg);
		}else if(msg instanceof HttpContent){
			handleHttpContent(ctx,(HttpContent)msg);
		}else {
			System.out.println("channelRead-->msg-->"+msg);
		}
	}
	
	private void handleHttpContent(ChannelHandlerContext ctx, HttpContent httpContent) throws RequestHandleException {
		if(handlerList!=null) {
			for (AbstractRequestHandler requestHandler : handlerList) {
				requestHandler.doHandleHttpContent(ctx, httpContent);
			}
		}
	}
	private void handleHttpRequest(ChannelHandlerContext ctx,HttpRequest msg) throws RequestHandleException {
		if(msg instanceof HttpRequest) {
			DefaultHttpRequest httpRequest = (DefaultHttpRequest)msg;
			DecoderResult decoderResult = httpRequest.decoderResult();
			if(decoderResult.isSuccess()) {
				uri = httpRequest.uri();
				handlerList= getHandlerList(uri);
				if(handlerList!=null) {
					for (AbstractRequestHandler requestHandler : handlerList) {
						requestHandler.doHandleHttpRequest(ctx, httpRequest);
					}
				}else {
					HttpResponse badGetwayResponse = FileServerUtils.simpleHttpResponse(HttpResponseStatus.BAD_GATEWAY,"无效的服务地址");
					ctx.writeAndFlush(badGetwayResponse).addListener(ChannelFutureListener.CLOSE);
				}
			}else {
				decoderResult.cause().printStackTrace();
			}
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	public List<AbstractRequestHandler> getHandlerList(String uri){
		for (Entry<Pattern, List<AbstractRequestHandler>> entrySet : requestHandlerMapping.entrySet()) {
			Pattern key = entrySet.getKey();
			if(key.matcher(uri).matches()) {
				return entrySet.getValue();
			}
		}
		return null;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Attribute<Long> attr = channel.attr(ACTIVE_TIME);
		attr.set(System.currentTimeMillis());
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Attribute<Long> attr = channel.attr(ACTIVE_TIME);
		Long activeTime = attr.get();
		System.out.println("耗时："+(System.currentTimeMillis()-activeTime)/1000+"s");
		super.channelInactive(ctx);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("发送-->处理时发生错误");
		ctx.writeAndFlush(FileServerUtils.simpleHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, "处理时发生错误"))
		.addListener(new CloseChannelFuture());
		cause.printStackTrace();
	}
	
}
