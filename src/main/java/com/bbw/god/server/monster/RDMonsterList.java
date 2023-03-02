package com.bbw.god.server.monster;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 友怪列表
 *
 * @author suhq
 * @date 2019年3月13日 上午12:00:54
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDMonsterList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDMonsterInfo> monsters = null;// 好友怪物
	private Long nextBeatTime = null;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDMonsterInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private Long id;// 好友怪物ID
		private String guName;
		private String monsterName;
		private Integer headIcon = null;// 头像框
		private Integer head;
		private Integer level;
		private Integer blood;// 怪物血量
		private Integer remainTime;// 剩余时间

	}

}
