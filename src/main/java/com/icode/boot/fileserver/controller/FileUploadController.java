package com.icode.boot.fileserver.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

@RestController
public class FileUploadController {
	@Value("${fileserver.root.path}")
	private String fileServerRootPath;
	/**
	 * 	单个文件上传
	 * @param filePart
	 * 			上传的文件Part
	 * @param overwrite
	 * 			是否允许覆盖
	 * @param dirPath
	 * 			文件存放目录
	 * @return
	 */
	@RequestMapping(value="/upload",method= {RequestMethod.POST},produces= {"application/json;charset=utf-8"})
	public String upload(MultipartFile filePart,@RequestParam(defaultValue="false")String overwrite,String dirPath){
		JSONObject resJson = new JSONObject();
		resJson.put("resCode", "0");//默认失败
		if(!StringUtils.hasText(dirPath)) {
			dirPath = "default";
		}
		//是否允许覆盖已存在文件(隐式表示默认为false)
		boolean isOverwrite = "true".equalsIgnoreCase(overwrite);
		if(StringUtils.hasText(dirPath)) {
			doSingleUpload(filePart, dirPath,isOverwrite,resJson);
		}else {
			resJson.put("resMsg", "dirPath不能为空");
		}
		return resJson.toJSONString();
	}
	/**
	 * 	处理单个文件上传
	 * @param filePart
	 * 				文件Part
	 * @param dirPath
	 * 				文件上传的目录（若果没有，自动创建）
	 * @param isOverwrite
	 * 				是否允许覆盖已存在的文件
	 * @param resJson
	 */
	private void doSingleUpload(MultipartFile filePart, String dirPath, boolean isOverwrite, JSONObject resJson) {
		String filename = filePart.getOriginalFilename();
		long startTime = System.currentTimeMillis();
		int recvfileSize = 0;
		if(filename!=null&&filename.trim()!="") {
			FileOutputStream outputStream = null;
			FileChannel outChannel = null;
			File lockFile = null;
			InputStream in = null;
			String basePath = fileServerRootPath+dirPath;
			File basePathFile = new File(basePath);
			if(!basePathFile.exists()) {
				basePathFile.mkdirs();
			}
			if(basePathFile.isDirectory()) {
				try {
					lockFile = new File(basePath,filename+".LOCK");
					boolean lockCreated = lockFile.createNewFile();
					if(lockCreated) {
						in = filePart.getInputStream();
						File file = new File(basePath,filename);
						if(!file.exists()||isOverwrite) {
							if(!file.exists()) {//当前路径下不存在需要上传的文件名
								file.createNewFile();
							}else {//当前路径下存在需要上传的文件名,允许覆盖
								file.delete();
								file.createNewFile();
							}
							if(file.exists()&&file.length()==0) {
								outputStream = new FileOutputStream(file,false);
								outChannel = outputStream.getChannel();
								byte[] buf = new byte[1024*8];
								int len = 0;
								while((len=in.read(buf))!=-1) {
									outChannel.write(ByteBuffer.wrap(buf,0,len));
									recvfileSize += len;
								}
								long endTime = System.currentTimeMillis();
								System.out.println("上传文件大小："+filePart.getSize()/1024+"KB,接收文件大小:"+recvfileSize/1024+"KB,耗时："+(endTime-startTime)+"ms");
								resJson.put("resCode", "1");
								resJson.put("resMsg", "上传成功");
							}else {
								resJson.put("resMsg", "文件已存在");
							}
						}else {
							resJson.put("resMsg", "文件已存在");
						}
					}else {
						resJson.put("resMsg", "锁文件创建失败");
					}
				}catch (Exception e) {
					resJson.put("resMsg", "文件上传失败");
				}finally {
					if(in!=null) {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(lockFile!=null) {
						lockFile.delete();
					}
					if(outChannel!=null) {
						try {
							outChannel.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(outChannel!=null) {
						try {
							outChannel.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else {
				resJson.put("resMsg","文件名不能为空");
			}
		}else{
			resJson.put("resMsg", "创建目录失败");
		}
	}
}
