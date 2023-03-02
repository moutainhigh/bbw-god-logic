package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 王中王抽奖结果
 * @date 2020/9/18 15:55
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RDDrawResult extends RDCommon {
    private static final long serialVersionUID = -3678615115430799669L;
    private List<ResultInfo> firstInfos;
    private List<ResultInfo> secondInfos;
    private List<ResultInfo> thirdInfos;
    private List<ResultInfo> fourthInfos;
    private List<ZhuangYuanNOInfo> zhuangYuanNOList;
    @Data
    public static class ResultInfo {
        private String number;
        private String nickname;

        public ResultInfo(String number) {
            this.number = number;
        }

        public ResultInfo(String number, String nickname) {
            this.number = number;
            this.nickname = nickname;
        }

    }
}
