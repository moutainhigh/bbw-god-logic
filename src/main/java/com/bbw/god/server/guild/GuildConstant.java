package com.bbw.god.server.guild;

/**
* @author lwb  
* @date 2019年5月15日  
* @version 1.0  
*/
public class GuildConstant {

	public static final int MEMBER_FILL=-1;//行会满员
	public static final int MEMBER_CAN_JOIN=0;//行会可申请
	public static final int MEMBER_JOINING=1;//已在审核列表
	
	public static final int STATUS_NOT=-1;//任务不可接
	public static final int STATUS_NORMAL=0;//可接
	public static final int STATUS_DO=1;//已接
	public static final int STATUS_FINISHED=2;//完成
	
	public static final int REWARD_TYPE=66; //行会类型的物品 如经验和贡献
	public static final int REWARD_EXP_ID=100001;//经验ID
	public static final int REWARD_CONTRBUTION_ID=100002;//贡献ID
	
	public static final int OPTION_ACCEPT=0;//任务接受
	public static final int OPTION_GAIN=1;//领奖
	public static final int OPTION_CANCEL=2;//取消
	
	public static final int COMPLETE=8;//八卦任务每日可完成次数
	public static final int OPEN_LEVEL=12;//开放等级
	public static final int NAME_LENGTH=6;//名称长度
	public static final int MAX_JOIN=6;
	
	public static final int GRADE_NOMAL=0;//普通成员
	public static final int GRADE_VICE_BOSS=1;//副队长
	public static final int GRADE_BOSS=2;//队长
}
