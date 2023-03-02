package com.bbw.god.gameuser.helpabout;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户帮助阅读奖励 返回集
* @author lwb  
* @date 2019年4月10日  
* @version 1.0  
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDHelpAbout extends RDSuccess implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<UserHelpAbout.Info> helpAbouts = null;// 所有帮助列表
	
}
