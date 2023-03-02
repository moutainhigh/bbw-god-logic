package com.bbw.common;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 返回数据。包含res,message字段
 * res == 1：业务操作失败默认代码
 * res == 2：参数错误、校验未通过。
 * res == 3：执行业务的条件不满足。前置业务不存在、关联业务不存在等。
 * res == 0：业务被正确执行并返回
 * res < 0: 系统错误。
 * res == -1: 默认的系统错误代码。
 * </pre>
 * 
 * @author lsj
 * @email lsj@bamboowind.cn
 * @date 2018年4月18日
 */
public class Rst extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public Rst() {
	}

	private Rst(int code, String msg) {
		put("res", code);
		put("message", msg);
	}

	@Override
	public Rst put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	/**
	 * 业务成功
	 * 
	 * @return
	 */
	public Rst businessSucess() {
		return businessOK("");
	}

	/**
	 * 参数错误、校验未通过
	 * 
	 * @param msg
	 * @return
	 */
	public static Rst paramFAIL(String msg) {
		Rst r = new Rst(ErrorCode.DEFAULT_PARAM_FAIL_CODE, msg);
		return r;
	}

	/**
	 * 执行业务的条件不满足。前置业务不存在、关联业务不存在等。
	 * 
	 * @param msg
	 * @return
	 */
	public static Rst conditionFAIL(String msg) {
		Rst r = new Rst(ErrorCode.DEFAULT_CONDITION_FAIL_CODE, msg);
		return r;
	}

	/**
	 * 业务成功
	 * 
	 * @return
	 */
	public static Rst businessOK() {
		return businessOK("");
	}

	/**
	 * 业务成功
	 * 
	 * @param msg
	 * @return
	 */
	public static Rst businessOK(String msg) {
		return new Rst(ErrorCode.DEFAULT_BUSINESS_OK_CODE, msg);
	}

	/**
	 * 业务执行失败
	 * 
	 * @param msg
	 * @return
	 */
	public static Rst businessFAIL(String msg) {
		Rst r = new Rst(ErrorCode.DEFAULT_BUSINESS_FAIL_CODE, msg);
		return r;
	}

	/**
	 * 国际化提示信息
	 * @param msgKey
	 * @return
	 */
	public static Rst failFromLocalMessage(String msgKey) {
		String msg = LM.I.getMsg(msgKey);
		Rst r = new Rst(ErrorCode.DEFAULT_BUSINESS_FAIL_CODE, msg);
		return r;
	}

	/**
	 * 国际化提示信息
	 * @param msgKey
	 * @param args
	 * @return
	 */
	public static Rst failFromLocalMessage(String msgKey, Object... args) {
		String msgTpl = LM.I.getMsg(msgKey);
		String msg = String.format(msgTpl, args);
		Rst r = new Rst(ErrorCode.DEFAULT_BUSINESS_FAIL_CODE, msg);
		return r;
	}

	/**
	 * 条件满足，业务执行失败
	 * 
	 * @param code
	 * @param msg
	 * @return
	 */
	public static Rst businessFAIL(int code, String msg) {
		Rst r = new Rst(code, msg);
		return r;
	}

	public static Rst loadJSONObject(JSONObject jsonObject) {
		Rst rst = new Rst();
		if (null != jsonObject) {
			for (String key : jsonObject.keySet()) {
				rst.put(key, jsonObject.get(key));
			}
		}
		return rst;
	}
	//
	//	public static JSONObject setOKMsg(JSONObject js, String message) {
	//		if (js == null) {
	//			js = new JSONObject();
	//		}
	//		js.put("message", message);
	//		js.put("res", 0);
	//		return js;
	//	}
	//
	//	public static JSONObject setOKMsg(String message) {
	//		JSONObject js = new JSONObject();
	//		js.put("message", message);
	//		js.put("res", 0);
	//		return js;
	//	}

}
