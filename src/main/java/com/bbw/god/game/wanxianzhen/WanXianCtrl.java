package com.bbw.god.game.wanxianzhen;

import com.bbw.App;
import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.WanXianCPFightLogEntity;
import com.bbw.god.detail.async.WanXianCPFightLogAsyncHandler;
import com.bbw.god.game.CR;
import com.bbw.god.game.wanxianzhen.service.ChampionPredictionService;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import com.bbw.god.game.wanxianzhen.service.WanXianScoreRankService;
import com.bbw.god.game.wanxianzhen.service.WanXianSeasonService;
import com.bbw.god.game.wanxianzhen.service.race.WanXianEliminationSeriesRace;
import com.bbw.god.game.wanxianzhen.service.race.WanXianQualifyingRace;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 万仙阵相关入口
 * @author lwb
 * @date 2020/5/9 16:43
 */
@RestController
public class WanXianCtrl extends AbstractController {
    @Autowired
    private WanXianLogic wanXianLogic;
    @Autowired
    private WanXianQualifyingRace wanXianQualifyingRace;
    @Autowired
    private WanXianSeasonService wanXianSeasonService;
    @Autowired
    private ChampionPredictionService championPredictionService;
    @Autowired
    private WanXianScoreRankService wanXianScoreRankService;
    @Autowired
    private WanXianEliminationSeriesRace wanXianEliminationSeriesRace;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private App app;
    @Autowired
    private WanXianCPFightLogAsyncHandler wanXianCPFightLogAsyncHandler;

    @RequestMapping(CR.WanXian.GET_WANXIAN_TYPE)
    public RDWanXian getWanXianType() {
        RDWanXian rd = new RDWanXian();
        if (!app.runAsProd()) {
            WanXianTool.getCountdown(1, 1000, rd);
        } else {
            WanXianTool.getCountdown(gameUserService.getActiveGid(getUserId()), 1000, rd);
        }
        return rd;
    }

    /**
     * 获取主页信息
     * 主页信息分为：常规报名页，各类比赛页面
     *
     * @return
     */
    @RequestMapping(CR.WanXian.MAIN_PAGE)
    public RDWanXian mainPage(Integer minPageSize, Integer maxPageSize, Integer type, Integer wxtype) {
        type=checkType(type);
        RDWanXian rd=new RDWanXian();
        rd.setNewSeason(0);
        int gid=gameUserService.getActiveGid(getUserId());
        WanXianTool.getCountdown(gid,type,rd);
        rd.setCurrentWxType(rd.getWxType());
        rd.setSeasonOrder(WanXianTool.getSeasonOrder(type,gid)-1);
        WanXianTool.setShowRaceEnum(wxtype,rd);
        if (rd.getNewSeason()==1){
            return rd;
        }
        minPageSize=minPageSize==null?10:minPageSize;
        maxPageSize=maxPageSize==null?10:maxPageSize;
        wanXianLogic.getMainPageInfo(getUserId(),rd,minPageSize,maxPageSize,type);
        return rd;
    }

    /**
     * 资格赛 获取榜单
     * @param current
     * @param pageSize
     * @return
     */
    @RequestMapping(CR.WanXian.LIST_QUALIFYING_RANK)
    public RDWanXian listQualifingRank(Integer current,Integer pageSize,Integer type){
        type=checkType(type);
        int gid=gameUserService.getActiveGid(getUserId());
        RDWanXian rd=new RDWanXian();
        WanXianTool.getCountdown(gameUserService.getActiveGid(getUserId()),type,rd);
        if (DateUtil.getToDayWeekDay()>2){
            rd.setCurrentWxType(rd.getWxType());
            WanXianTool.setShowRaceEnum(WanXianPageType.QUALIFYING_RACE.getVal(),rd);
        }
        String baseKey=wanXianScoreRankService.getSoreRankKey(gid,type,rd.getWxShowRace());
        List<RDWanXian.RDUser> list=wanXianQualifyingRace.getRankList(baseKey,pageSize,current);
        rd.setRanks(list);
        return rd;
    }

