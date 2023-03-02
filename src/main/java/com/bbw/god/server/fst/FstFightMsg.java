package com.bbw.god.server.fst;

import lombok.Data;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-07-02
 */
@Data
public class FstFightMsg {
    private Long id;
    private Integer isWin=0;
    private Integer isAttack;
    private String oppo;
    private Integer rank;

    public static FstFightMsg getInstance(FstVideoLog log,String nickname){
        FstFightMsg msg=new FstFightMsg();
        msg.setId(log.getId());
        msg.setIsAttack(log.isAttack()?1:0);
        msg.setOppo(nickname);
        msg.setRank(log.getRank());
        msg.setIsWin(log.isWin()?1:0);
        return msg;
    }
}
