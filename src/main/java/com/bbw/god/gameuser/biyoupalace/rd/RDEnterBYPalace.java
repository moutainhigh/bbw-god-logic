package com.bbw.god.gameuser.biyoupalace.rd;

import java.util.List;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 进入碧游宫
 * 
 * @author suhq
 * @date 2019-09-10 10:10:52
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDEnterBYPalace extends RDSuccess {
	private List<RDChapter> typesChapters;// 碧游宫篇章信息
	private List<RDChapter> sbChapters;// 碧游宫篇章信息
	private Integer curChapterType = 10;// 当前修炼
	private RDFinalAward finalAward;// 真传奖励
	private Integer refreshDayTimes;// 日刷新次数
	private Integer resetDayTimes;// 日重置次数
	private String lockSkill = "";// 碧游宫刷新已解锁的技能
}
