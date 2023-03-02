package com.bbw.god.login.repairdata;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.enums.ZxzDifficultyEnum;
import com.bbw.god.game.zxz.rank.ZxzRankService;
import com.bbw.god.game.zxz.service.InitUserZxzService;
import com.bbw.god.game.zxz.service.ZxzService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.RESET_USER_ZXZ;

/**
 * 重置玩家诛仙阵的信息
 * @author: hzf
 * @create: 2023-02-09 11:21
 **/
@Service
public class ResetUserZxzService implements BaseRepairDataService  {
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private InitUserZxzService initUserZxzService;
    @Autowired
    private UserZxzFourSaintsService userZxzFourSaintsService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private GameUserService gameUserService;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(RESET_USER_ZXZ)) {
            //四圣数据重置
            List<UserZxzFourSaintsCardGroupInfo> userZxzFourSaintsCardGroups = userZxzFourSaintsService.getUserZxzFourSaintsCardGroups(gu.getId());
            if (ListUtil.isNotEmpty(userZxzFourSaintsCardGroups)) {
                gameUserService.deleteItems(gu.getId(),userZxzFourSaintsCardGroups);
            }
            List<UserZxzFourSaintsInfo> userZxzFourSaints = userZxzFourSaintsService.getUserZxzFourSaints(gu.getId());
            if (ListUtil.isNotEmpty(userZxzFourSaints)) {
                gameUserService.deleteItems(gu.getId(),userZxzFourSaints);
            }
            //诛仙阵数据重置
            List<UserZxzRegionInfo> userZxzRegions = zxzService.getUserZxzRegions(gu.getId());
            if (ListUtil.isNotEmpty(userZxzRegions)) {
                userCacheService.delUserDatas(userZxzRegions);
            }
            List<UserZxzCardGroupInfo> userZxzCardGroup = zxzService.getUserZxzCardGroup(gu.getId());
            if (ListUtil.isNotEmpty(userZxzCardGroup)){
                userCacheService.delUserDatas(userZxzCardGroup);
            }
            UserZxzInfo userZxz = zxzService.getUserZxz(gu.getId());
            if (null != userZxz) {
                gameUserService.deleteItem(userZxz);
            }
            List<UserEntryInfo> userEntry = zxzService.getUserEntry(gu.getId());
            if (ListUtil.isNotEmpty(userEntry)) {
                gameUserService.deleteItems(gu.getId(), userEntry);
            }
            List<UserPassRegionCardGroupInfo> userPassRegionCardGroupInfos = zxzService.getUserPassRegionCardGroupInfos(gu.getId());
            if (ListUtil.isNotEmpty(userPassRegionCardGroupInfos)) {
                gameUserService.deleteItems(gu.getId(), userPassRegionCardGroupInfos);
            }
            initUserZxzService.initUserZxz(gu.getId());
        }
    }
}
