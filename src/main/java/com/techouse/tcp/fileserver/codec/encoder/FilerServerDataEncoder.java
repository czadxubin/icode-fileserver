package com.techouse.tcp.fileserver.codec.encoder;

import static com.techouse.tcp.fileserver.dto.trans.TechouseTransData.*;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.alibaba.fastjson.JSON;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransData;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
public class FilerServerDataEncoder extends MessageToByteEncoder<TechouseTransData> {
	@Override
	protected void encode(ChannelHandlerContext ctx, TechouseTransData msg, ByteBuf out) throws Exception {
		TechouseTransDataType dataType = msg.getTransDataType();
		switch (dataType) {
		case JSON:
			doEncodeJsonData(ctx,msg,out);
			break;
		case BINARY:
			doEncodeBinaryData(ctx,msg,out);
			break;
		default:
			break;
		}
	}
	/**
	 * 编码二进制数据
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	private void doEncodeBinaryData(ChannelHandlerContext ctx, TechouseTransData msg, ByteBuf out) {
		
	}
	/**
	 * 编码JSON文本数据
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	private void doEncodeJsonData(ChannelHandlerContext ctx, TechouseTransData msg, ByteBuf out) {
		String jsonString = JSON.toJSONString(msg);
		System.out.println("encode text length :" + jsonString.length() );
		byte[] dataBytes = jsonString.getBytes(CharsetUtil.UTF_8);
		int textLength = dataBytes.length;
		//文本内容长度<=分块长度
		if(textLength<=CHUNKED_SIZE) {
			writeFullData(out, dataBytes, DATA_TYPE_JSON, CHUNK_TYPE_NO_CHUNKED);
		}else {
			//需要分块写出
			int chunkedCount = textLength / CHUNKED_SIZE;
			int chunkedRemainder = textLength % CHUNKED_SIZE;
			if(chunkedRemainder!=0) {
				chunkedCount++;
			}
			for (int i = 1; i < chunkedCount; i++) {
				int start = (i-1) * CHUNKED_SIZE;
				int end = i * CHUNKED_SIZE;
				//写出完整的数据块
				byte chunkedType = -1;
				if(i!=1) {
					chunkedType = CHUNK_TYPE_CONTIUNE;
				}else {
					chunkedType = CHUNK_TYPE_START;
				}
				writeChunkData(out,dataBytes,start,end,DATA_TYPE_JSON, chunkedType);
			}
			//写出最后一段数据块
			int start = (chunkedCount-1)*CHUNKED_SIZE;
			int end = textLength;
			writeChunkData(out,dataBytes,start,end,DATA_TYPE_JSON, CHUNK_TYPE_END);
		}
	}
	/**
	 * 	写出数据
	 * @param out
	 * @param data
	 * 			真是数据
	 * @param dataLength
	 * 			报文长度
	 * @param dataType
	 * 			数据类型
	 * @param chunkedType
	 * 			分块类型
	 */
	private void writeFullData(ByteBuf out,byte[] data,short dataType,byte chunkedType) {
		//写出长度标识4字节
		out.writeInt(data.length);
		//写出类型标识2字节
		out.writeShort(dataType);
		//写出分块标识1字节
		out.writeByte(chunkedType);
		//写出数据
		out.writeBytes(data);
	}
	/**
	 * 	写出指定数组位置，写出数据
	 * @param out
	 * @param data
	 * 			数据数组
	 * @param start
	 * 			数据数组开始索引
	 * @param end
	 * 			数据数组结束索引，不包含
	 * @param dataType
	 * 			数据类型
	 * @param chunkedType
	 * 			分块标识类型
	 */
	private void writeChunkData(ByteBuf out,byte[] data,int start,int end,short dataType,byte chunkedType) {
		//写出长度标识4字节
		out.writeInt(end-start);
		//写出类型标识2字节
		out.writeShort(dataType);
		//写出分块标识1字节
		out.writeByte(chunkedType);
		//写出数据
		for (int i = start; i < end; i++) {
			out.writeByte(data[i]);
		}
	}
	
	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		return super.acceptOutboundMessage(msg);
	}
	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		super.flush(ctx);
	}
}
