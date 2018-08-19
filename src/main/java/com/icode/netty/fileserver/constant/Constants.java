package com.icode.netty.fileserver.constant;

import com.icode.netty.fileserver.model.ClientInfo;

import io.netty.util.AttributeKey;

public class Constants {
	public static final AttributeKey<ClientInfo> CLIENT_INFO = AttributeKey.newInstance("CLIENT_INFO");
	/**客户端认证次数，防止恶意尝试认证**/
	public static final AttributeKey<Integer> CLIENT_AUTH_TIMES = AttributeKey.newInstance("CLIENT_AUTH_TIMES");
}
