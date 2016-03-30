package com.dummas.calendar.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespInfo {
	
	private int status;
	private String reason;
	private Object result;
	
	public RespInfo(int status, String reason, Object result)
	{
		this.status = status;
		this.reason = reason;
		this.result = result;
	}
	
	public RespInfo(int status, String reason)
	{
		this.status = status;
		this.reason = reason;
	}

	public int getStatus()
	{
		return status;
	}
	
	public String getReason()
	{
		return reason;
	}
	
	public Object getResult()
	{
		return this.result;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public void setReason(String reason)
	{
		this.reason = reason;
	}
	
	public void setResult(Object result)
	{
		this.result = result;
	}
}
