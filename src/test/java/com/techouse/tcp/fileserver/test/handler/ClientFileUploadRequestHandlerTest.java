package com.techouse.tcp.fileserver.test.handler;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.techouse.tcp.fileserver.dto.TechouseRequest;
import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.TechouseResponseHeader;
import com.techouse.tcp.fileserver.test.FileServerTest;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadReqBody;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadResBody;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientFileUploadRequestHandlerTest extends ChannelInboundHandlerAdapter{
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("请求文件上传...");
		// 发送上传文件请求
		String filePath =FileServerTest.UPLOAD_FILE_PATH;
		File file = new File(filePath);
		FileUploadReqBody reqBody = new FileUploadReqBody();
		reqBody.setDir_path("/111/222");
		reqBody.setFile_name(file.getName());
		reqBody.setFile_size(file.length());
		TechouseRequestHeader reqHeader = new TechouseRequestHeader();
		reqHeader.setReq_id(UUID.randomUUID().toString());
		reqHeader.setReq_type(ConstantsUtils.ReqTypeConstants.FILE_UPLOAD);
		TechouseRequest<FileUploadReqBody> request = new TechouseRequest<FileUploadReqBody>(reqHeader );
		request.setReq_b(reqBody);
		ctx.writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("请求发送成功");
				}else {
					future.cause().printStackTrace();
				}
			}
		}).sync();
		super.handlerAdded(ctx);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof String ) {
			System.out.println("响应信息："+msg);
			String message = (String) msg;
			JSONObject resJson = JSON.parseObject(message);
			String resHeaderStr = resJson.getString(ConstantsUtils.RESPONSE_HEADER_KEY);
			if(StringUtils.isNotBlank(resHeaderStr)) {
				TechouseResponseHeader resHeader = JSON.parseObject(resHeaderStr,TechouseResponseHeader.class);
				String res_code = resHeader.getRes_code();
				if("1".equals(res_code)) {
					System.out.println("上传请求得到允许，准备上传文件数据");
					ctx.channel().pipeline().remove("clientFileUploadRequestHandlerTest");
					System.out.println("移除文件上传请求Handler");
					System.out.println("添加文件数据上传Handler");
					FileUploadResBody resBody = resJson.getObject(ConstantsUtils.RESPONSE_BODY_KEY, FileUploadResBody.class);
					Integer taskId = resBody.getUpload_id();
					ctx.channel().pipeline().addLast("clientFileDataUploadHandlerTest", new ClientFileDataUploadHandlerTest(taskId));
				}else {
					String res_msg = resHeader.getRes_msg();
					System.out.println("上传请求响应错误："+res_msg);
					ctx.channel().close();
				}
			}
		super.channelRead(ctx, msg);
		}
	}
}
