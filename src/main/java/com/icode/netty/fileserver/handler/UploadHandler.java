package com.icode.netty.fileserver.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.icode.netty.fileserver.exception.RequestHandleException;
import com.icode.netty.fileserver.utils.FileServerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public class UploadHandler extends AbstractRequestHandler{
	private String uploadFileName;
	private boolean isUploadFinished;
	private FileChannel outFileChannel;
	private int fileSize;
	
	@Override
	public void doHandleHttpRequest(ChannelHandlerContext ctx, HttpRequest httpRequest)  throws RequestHandleException{
		String uri = httpRequest.uri();
		System.out.println("接收到上传文件请求...");
		uploadFileName = uri.substring(8);
		System.out.println("上传文件名称："+uploadFileName);
		ReferenceCountUtil.release(httpRequest);
	}

	@Override
	public void doHandleHttpContent(ChannelHandlerContext ctx, HttpContent httpContent) throws RequestHandleException {
		if(uploadFileName==null) {
			return ;
		}
		ByteBuf content = httpContent.content();
		FileOutputStream output = null;
		try {
			if(outFileChannel == null) {
				File file = new File("E:\\",uploadFileName);
				if(!file.exists()) {
					file.createNewFile();
				}
				output  = new FileOutputStream(file,false);
				outFileChannel = output.getChannel(); 
			}
			int length = content.readableBytes();
			//注意：ByteBuf.readBytes不会改变outFileChannel的position
			content.readBytes(outFileChannel, length);
			fileSize = fileSize+length;
			if(httpContent instanceof LastHttpContent) {
				isUploadFinished = true;
				HttpResponse okReponse = FileServerUtils.simpleHttpResponse(HttpResponseStatus.OK, "上传完成");
				ctx.writeAndFlush(okReponse).addListener(ChannelFutureListener.CLOSE);
				System.out.println("接收文件大小："+fileSize/1024+"KB");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			ReferenceCountUtil.release(httpContent);
			if(isUploadFinished&&outFileChannel != null) {
				try {
					outFileChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(isUploadFinished&&output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
