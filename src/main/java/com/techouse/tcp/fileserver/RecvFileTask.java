package com.techouse.tcp.fileserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataContinue;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataFirst;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryChunkDataLast;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryData;
import com.techouse.tcp.fileserver.dto.trans.TransBinaryNoChunkData;
import com.techouse.tcp.fileserver.po.FileUploadRequestInfo;

/**
 * 接收文件数据任务类
 * @author xiaobao
 *
 */
public class RecvFileTask {
	private long startTime;
	private long endTime;
	private FileUploadRequestInfo info;
	private RandomAccessFile outFile;
	private File lockFile;
	private List<RecvFileTaskStatusListener> listeners = new ArrayList<>();
	public RecvFileTask(FileUploadRequestInfo info) throws Exception{
		this.startTime = System.currentTimeMillis();
		this.info = info;
		init();
	}
	public void addListener(RecvFileTaskStatusListener listener) {
		this.listeners.add(listener);
	}
	/**
	 * 对象实例化，可以做一些初始化操作
	 * @throws IOException
	 */
	protected void init()  throws IOException{
		File file = info.getFile();
		boolean createNewFile = true;
		if(!file.exists() ){
			createNewFile = file.createNewFile();
		}
		if(createNewFile) {
			outFile = new RandomAccessFile(file,"rw");
			lockFile = info.getLockFile();
		}else {
			throw new RuntimeException("创建上传任务失败");
		}
	}
	
	/**
	 * 处理二进制数据入口方法
	 * @param binaryData
	 * @throws Exception 
	 */
	public void handleFileData(TransBinaryData binaryData) throws Exception {
		try {
			if(binaryData instanceof TransBinaryNoChunkData) {
				handleNoChunkFileData((TransBinaryNoChunkData)binaryData);
			}else if(binaryData instanceof  TransBinaryChunkDataFirst) {
				handleFirstChunkFileData((TransBinaryChunkDataFirst)binaryData);
			}else if(binaryData instanceof TransBinaryChunkDataContinue) {
				handleContinueChunkFileData((TransBinaryChunkDataContinue)binaryData);
			}else if(binaryData instanceof TransBinaryChunkDataLast) {
				handleLastChunkFileData((TransBinaryChunkDataLast)binaryData);
			}
		}catch (Exception e) {
			caughtException(e);
		}
	}
	/**
	 * 捕获异常处理
	 * @param e
	 * @throws Exception 
	 */
	protected void caughtException(Exception e) throws Exception {
		for (RecvFileTaskStatusListener listener : listeners) {
			listener.caughtException(this, e);
		}
	}
	/**
	 * 处理无分块数据传输
	 * @param binaryData
	 */
	protected void handleNoChunkFileData(TransBinaryNoChunkData binaryData) throws Exception{
		writeFileData(binaryData.getData());
		//触发完成事件
		for (RecvFileTaskStatusListener listener : listeners) {
			listener.taskFinished(this);
		}
	}
	/**
	 * 处理第一块分块数据
	 * @param data
	 */
	protected void handleFirstChunkFileData(TransBinaryChunkDataFirst binaryData)throws Exception {
		writeFileData(binaryData.getData());
	}
	/**
	 * 处理中间分块数据
	 * @param data
	 */
	protected void handleContinueChunkFileData(TransBinaryChunkDataContinue binaryData)throws Exception {
		writeFileData(binaryData.getData());
	}
	
	/**
	 * 处理最后一块数据传输
	 * @param data
	 */
	protected void handleLastChunkFileData(TransBinaryChunkDataLast binaryData)throws Exception {
		writeFileData(binaryData.getData());
		//触发完成事件
		for (RecvFileTaskStatusListener listener : listeners) {
			listener.taskFinished(this);
		}
	}
	
	private void writeFileData(byte[] data) throws IOException {
		outFile.write(data, 4, data.length-4);
	}
	public FileUploadRequestInfo getInfo() {
		return info;
	}
	public void setInfo(FileUploadRequestInfo info) {
		this.info = info;
	}
	public RandomAccessFile getOutFile() {
		return outFile;
	}
	public void setOutFile(RandomAccessFile outFile) {
		this.outFile = outFile;
	}
	public File getLockFile() {
		return lockFile;
	}
	public void setLockFile(File lockFile) {
		this.lockFile = lockFile;
	}
	public List<RecvFileTaskStatusListener> getListeners() {
		return listeners;
	}
	public void setListeners(List<RecvFileTaskStatusListener> listeners) {
		this.listeners = listeners;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	/**
	 * 获取任务ID
	 * @return
	 */
	public int getTaskId() {
		return info.getUpload_id();
	}
}
