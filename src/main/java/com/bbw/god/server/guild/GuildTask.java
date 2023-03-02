package com.bbw.god.server.guild;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年3月16日 下午3:16:01 
* 类说明 
*/
@Data
public class GuildTask implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer taskId;// 任务生成ID
	private Integer taskIndex;// 任务原始ID
	private Integer status = 0;
	private Integer edNumber;// 八卦数字按顺序1-8
	private Integer target;
	private Integer progressInt = 0;
	private String progress;
	private Integer level;
	private List<GuildReward> rewards;

	public void addVal(int val) {
		progressInt += val;
		if (progressInt >= target) {
			status = GuildConstant.STATUS_FINISHED;
			progressInt = target;
		}
		progress = progressInt + "/" + target;
	}

	/**
	 * 是否接受了该任务
	 * @return
	 */
	public boolean ifAccept(){
		return status!=GuildConstant.STATUS_NOT && status!=GuildConstant.STATUS_NORMAL;
	}
}
