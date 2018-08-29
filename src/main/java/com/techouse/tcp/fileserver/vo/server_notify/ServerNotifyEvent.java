package com.techouse.tcp.fileserver.vo.server_notify;

import com.techouse.tcp.fileserver.event.CloseConnectionEvent;

public class ServerNotifyEvent {
	public static ServerNotifyEvent CLOSE_CONNECTION_EVENT = new ServerNotifyEvent(CloseConnectionEvent.class);
	private String eventClass;
	public ServerNotifyEvent(Class<?> clazz) {
		this.eventClass = clazz.getName();
	}
	public ServerNotifyEvent(String eventClass) {
		this.eventClass = eventClass;
	}

	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}
}
