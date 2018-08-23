package com.techouse.tcp.fileserver;

/**
 * 接收文件数据任务状态接口类，提供状态监听回调
 * @author xiaobao
 *
 */
public interface RecvFileTaskStatusListener {
	/**捕获到文件接收异常**/
	public void caughtException(RecvFileTask task,Exception e)throws Exception ;
	/**
	 * 任务处理完成
	 */
	public void taskFinished(RecvFileTask task);
}
