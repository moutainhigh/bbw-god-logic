package com.bbw.god.activity.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 天灯寄语
 *
 * @author fzj
 * @date 2022/2/8 16:22
 */
@Data
public class RDSkyLanternMessage extends RDSuccess{
    /** 玩家寄语 */
    private String userMessage;
    /** 其他寄语 */
    private List<String> otherMessage;
}
