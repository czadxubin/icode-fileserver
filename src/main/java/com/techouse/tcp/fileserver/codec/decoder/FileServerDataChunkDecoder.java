package com.techouse.tcp.fileserver.codec.decoder;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.techouse.tcp.fileserver.dto.trans.TechouseTransDataType;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataContinue;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataFirst;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataLast;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryNoChunkData;
import com.techouse.tcp.fileserver.dto.trans.TransData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

public class FileServerDataChunkDecoder extends MessageToMessageDecoder<TransData>{
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
	protected void decode(ChannelHandlerContext ctx, TransData msg, List<Object> out) throws Exception {
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
	private void doDecodeNoChunkedData(ChannelHandlerContext ctx, TransData msg, List<Object> out) {
		if(msg.getDataType()==TechouseTransDataType.JSON) {
			String JsonString = new String(msg.getData(),CharsetUtil.UTF_8);
			out.add(JsonString);
		}else if(msg.getDataType()==TechouseTransDataType.BINARY) {
			out.add(new TransBinaryNoChunkData(msg.getData()));
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
	private void doDecodeChunkedData(ChannelHandlerContext ctx, TransData msg, List<Object> out) {
		if(msg.getDataType()==TechouseTransDataType.JSON) {//json文本分块
			buf = ArrayUtils.addAll(buf, msg.getData());
			if(msg.isLast()){
				String JsonString = new String(buf,CharsetUtil.UTF_8);
				out.add(JsonString);
				buf = null;
			}
		}else if(msg.getDataType()==TechouseTransDataType.BINARY) {//数据分块
			TransBinaryData transBinaryData = null;
			if(msg.isFirst()) {
				transBinaryData = new TransBinaryChunkDataFirst(msg.getData());
			}else if(msg.isContinue()) {
				transBinaryData = new TransBinaryChunkDataContinue(msg.getData());
			}else if(msg.isLast()) {
				transBinaryData = new TransBinaryChunkDataLast(msg.getData());
			}
			if(transBinaryData!=null) {
				out.add(transBinaryData);
			}
		}
	}
}
