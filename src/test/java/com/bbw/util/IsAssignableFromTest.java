package com.bbw.util;

import org.junit.Test;

import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.special.UserSpecial;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-11 15:48
 */
public class IsAssignableFromTest {

	@Test
	public void test() {
		System.out.println(UserCard.class.equals(UserCard.class));
		System.out.println(UserSpecial.class.equals(UserCard.class));
		System.out.println(UserCard.class.isAssignableFrom(UserCard.class));
		System.out.println(UserData.class.isAssignableFrom(UserCard.class));
		System.out.println(UserSpecial.class.isAssignableFrom(UserCard.class));
		System.out.println(UserCard.class.isAssignableFrom(UserData.class));
		System.out.println(UserSpecial.class.isAssignableFrom(UserData.class));
	}

}
