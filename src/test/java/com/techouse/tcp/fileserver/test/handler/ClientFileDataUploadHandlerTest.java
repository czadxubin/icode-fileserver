package com.techouse.tcp.fileserver.test.handler;

import java.io.File;

import com.techouse.tcp.fileserver.dto.trans.TransBinaryFileData;
import com.techouse.tcp.fileserver.test.FileServerTest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
public class ClientFileDataUploadHandlerTest extends ChannelInboundHandlerAdapter {
	
	private int taskId;

	public ClientFileDataUploadHandlerTest(int taskId) {
		this.taskId = taskId;
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		//写出数据
		String pathname = FileServerTest.UPLOAD_FILE_PATH;
		File file = new File(pathname);
		System.out.println("写文件数据");
		ctx.writeAndFlush(new TransBinaryFileData(file ));
	}
	public static byte[] intToByteArray(int a) {   
		return new byte[] {   
		        (byte) ((a >> 24) & 0xFF),   
		        (byte) ((a >> 16) & 0xFF),      
		        (byte) ((a >> 8) & 0xFF),      
		        (byte) (a & 0xFF)};   
	}   
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		//上传错误或者成功应答
		
		
	}
}
