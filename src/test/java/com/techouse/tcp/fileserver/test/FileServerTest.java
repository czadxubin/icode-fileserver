package com.techouse.tcp.fileserver.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.techouse.tcp.fileserver.codec.TechouseFileServerCodec;
import com.techouse.tcp.fileserver.codec.decoder.FileServerDataChunkDecoder;
import com.techouse.tcp.fileserver.test.handler.BinaryDataHandlerTest;
import com.techouse.tcp.fileserver.test.handler.FileServerClientAuthHanderTest;
import com.techouse.tcp.fileserver.test.handler.SimpleTextNoChunkHandlerTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class FileServerTest {
	public static final String UPLOAD_FILE_PATH = "D:\\迅雷下载\\eclipse-jee-oxygen-3a-win32-x86_64.zip";
	/**
	 * 	测试服务器文本处理
	 * @throws Exception 
	 */
	@Test
	public void testFileServerHandleTextNoChunk() throws Exception {
		String host = "127.0.0.1";
        int port = 8888;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); 
            b.group(workerGroup); 
            b.channel(NioSocketChannel.class); 
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ChannelPipeline pipeline = ch.pipeline();
//                	pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                	pipeline.addLast("techouseFileServerCodec",new TechouseFileServerCodec());
					pipeline.addLast("fileServerDataChunkDecoder",new FileServerDataChunkDecoder());
                	pipeline.addLast("simpleTextNoChunkHandlerTest",new SimpleTextNoChunkHandlerTest());
                	
                	
                }
            });
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); 
            // Wait until the connection is closed.
            Channel channel = f.channel();
			channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
	}
	/**
	 * 	测试服务器文本处理
	 * @throws Exception 
	 */
	@Test
	public void testFileServerHandleTextNoChunkWithConcurrent() throws Exception{
		ExecutorService executor = Executors.newFixedThreadPool(500);
		for (int i = 0; i < 1000; i++) {
			executor.execute(new Runnable() {
				private FileServerTest test = new FileServerTest();
				@Override
				public void run() {
					try {
						test.testFileServerHandleTextNoChunk();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		System.in.read();
	}
	/**
	 * 	测试服务器二进制数据处理
	 * @throws Exception 
	 */
	@Test
	public void testFileServerHandleBinaryData() throws Exception {
		String host = "127.0.0.1";
        int port = 8888;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); 
            b.group(workerGroup); 
            b.channel(NioSocketChannel.class); 
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ChannelPipeline pipeline = ch.pipeline();
//                	pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                	pipeline.addLast("techouseFileServerCodec",new TechouseFileServerCodec());
					pipeline.addLast("fileServerDataChunkDecoder",new FileServerDataChunkDecoder());
                	pipeline.addLast("simpleTextNoChunkHandlerTest",new BinaryDataHandlerTest());
                	
                	
                }
            });
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); 
            // Wait until the connection is closed.
            Channel channel = f.channel();
			channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
	}
	
	/**
	 * 	测试服务器二进制数据处理
	 * @throws Exception 
	 */
	@Test
	public void testFileServerClientAuth() throws Exception {
		String host = "127.0.0.1";
        int port = 8888;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); 
            b.group(workerGroup); 
            b.channel(NioSocketChannel.class); 
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ChannelPipeline pipeline = ch.pipeline();
//                	pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                	pipeline.addLast("techouseFileServerCodec",new TechouseFileServerCodec());
					pipeline.addLast("fileServerDataChunkDecoder",new FileServerDataChunkDecoder());
                	pipeline.addLast("fileServerClientAuthHanderTest",new FileServerClientAuthHanderTest());
                	
                	
                }
            });
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); 
            // Wait until the connection is closed.
            Channel channel = f.channel();
			channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
	}
}
