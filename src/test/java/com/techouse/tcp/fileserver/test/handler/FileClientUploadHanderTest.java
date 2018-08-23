package com.techouse.tcp.fileserver.test.handler;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.techouse.tcp.fileserver.dto.TechouseRequest;
import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.TechouseResponseHeader;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.client_auth.ClientAuthReqBody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FileClientUploadHanderTest extends ChannelInboundHandlerAdapter{
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接到文件服务器，准备认证...");
		TechouseRequestHeader reqHeader = new TechouseRequestHeader();
		reqHeader.setReq_id("1234");
		reqHeader.setReq_type(ConstantsUtils.ReqTypeConstants.CLIENT_AUTH);
		TechouseRequest<ClientAuthReqBody> request = new TechouseRequest<ClientAuthReqBody>(reqHeader);
		ClientAuthReqBody reqBody = new ClientAuthReqBody();
		reqBody.setClient_id("client_id");
		reqBody.setClient_secret("client_secret");
		reqBody.setRoot_path("/test/upload");
		request.setReq_b(reqBody);
		ctx.writeAndFlush(request);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String ) {
			String message = (String) msg;
			JSONObject resJson = JSON.parseObject(message);
			String resHeaderStr = resJson.getString(ConstantsUtils.RESPONSE_HEADER_KEY);
			if(StringUtils.isNotBlank(resHeaderStr)) {
				TechouseResponseHeader resHeader = JSON.parseObject(resHeaderStr,TechouseResponseHeader.class);
				String res_code = resHeader.getRes_code();
				if("1".equals(res_code)) {
					System.out.println("认证成功");
					//发送
				}else {
					String res_msg = resHeader.getRes_msg();
					System.out.println("认证失败，错误信息："+res_msg);
				}
			}
		}else {
			ctx.fireChannelRead(msg);
		}
	}

}
