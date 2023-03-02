package com.bbw.god.city.yed;

import com.bbw.god.game.config.city.YdEventEnum;
import lombok.Data;

import java.util.List;

/**
 * 野地事件数据
 *
 * @author suhq
 * @date 2019年3月1日 上午11:09:05
 */
@Data
public class EPYeDTrigger {
	private YdEventEnum event;// 野地的事件标识
	private Long income;// 特定事件可能要传的收益值
	private Long loss;// 特定事件可能要传的损失值
	private List<Integer> goodsIds;// 相关的特产或法宝的id集合

	public static EPYeDTrigger fromIncome(YdEventEnum event, long income) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setIncome(income);
		return ev;
	}

	public static EPYeDTrigger fromIncome(YdEventEnum event, List<Integer> goodsIds) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setGoodsIds(goodsIds);
		return ev;
	}

	public static EPYeDTrigger fromIncome(YdEventEnum event, long income, List<Integer> goodsIds) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setIncome(income);
		ev.setGoodsIds(goodsIds);
		return ev;
	}

	public static EPYeDTrigger fromEnum(YdEventEnum event) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		return ev;
	}

	public static EPYeDTrigger fromLoss(YdEventEnum event, long loss) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setLoss(loss);
		return ev;
	}

	public static EPYeDTrigger fromLoss(YdEventEnum event, List<Integer> goodsIds) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setGoodsIds(goodsIds);
		return ev;
	}

	public static EPYeDTrigger fromLoss(YdEventEnum event, List<Integer> goodsIds, long loss) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setGoodsIds(goodsIds);
		ev.setLoss(loss);
		return ev;
	}

	public static EPYeDTrigger fromLoss(YdEventEnum event, long loss, List<Integer> goodsIds) {
		EPYeDTrigger ev = new EPYeDTrigger();
		ev.setEvent(event);
		ev.setLoss(loss);
		ev.setGoodsIds(goodsIds);
		return ev;
	}
}
