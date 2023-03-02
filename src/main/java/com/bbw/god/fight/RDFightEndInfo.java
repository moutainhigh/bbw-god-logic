package com.bbw.god.fight;

import com.bbw.god.city.yeg.YeGuaiEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 打赢野怪缓存信息供开宝箱用
 * 
 * @author suhq
 * @date 2019年3月12日 下午2:23:35
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFightEndInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	// 元素属性
	private Integer eleType = null;
	// 对手等级
	private Integer oppLevel = null;
	// 免费开箱子次数
	private Integer freeTime = null;
	// 铜钱加成
	private Double copperAddRate = null;
	// 剩余开箱子次数
	private Integer remainTime = null;
	// 野怪类型
	private YeGuaiEnum yeGtype = null;

	private String nickname=null;

	private List<Integer> openBoxTypes = new ArrayList<>();

	/** 是否是商帮特殊野怪宝箱 */
	private boolean isBusinessGang;

	public boolean ifNickname(String name){
		if (nickname==null){
			return false;
		}
		return nickname.equals(name);
	}
}
