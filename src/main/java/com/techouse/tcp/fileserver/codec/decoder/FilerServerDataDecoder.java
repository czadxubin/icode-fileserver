package com.techouse.tcp.fileserver.codec.decoder;

import java.util.List;

import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;
import com.techouse.tcp.fileserver.dto.trans.TransData;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FilerServerDataDecoder extends ByteToMessageDecoder {
	public static final int NEXT_READ_LENGTH_FIELD = 0;
	public static final int NEXT_READ_DATA_TYPE = 1;
	public static final int NEXT_READ_CHUNK_TYPE = 2;
	public static final int NEXT_READ_DATA = 3;
	private Integer nextRead = NEXT_READ_LENGTH_FIELD;
	private Integer dataLength = null;
	private TechouseTransDataType dataType;
	private Byte chunkedType = null;
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//读取长度字段>4
		if(nextRead == NEXT_READ_LENGTH_FIELD && in.readableBytes()>=4) {
			dataLength = in.readInt();
			nextRead = NEXT_READ_DATA_TYPE;
			return ;
		}
		//读取数据类型字段>2
		if(nextRead == NEXT_READ_DATA_TYPE && in.readableBytes()>=2) {
			short s = in.readShort();
			if(s==TechouseTransDataType.JSON.value) {
				dataType = TechouseTransDataType.JSON;
			}else if(s==TechouseTransDataType.BINARY.value) {
				dataType = TechouseTransDataType.BINARY;
			}
			nextRead = NEXT_READ_CHUNK_TYPE;
			return ;
		}
		//读取分块字段>1
		if(nextRead == NEXT_READ_CHUNK_TYPE && in.readableBytes()>=1) {
			chunkedType = in.readByte();
			nextRead = NEXT_READ_DATA;
			return ;
		}
		//数据读取真实数据
		if(nextRead == NEXT_READ_DATA && in.readableBytes()>=dataLength) {
			nextRead = NEXT_READ_LENGTH_FIELD;
			byte[] buf = new byte[dataLength];
			in.readBytes(buf, 0, dataLength);
			//传出到下一个Decoder
			out.add(new TransData(dataLength, dataType, chunkedType, buf));
			//初始化
			init();
			return ;
		}
	}
	private void init() {
		dataLength = null;
		dataType = null;
		chunkedType = null;
	}
}
