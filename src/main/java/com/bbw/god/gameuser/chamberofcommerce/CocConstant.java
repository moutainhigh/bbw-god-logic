package com.bbw.god.gameuser.chamberofcommerce;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @author lwb  
* @date 2019年5月28日  
* @version 1.0  
*/
@Getter
@AllArgsConstructor
public class CocConstant {
	public static final int EXP_TASK_STATUS_DOING = -1;// ("进行中",-1),
	public static final int EXP_TASK_STATUS_FINISHED = 0;// ("完成",0),
	public static final int EXP_TASK_STATUS_GONE = 1;// ("已领取",1),

	public static final int STATUS_HAVE_BUY=2;//("已购",2),
	public static final int STATUS_CAN_BUY=1;///("可购",1),
	public static final int STATUS_CANT_BUY=0;//("不可购",0),
	
	public static final int SPECIAL_LOW=10;//("低级特产",10),
	public static final int SPECIAL_MIDDEL=20;//("中级特产",20),
	public static final int SPECIAL_HEIGH=30;//("高级特产",30),
	
	public static final int TASK_STATUS_STOP=-1;//("不可接受",-1),
	public static final int TASK_STATUS_WAIT=0;//("待接受",0),
	public static final int TASK_STATUS_DOING=1;//("已接受",1),
	public static final int TASK_STATUS_FINISHED=2;//("已完成",2),
	public static final int TASK_STATUS_GONE=3;//("已领取",3),
	
	public static final int TYPE_GIFT=1000;//("礼包",1000);

	public static final int LEVEL_LOW=1;
	public static final int LEVEL_MIDDLE=2;
	public static final int LEVEL_HEIGH=3;//"高级任务",

	public static final int TASK_TYPE_TRADE=1;//"交易"
	public static final int TASK_TYPE_TRIANING=2;//("练兵",2),
	public static final int TASK_TYPE_SPECIAL=3;//("特殊",3),

	public static final int OPEN_LEVEL=20;//开启等级
}
