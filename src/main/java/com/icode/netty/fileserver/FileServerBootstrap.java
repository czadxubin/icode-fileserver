package com.icode.netty.fileserver;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

public class FileServerBootstrap {
//	public static void main(String[] args) throws Exception {
//		String hostname = "127.0.0.1";
//		int port = 8888;
//		IcodeFileServer icodeFileServer = new IcodeFileServer();
//		icodeFileServer.setBacklogNum(128);
//		icodeFileServer.setHostname(hostname);
//		icodeFileServer.setPort(port);
//		new Thread(icodeFileServer).start();
//	}
	public static final int DEFAULT_LENG_THFIELD_OFFSET = 0;
	public static final int DEFAULT_LENG_THFIELD_LENGTH = 2;
	public static final int DEFAULT_MAX_FRAME_LENGTH = 65535;
	
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
			.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
//						pipeline.addLast(new LoggingHandler(LogLevel.INFO));
					pipeline.addLast("idleStateHandler",new IdleStateHandler(false, readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
					pipeline.addLast("httpServerCodec",new HttpServerCodec());
					pipeline.addLast(new FileServerRequestController());
				}
				
			})
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.localAddress(new InetSocketAddress(hostname, port));
			// Bind and start to accept incoming connections.
			ChannelFuture bindFuture = boot.bind().sync();
			bindFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						System.out.println("文件服务器绑定到【"+hostname+":"+port+"】成功！");
					}else {
						future.cause().printStackTrace(System.err);
					}
				}
			})
			// Wait until the server socket is closed.
			.channel().closeFuture().sync();
		}finally {
			//优雅的关闭
			if(workerGroup!=null) {
				workerGroup.shutdownGracefully();
			}
			if(bossGroup!=null) {
				bossGroup.shutdownGracefully();
			}
		}
	}
}
