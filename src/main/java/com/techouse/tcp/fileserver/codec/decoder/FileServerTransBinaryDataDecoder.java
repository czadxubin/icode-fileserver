package com.techouse.tcp.fileserver.codec.decoder;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

public class FileServerTransBinaryDataDecoder extends MessageToMessageDecoder<TransBinaryData>{
	private byte[] buf;
	/**
	 * 	处理分块数据：<br>
	 * 	<p>文本数据，转换为String</p>
	 * 	<p>二进制数据，转换为TransBinaryData，具体分为四种：<br>
	 * 		<li>TransBinaryNoChunkData</li>
	 * 		<li>TransBinaryChunkDataFirst</li>
	 * 		<li>TransBinaryChunkDataContinue</li>
	 * 		<li>TransBinaryChunkDataLast</li>
	 * 	</p>
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, TransBinaryData msg, List<Object> out) throws Exception {
		if(msg.isChunkedData()) {
			doDecodeChunkedData(ctx,msg,out);
		}else {
			doDecodeNoChunkedData(ctx,msg,out);
		}
	}
	/**
	 * 	处理无分块的数据<br>
	 * 	<p>如果是JSON数据，转换为文本</p>
	 * 	<p>如果是二进制数据，包装为TransBinaryNoChunkData</p>
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	private void doDecodeNoChunkedData(ChannelHandlerContext ctx, TransBinaryData msg, List<Object> out) {
		if(msg.getDataType()==TechouseTransDataType.TEXT_JSON_DATA) {
			String JsonString = new String(msg.getData(),CharsetUtil.UTF_8);
			out.add(JsonString);
		}else if(msg.getDataType()==TechouseTransDataType.BINARY_FILE_DATA) {
			out.add(msg);
		}
	}

	/**
	 * 	处理分块数据<br>
	 * 	<p>如果是JSON数据分块，合成文本后向下传递</p>
	 * 	<p>如果是二进制数据分块，数据包装为TransBinaryData直接向下传递</p>
	 * @param ctx
	 * @param msg
	 * @param out
	 */
	private void doDecodeChunkedData(ChannelHandlerContext ctx, TransBinaryData msg, List<Object> out) {
		if(msg.getDataType()==TechouseTransDataType.TEXT_JSON_DATA) {//JSON分块数据
			buf = ArrayUtils.addAll(buf, msg.getData());
			if(msg.isLast()){
				String JsonString = new String(buf,CharsetUtil.UTF_8);
				out.add(JsonString);
				buf = null;
			}
		}else if(msg.getDataType()==TechouseTransDataType.BINARY_FILE_DATA) {//文件数据分块
			out.add(msg);
		}
	}
}
