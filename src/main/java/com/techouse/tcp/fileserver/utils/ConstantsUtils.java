package com.techouse.tcp.fileserver.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.techouse.tcp.fileserver.dto.TechouseRequestHeader;
import com.techouse.tcp.fileserver.vo.client_auth.ClientAuthReqBody;
import com.techouse.tcp.fileserver.vo.file_upload.FileUploadReqBody;
import com.techouse.tcp.fileserver.vo.server_notify.ServerNotifyEvent;

import io.netty.util.AttributeKey;

public class ConstantsUtils {
	/**文件服务器文件根路径**/
	public static String SERVER_FILE_ROOT_PATH = "E:/";
	public static final String REQUEST_HEADER_KEY = "req_h";
	public static final String REQUEST_BODY_KEY = "req_b";
	public static final String REQUEST_ID_KEY = "req_id" ;
	public static final String REQUEST_TYPE_KEY = "req_type" ;
	
	public static final String RESPONSE_HEADER_KEY = "res_h";
	public static final String RESPONSE_BODY_KEY = "res_b";
	public static final String RESPONSE_ID_KEY = "res_id" ;
	public static final String RESPONSE_TYPE_KEY = "res_type" ;
	public static class ReqTypeConstants{
		/**客户端认证请求*/
		public final static String CLIENT_AUTH = "CLIENT_AUTH";
		/**文件上传请求**/
		public final static String FILE_UPLOAD = "FILE_UPLOAD";
		/**服务端主动通知，不需回应**/
		public static final String SERVER_NOTIFY_NO_REPLY = "SERVER_NOTIFY_NO_REPLY";
	}
	public static class FileServerAttr {
		/**客户端认证完成，保存客户端认证信息**/
		public static final AttributeKey<ClientAuthReqBody> CLIENT_AUTH_ATTR_KEY = AttributeKey.newInstance("CLIENT_AUTH_ATTR_KEY");
		/**JSON请求保存请求头**/
		public static final AttributeKey<TechouseRequestHeader> RREQUEST_HEADER_ATTR_KEY = AttributeKey.newInstance("RREQUEST_HEADER_ATTR_KEY");
		/**客户端上线时间**/
		public static final AttributeKey<Date> CLIENT_ONLINE_TIME = AttributeKey.newInstance("CLIENT_ONLINE_TIME");
	}
	
	/**请求类型MAP**/
	public static Map<String,Class<?>> REQUEST_TYPE_MAP = null;	
	static {
		REQUEST_TYPE_MAP = new HashMap<>();
		REQUEST_TYPE_MAP.put(ReqTypeConstants.CLIENT_AUTH, ClientAuthReqBody.class);
		REQUEST_TYPE_MAP.put(ReqTypeConstants.FILE_UPLOAD, FileUploadReqBody.class);
	}
	
}