    /**
     * 资格赛榜单翻页
     * @param group
     * @param type
     * @return
     */
    @RequestMapping(CR.WanXian.LIST_ELIMINATION_SERIES_GROUP)
    public RDWanXian listEliminationSeriesGroup(Integer group,Integer type){
        type=checkType(type);
        RDWanXian rd=new RDWanXian();
        WanXianTool.getCountdown(gameUserService.getActiveGid(getUserId()),type,rd);
        if (rd.getWxType()!=null &&  WanXianPageType.ELIMINATION_SERIES_RACE.getVal()!=rd.getWxType()) {
			rd.setWxType(null);
			rd.setCountdown(null);
			rd.setCountdownName(null);
		}
        int gid=gameUserService.getActiveGid(getUserId());
        List<RDWanXian.RDFightLog> fightLogs=wanXianSeasonService.getFightUsers(gid,type,"group_"+group);
        int index=0;
        int res=0;
        if (DateUtil.getToDayWeekDay()>5) {
			index=8;
			res=7;
		}else {
	        if (rd.getWxShowRace()==null){
	            index=4;
	        }else {
	            switch (rd.getWxShowRace()){
	                case EMAIL_ELIMINATION_SERIES_RACE_1: index=6;res=4;break;
	                case EMAIL_ELIMINATION_SERIES_RACE_2:index=7;res=6;break;
	                case EMAIL_ELIMINATION_SERIES_RACE_3:index=8;res=7;break;
	            };
	        }
		}
        List<RDWanXian.RDFightLog> rdFightLogs=new ArrayList<>();
        for (int i=0;i<fightLogs.size();i++){
            RDWanXian.RDFightLog log=fightLogs.get(i);
            if (i<index){
                if (i>=res){
                    log.setWinner(0);
                }
                rdFightLogs.add(log);
            }
        }
        rd.setRaces(rdFightLogs);
        return rd;
    }
    /**
     * 历史荣誉榜单
     * @return
     */
    @RequestMapping(CR.WanXian.HISTORY_SEASON)
    public RDWanXian historySeason(Integer order,Integer type){
        type=checkType(type);
        int gid=gameUserService.getActiveGid(getUserId());
        RDWanXian rdWanXian=new RDWanXian();
        List<RDWanXian.RDUser> list=wanXianLogic.getHistoryRank(gid,type,order);
        if (list!=null && !list.isEmpty()){
            rdWanXian.setRankList(list);
        }
        if (WanXianLogic.TYPE_SPECIAL_RACE==type.intValue()){
            //补充当前特色赛类型
        	if (order==null) {
				order=WanXianTool.getSeasonOrder(type,gid)-1;
			}
            WanXianSpecialType wst=wanXianSeasonService.getSpecialTypeByOrder(gid,order);
            rdWanXian.setCurrentRace(wanXianSeasonService.getCurrentSpecialType(gid).getVal());
            rdWanXian.setPreRace(wst.getVal());
        }
        rdWanXian.setSeasonOrder(WanXianTool.getSeasonOrder(type,gid)-1);
        return rdWanXian;
    }
    /**
     * 报名
     * @return
     */
    @RequestMapping(CR.WanXian.SIGN_UP)
    public Rst signUp(Integer type){
        type=checkType(type);
        int gid=gameUserService.getActiveGid(getUserId());
        wanXianLogic.signUpRace(type,getUserId(),gid);
        return Rst.businessOK();
    }

    /**
     * 保存卡组
     * @param cardIds
     * @return
     */
    @RequestMapping(CR.WanXian.SAVE_CARDGROUP)
    public Rst saveCardGroup(String cardIds,Integer type){
        type=checkType(type);
        int gid=gameUserService.getActiveGid(getUserId());
        wanXianLogic.saveCardGroup(getUserId(),type,cardIds,gid);
        return Rst.businessOK();
    }

