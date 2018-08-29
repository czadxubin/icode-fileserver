package com.techouse.tcp.fileserver.codec.decoder;

import java.util.List;

import com.techouse.tcp.fileserver.dto.trans.ChunkType;
import com.techouse.tcp.fileserver.dto.trans.FlagType;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FilerServerDataDecoder extends ByteToMessageDecoder {
	public static final int NEXT_READ_LENGTH_FIELD = 0;
	public static final int NEXT_READ_DATA_TYPE = 1;
	public static final int NEXT_READ_CHUNK_TYPE = 2;
	public static final int NEXT_READ_CHUNK_ID = 3;
	public static final int NEXT_READ_FLAG_TYPE = 4;
	public static final int NEXT_READ_CRC = 5;
	public static final int NEXT_READ_DATA = 6;
	private Integer nextRead = NEXT_READ_LENGTH_FIELD;
	/**数据长度 4字节**/
	private Integer dataLength = null;
	/**数据类型 1字节**/
	private TechouseTransDataType dataType;
	/**分块类型 1字节**/
	private ChunkType chunkType = null;
	/**分块编号 4字节**/
	private Integer chunkId;
	/**标志位 1字节**/
	private FlagType flagType;
	/**16位校验和**/
	private Short crc16;
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//读取长度字段>4
		if(nextRead == NEXT_READ_LENGTH_FIELD && in.readableBytes()>=4) {
			dataLength = in.readInt();
			nextRead = NEXT_READ_DATA_TYPE;
			return ;
		}
		//读取数据类型字段>1
		if(nextRead == NEXT_READ_DATA_TYPE && in.readableBytes()>=1) {
			byte s = in.readByte();
			if(s==TechouseTransDataType.TEXT_JSON_DATA.value) {
				dataType = TechouseTransDataType.TEXT_JSON_DATA;
			}else if(s==TechouseTransDataType.BINARY_FILE_DATA.value) {
				dataType = TechouseTransDataType.BINARY_FILE_DATA;
			}
			nextRead = NEXT_READ_CHUNK_TYPE;
			return ;
		}
		//读取分块类型字段>1
		if(nextRead == NEXT_READ_CHUNK_TYPE && in.readableBytes()>=1) {
			byte s = in.readByte();
			if(s==ChunkType.CHUNK_CONTINUE.value) {
				chunkType = ChunkType.CHUNK_CONTINUE;
			}else if(s==ChunkType.NO_CHUNK.value) {
				chunkType = ChunkType.NO_CHUNK;
			}else if(s==ChunkType.CHUNK_FIRST.value) {
				chunkType = ChunkType.CHUNK_FIRST;
			}else if(s==ChunkType.CHUNK_LAST.value) {
				chunkType = ChunkType.CHUNK_LAST;
			}
			nextRead = NEXT_READ_CHUNK_ID;
			return ;
		}
		//读取分块编号字段>4
		if(nextRead == NEXT_READ_CHUNK_ID && in.readableBytes()>=4) {
			chunkId = in.readInt();
			nextRead = NEXT_READ_FLAG_TYPE;
			return ;
		}
		//读取标志位字段>1
		if(nextRead == NEXT_READ_FLAG_TYPE && in.readableBytes()>=1) {
			byte s = in.readByte();
			if(s==FlagType.SEND.value) {
				flagType = FlagType.SEND;
			}else if(s==FlagType.ACK.value) {
				flagType = FlagType.ACK;
			}else if(s==FlagType.NAK.value) {
				flagType = FlagType.NAK;
			}else if(s==FlagType.FIN.value) {
				flagType = FlagType.FIN;
			}
			nextRead = NEXT_READ_CRC;
			return ;
		}
		//读取CRC校验位>2
		if(nextRead == NEXT_READ_CRC && in.readableBytes()>=2) {
			crc16 = in.readShort();
			nextRead = NEXT_READ_DATA;
		}
		//数据读取真实数据
		if(nextRead == NEXT_READ_DATA && in.readableBytes()>=dataLength) {
			nextRead = NEXT_READ_LENGTH_FIELD;
			byte[] buf = new byte[dataLength];
			in.readBytes(buf, 0, dataLength);
			//传出到下一个Decoder
			out.add(new TransBinaryData(dataType, chunkType, chunkId, flagType, crc16, buf));
			//初始化
			init();
			return ;
		}
	}
	private void init() {
		dataLength = null;
		dataType = null;
		chunkType = null;
		chunkId = null;
		flagType = null;
		crc16 = null;
	}
}
