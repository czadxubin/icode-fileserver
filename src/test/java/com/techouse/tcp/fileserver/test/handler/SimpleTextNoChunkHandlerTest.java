package com.techouse.tcp.fileserver.test.handler;

import java.io.InputStream;
import java.util.List;

import com.techouse.tcp.fileserver.dto.TechouseRequest;
import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleTextNoChunkHandlerTest extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("成功连接到服务器！！！");
		TechouseRequestHeader reqHeader = new TechouseRequestHeader();
		reqHeader.setReq_id("1234");
		reqHeader.setReq_type("hello");
		InputStream input = SimpleTextNoChunkHandlerTest.class.getResourceAsStream("/测试文本.txt");
		List<String> lines = org.apache.commons.io.IOUtils.readLines(input );
		StringBuffer sb = new StringBuffer();
		for (String line : lines) {
			sb.append(line+"\n");
		}
		TechouseRequest<String> reqData = new TechouseRequest<String>(reqHeader );
		reqData.setReq_b(sb.toString());
		ctx.writeAndFlush(reqData).addListener(ChannelFutureListener.CLOSE);
		super.channelActive(ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}
}