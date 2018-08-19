package com.icode.netty.fileserver.channelfuture;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class CloseChannelFuture implements ChannelFutureListener{

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if(future.isSuccess()) {
			System.out.println(future.channel());
			future.channel().close().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("channel is closed...");
				}
			});
		}
	}

}
