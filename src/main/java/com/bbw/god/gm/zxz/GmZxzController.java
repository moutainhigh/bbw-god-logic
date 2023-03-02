package com.bbw.god.gm.zxz;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.CR;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.game.zxz.rd.*;
import com.bbw.god.game.zxz.service.*;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author: hzf
 * @create: 2022-09-24 15:40
 **/
@RestController
public class GmZxzController {
    @Autowired
    private ZxzLogic zxzLogic;
    @Autowired
    private ZxzService zxzService;
    @Autowired
    private ZxzRefreshService zxzRefreshService;
    @Autowired
    private InitZxzEnemyService initZxzEnemyService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ZxzRepairRankService zxzRepairRankService;

    @Autowired
    private InitUserZxzService initUserZxzService;
    @Autowired
    private RedisZSetUtil<Long> rankingList;
    @Autowired
    private UserCacheService userCacheService;

    long uid = 210831009600001L;

    /**
     * 修护榜单
     * @param serverGroup
     * @param regionId
     * @param beginDate
     */
    @GetMapping("/gm/zxz!repairRank")
    public void repairRank(Integer serverGroup,Integer regionId,Integer beginDate){
        zxzRepairRankService.repairRank(serverGroup,regionId,beginDate);
    }

    /**
     * 诛仙阵敌方配置初始化
     *
     * @return
     */
    @GetMapping("/gm/zxzInit")
    public ZxzInfo zxzInit() {
        return initZxzEnemyService.initZxzEnemyConfig();
    }

    /**
     * 删除敌方配置
     * @param beginDate
     * @return
     */
    @GetMapping("/gm/delZxzInit")
    public RDSuccess delZxzInit(String beginDate){
        return initZxzEnemyService.delZxzInit(beginDate);
    }
    /**
     * 设置符册
     * @param regionId
     * @param fuCeDataId
     * @return
     */

    @GetMapping("gm/zxz!setFuCe")
    public RDSuccess setFuCe(Integer regionId,long fuCeDataId){
        return zxzLogic.setFuCe(uid,regionId,fuCeDataId);
    }

    /**
     * 敌方配置
     * @param regionId 区域Id
     * @return
     */
    @GetMapping("gm/zxz!enemyConfig")
    public RdEnemyRegion enemyConfig(Integer regionId){
        return zxzLogic.enemyRegion(regionId);
    }


    /**
     * 进入诛仙阵
     * @return
     */
    @GetMapping("gm/zxz!enter")
    public RdZxzLevel enter() {
        return zxzLogic.enter(uid);
    }


    /**
     * 进入难度
     * @param difficulty 难度
     * @param regionId 区域Id
     * @return
     */
    @GetMapping("gm/zxz!enterLevel")
    public RdZxzRegion enterLevel(Integer difficulty, Integer regionId){
        return zxzLogic.enterLevel(uid,difficulty,regionId);
    }

    /**
     * 进入区域
     * @param regionId 区域Id
     * @return
     */
    @GetMapping("gm/zxz!enterRegion")
    public RdZxzRegionDefender enterRegion(Integer regionId){
        return zxzLogic.enterRegion(uid,regionId);
    }



    /**
     * 编辑词条
     * @param entries 词条ids
     * @param unEntries 词条Ids id@等级,id@等级,id@等级
     * @param regionId 区域Id
     * @return
     */
    @GetMapping("gm/zxz!editEntry")
    public RdEntry editEntry(Integer regionId, String entries,String unEntries){
        return zxzLogic.editEntry(uid,entries,regionId,unEntries);
    }
    /**
     * 查看词条
     * @param difficulty
     * @return
     */
    @GetMapping("gm/zxz!getEntry")
    public RdZxzEntry getEntry(Integer difficulty){
        return zxzLogic.getEntry(uid,difficulty);
    }

    /**
     * 查看用户卡组
     * @param regionId
     * @return
     */
    @GetMapping("gm/zxz!getUserCardGroup")
    public RdUserCardGroup getUserCardGroup(Integer regionId){
        return zxzLogic.getUserCardGroup(uid,regionId);
    }
    /**
     * 查看上榜用户卡组
     * @param uid
     * @param regionId
     * @param beginDate
     * @return
     */
    @GetMapping("/gm/zxz!getUserRankCardGroup")
    public RdUserRankCardGroup getUserRankCardGroup(long uid, Integer regionId, Integer beginDate){
        return zxzLogic.getUserRankCardGroup(uid,regionId,beginDate);
    }
    /**
     * 诛仙阵：编辑用户卡组
     * @param cardIds 卡牌ids
     * @param regionId 区域id
     * @return
     */
    @GetMapping("gm/zxz!editCardGroup")
    public RDSuccess editCardGroup(String cardIds,Integer regionId){
        return zxzLogic.editCardGroup(uid,cardIds,regionId);
    }

