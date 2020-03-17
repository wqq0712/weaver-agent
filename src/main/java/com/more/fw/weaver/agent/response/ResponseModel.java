package com.more.fw.weaver.agent.response;


/**
 * 强烈建议所有返回结果都继承该类
 * 响应对象
 * @author qq.wang
 * 2018年7月6日
 */
public class ResponseModel {
	
	/**
	 * 响应码
	 */
	protected int code;
	
	/**
	 * 响应消息
	 */
	protected String msg;
	
	public ResponseModel(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public ResponseModel(ResponseStatus respStatus) {
		this.code = respStatus.getCode();
		this.msg = respStatus.getMsg();
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public static ResponseModel success(String msg) {
		return new ResponseModel(ResponseStatus.SUCCESS.getCode(), msg);
	}
	
	public static ResponseModel fail(ResponseStatus respStatus) {
		return new ResponseModel(respStatus.getCode(), respStatus.getMsg());
	}
	
	/**
	 * 数据校验失败返回json对象专用
	 * @param code
	 * @param msg
	 * @return
	 */
	public static ResponseModel validFail(String msg) {
		return new ResponseModel(ResponseStatus.FORM_INVALID.getCode(), msg);
	}
	
}
