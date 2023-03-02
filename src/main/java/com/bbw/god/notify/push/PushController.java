package com.bbw.god.notify.push;

import com.bbw.common.StrUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 推送功能控制器
 * @date 2019/12/20 17:47
 */

@RestController
public class PushController extends AbstractController {
	@Autowired
	private GameUserService gameUserService;

	@RequestMapping(CR.Push.GET_PUSH)
	public RDPushInfo getPush() {
		UserPush userPush = gameUserService.getSingleItem(getUserId(), UserPush.class);
		if (userPush != null) {
			List<Integer> ablePushList = userPush.getAblePushList();
			return RDPushInfo.getInstance(ablePushList);
		}
		List<Integer> pushList = PushEnum.getAllPushValueList();
		userPush = new UserPush(getUserId(), pushList);
		gameUserService.addItem(getUserId(), userPush);
		return RDPushInfo.getInstance(pushList);
	}

	@RequestMapping(CR.Push.UPDATE_PUSH)
	public RDPushInfo updatePush(String pushValueList) {
		List<Integer> pushList = new ArrayList<>();
		if (!StrUtil.isBlank(pushValueList)) {
			String[] pushValue = pushValueList.split(",");
			for (String str : pushValue) {
				int value = Integer.parseInt(str);
				if (PushEnum.fromValue(value) != null) {
					pushList.add(value);
				}
			}
		}
		UserPush userPush = gameUserService.getSingleItem(getUserId(), UserPush.class);
		if (userPush == null) {
			userPush = new UserPush(getUserId());
		}
		userPush.setAblePushList(pushList);
		gameUserService.updateItem(userPush);
		return RDPushInfo.getInstance(pushList);
	}
}
