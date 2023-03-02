package com.bbw.god.gameuser.treasure;

import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 查看奖励内容
 * @date 2020/3/17 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RDSeeAward extends RDCommon {
	private static final long serialVersionUID = 1L;
	/**可产出的法宝id集合*/
	private List<Integer> treasureIds;
	/**可选择的奖励数量*/
	private Integer ableSelected;
}
