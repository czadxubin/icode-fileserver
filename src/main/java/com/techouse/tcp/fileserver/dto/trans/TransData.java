package com.techouse.tcp.fileserver.dto.trans;
import static com.techouse.tcp.fileserver.dto.trans.TechouseTransData.*;
public class TransData implements TransChunkData{
	private int dataLength;
	private TechouseTransDataType dataType;
	private byte chunkType;
	private byte[] data;
	public TransData(int dataLength, TechouseTransDataType dataType, byte chunkType, byte[] data) {
		this.dataLength = dataLength;
		this.dataType = dataType;
		this.chunkType = chunkType;
		this.data = data;
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
	public byte getChunkType() {
		return chunkType;
	}
	public void setChunkType(byte chunkType) {
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
		return chunkType==CHUNK_TYPE_START;
	}
	@Override
	public boolean isLast() {
		return chunkType==CHUNK_TYPE_END;
	}
	@Override
	public boolean isChunkedData() {
		return chunkType!=CHUNK_TYPE_NO_CHUNKED;
	}
	@Override
	public boolean isContinue() {
		return chunkType==CHUNK_TYPE_CONTIUNE;
	}
}
