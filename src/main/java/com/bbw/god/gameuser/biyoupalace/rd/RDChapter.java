package com.bbw.god.gameuser.biyoupalace.rd;

import java.util.List;

import com.bbw.god.game.award.RDAward;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 篇章
 * 
 * @author suhq
 * @date 2019-09-10 10:10:52
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDChapter {
	private Integer type;
	private Integer chapter;// 篇章
	private Integer progress = 0;// 进度
	private List<RDAward> awards;// 奖励
	private Integer status = 0;// 状态
}
