package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangEnum;
import com.bbw.god.gameuser.businessgang.Enum.BusinessGangNpcEnum;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 挖宝服务类
 *
 * @author: huanghb
 * @date: 2022/1/30 17:21
 */
@Slf4j
@Service
public class DigTreasureService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private DigTreasurePosService digTreasurePosService;
    @Autowired
    private UserBusinessGangService userBusinessGangService;

    /**
     * 是否显示挖宝
     *
     * @param uid
     * @return
     */
    public boolean isShowDigTreasure(long uid) {
        log.info("触发挖宝功能节点");
        //获得当前商帮好感度
        UserBusinessGangInfo userBusinessGang = userBusinessGangService.getUserBusinessGang(uid);
        Integer currntBusinessGang = userBusinessGang.getCurrentBusinessGang();
        Integer npcFavorability = userBusinessGang.getFavorability(BusinessGangNpcEnum.CHI_ZL.getId());
        //是否临时开启挖宝功能
        Integer tempOpenNeedFavorability = DigTreasureTool.getTempOpenNeedFavorability();
        boolean isTempOpenDigTreasure = currntBusinessGang == BusinessGangEnum.ZHAO_BAO.getType() && npcFavorability >= tempOpenNeedFavorability;
        if (isTempOpenDigTreasure) {
            return true;
        }
        //是否永久开启挖宝功能
        Integer permanentOpenNeedFavorability = DigTreasureTool.getPermanentOpenNeedFavorability();
        boolean isPermanentOpenDigTreasure = npcFavorability >= permanentOpenNeedFavorability;
        if (isPermanentOpenDigTreasure) {
            return true;
        }
        //未开启挖宝功能
        return false;
    }

    /**
     * 到新的地方时刷新挖宝情况
     *
     * @param uid
     * @return
     */
    public RDDigTreasureInfo refreshMyDigStatusToNewPalac(long uid, Integer userPos) {
        if (!isShowDigTreasure(uid)) {
            return null;
        }
        //获得玩家挖宝信息
        UserDigTreasure userDigTreasure = getUserCurrentDigTreasureByPos(uid, userPos);
        //返回玩家当前位置挖宝情况
        return RDDigTreasureInfo.getInstance(userDigTreasure);
    }


    /**
     * 获得用户当前指定位置挖宝信息
     *
     * @param uid
     * @return
     */
    public UserDigTreasure getUserCurrentDigTreasureByPos(long uid, int userPos) {
        List<UserDigTreasure> userDigTreasures = getUserCurrentDigTreasures(uid);
        //如果用户挖宝信息存在直接返回
        if (ListUtil.isNotEmpty(userDigTreasures)) {
            Optional<UserDigTreasure> optionalUserDigTreasure = userDigTreasures.stream().filter(tmp -> tmp.getBaseId() == userPos).findFirst();
            if (optionalUserDigTreasure.isPresent()) {
                return optionalUserDigTreasure.get();
            }
            return generateSingleUserDigTreasure(uid, userPos);
        }
        //如歌当前格子不存在挖宝信息则生成
        return generateSingleUserDigTreasure(uid, userPos);
    }

    /**
     * 获得用户当前所有挖宝信息
     *
     * @param uid
     * @return
     */
    protected List<UserDigTreasure> getUserCurrentDigTreasures(long uid) {
        List<UserDigTreasure> userDigTreasures = getUserDigTreasures(uid);
        //如果用户挖宝信息存在直接返回
        if (ListUtil.isNotEmpty(userDigTreasures)) {
            return userDigTreasures;
        }
        //生成藏宝信息
        userDigTreasures = generateAllUserDigTreasures(uid);
        return userDigTreasures;
    }

    /**
     * 生成单个格子挖宝信息
     *
     * @param uid
     * @param userPos
     * @return
     */
    protected UserDigTreasure generateSingleUserDigTreasure(long uid, int userPos) {
        //检查宝藏奖励id是否存在
        UserDigTreasurePos userDigTreasurePos = digTreasurePosService.getDigTreasurePos(uid);
        //奖励集合
        Integer[] floorAwardIds = userDigTreasurePos.getFloorAward(userPos);
        //生成宝藏信息
        UserDigTreasure userDigTreasure = UserDigTreasure.instance(uid, userPos, floorAwardIds);
        addDigTreasures(userDigTreasure);
        return userDigTreasure;
    }

    /**
     * 生成多个格子挖宝信息
     *
     * @param uid
     * @param ownDigTreasurePosList 拥有挖宝信息的位置集合
     * @return
     */
    protected List<UserDigTreasure> generateMultipleUserDigTreasure(long uid, List<Integer> ownDigTreasurePosList) {
        //检查宝藏奖励id是否存在
        UserDigTreasurePos userDigTreasurePos = digTreasurePosService.getDigTreasurePos(uid);
        List<Integer> poses = userDigTreasurePos.getDigForTreasurePoses();
        //生成宝藏信息
        List<UserDigTreasure> userDigTreasures = new ArrayList<>();
        for (Integer pos : poses) {
            //是否拥有挖宝信息
            boolean isOwnDigTreasureInfo = ownDigTreasurePosList.contains(pos);
            if (isOwnDigTreasureInfo) {
                continue;
            }
            Integer[] floorAwardIds = userDigTreasurePos.getFloorAward(pos);
            UserDigTreasure userDigTreasure = UserDigTreasure.instance(uid, pos, floorAwardIds);
            userDigTreasures.add(userDigTreasure);
        }
        return userDigTreasures;
    }

    /**
     * 生成所有格子藏宝信息
     *
     * @return uid
     */
    private List<UserDigTreasure> generateAllUserDigTreasures(long uid) {
        //获得挖宝位置
        UserDigTreasurePos userDigTreasurePos = digTreasurePosService.getDigTreasurePos(uid);
        List<Integer> poses = userDigTreasurePos.getDigForTreasurePoses();
        //宝藏奖励id不存在则随机生成
        int serverGroup = gameUserService.getActiveGid(uid);
        //生成玩家宝藏信息
        List<UserDigTreasure> userDigTreasures = new ArrayList<>();
        for (Integer roadId : poses) {
            Integer[] floorAwardIds = userDigTreasurePos.getFloorAward(roadId);
            UserDigTreasure userDigTreasure = UserDigTreasure.instance(uid, roadId, floorAwardIds);
            userDigTreasures.add(userDigTreasure);
        }
        addDigTreasures(userDigTreasures);
        return userDigTreasures;
    }

    /**
     * 获得玩家挖宝信息
     *
     * @param uid
     * @return
     */
    private List<UserDigTreasure> getUserDigTreasures(long uid) {
        List<UserDigTreasure> userDatas = userCacheService.getUserDatas(uid, UserDigTreasure.class);
        if (ListUtil.isEmpty(userDatas)) {
            return new ArrayList<>();
        }
        //是否下一个刷新时间点
        boolean isNextRefreshTime = DigTreasureTool.isNeedRefresh(userDatas.get(0).getDateTime());
        if (!isNextRefreshTime) {
            return userDatas;
        }
        userCacheService.delUserDatas(userDatas);
        return new ArrayList<>();
    }

    /**
     * 添加挖宝信息
     *
     * @param userDigTreasures
     */
    public void addDigTreasures(List<UserDigTreasure> userDigTreasures) {
        userCacheService.addUserDatas(userDigTreasures);
    }

    /**
     * 添加挖宝信息
     *
     * @param userDigTreasure
     */
    private void addDigTreasures(UserDigTreasure userDigTreasure) {
        userCacheService.addUserData(userDigTreasure);
    }
}
