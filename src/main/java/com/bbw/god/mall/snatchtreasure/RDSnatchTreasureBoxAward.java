package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 周累计宝箱奖励预览
 * @date 2020/6/30 14:10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RDSnatchTreasureBoxAward extends RDCommon {
	private static final long serialVersionUID = -6387662939746672086L;
	private Integer boxId;
	private List<Award> awards;
}
