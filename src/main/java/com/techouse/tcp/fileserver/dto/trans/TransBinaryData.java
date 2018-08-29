package com.techouse.tcp.fileserver.dto.trans;

import com.techouse.tcp.fileserver.utils.ByteUtils;
import com.techouse.tcp.fileserver.utils.CRCUtils;

public class TransBinaryData implements ITechouseTransData,ITransChunkType{
	/**数据长度，4字节**/
	private int dataLength;
	/**数据类型，1字节**/
	private TechouseTransDataType dataType;
	/**分块类型，1字节**/
	private ChunkType chunkType;
	/**分块编号,4字节**/
	private int chunkId;
	/**标志位 1字节**/
	private FlagType flagType;
	/**crc16位校验和，2字节**/
	private short crc;
	/**数据**/
	private byte[] data;
	
	/**
	 * 根据指定参数创建一个传输对象
	 * @param dataType
	 * @param chunkType
	 * @param chunkId
	 * @param flagType
	 * @param crc
	 * @param data
	 */
	public TransBinaryData(TechouseTransDataType dataType, ChunkType chunkType, int chunkId, FlagType flagType,
			short crc, byte[] data) {
		this.dataType = dataType;
		this.chunkType = chunkType;
		this.chunkId = chunkId;
		this.flagType = flagType;
		this.crc = crc;
		this.data = data;
		this.dataLength = data!=null?data.length:0;
	}
	/**
	 * 
	 * @param data
	 * @param dataType
	 * @param chunkType
	 * @param chunkId
	 * @param flagType
	 */
	public TransBinaryData(byte[] data,TechouseTransDataType dataType, ChunkType chunkType, int chunkId,FlagType flagType) {
		if(data!=null) {
			this.dataLength = data.length;
		}
		this.dataType = dataType;
		this.chunkType = chunkType;
		if(chunkType!=ChunkType.NO_CHUNK) {
			this.chunkId = chunkId;
		}
		this.data = data;
		//计算crc
		crc = caculateCrc(data, dataType, chunkType, chunkId, flagType);
		
	}
	
	/**
	 * 参加校验的字段：长度标识4、类型标识1、块标识1、块序号4、标志位1、数据
	 * @return
	 */
	public static short caculateCrc(byte[] data,TechouseTransDataType dataType,ChunkType chunkType,int chunkId,FlagType flagType) {
		int dataLength = 0;
		if(data!=null) {
			dataLength = data.length;
		}
		byte[] target = new byte[11+dataLength];
		//cp 数据长度 4
		System.arraycopy(ByteUtils.intToByte(dataLength),0,target,0,4);
		//cp 数据类型 1
		target[4] = dataType.value;
		//cp 块类型 1
		target[5] = chunkType.value;
		//cp 块序号 4
		System.arraycopy(ByteUtils.intToByte(chunkId),0,target,6,4);
		//cp 标志位 1
		target[10] = flagType.value;
		//cp 数据长度
		if(data!=null) {
			System.arraycopy(data, 0, target, 11, data.length);
		}
		return CRCUtils.getCRCShort(target);
		
	}
	/**
	 * 计算CRC16与字段crc进行对比
	 * @return
	 */
	public boolean isValidData() {
		return this.crc == caculateCrc(data, dataType, chunkType, chunkId, flagType);
	}
	public int getDataLength() {
		return dataLength;
	}
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	public TechouseTransDataType getDataType() {
		return dataType;
	}
	public void setDataType(TechouseTransDataType dataType) {
		this.dataType = dataType;
	}
	public ChunkType getChunkType() {
		return chunkType;
	}
	public void setChunkType(ChunkType chunkType) {
		this.chunkType = chunkType;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public boolean isFirst() {
		return chunkType==ChunkType.CHUNK_FIRST;
	}
	@Override
	public boolean isLast() {
		return chunkType==ChunkType.CHUNK_LAST;
	}
	@Override
	public boolean isChunkedData() {
		return chunkType!=ChunkType.NO_CHUNK;
	}
	@Override
	public boolean isContinue() {
		return chunkType==ChunkType.CHUNK_CONTINUE;
	}
	public int getChunkId() {
		return chunkId;
	}
	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}
	public short getCrc() {
		return crc;
	}
	public void setCrc(short crc) {
		this.crc = crc;
	}

	@Override
	public TechouseTransDataType whatTransDataType() {
		return this.dataType;
	}
}
