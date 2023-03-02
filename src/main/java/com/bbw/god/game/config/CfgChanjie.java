package com.bbw.god.game.config;

import java.io.Serializable;
import java.util.List;

import com.bbw.exception.CoderException;

import lombok.Data;

/**
* @author lwb  
* @date 2019年6月17日  
* @version 1.0  
*/
@Data
public class CfgChanjie implements CfgInterface{
	private String key;
	private List<MailInfo> mails;
	private List<HonorAward> honorlv;
	private List<SpecialInfo> specialHonor;

	@Override
	public Serializable getId() {
		return key;
	}

	@Override
	public int getSortId() {
		return 1;
	}
	
	@Data
	public static class MailInfo{
		private Integer id;
		private String title;
		private String content;
		private String awards;
		private String memo;
	}
	@Data
	public static class SpecialInfo{
		private Integer id;
		private String content;
		private String memo;
	}
	public MailInfo getMail(Integer mailId) {
		for(MailInfo mail:mails) {
			if (mail.getId().equals(mailId)) {
				return mail;
			}
		}
		throw CoderException.high("阐截斗法：无效的邮件配置ID");
	}
	@Data
	public static class HonorAward{
		private Integer lv;//等级
		private String memo;//头衔说明
		private Double multiple;//倍数
		private Integer honor;//积分
	}

}
