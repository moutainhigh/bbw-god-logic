package com.bbw.god.gameuser.limit;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUserService;

@Service
public class UserLimitService {

	@Autowired
	private GameUserService gameUserService;

	public UserLimit getLoginLimit(long guId) {
		Date now = DateUtil.now();
		List<UserLimit> userLimits = gameUserService.getMultiItems(guId, UserLimit.class);
		if (ListUtil.isNotEmpty(userLimits)) {
			userLimits = userLimits.stream().filter(ul -> ul.getType() == UserLimitType.LOGIN_LIMIT.getValue())
					.collect(Collectors.toList());
			if (ListUtil.isNotEmpty(userLimits)) {
				UserLimit ul = userLimits.get(userLimits.size() - 1);
				if (DateUtil.isBetweenIn(now, ul.getLimitBegin(), ul.getLimitEnd())) {
					return ul;
				}
			}
		}
		return null;
	}

	/**
	 * 根据玩家id判断是否限制登陆
	 * @param uid 玩家id
	 * @return
	 */
	public boolean isLimit(long uid) {
		UserLimit loginLimit = getLoginLimit(uid);
		if (loginLimit == null) {
			return false;
		}
		long limitEnd = DateUtil.toDateTimeLong(loginLimit.getLimitEnd());
		return DateUtil.toDateTimeLong() <= limitEnd;
	}
}
