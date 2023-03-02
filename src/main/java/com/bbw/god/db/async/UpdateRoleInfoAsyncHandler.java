package com.bbw.god.db.async;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.AccountRoleMapEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.AccountRoleMapService;
import com.bbw.god.db.service.InsRoleInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 区服玩家信息更新处理器
 *
 * @author: suhq
 * @date: 2021/12/16 2:16 下午
 */
@Slf4j
@Async
@Component
public class UpdateRoleInfoAsyncHandler {

	/**
	 * 1:更新整个对象，2:更新昵称,3:更新最后登录时间,4:更新等级,5:更新累充
	 *
	 * @param role
	 * @param type 1:更新整个对象，2:更新昵称,3:更新最后登录时间,4:更新等级,5:更新累充
	 */
	public void setRoleInfo(InsRoleInfoEntity role, int type) {
		try {
			//获取spring bean
			InsRoleInfoService service = SpringContextUtil.getBean(InsRoleInfoService.class);
			AccountRoleMapService arms = (AccountRoleMapService) SpringContextUtil.getBean("accountRoleMapService");
			switch (type) {
				case 0://0:插入对象对象
					//获取spring bean
					AccountRoleMapEntity entity = AccountRoleMapEntity.from(role);
					arms.insertOrUpdate(entity);
					break;
				case 1://1:更新整个对象
					service.updateById(role);
					AccountRoleMapEntity armEntity1 = AccountRoleMapEntity.from(role);
					arms.insertOrUpdate(armEntity1);
					break;
				case 2://2:更新昵称
					service.updateNickname(role.getUid(), role.getNickname());
					InsRoleInfoEntity iriEntity = service.selectById(role.getUid());
					AccountRoleMapEntity armEntity2 = AccountRoleMapEntity.from(iriEntity);
					arms.insertOrUpdate(armEntity2);
					break;
				case 3://3:更新最后登录时间
					service.updateLastLoginDate(role.getUid(), role.getLastLoginDate());
					break;
				case 4://4:更新等级
					service.updateLevel(role.getUid(), role.getLevel());
					break;
				case 5://5:更新累充
					service.incPay(role.getUid(), role.getPay());
					break;
				default:
					break;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
