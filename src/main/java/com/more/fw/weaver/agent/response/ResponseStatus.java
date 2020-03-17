package com.more.fw.weaver.agent.response;

public enum ResponseStatus {
	
	SUCCESS(0, ""), 
	BAD_REQUEST(400, "请求参数错误, 请检查!"), 
	UNLOGIN(401, "您未登录或者会话超时, 请重新登录!"),
	FORM_INVALID(402, "表单数据校验错误"),
	UNAUTHORIZED(403, "您无权限访问这个资源!"), 
	NOT_FOUND(404, "资源不存在"),
	FORM_SUBMIT_REPEAT(405, "表单重复提交"),
	SERVER_ERROR(500, "服务器内部错误, 请联系管理处理!");
	
	private int code;	//状态码
	private String msg;	//消息
	
	private ResponseStatus(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
	
	
}
