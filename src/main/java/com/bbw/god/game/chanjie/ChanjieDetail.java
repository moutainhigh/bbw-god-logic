package com.bbw.god.game.chanjie;

import java.io.Serializable;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年9月10日 下午5:06:21 
* 类说明 战斗日志JSON
*/
@Data
public class ChanjieDetail implements Serializable{
	private static final long serialVersionUID = 1L;
	private String headName1;
	private String headName2;
	private Integer honerLV1;
	private Integer honerLV2;
}
