package com.bbw.god.server.fst;

import lombok.Data;

/**
 * 说明：封神台榜单信息
 *
 * @author lwb
 * date 2021-06-29
 */
@Data
public class FstRankerParam {
    private Integer pvpRanking;
    private Long id;
    private String nickname;
    private Integer head;
    private Integer iconId;
    private Integer level;
    /**
     * 当前阶段获得的积分
     */
    private Integer ablePoints = null;
    /**
     * 是否允许挑战
     */
    private Integer fightAble = null;
    private Integer isPromotionRank=0;
    
    public static FstRankerParam getInstance(long uid,int rank,String nickname){
        FstRankerParam fstRankerParam=new FstRankerParam();
        fstRankerParam.setId(uid);
        fstRankerParam.setPvpRanking(rank);
        fstRankerParam.setNickname(nickname);
        return fstRankerParam;
    }
}
