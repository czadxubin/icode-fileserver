package com.techouse.tcp.fileserver.handler;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.techouse.tcp.fileserver.FileClientUploadFileManager;
import com.techouse.tcp.fileserver.RecvFileTask;
import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.dto.TechouseResponse;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.exception.TaskCountOutOfBoundException;
import com.techouse.tcp.fileserver.po.FileUploadRequestInfo;
import com.techouse.tcp.fileserver.utils.CommonUtils;
import com.techouse.tcp.fileserver.utils.ConstantsUtils;
import com.techouse.tcp.fileserver.vo.client_auth.ClientAuthReqBody;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadReqBody;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadResBody;

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
public class TechouseFileUploadServerHandlerBack extends BaseFileServerHanlder<FileUploadReqBody> {
	private ClientAuthReqBody clientAuthInfo;
	private FileClientUploadFileManager uploadManager;
	public TechouseFileUploadServerHandlerBack() {
		this.uploadManager = new FileClientUploadFileManager();
	}
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
			TransBinaryData binaryData = (TransBinaryData )msg;
			byte[] data = binaryData.getData();
			int taskId = data[3] & 0xFF | (data[2] & 0xFF) << 8 |(data[1] & 0xFF) << 16 | (data[0] & 0xFF) << 24;
			RecvFileTask task = uploadManager.getTask(taskId);
			if(task!=null) {
			try {
				task.handleFileData(binaryData);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("接收文件流发生异常，移除任务实例");
				}
			}else {
			  System.out.println("任务实例不存在...");
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
		File lockFile = null;
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
				File file = new File(filePath);
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
						RecvFileTask task = new RecvFileTask(info);
						task.addListener(uploadManager);
						try {
							Integer taskId = uploadManager.publishRecvFileTask(task);
							info.setUpload_id(taskId);
							FileUploadResBody resBody = new FileUploadResBody();
							resBody.setUpload_id(taskId);
							//成功返回准备接收文件数据
							response = CommonUtils.generateGenericReponse(req_id, req_type, "1", "成功",resBody);
						}catch (TaskCountOutOfBoundException e) {
							response =  CommonUtils.generateGenericReponse(req_id, req_type, "0", "达到上传服务可处理上限，稍后重试！");
							//删除锁文件
							lockFile.delete();
						}
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
}
