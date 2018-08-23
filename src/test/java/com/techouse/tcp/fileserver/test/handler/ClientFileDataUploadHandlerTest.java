package com.techouse.tcp.fileserver.test.handler;

import static com.techouse.tcp.fileserver.dto.trans.TechouseTransData.CHUNKED_SIZE;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.lang3.ArrayUtils;

import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataContinue;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataFirst;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataLast;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryNoChunkData;
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
		//上传文件数据
		String filePath = FileServerTest.UPLOAD_FILE_PATH;
		File file = new File(filePath);
		FileInputStream fileIn = new FileInputStream(file);
		//文件分块
		long fileSize = file.length();
		int chunkCount = (int) (fileSize/CHUNKED_SIZE);
		int remainder = (int) (fileSize%CHUNKED_SIZE);
		if(remainder !=0) {
			chunkCount++;
		}
		byte[] taskIdData = intToByteArray(taskId);
		//不需要分块传输
		if(chunkCount==1) {
			byte[] fileData = new byte[(int)fileSize];
			int read = fileIn.read(fileData);
			byte[] addAll = ArrayUtils.addAll(taskIdData, fileData);
			ctx.writeAndFlush(new TransBinaryNoChunkData(addAll));
		}else {//需要分块
			for (int i = 1; i < chunkCount; i++) {
				byte[] fileData = new byte[CHUNKED_SIZE];
				int read = fileIn.read(fileData);
				TransBinaryData binaryData = null;
				if(i!=1) {
					binaryData = new TransBinaryChunkDataContinue(fileData);
				}else {
					binaryData = new TransBinaryChunkDataFirst(fileData);
				}
				ctx.writeAndFlush(binaryData);
			}
			//写最后一块
			int lastLength = (int) (fileSize - (chunkCount-1)*CHUNKED_SIZE);
			byte[] fileData = new byte[lastLength];
			int read = fileIn.read(fileData);
			ctx.writeAndFlush(new TransBinaryChunkDataLast(fileData));
		}
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
