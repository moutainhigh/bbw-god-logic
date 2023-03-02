package com.bbw.god.gameuser.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月21日 下午4:27:47 
* 类说明 
*/
@Data
public class CfgTaskConfig implements CfgInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private int groupId;
	private List<CfgTaskEntity> tasks;
	private List<CfgBox> boxs;
	@Override
	public Integer getId() {
		return groupId;
	}

	@Override
	public int getSortId() {
		return groupId;
	}
	@Data
	public static class CfgBox implements Serializable{
		private static final long serialVersionUID = 1L;
		private Integer id; //宝箱Id
		private Integer boxKey;
		private Integer score; //宝箱所需积分
		private Boolean isValid;//宝箱是否有效
		private List<Award> awards; //展示的奖励
	}

	/**
	 * 获取所有有效的任务和宝箱的id
	 * 
	 * @return
	 */
	public List<Integer> getAllIds() {
		List<Integer> ids = new ArrayList<Integer>();
		for (CfgTaskEntity task : tasks) {
			if (task.getIsValid()) {
				ids.add(task.getId());
			}
		}
		for (CfgBox box : boxs) {
			if (box.getIsValid()) {
				ids.add(box.getId());
			}
		}
		return ids;
	}

	public List<CfgTaskEntity> getTasksOrderBySeq() {
		return tasks.stream().sorted(Comparator.comparing(CfgTaskEntity::getSeq)).collect(Collectors.toList());
	}
}