    /**
     * 获取战斗日志
     * @param logType  1~7 返回玩家 对应星期的战绩，8，9为全部的小组赛和决赛记录
     * @param uid 传递UID时为指定玩家战绩，
     * @return
     */
    @RequestMapping(CR.WanXian.LIST_FIGHT_LOGS)
    public RDWanXian listFightLogs(Integer logType,Long uid,Integer type){
        type=checkType(type);
        if (uid!=null){
            //获取指定玩家的所有战报
            return wanXianLogic.getFightLogs(uid,type);
        }
        if (logType!=null && (logType==8||logType==9)){
            return wanXianLogic.getFightLogs(gameUserService.getActiveGid(getUserId()),type,logType);
        }
        return wanXianLogic.getMyHistoryFightLogs(getUserId(),type);
    }
    /**
     * 冠军预测界面
     * @return
     */
    @RequestMapping(CR.WanXian.CHAMPION_PREDICTION_PAGE)
    public RDWanXian championPredictionPage(Long uid,Integer type){
        type=checkType(type);
        RDWanXian rd=new RDWanXian();
        WanXianTool.getCountdown(gameUserService.getActiveGid(getUserId()),type,rd);
        int gid=gameUserService.getActiveGid(getUserId());
        rd.setCurrentRace(WanXianTool.getCurrentSpecialType(gid));
        championPredictionService.getChampionPredictionPage(uid==null?getUserId():uid,type,rd);
        return rd;
    }

    /**
     * 冠军预测
     * @param raceType 4或8对应4强和8强
     * @param users
     * @return
     */
    @RequestMapping(CR.WanXian.CHAMPION_PREDICTION)
    public RDCommon championPrediction(Long uid,Integer raceType, String users,Integer type){
        type=checkType(type);
        return championPredictionService.championPrediction(uid==null?getUserId():uid,raceType,type,users);
    }
    /**
     * 获取万仙阵卡组
     * @return
     */
    @RequestMapping(CR.WanXian.GET_CARD_GROUP)
    public RDWanXianCardGroup getCardGroup(Long uid,Integer type,Integer season){
        type=checkType(type);
        RDWanXian rdWanXian = null;
        if (uid!=null && season!=null){
            rdWanXian= wanXianLogic.getUserHistoryCardGroup(uid,type,season);
        }else {
            if (uid==null) {
                uid=getUserId();
            }
            rdWanXian= wanXianLogic.getUserCardGroup(uid,type);
        }
        RDWanXianCardGroup rd=new RDWanXianCardGroup();
        rd.setCurrentWxType(rdWanXian.getCurrentWxType());
        if(rdWanXian.getCardGroup()!=null){
            rd.updateCardGroup(rdWanXian.getCardGroup());
        }
        rd.setMyStatus(rdWanXian.getMyStatus());
        return rd;
    }

    /**
     * 获取奖励预览
     * @return
     */
    @RequestMapping(CR.WanXian.LIST_AWARDS)
    public RDWanXian listAward(Integer type){
        type=checkType(type);
        return wanXianLogic.listAward(getUserId(),type);
    }

    /**
     * 记录点击预测界面中的选手战绩 查看视频的玩家流程
     * @param vidKey
     * @return
     */
    @RequestMapping(CR.WanXian.PLAY_VIDEO)
    public Rst playVideo(String vidKey){
        Long uid=getUserId();
        if (uid==null){
            return Rst.businessOK();
        }
        int lv = gameUserService.getGameUser(uid).getLevel();
        WanXianCPFightLogEntity logEntity = WanXianCPFightLogEntity.instance(getUserId(), lv, vidKey);
        wanXianCPFightLogAsyncHandler.log(logEntity);
        return Rst.businessOK();
    }
    @RequestMapping(CR.WanXian.LOGS_BY_VIDKEY)
    public RDWanXian logsByVidKey(String vidKey,Integer type){
        RDWanXian rd=new RDWanXian();
        int gid=gameUserService.getActiveGid(getUserId());
        List<RDWanXian.RDFightLog> list=wanXianEliminationSeriesRace.getLogsByVidkey(gid,type,vidKey);
        if (list!=null || !list.isEmpty()){
            rd.setLogs(list);
        }
        return rd;
    }

    /**
     * 当type等于空或者错误的type时  将type重新赋值
     * @param type
     */
    private int checkType(Integer type){
        if (type==null){
            type=WanXianLogic.TYPE_REGULAR_RACE;
        }else if (WanXianLogic.TYPE_REGULAR_RACE!=type.intValue() && WanXianLogic.TYPE_SPECIAL_RACE!=type.intValue()){
            type=WanXianLogic.TYPE_REGULAR_RACE;
        }
        return type;
    }
}
