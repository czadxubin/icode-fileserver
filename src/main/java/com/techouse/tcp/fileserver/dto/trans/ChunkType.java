package com.techouse.tcp.fileserver.dto.trans;

public enum ChunkType {
	/**不分块**/
	NO_CHUNK((byte)-1),
	/**分块 开始**/
	CHUNK_FIRST((byte)1),
	/**分块 中间**/
	CHUNK_CONTINUE((byte)2),
	/**分块 末尾**/
	CHUNK_LAST((byte)0);
	
	public final byte value;
	private ChunkType(byte value) {
		this.value = value;
	}
}
