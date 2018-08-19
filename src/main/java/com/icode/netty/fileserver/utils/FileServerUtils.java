package com.icode.netty.fileserver.utils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class FileServerUtils {
	/**
	 * 返回简单的http响应（成功或者错误信息）
	 * @param httpResponseStatus
	 * @param msg
	 * @return
	 */
	public static HttpResponse simpleHttpResponse(HttpResponseStatus httpResponseStatus,String msg) {
		HttpResponse badGetwayResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
		return badGetwayResponse;
	}
}
