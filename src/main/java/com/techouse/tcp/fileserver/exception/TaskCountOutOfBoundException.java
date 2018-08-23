package com.techouse.tcp.fileserver.exception;

/**
 * 任务数量超过限制
 * @author xiaobao
 *
 */
public class TaskCountOutOfBoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskCountOutOfBoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TaskCountOutOfBoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public TaskCountOutOfBoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TaskCountOutOfBoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TaskCountOutOfBoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
