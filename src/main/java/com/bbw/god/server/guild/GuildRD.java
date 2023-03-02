package com.bbw.god.server.guild;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.CfgGuild.BoxReward;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行会返回实体
* @author lwb  
* @date 2019年5月14日  
* @version 1.0  
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class GuildRD extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name=null;
	private List<RdGuildInfo> guilds=null;
	private GuildInfo guildInfo=null;
	private Integer taskId=null;
	private Integer totalPage=null;
	private RdGuildTaskInfo guildTask = null;
	private Integer addedGuildExp=null;
	private Integer addedContrbution=null;
	private List<UserGuild.WordsStatus> words=null;
	private List<GuildShop> shopList=null;
	private GuildInfo.GuildWords word=null;
	private String myWord=null;
	private Integer contrbution=null;
	private Integer addedEdNumber=null;
	private Integer gainNum=null;
	private Integer maxGainNum=null;
	private Integer gainedNum=null;
	private Integer produceBox=null;//产生宝箱
	private Integer guildLv = 0;
	private GuildTask taskInfo = null;
	@Data
	public static class RdGuildInfo{
		private Long id;
		private String guildName;
		private String bossName;
		private Integer lv;
		private String peopleProgress;
		private Integer status;
	}
	
	private List<PlayerInfo> players=null;
	@Data
	public static class PlayerInfo{
		private Long id;
		private String name;
		private Integer lv;
	}
	
	private List<RdMember> members=null;
	
	@Data
	public static class RdMember{
		private Long id;
		private String name;
		private Integer lv;
		private Integer contribution;
		private Long leaveDay;
		private Integer grade=0;//0级普通成员 1级副队长 2队长
	}
	@Data
	public static class RdGuildTaskInfo implements Serializable{
		private static final long serialVersionUID = 1L;
		private List<GuildTask> tasks = new ArrayList<GuildTask>();
		private Integer buildDate = 20200101;// 生成任务时间
		private Integer refreshCount = 0;// 任务刷新次数
		private Integer complete = GuildConstant.COMPLETE;// 任务完成次数
		private List<BoxReward> box = new ArrayList<BoxReward>();// 可开宝箱 大小即为可开数量
		private Integer gainBoxNum = 0;// 获得宝箱数
		private List<Integer> progress;//八卦字进度
		private int[] acceptStatus=null;//八卦字接受状态 1表示 有人接受但不在线  2表示有人接受且在线
		private Long time;
		
		public static RdGuildTaskInfo instance(UserGuildTaskInfo info) {
			RdGuildTaskInfo rdGuildTaskInfo=new RdGuildTaskInfo();
			rdGuildTaskInfo.setBox(info.getBox());
			rdGuildTaskInfo.setBuildDate(info.getBuildDate());
			rdGuildTaskInfo.setComplete(info.getComplete());
			rdGuildTaskInfo.setGainBoxNum(info.getGainBoxNum());
			rdGuildTaskInfo.setRefreshCount(info.getRefreshCount());
			rdGuildTaskInfo.setTasks(info.getTasks());
			rdGuildTaskInfo.setTime(DateUtil.millisecondsInterval(DateUtil.getDateEnd(new Date()), new Date()));
			return rdGuildTaskInfo;
		}
	}
}
