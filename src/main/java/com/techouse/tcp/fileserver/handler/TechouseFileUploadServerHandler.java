package com.techouse.tcp.fileserver.handler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.TechouseResponse;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.event.FileRecvFinishedEvent;
import com.techouse.tcp.fileserver.po.FileUploadRequestInfo;
import com.techouse.tcp.fileserver.utils.CommonUtils;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.client_auth.ClientAuthReqBody;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadReqBody;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadResBody;
import com.techouse.tcp.fileserver.vo.server_notify.ServerNotifyEvent;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理文件上传的请求：
 * <p><ol>
 * <li>处理上传请求-->是否允许上传
 * <li>允许上传-->响应文件上传编码，编号唯一
 * <li>处理二进制流（即文件上传流）数据流的前四位代表，服务器上面返回的上传编号，用于分发数据至对应的处理类进行数据处理
 * <ol></p>
 * @author xiaobao
 *
 */
@SuppressWarnings("rawtypes")
public class TechouseFileUploadServerHandler extends BaseFileServerHanlder<FileUploadReqBody> {
	private ClientAuthReqBody clientAuthInfo;
	/**上传ID生成**/
	private AtomicInteger uploadIdGen = new AtomicInteger(Integer.MAX_VALUE);
	private File file;
	private File lockFile;
	private RandomAccessFile outFile;
	public TechouseFileUploadServerHandler() {}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("TechouseFileUploadHandler 被添加，准备接受文件上传请求...");
		clientAuthInfo = ctx.channel().attr(ConstantsUtils.FileServerAttr.CLIENT_AUTH_ATTR_KEY).get();
		System.out.println("客户端认证信息："+clientAuthInfo);
		super.handlerAdded(ctx);
	}
	/**
	 * 处理文件流数据
	 */
	@Override
	protected void doNotHandled(ChannelHandlerContext ctx, Object msg) {
		if(msg instanceof TransBinaryData) {
			TransBinaryData binaryData = (TransBinaryData) msg;
			try {
				if(!binaryData.isChunkedData()) {
					handleNoChunkFileData(ctx,binaryData);
				}else if(binaryData.isFirst()) {
					handleFirstChunkFileData(ctx,binaryData);
				}else if(binaryData.isContinue()) {
					handleContinueChunkFileData(ctx,binaryData);
				}else if(binaryData.isLast()) {
					handleLastChunkFileData(ctx,binaryData);
				}
			}catch (Exception e) {
				System.out.println("文件接收失败");
			}
		}
	}
	/**
	 * 处理文件上传请求
	 */
	@Override
	protected void doHandleRequest(ChannelHandlerContext ctx, FileUploadReqBody reqBody, TechouseRequestHeader reqHeader)
			throws Exception {
		System.out.println("TechouseFileUploadHandler 处理文件上传请求...");
		String req_id = reqHeader.getReq_id();
		String req_type = reqHeader.getReq_type();
		TechouseResponse response = null;
		String lockFilePath = null;
		lockFile = null;
		try {
			String file_name = reqBody.getFile_name();
			String dir_path = reqBody.getDir_path();
			long file_size = reqBody.getFile_size();
			if(StringUtils.isNotBlank(file_name)&&
					StringUtils.isNotBlank(dir_path)&&
					file_size>0) {
				String fileDirPath = ConstantsUtils.SERVER_FILE_ROOT_PATH+"/"+clientAuthInfo.getRoot_path()+"/"+dir_path;
				String filePath = fileDirPath+"/"+file_name;
				lockFilePath = filePath+".LOCK";
				lockFile = new File(lockFilePath);
				file = new File(filePath);
				if(!file.exists()) {
					boolean createLockFile = false;
					//创建文件夹
					File fileDir = lockFile.getParentFile();
					if(!fileDir.exists()) {
						fileDir.mkdirs();
					}
					createLockFile = lockFile.createNewFile();
					//锁文件创建成功，可以接收文件
					if(createLockFile) {
						FileUploadRequestInfo info = new FileUploadRequestInfo();
						info.setFile(file);
						info.setLockFile(lockFile);
						Integer taskId = uploadIdGen.incrementAndGet();
						info.setUpload_id(taskId);
						FileUploadResBody resBody = new FileUploadResBody();
						resBody.setUpload_id(taskId);
						outFile = new RandomAccessFile(file, "rw");
						//成功返回准备接收文件数据
						response = CommonUtils.generateGenericReponse(req_id, req_type, "1", "成功",resBody);
						//删除锁文件
						lockFile.delete();
					}else {
						response = CommonUtils.generateGenericReponse(req_id, req_type, "0", "存在正在上传的请求，请勿重复上传");
					}
				}else {
					response = CommonUtils.generateGenericReponse(req_id, req_type, "0", "存在同名文件");
				}
			}else {
				response = CommonUtils.generateGenericReponse(req_id, req_type, "0", "参数请求错误");
			}
		}catch (Exception e) {
			response = CommonUtils.generateGenericReponse(req_id, req_type, "0", "请求处理发生错误");
		}
		ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("进入接收文件数据模式");
					if(response!=null) {
						
					}
				}else {
					future.cause().printStackTrace();
					System.out.println("发送准备接收文件响应失败");
				}
			}
		});
	}
	/**
	 * 成功处理完成上传数据,不分块或者是分块的最后一个
	 * @param ctx
	 * @param binaryData
	 */
	private void successFinished(ChannelHandlerContext ctx, TransBinaryData binaryData) {
		//关闭资源
		if(outFile !=null) {
			try {
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//删除锁文件
		if(lockFile!=null&&lockFile.exists()) {
			lockFile.delete();
		}
		//发送成功处理上传完成
		ServerNotifyEvent fileRecv = new ServerNotifyEvent(FileRecvFinishedEvent.class);
		TechouseResponse<?> respone = CommonUtils.generateGenericReponse(UUID.randomUUID().toString(), ConstantsUtils.ReqTypeConstants.SERVER_NOTIFY_NO_REPLY, "1", "文件接收完成",fileRecv);
		ctx.writeAndFlush(respone).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				//发送关闭请求10s后主动关闭连接
				if(future.isSuccess()) {
					future.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							Date finishedTime = new Date();
							Date oneLineTime = future.channel().pipeline().channel().attr(ConstantsUtils.FileServerAttr.CLIENT_ONLINE_TIME).get();
							System.out.println(finishedTime.getTime()-oneLineTime.getTime()+"ms");
							future.channel().close();
						}
					}, 10, TimeUnit.SECONDS);
				}
			}
		});
	}
	/**
	 * 处理无分块数据传输
	 * @param binaryData
	 */
	protected void handleNoChunkFileData(ChannelHandlerContext ctx,TransBinaryData binaryData) throws Exception{
		writeFileData(binaryData.getData());
		successFinished(ctx,binaryData);
	}
	
	/**
	 * 处理第一块分块数据
	 * @param data
	 */
	protected void handleFirstChunkFileData(ChannelHandlerContext ctx,TransBinaryData binaryData)throws Exception {
		writeFileData(binaryData.getData());
	}
	/**
	 * 处理中间分块数据
	 * @param data
	 */
	protected void handleContinueChunkFileData(ChannelHandlerContext ctx,TransBinaryData binaryData)throws Exception {
		writeFileData(binaryData.getData());
	}
	
	/**
	 * 处理最后一块数据传输
	 * @param data
	 */
	protected void handleLastChunkFileData(ChannelHandlerContext ctx,TransBinaryData binaryData)throws Exception {
		writeFileData(binaryData.getData());
		successFinished(ctx,binaryData);
	}
	
	private void writeFileData(byte[] data) throws IOException {
		outFile.write(data, 0, data.length);
	}
}
