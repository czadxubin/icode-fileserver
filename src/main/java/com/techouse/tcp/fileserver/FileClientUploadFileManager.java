package com.techouse.tcp.fileserver;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.techouse.tcp.fileserver.exception.TaskCountOutOfBoundException;

/**
 * 负责管理文件上传数据流，池处理方式
 * @author xiaobao
 *
 */
public class FileClientUploadFileManager implements RecvFileTaskStatusListener {
	private static int DEFAULT_MAX_TASK_COUNT=1;
	/**最大任务数量**/
	private int maxTaskCount = DEFAULT_MAX_TASK_COUNT;
	/**上传ID生成**/
	private AtomicInteger uploadIdGen = new AtomicInteger(Integer.MAX_VALUE);
	/**任务集合，维护当前正在执行的上传任务**/
	private HashMap<Integer,RecvFileTask> taskMap;
	public FileClientUploadFileManager() {
		this(DEFAULT_MAX_TASK_COUNT);
	}
	public FileClientUploadFileManager(int maxTaskCount) {
		taskMap = new HashMap<>(maxTaskCount);
		this.maxTaskCount = maxTaskCount;
	}
	/**
	 * 生成一个可用（无重复）的上传编号
	 * @return
	 */
	private Integer generateAvailableUploadId() {
		int incre = uploadIdGen.intValue();
		do {
			incre = uploadIdGen.incrementAndGet();
		}
		while(taskMap.containsKey(incre));
		return incre;
	}
	
	/**
	 * 发布任务
	 * @return
	 */
	public Integer publishRecvFileTask(RecvFileTask task) throws TaskCountOutOfBoundException{
		if(taskMap.size()>=maxTaskCount) {
			throw new TaskCountOutOfBoundException();
		}
		//获取上传编号
		Integer taskId = generateAvailableUploadId();
		taskMap.put(taskId, task);
		return taskId;
	}
	
	/**
	 * 根据任务号得到任务实例，可能返回空
	 * @param taskId
	 * @return
	 */
	public RecvFileTask getTask(int taskId) {
		return taskMap.get(taskId);
	}
	
	@Override
	public void caughtException(RecvFileTask task,Exception e) throws Exception {
		recycleResource(task);
		throw e;
		
	}
	@Override
	public void taskFinished(RecvFileTask task) {
		task.setEndTime(System.currentTimeMillis());
		recycleResource(task);
		System.out.println("上传耗时："+(task.getEndTime()-task.getStartTime())+"ms");
	}
	
	/**
	 * 回收资源
	 */
	public void recycleResource(RecvFileTask task){
		RandomAccessFile  outFile = task.getOutFile();
		File lockFile = task.getLockFile();
		if(outFile !=null) {
			try {
				outFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(lockFile!=null&&lockFile.exists()) {
			lockFile.delete();
		}
		//释放taskMap资源
		taskMap.remove(task.getTaskId());
	}
}
