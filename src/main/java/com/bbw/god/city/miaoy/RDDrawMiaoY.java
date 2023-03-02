package com.bbw.god.city.miaoy;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 到达鹿台
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDrawMiaoY extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer result;// 庙宇投注结果
	private Integer attachGod = null;// 附体神仙

}
