package com.bbw.god.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

/**
 * 玩家竞技明细
 * 
 * @author suhq
 * @date 2019-07-25 11:35:09
 */
@Data
@TableName("ins_game_pvp_detail")
public class InsGamePvpDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id; //
	private Integer serverGroup; // 区服组
	private Integer fightType;// 战斗类型
	private String fightTypeName;// 战斗类型
	private Integer roomId;// 房间ID
	private Long user1; // 座位号1
	private Long user2; // 座位号2
	private Long winner; // 胜利者
	private String dataJson;// 战斗详细数据
	private Long fightTime; // yyyyMMddHHmmss
}
