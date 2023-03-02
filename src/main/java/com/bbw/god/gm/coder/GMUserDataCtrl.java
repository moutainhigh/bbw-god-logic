package com.bbw.god.gm.coder;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.historydata.DelHistoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 玩家数据操作接口
 *
 * @author: suhq
 * @date: 2022/11/23 3:40 下午
 */
@RestController
@RequestMapping("/gm")
public class GMUserDataCtrl extends AbstractController {
    @Value("${bbw-god.redis-userdata-in-days:10}")
    private int cacheDays;// redis中缓存的多少天内登录的用户数据

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private DelHistoryDataService delHistoryDataService;

    /**
     * 查询玩家特定类型的数据
     *
     * @param uid
     * @param dataType
     * @return
     */
    @RequestMapping("data!listUserDatas")
    public Rst listUserDatas(long uid, String dataType) {
        checkIsAbleDo(uid);
        UserDataType userDataType = UserDataType.fromRedisKey(dataType);
        List<? extends UserData> datas = gameUserService.getMultiItems(uid, userDataType.getEntityClass());
        Rst rst = Rst.businessOK();
        if (ListUtil.isEmpty(datas)) {
            return rst;
        }
        for (UserData data : datas) {
            rst.put(data.getId().toString(), JSONUtil.toJson(data));
        }
        return rst;
    }

    /**
     * 删除玩家数据
     *
     * @param uid
     * @param dataType
     * @param dataIds
     * @return
     */
    @RequestMapping("data!deleteUserDatas")
    public Rst deleteUserDatas(long uid, String dataType, String dataIds) {
        checkIsAbleDo(uid);
        List<Long> dataIdList = ListUtil.parseStrToLongs(dataIds);
        UserDataType userDataType = UserDataType.fromRedisKey(dataType);
        List<? extends UserData> datas = gameUserService.getUserDatas(uid, dataIdList, userDataType.getEntityClass());
        Rst rst = Rst.businessOK();
        if (ListUtil.isEmpty(datas)) {
            return rst;
        }
        delHistoryDataService.delUserData(uid, datas);
        return rst;
    }

    /**
     * 检查是否能执行操作
     *
     * @param uid
     */
    private void checkIsAbleDo(long uid) {
        InsRoleInfoEntity role = insRoleInfoService.selectById(uid);
        if (null == role) {
            throw ExceptionForClientTip.fromMsg("角色不存在");
        }
        Integer lastLoginDate = role.getLastLoginDate();
        Date daysAgo = DateUtil.addDays(DateUtil.now(), -cacheDays);
        int daysAgoInt = DateUtil.toDateInt(daysAgo);
        if (lastLoginDate < lastLoginDate) {
            throw ExceptionForClientTip.fromMsg("该接口不允许操作" + daysAgoInt + "之前前的数据，请找相关人员进行库操作");
        }
    }
}
