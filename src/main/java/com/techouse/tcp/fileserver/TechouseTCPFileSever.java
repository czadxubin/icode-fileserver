package com.techouse.tcp.fileserver;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.techouse.tcp.fileserver.codec.TechouseFileServerCodec;
import com.techouse.tcp.fileserver.codec.decoder.FileServerDataChunkDecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 
 * Copyright: Copyright (c) 2018 www.techouse.top
 * 
 * @ClassName: TechouseTCPFileSever.java
 * @Description: 服务器启动类
 * @version: v1.0.0
 * @author: 许宝众
 * @date: 2018年8月19日 下午3:05:48
 *
 *        Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2018年8月19日
 *        许宝众 v1.0.0 首次添加
 */
public class TechouseTCPFileSever {
	private static long readerIdleTime = 300;
	private static long writerIdleTime = 300;
	private static long allIdleTime = 300;

	private static final String hostname = "0.0.0.0";
	private static final int port = 8888;

	public static void main(String[] args) throws Exception {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap boot = new ServerBootstrap();
		try {
			boot
			.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
//							pipeline.addLast(new LoggingHandler(LogLevel.INFO));
					pipeline.addLast("idleStateHandler", new IdleStateHandler(false, readerIdleTime,
							writerIdleTime, allIdleTime, TimeUnit.SECONDS));
					pipeline.addLast("techouseFileServerCodec",new TechouseFileServerCodec());
					pipeline.addLast("fileServerDataChunkDecoder",new FileServerDataChunkDecoder());
					pipeline.addLast("fileServerDispatcher",new FileServerDispatcher());
				}

			}).option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true)
			.localAddress(new InetSocketAddress(hostname, port));
			// Bind and start to accept incoming connections.
			ChannelFuture bindFuture = boot.bind().sync();
			bindFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						System.out.println("文件服务器绑定到【" + hostname + ":" + port + "】成功！");
					} else {
						future.cause().printStackTrace(System.err);
					}
				}
			})
					// Wait until the server socket is closed.
					.channel().closeFuture().sync();
		} finally {
			// 优雅的关闭
			if (workerGroup != null) {
				workerGroup.shutdownGracefully();
			}
			if (bossGroup != null) {
				bossGroup.shutdownGracefully();
			}
		}
	}
}
