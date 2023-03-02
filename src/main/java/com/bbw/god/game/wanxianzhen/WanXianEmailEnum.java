package com.bbw.god.game.wanxianzhen;

import com.bbw.common.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/4/23 17:17
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum WanXianEmailEnum {
    EMAIL_FAIL(110,"淘汰邮件",0,0),
    EMAIL_CHAMPION(120,"冠军邮件",101,0),
    EMAIL_QUALIFYING_RACE_1(13001,"资格赛第1轮",108,1),
    EMAIL_QUALIFYING_RACE_2(13151,"资格赛第2轮",108,2),
    EMAIL_QUALIFYING_RACE_3(13301,"资格赛第3轮",108,3),
    EMAIL_QUALIFYING_RACE_4(13451,"资格赛第4轮",108,4),
    EMAIL_QUALIFYING_RACE_5(13002,"资格赛第5轮",108,1),
    EMAIL_QUALIFYING_RACE_6(13152,"资格赛第6轮",108,2),
    EMAIL_QUALIFYING_RACE_7(13302,"资格赛第7轮",108,3),
    EMAIL_QUALIFYING_RACE_8(13452,"资格赛第8轮",108,4),
    EMAIL_ELIMINATION_SERIES_RACE_1(13003,"淘汰赛第1轮",107,1),
    EMAIL_ELIMINATION_SERIES_RACE_2(13004,"淘汰赛第2轮",106,1),
    EMAIL_ELIMINATION_SERIES_RACE_3(13005,"淘汰赛第3轮",105,1),
    EMAIL_GROUP_STAGE_1(13006,"小组赛第1轮",0,1),
    EMAIL_GROUP_STAGE_2(13106,"小组赛第2轮",0,2),
    EMAIL_GROUP_STAGE_3(13206,"小组赛第3轮",0,3),
    EMAIL_GROUP_STAGE_4(13306,"小组赛第4轮",0,4),
    EMAIL_GROUP_STAGE_5(13406,"小组赛第5轮",0,5),
    EMAIL_GROUP_STAGE_6(13506,"小组赛第6轮",104,6),
    EMAIL_FINAL_RACE_1(13007,"决赛第一轮",103,1),
    EMAIL_FINAL_RACE_2(13157,"决赛第二轮",102,2),
    EMAIL_FINAL_RACE_3(13307,"决赛第二轮",102,3),
    EMAIL_FINAL_RACE_4(13457,"决赛第二轮",102,4);
    private int val;
    private String memo;
    private int awardPid;
    private int seq;

    public static List<WanXianEmailEnum> getEnumValByWeekday(int... weekdays){
        List<WanXianEmailEnum> list=new ArrayList<>();
        for (int weekday:weekdays){
            for (WanXianEmailEnum wxe:values()){
                if (wxe.getVal()%10==weekday){
                    list.add(wxe);
                }
            }
        }
        list=list.stream().sorted(Comparator.comparing(WanXianEmailEnum::getVal).reversed()).collect(Collectors.toList());
        return list;
    }

    public static WanXianEmailEnum fromVal(int val){
        for (WanXianEmailEnum emailEnum:values()){
            if (emailEnum.getVal()==val){
                return emailEnum;
            }
        }
        return null;
    }

    public static WanXianEmailEnum getValByOrder(Integer order){
        int weekday=DateUtil.getToDayWeekDay();
        if (weekday==1 && order==0){
            return null;
        }
        if (weekday==2 && order==0){
            return WanXianEmailEnum.EMAIL_QUALIFYING_RACE_4;
        }
        for (WanXianEmailEnum wxe:values()){
            if (wxe.getVal()%10==weekday && wxe.getSeq()==order){
                return wxe;
            }
        }
        return null;
    }

    public static List<Integer> getAllShowEnum(int order){
        int weekday=DateUtil.getToDayWeekDay();
        if (weekday==1 && order==0){
            return new ArrayList<>();
        }
        List<Integer> list=new ArrayList<>();
        for (WanXianEmailEnum wxe:values()){
            if (wxe.getVal()%10>0 && wxe.getVal()%10<weekday){
                list.add(wxe.getVal());
            }
            if (wxe.getVal()%10==weekday && wxe.getSeq()<=order){
                list.add(wxe.getVal());
            }
        }
        Collections.reverse(list);
        return list;
    }

    public static WanXianEmailEnum getMaxShowEnumVal(int gid,int type){
        RDWanXian rd=new RDWanXian();
        WanXianTool.getCountdown(gid,type,rd);
        if (rd.getWxShowRace()==null){
            int today=DateUtil.getToDayWeekDay();
            if (today==2) {
            	return WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8;
			}
            if (today==3){
                return WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8;
            }if (today==7){
                return WanXianEmailEnum.EMAIL_GROUP_STAGE_6;
            }
        }
        return rd.getWxShowRace();
    }

    public static List<Integer> getGroupVals(){
        List<Integer> vals=new ArrayList<>();
        vals.add(WanXianEmailEnum.EMAIL_GROUP_STAGE_1.getVal());
        vals.add(WanXianEmailEnum.EMAIL_GROUP_STAGE_2.getVal());
        vals.add(WanXianEmailEnum.EMAIL_GROUP_STAGE_3.getVal());
        vals.add(WanXianEmailEnum.EMAIL_GROUP_STAGE_4.getVal());
        vals.add(WanXianEmailEnum.EMAIL_GROUP_STAGE_5.getVal());
        vals.add(WanXianEmailEnum.EMAIL_GROUP_STAGE_6.getVal());
        return vals;
    }
    public static List<Integer> getQualifyingVals(){
        List<Integer> vals=new ArrayList<>();
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_1.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_2.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_3.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_4.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_5.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_6.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_7.getVal());
        vals.add(WanXianEmailEnum.EMAIL_QUALIFYING_RACE_8.getVal());
        return vals;
    }
}