    /**
     * 扫荡
     * @param uid
     * @param difficulty 难度类型
     * @return
     */
    @GetMapping("gm/zxz!mopUp")
    public RDCommon mopUp(long uid,Integer difficulty){
        return zxzLogic.mopUp(uid,difficulty);
    }


    /**
     * 区域榜单
     * @param difficulty
     * @param regionId
     * @param beginDate
     * @param startRank
     * @param endRank
     * @return
     */
    @GetMapping("gm/zxz!getZxzRank")
    public RdZxzRank getZxzRank(Integer difficulty, Integer regionId, Integer beginDate, Integer startRank, Integer endRank){
        return zxzLogic.getZxzRank(uid,difficulty,regionId,beginDate,startRank,endRank);
    }
    /**
     * 开宝箱
     * @param defenderId
     * @return
     */
    @GetMapping("gm/zxz!openBox")
    public RDCommon openBox(Integer defenderId){

        return zxzLogic.openBox(uid,defenderId);
    }

    /**
     * 开全通宝箱
     * @param difficulty
     * @return
     */
    @GetMapping("gm/zxz!openDifficultyPassBox")
    public RDCommon openAllPassBox(Integer difficulty){
        return zxzLogic.openDifficultyPassBox(uid,difficulty);
    }

    /**
     * 开启下个难度
     * @param uid
     * @param currentDifficulty 当前难度
     * @param clearanceScore 通关评分
     */
    @GetMapping("gm/zxz!openNextDifficulty")
    public void openNextDifficulty(long uid,Integer currentDifficulty, Integer clearanceScore) {
        Integer integer = zxzService.openNextDifficulty(uid, currentDifficulty, clearanceScore);
        UserZxzInfo userZxz = zxzService.getUserZxz(uid);
        UserZxzDifficulty userZxzDifficulty = userZxz.gainUserZxzLevel(integer);
        userZxzDifficulty.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        gameUserService.updateItem(userZxz);
    }

    /**
     * 重置玩家诛仙阵的数据
     * @param uids
     * @return
     */
    @GetMapping("gm/zxz!resetUserZxzData")
    public Rst resetUserZxzData(String uids){
        List<Long> uidList  = ListUtil.parseStrToLongs(uids);
        if (ListUtil.isEmpty(uidList)) {
            return Rst.businessFAIL("uid为空");
        }
        for (long uid : uidList) {
            //诛仙阵数据重置
            List<UserZxzRegionInfo> userZxzRegions = zxzService.getUserZxzRegions(uid);
            if (ListUtil.isNotEmpty(userZxzRegions)) {
                userCacheService.delUserDatas(userZxzRegions);
            }
            List<UserZxzCardGroupInfo> userZxzCardGroup = zxzService.getUserZxzCardGroup(uid);
            if (ListUtil.isNotEmpty(userZxzCardGroup)){
                userCacheService.delUserDatas(userZxzCardGroup);
            }
            UserZxzInfo userZxz = zxzService.getUserZxz(uid);
            if (null != userZxz) {
                gameUserService.deleteItem(userZxz);
            }
            List<UserEntryInfo> userEntry = zxzService.getUserEntry(uid);
            if (ListUtil.isNotEmpty(userEntry)) {
                gameUserService.deleteItems(uid, userEntry);
            }
            List<UserPassRegionCardGroupInfo> userPassRegionCardGroupInfos = zxzService.getUserPassRegionCardGroupInfos(uid);
            if (ListUtil.isNotEmpty(userPassRegionCardGroupInfos)) {
                gameUserService.deleteItems(uid, userPassRegionCardGroupInfos);
            }
            initUserZxzService.initUserZxz(uid);
        }
        return Rst.businessOK();
    }

    /**
     * 删除榜单 接口
     * @param serverGroup
     * @param zxzBeginDate
     * @return
     */
    @GetMapping("gm/zxz!delZxzRank")
    public Rst delZxzRank(int serverGroup,int zxzBeginDate){
        List<CfgZxzLevel> levels = ZxzTool.getCfg().getLevels();
        List<String> keys = new ArrayList<String>();
        for (CfgZxzLevel level : levels) {
            List<Integer> regions = level.getRegions();
            for (Integer region : regions) {
                keys.add(getKey(serverGroup,level.getDifficulty(),region,zxzBeginDate));
            }
        }
        for (String key : keys) {
            rankingList.remove(key);
        }
        return Rst.businessOK().put("keys",keys);
    }

    private String getKey(int serverGroup, int zxzLevel, int zxzRegion, int zxzBeginDate) {
        String zxzRankKey = GameRedisKey.getDataTypeKey(serverGroup, "zxzRank");
        zxzRankKey += GameRedisKey.SPLIT + zxzBeginDate;
        zxzRankKey += GameRedisKey.SPLIT + zxzLevel;
        zxzRankKey += GameRedisKey.SPLIT + zxzRegion;
        return zxzRankKey;
    }
}
