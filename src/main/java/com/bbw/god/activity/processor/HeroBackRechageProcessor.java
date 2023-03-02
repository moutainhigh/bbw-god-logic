package com.bbw.god.activity.processor;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.bbw.god.activity.config.ActivityEnum;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年1月7日 上午10:32:28 
* 类说明 
*/
@Service
public class HeroBackRechageProcessor extends HeroBackLogic{
	public HeroBackRechageProcessor() {
		this.activityTypeList = Arrays.asList(ActivityEnum.HERO_BACK_RECHARGE);
	}
}
