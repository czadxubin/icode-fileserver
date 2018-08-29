package com.techouse.tcp.fileserver.codec.encoder;

import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.CHUNKED_SIZE;
import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.CHUNK_TYPE_CONTIUNE;
import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.CHUNK_TYPE_END;
import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.CHUNK_TYPE_NO_CHUNKED;
import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.CHUNK_TYPE_START;
import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.TEXT_DATA_TYPE;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.RandomAccess;

import com.alibaba.fastjson.JSON;
import com.techouse.tcp.fileserver.dto.trans.ChunkType;
import com.techouse.tcp.fileserver.dto.trans.FlagType;
import com.techouse.tcp.fileserver.dto.trans.ITechouseTransData;
import com.techouse.tcp.fileserver.dto.trans.ITransDataType;
import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryFileData;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
public class FilerServerDataEncoder extends MessageToByteEncoder<ITechouseTransData> {
	@Override
	protected void encode(ChannelHandlerContext ctx, ITechouseTransData msg, ByteBuf out) throws Exception {
		TechouseTransDataType dataType = msg.whatTransDataType();
		switch (dataType) {
		case TEXT_JSON_DATA:
			doEncodeJsonData(ctx,msg,out);
			break;
		case BINARY_FILE_DATA:
			doEncodeBinaryFileData(ctx,(TransBinaryFileData)msg,out);
			break;
		default:
			break;
		}
	}
	/**
	 * 编码文件二进制数据
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	private void doEncodeBinaryFileData(ChannelHandlerContext ctx, TransBinaryFileData msg, ByteBuf out) {
		File file = msg.getFile();
		if(file!=null && file.exists() && file.length()>0) {
			RandomAccessFile randomFile = null;
			try {
				TechouseTransDataType dataType = TechouseTransDataType.BINARY_FILE_DATA;
				randomFile = new RandomAccessFile(file, "rw");
				long dataLength = file.length();
				if(dataLength<=CHUNKED_SIZE) {
					byte[] data = new byte[(int)dataLength];
					randomFile.seek(0);
					randomFile.read(data, 0, (int)dataLength);
					//计算校验和
					short crc16 = TransBinaryData.caculateCrc(data, dataType, ChunkType.NO_CHUNK, 0, FlagType.SEND);
					writeData(ctx,out, data,dataType,ChunkType.NO_CHUNK,FlagType.SEND,crc16);
					
				}else {
					//需要分块写出
					int chunkedCount = (int) (dataLength / CHUNKED_SIZE);
					int chunkedRemainder = (int) (dataLength % CHUNKED_SIZE);
					if(chunkedRemainder!=0) {
						chunkedCount++;
					}
					ChunkType chunkType = null;
					for (int i = 1; i < chunkedCount; i++) {
						int start = (i-1) * CHUNKED_SIZE;
//						int end = i * CHUNKED_SIZE;
						//写出完整的数据块
						if(i!=1) {
							chunkType = ChunkType.CHUNK_CONTINUE;
						}else {
							chunkType = ChunkType.CHUNK_FIRST;
						}
						byte[] desData = new byte[CHUNKED_SIZE];
						randomFile.seek(start);
						randomFile.read(desData, 0, CHUNKED_SIZE);
						//计算校验和
						short crc16 = TransBinaryData.caculateCrc(desData, dataType, chunkType, i, FlagType.SEND);
						//写出
						writeData(ctx,out, desData,dataType ,chunkType, FlagType.SEND, crc16);
					}
					//写出最后一段数据块
					int start = (chunkedCount-1)*CHUNKED_SIZE;
					int lastLen = (int) (dataLength-start);
					byte[] desData = new byte[lastLen];
					randomFile.seek(start);
					randomFile.read(desData, 0, lastLen);
					//计算校验和
					short crc16 = TransBinaryData.caculateCrc(desData, TechouseTransDataType.TEXT_JSON_DATA, chunkType, chunkedCount, FlagType.SEND);
					writeData(ctx,out, desData, dataType,ChunkType.CHUNK_LAST, FlagType.SEND, crc16);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			System.out.println("客户端错误：文件状态异常（文件对象为空或文件不存在或文件大小为0）");
		}
//		if(msg instanceof TransBinaryData) {
//			TransBinaryData binaryData=(TransBinaryData)msg;
//			byte[] data = binaryData.getData();
//			short dataType = BINARY_FILE_DATA_TYPE;
//			byte chunkType = CHUNK_TYPE_NO_CHUNKED;
//			if(msg instanceof TransBinaryChunkDataFirst) {
//				chunkType = CHUNK_TYPE_START;
//			}else if(msg instanceof TransBinaryChunkDataContinue) {
//				chunkType = CHUNK_TYPE_CONTIUNE;
//			}else if(msg instanceof TransBinaryChunkDataLast) {
//				chunkType = CHUNK_TYPE_END;
//			}
//			writeFullData(out, data, dataType, chunkType);
//		}
	}
	/**
	 * 编码JSON文本数据
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	private void doEncodeJsonData(ChannelHandlerContext ctx, ITransDataType msg, ByteBuf out) {
		String jsonString = JSON.toJSONString(msg);
		System.out.println("encode text length :" + jsonString.length() );
		byte[] data = jsonString.getBytes(CharsetUtil.UTF_8);
		int dataLength = data.length;
		//文本内容长度<=分块长度
		if(dataLength<=CHUNKED_SIZE) {
			//计算校验和
			short crc16 = TransBinaryData.caculateCrc(data, TechouseTransDataType.TEXT_JSON_DATA, ChunkType.NO_CHUNK, 0, FlagType.SEND);
			writeData(ctx,out, data,TechouseTransDataType.TEXT_JSON_DATA,ChunkType.NO_CHUNK,FlagType.SEND,crc16);
		}else {
			//需要分块写出
			int chunkedCount = dataLength / CHUNKED_SIZE;
			int chunkedRemainder = dataLength % CHUNKED_SIZE;
			if(chunkedRemainder!=0) {
				chunkedCount++;
			}
			ChunkType chunkType = null;
			for (int i = 1; i < chunkedCount; i++) {
				int start = (i-1) * CHUNKED_SIZE;
//				int end = i * CHUNKED_SIZE;
				//写出完整的数据块
				if(i!=1) {
					chunkType = ChunkType.CHUNK_CONTINUE;
				}else {
					chunkType = ChunkType.CHUNK_FIRST;
				}
				byte[] desData = new byte[CHUNKED_SIZE];
				System.arraycopy(data, start,desData,0,CHUNKED_SIZE);
				//计算校验和
				short crc16 = TransBinaryData.caculateCrc(desData, TechouseTransDataType.TEXT_JSON_DATA, chunkType, i, FlagType.SEND);
				//写出
				writeData(ctx,out, desData, TechouseTransDataType.TEXT_JSON_DATA,chunkType, FlagType.SEND, crc16);
			}
			//写出最后一段数据块
			int start = (chunkedCount-1)*CHUNKED_SIZE;
			int lastLen = dataLength - start;
			byte[] desData = new byte[lastLen];
			System.arraycopy(data, start,desData,0,lastLen);
			//计算校验和
			short crc16 = TransBinaryData.caculateCrc(desData, TechouseTransDataType.TEXT_JSON_DATA, chunkType, chunkedCount, FlagType.SEND);
			writeData(ctx,out, desData, TechouseTransDataType.TEXT_JSON_DATA,ChunkType.CHUNK_LAST, FlagType.SEND, crc16);
		}
	}
	/**写出全部数据数据
	 * 
	 * @param out
	 * @param data
	 * @param dataType
	 * @param flagType
	 * @param crc16
	 */
	private void writeData(ChannelHandlerContext ctx,ByteBuf out,byte[] data,TechouseTransDataType dataType,ChunkType chunkType,FlagType flagType,short crc16) {
		//写出长度标识4字节
		out.writeInt(data.length);
		//写出类型标识2字节
		out.writeByte(dataType.value);
		//写出分块标识1字节
		out.writeByte(chunkType.value);
		//块Id，无分块时，此字段无意义
		out.writeInt(-1);
		//标志位
		out.writeByte(flagType.value);
		//校验和
		out.writeShort(crc16);
		//写出数据
		out.writeBytes(data);
	}
	
	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		super.flush(ctx);
	}
}
