package com.techouse.tcp.fileserver.dto.trans;

public interface TransChunkData{
	/**
	 * 	是否为分块数据
	 * @return
	 */
	public boolean isChunkedData();
	/**
	 * 	第一个块数据
	 * @return
	 */
	public boolean isFirst();
	/**
	 * 	中间阶段分款数据
	 */
	public boolean isContinue();
	/**
	 * 	最后一个块数据
	 * @return
	 */
	public boolean isLast();
}
