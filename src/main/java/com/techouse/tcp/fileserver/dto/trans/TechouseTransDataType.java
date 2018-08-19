package com.techouse.tcp.fileserver.dto.trans;
import static com.techouse.tcp.fileserver.dto.trans.TechouseTransData.*;
public enum TechouseTransDataType {
	BINARY(DATA_TYPE_BINARY),
	JSON(DATA_TYPE_JSON);
	public final int value;
	private TechouseTransDataType(int value) {
		this.value = value;
	}
}
