package com.bbw.god.gameuser.task.fshelper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年2月13日 上午11:52:03 
* 类说明 
*/
@Getter
@AllArgsConstructor
public enum FsTaskEnum {
	Daily(91, "每日任务"),
	Coc(92, "商会任务"),
	Guild(93, "行会任务"),
	NewBie(94, "新手任务"),;
	private final int val;
	private final String memo;

	public static FsTaskEnum fromVal(int val) {
		for (FsTaskEnum tEnum : values()) {
			if (tEnum.getVal() == val) {
				return tEnum;
			}
		}
		return null;
	}
}
