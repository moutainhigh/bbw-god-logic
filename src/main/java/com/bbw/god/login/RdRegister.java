package com.bbw.god.login;

import java.util.List;

import com.bbw.god.rd.RDSuccess;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年10月11日 下午2:24:25 
* 类说明 
*/
@Data
public class RdRegister extends RDSuccess{
	private List<String> randomNames=null;
	
	public static RdRegister putRadomNames(List<String> randomNames) {
		RdRegister rd=new RdRegister();
		rd.setRandomNames(randomNames);
		return rd;
	}
}
