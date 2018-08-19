/*package com.icode.netty.fileserver;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class IcodeFileServer implements Runnable{
	public static final int DEFAULT_LENG_THFIELD_OFFSET = 0;
	public static final int DEFAULT_LENG_THFIELD_LENGTH = 2;
	public static final int DEFAULT_MAX_FRAME_LENGTH = 65535;
	private int backlogNum = 128;
	*//**文件服务器主机名**//*
	private String hostname;
	*//**文件服务器端口**//*
	private int port;
	*//**文件服务器 文件存放根目录**//*
	private String fileRootPath;
	
	private long readerIdleTime = 300;
	private long writerIdleTime = 300;
	private long allIdleTime = 300;
	
	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workerGroup;
	private boolean isRunning;
	private Channel serverChannel;
	@Override
	public void run() {
		//绑定端口Future
		try {
			bindServer()
		}catch (Exception e) {
			if(e instanceof BindException) {
				System.err.println("【"+hostname+":"+port+"】已被占用！！！");
			}else {
				e.printStackTrace();
			}
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
	
	*//**
	 *   关闭文件服务器
	 *//*
	public void closeServer() {
		System.out.println("-->请求关闭文件服务器");
		if(isRunning) {
			setRunning(false);
			//设置服务器关闭Future
			ChannelFuture closeFuture = serverChannel.close();
			closeFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						System.out.println("文件服务器【"+hostname+":"+port+"】关闭成功！");
						setRunning(false);
					}else {
						future.cause().printStackTrace(System.err);
					}
				}
			});
		}
	}
	
	*//**
	 * @return 返回绑定Future
	 * @throws InterruptedException 
	 *//*
	private ChannelFuture bindServer() throws InterruptedException {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		ServerBootstrap boot = new ServerBootstrap();
			
		boot
			.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
//					pipeline.addLast(new LoggingHandler(LogLevel.INFO));
					pipeline.addLast("idleStateHandler",new IdleStateHandler(false, readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
					pipeline.addLast("httpServerCodec",new HttpServerCodec());
					pipeline.addLast(new FileServerRequestController());
				}
				
			})
			.option(ChannelOption.SO_BACKLOG, backlogNum)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.localAddress(new InetSocketAddress(hostname, port));
		// Bind and start to accept incoming connections.
		return boot.bind().sync();
	}
	
	
	public int getBacklogNum() {
		return backlogNum;
	}
	public void setBacklogNum(int backlogNum) {
		this.backlogNum = backlogNum;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getFileRootPath() {
		return fileRootPath;
	}
	public void setFileRootPath(String fileRootPath) {
		this.fileRootPath = fileRootPath;
	}
	public NioEventLoopGroup getBossGroup() {
		return bossGroup;
	}
	public void setBossGroup(NioEventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
	}
	public NioEventLoopGroup getWorkerGroup() {
		return workerGroup;
	}
	public void setWorkerGroup(NioEventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
*/