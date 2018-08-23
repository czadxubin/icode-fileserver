package com.techouse.tcp.fileserver.dto.trans;
import static com.techouse.tcp.fileserver.dto.trans.ITechouseTransData.*;
public enum TechouseTransDataType {
	TEXT_DATA(TEXT_DATA_TYPE),
	TEXT_JSON_DATA(TEXT_JSON_DATA_TYPE),
	BINARY_DATA(BINARY_DATA_TYPE),
	BINARY_FILE_DATA(BINARY_FILE_DATA_TYPE);
	public final byte value;
	private TechouseTransDataType(byte value) {
		this.value = value;
	}
}
