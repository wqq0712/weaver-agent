package com.more.fw.weaver.agent.response;

public class ResultModel<T> extends ResponseModel{
	
	private T data;
	
	public ResultModel(ResponseStatus respStatus, T data) {
		super(respStatus);
		this.data = data;
	}
	
	public ResultModel(int code, String msg, T data) {
		super(code, msg);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public static <T> ResultModel<T> success(String msg, T data) {
		return new ResultModel<T>(ResponseStatus.SUCCESS.getCode(), msg , data);
	}
	
	public static <T> ResultModel<T> fail(ResponseStatus respStatus, T data) {
		return new ResultModel<T>(respStatus, data);
	}
}
