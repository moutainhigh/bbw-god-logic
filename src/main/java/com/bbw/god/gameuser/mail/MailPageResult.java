package com.bbw.god.gameuser.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-20 10:43
 */
public class MailPageResult {
	private int totalPage = 1;//总页数
	private List<UserMail> entityList = new ArrayList<UserMail>();

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<UserMail> getEntityList() {
		return entityList;
	}

	public void addEntity(Collection<UserMail> coll) {
		entityList.addAll(coll);
	}
}
