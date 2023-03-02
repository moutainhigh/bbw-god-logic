package com.bbw.god.city.nvwm.nightmare.nuwamarket.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 女娲集市交易记录
 *
 * @author fzj
 * @date 2022/5/31 16:53
 */
@Data
public class RDTradeRecordInfo extends RDSuccess {
    private List<RDTradeRecord> rdTradeRecords;
}
