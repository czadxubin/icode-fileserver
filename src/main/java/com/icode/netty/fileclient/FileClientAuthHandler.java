package com.icode.netty.fileclient;

import com.alibaba.fastjson.JSON;
import com.icode.netty.fileserver.model.ClientInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
public class FileClientAuthHandler extends SimpleChannelInboundHandler<String>{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//客户端连接成功准备进行认证操作
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setClientId("clientId");
		clientInfo.setClientSecret("clientSecret");
		ctx.writeAndFlush("auth_request:"+JSON.toJSON(clientInfo)).sync();
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		if("auth success".equals(msg)) {
			System.out.println("客户端认证完成！！");
			ctx.channel().pipeline().remove(FileClientAuthHandler.class);
		}else {
			ReferenceCountUtil.release(msg);
		}
	}

}
