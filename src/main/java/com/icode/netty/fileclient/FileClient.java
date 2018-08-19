/*package com.icode.netty.fileclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import static com.icode.netty.fileserver.IcodeFileServer.*;

public class FileClient {
	public static void main(String[] args) throws Exception {
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
                	pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                	//Decoder
                	pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(DEFAULT_MAX_FRAME_LENGTH, DEFAULT_LENG_THFIELD_OFFSET, DEFAULT_LENG_THFIELD_LENGTH,DEFAULT_LENG_THFIELD_OFFSET,DEFAULT_LENG_THFIELD_LENGTH));
                	pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                	pipeline.addLast("frameEncoder",new LengthFieldPrepender(DEFAULT_LENG_THFIELD_LENGTH));
                	//Encoder
                	pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
               
                	pipeline.addLast(new FileClientAuthHandler());
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
*/