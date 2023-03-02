package com.bbw.god.game.wanxianzhen;

import com.bbw.common.StrUtil;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import lombok.Data;

/**
 * @author lwb
 * 万仙阵定时器执行
 */
@Data
public class RDWanXianJob {
    //信息
    private String msg="";
    //是否异常
    private int res=0;

    public static RDWanXianJob instance(int gid,int type){
        RDWanXianJob rd=new RDWanXianJob();
        rd.runSuccess(gid,type);
        return rd;
    }

    public void runSuccess(int gid,int type){
        if (type==WanXianLogic.TYPE_REGULAR_RACE){
            msg ="常规赛--"+gid+"--正常执行";
        }
        msg = "特色赛--"+gid+"--正常执行";
    }

    public void runError(int gid,int type,String errorMsg){
        if (StrUtil.isBlank(errorMsg)){
            errorMsg="执行失败";
        }
        if (type==WanXianLogic.TYPE_REGULAR_RACE){
            msg ="常规赛--"+gid+"-----"+errorMsg;
        }
        msg = "特色赛--"+gid+"-----"+errorMsg;
        res=1;
    }

    public boolean hasError(){
        return res!=0;
    }
}
