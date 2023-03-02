package com.bbw.god.city.chengc;

import java.io.Serializable;

import com.bbw.god.rd.RDSuccess;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年12月11日 上午10:47:02 
* 类说明 
*/
@Data
public class RDPromoteInfo extends RDSuccess implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer fy=null;
	private Integer kc=null;
	private Integer qz=null;
	private Integer tcp=null;
	private Integer jxz=null;
	private Integer lbl=null;
	private Integer dc=null;
	private Integer ldf=null;
	private Integer hv=null;
	
	public static RDPromoteInfo instance(UserCity city) {
		RDPromoteInfo info=new RDPromoteInfo();
		info.setFy(city.getFy());
		info.setKc(city.getKc());
		info.setQz(city.getQz());
		info.setTcp(city.getTcp());
		info.setJxz(city.getJxz());
		info.setLbl(city.getLbl());
		info.setDc(city.getDc());
		info.setLdf(city.getLdf());
		info.setHv(city.getHierarchy());
		return info;
	}
}
