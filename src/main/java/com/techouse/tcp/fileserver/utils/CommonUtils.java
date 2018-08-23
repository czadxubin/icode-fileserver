package com.techouse.tcp.fileserver.utils;

import com.techouse.tcp.fileserver.dto.TechouseResponse;
import com.techouse.tcp.fileserver.dto.TechouseResponseHeader;

public class CommonUtils {
	public static TechouseResponse<?> generateGenericReponse(String req_id,String req_type,String res_code,String res_msg){
		return generateGenericReponse(req_id, req_type, res_code, res_msg, null);
	}
	public static <T> TechouseResponse<T> generateGenericReponse(String req_id,String req_type,String res_code,String res_msg,T resBody){
		TechouseResponseHeader resHeader = new TechouseResponseHeader(); 
		resHeader.setRes_id(req_id);
		resHeader.setRes_type(req_type);
		resHeader.setRes_code(res_code);
		resHeader.setRes_msg(res_msg);
		
		TechouseResponse<T> response = new TechouseResponse<T>();
		response.setRes_h(resHeader);
		if(resBody!=null) {
			response.setRes_b(resBody);
		}
		return response;
	}
}
