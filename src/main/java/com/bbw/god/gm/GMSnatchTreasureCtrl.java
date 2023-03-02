package com.bbw.god.gm;

import com.bbw.common.*;
import com.bbw.db.redis.RedisSetUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.redis.GameUserDataRedisUtil;
import com.bbw.god.gameuser.redis.UserRedisKey;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchTreasureStatisticService;
import com.bbw.god.mall.snatchtreasure.UserSnatchTreasureBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 夺宝管理接口
 *
 * @author longwh
 * @date 2023/2/28 8:49
 */
@RestController
@RequestMapping("/gm")
public class GMSnatchTreasureCtrl extends AbstractController {
    @Autowired
    private GameUserDataRedisUtil gameUserDataRedisUtil;
    @Autowired
    private SnatchTreasureStatisticService snatchTreasureStatisticService;
    @Autowired
    private RedisSetUtil<String> statusSetRedis;
    @Autowired
    private InsRoleInfoService insRoleInfoService;

    /**
     * 删除最近登录时间为“指定时段”的玩家的夺宝开箱数据
     *
     * @param startIntDate 开始时间：yyyyMMdd
     * @param endIntDate 结束时间：yyyyMMdd
     * @return
     */
    @RequestMapping("/delUserSnatchOpenBoxData")
    public Rst delUserSnatchOpenBoxData(String startIntDate, String endIntDate) {
        // 校验时间参数格式
        boolean isStartDate = ValidateUtil.isDate(startIntDate, DateUtil.DATE_INT_PATTERN);
        boolean isEndDate = ValidateUtil.isDate(endIntDate, DateUtil.DATE_INT_PATTERN);
        if (!isStartDate || !isEndDate) {
            return Rst.businessFAIL("时间格式错误");
        }
        // 获取“指定时段”玩家uids
        List<Long> uids = insRoleInfoService.getUidsLoginBetween(StrUtil.getInt(startIntDate), StrUtil.getInt(endIntDate));
        if (ListUtil.isEmpty(uids)) {
            return Rst.businessFAIL("未找到符合条件的玩家");
        }
        uids.clear();
        uids.add(221207008500001L);
        for (Long uid : uids) {
            // 删除夺宝抽奖统计数据
            snatchTreasureStatisticService.clean(uid);
            // 删除玩家夺宝开箱数据
            List<UserSnatchTreasureBox> snatchTreasureBoxList = gameUserService.getMultiItems(uid, UserSnatchTreasureBox.class);
            List<Long> ids = snatchTreasureBoxList.stream().map(UserData::getId).collect(Collectors.toList());
            gameUserDataRedisUtil.deleteFromRedis(uid, ids, UserSnatchTreasureBox.class);
            // 删除玩家夺宝开箱资源的数据库导入标识
            String dbLoadKey = "load";
            String runTimeVarKey = UserRedisKey.getRunTimeVarKey(uid, dbLoadKey);
            statusSetRedis.remove(runTimeVarKey, UserDataType.USER_SNATCH_TREASURE_BOX.getRedisKey());
        }
        return Rst.businessOK("已处理个数：" + uids.size());
    }
}