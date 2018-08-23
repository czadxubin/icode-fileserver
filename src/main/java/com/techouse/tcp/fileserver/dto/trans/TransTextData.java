package com.techouse.tcp.fileserver.dto.trans;

/**
 * 文本数据
 * @author xiaobao
 *
 */
public class TransTextData implements ITechouseTransData,ITransDataType {
	@Override
	public TechouseTransDataType whatTransDataType() {
		return TechouseTransDataType.TEXT_DATA;
	}
}
