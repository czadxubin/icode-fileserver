package com.techouse.tcp.fileserver.test.handler;

import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.CHUNKED_SIZE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.icode.netty.fileserver.channelfuture.CloseChannelFuture;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataContinue;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataFirst;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataLast;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryNoChunkData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BinaryDataHandlerTest extends ChannelInboundHandlerAdapter {
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("成功连接到服务器！！！,准备发送二进制数据");
		String filePath = "D:\\2018工作\\软件包\\eclipse-jee-oxygen-3a-win32-x86_64.zip";
		filePath = "C:\\Users\\xiaobao\\git\\icode-fileserver\\src\\test\\resources\\测试文本.txt";
		File file = new File(filePath);
		writeFile(file,ctx);
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
	
	
	public void writeFile(File file,ChannelHandlerContext ctx) {
		FileInputStream in = null;
		try {
			if(file.exists()) {
				in = new FileInputStream(file);
				long length = file.length();
				long chunkedCount = length / CHUNKED_SIZE;
				//需要分块写出
				if(chunkedCount!=0) {
					long chunkedRemainder = length % CHUNKED_SIZE;
					if(chunkedRemainder!=0) {
						chunkedCount++;
					}
					for (int i = 1; i < chunkedCount; i++) {
						//写出完整的数据块
						byte[] data = new byte[CHUNKED_SIZE];
						int read = in.read(data,0, CHUNKED_SIZE);
						if(read!=-1) {
							TransBinaryData binData = null;
							if(i!=1) {
								binData = new TransBinaryChunkDataContinue(data);
							}else {
								binData = new TransBinaryChunkDataFirst(data);
							}
							ctx.writeAndFlush(binData);
						}else {
							System.out.println("不分块文件读取失败");
						}
					}
					//写出最后一段数据块
					int start = (int) ((chunkedCount-1)*CHUNKED_SIZE);
					int lastLength = (int) length-start;
					byte[] data = new byte[lastLength];
					int read = in.read(data, 0, lastLength);
					if(read!=-1) {
						ctx.writeAndFlush(new TransBinaryChunkDataLast(data)).addListener(new CloseChannelFuture());
					}else {
						System.out.println("不分块文件读取失败");
					}
					
				}else {//不足分块大小，一次写出
					byte[] data = new byte[(int) length];
					int read = in.read(data, 0, (int)length);
					if(read!=-1) {
						ctx.writeAndFlush(new TransBinaryNoChunkData(data)).addListener(new CloseChannelFuture());
					}else {
						System.out.println("不分块文件读取失败");
					}
				}
				
			}else {
				System.out.println("file 不存在:"+file);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}