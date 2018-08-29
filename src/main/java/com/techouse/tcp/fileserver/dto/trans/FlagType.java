package com.techouse.tcp.fileserver.dto.trans;

public enum FlagType {
	/**确认应答**/
	SEND((byte)-1),
	/**确认应答**/
	ACK((byte)0),
	/**否定应答，用于重传**/
	NAK((byte)1),
	/**完成应答**/
	FIN((byte)2);
	
	public final byte value;
	private FlagType(byte value) {
		this.value = value;
	}
}
