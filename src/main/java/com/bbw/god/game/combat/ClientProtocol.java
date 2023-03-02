package com.bbw.god.game.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bbw.common.StrUtil;
import com.bbw.god.game.combat.data.param.CardMovement;

import lombok.extern.slf4j.Slf4j;

/**
 * 客户端协议解析
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-24 16:31
 */
@Slf4j
public class ClientProtocol {
	/**
	 * 解析卡牌上阵信息。多张卡牌上阵之间以‘N’分隔，代表“AND”;卡牌位移之间以‘T’分隔，代表TO。
	 * @param pos
	 */
	public static List<CardMovement> parse(String moveToBattle) {
		if (StrUtil.isNull(moveToBattle)) {
			return new ArrayList<>(0);
		}
		HashMap<Integer, CardMovement> map = new HashMap<>();
		String[] changePos = moveToBattle.split("N");
		for (String pos : changePos) {
			String[] position = pos.split("T");
			int from = StrUtil.getInt(position[0], -1);
			CardMovement move = new CardMovement(from, StrUtil.getInt(position[1], -1));
			map.put(from, move);
		}
		List<CardMovement> unique = new ArrayList<>(map.size());
		unique.addAll(map.values());
		if (unique.size() != changePos.length) {
			log.error("存在重复的上牌数据！moveToBattle=" + moveToBattle);
		}
		return unique;
	}
}
