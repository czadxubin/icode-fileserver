package com.techouse.tcp.fileserver.handler;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.TechouseResponse;
import com.techouse.tcp.fileserver.dto.TechouseResponseHeader;
import com.techouse.tcp.fileserver.utils.CommonUtils;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.client_auth.ClientAuthReqBody;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.util.Attribute;

/**
 * 客户端认证Handler
 * @author xiaobao
 *
 */
public class FileClientAuthHandler extends BaseFileServerHanlder<ClientAuthReqBody>{
	@Override
	protected void doHandleRequest(ChannelHandlerContext ctx, ClientAuthReqBody reqBody,TechouseRequestHeader reqHeader) {
		String req_id = reqHeader.getReq_id();
		String req_type = reqHeader.getReq_type();
		String client_id = reqBody.getClient_id();
		String client_secret = reqBody.getClient_secret();
		String root_path = reqBody.getRoot_path();
		if(StringUtils.isNotBlank(client_id)
				&&StringUtils.isNotBlank(client_secret)
				&&StringUtils.isNotBlank(root_path)) {
			//创建客户端文件根路径
			File clientRootDir = new File(ConstantsUtils.SERVER_FILE_ROOT_PATH,root_path);
			boolean mkdirs = true;
			if(!clientRootDir.exists()||!clientRootDir.isDirectory()) {
				mkdirs = clientRootDir.mkdirs();
			}
			if(mkdirs) {
				TechouseResponse reponse = CommonUtils.generateGenericReponse(req_id, req_type, "1", "成功");
				ctx.writeAndFlush(reponse).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						Channel channel = ctx.channel();
						if(future.isSuccess()) {
							Attribute<ClientAuthReqBody> clientAuthAttr = channel.attr(ConstantsUtils.FileServerAttr.CLIENT_AUTH_ATTR_KEY);
							clientAuthAttr.set(reqBody);
							System.out.println("客户端认证成功");
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.remove("fileClientAuthHandler");
							pipeline.addLast("techouseFileUploadServerHandler",new TechouseFileUploadServerHandler());
						}else {
							future.cause().printStackTrace();
							channel.close();
						}
					}
				});
			}else {
				//客户端根路径创建失败
				TechouseResponse reponse = CommonUtils.generateGenericReponse(req_id, req_type, "0", "指定根路径创建失败");
				ctx.writeAndFlush(reponse).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						System.out.println("指定根路径创建失败");
						future.channel().close();
					}
				});
			}
		}else {
			//认证失败
			TechouseResponse reponse = CommonUtils.generateGenericReponse(req_id, req_type, "0", "认证失败");
			ctx.writeAndFlush(reponse).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("客户端认证失败");
					future.channel().close();
				}
			});
		}	
	}
	
}
