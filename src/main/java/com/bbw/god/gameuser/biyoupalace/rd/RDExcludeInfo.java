package com.bbw.god.gameuser.biyoupalace.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 篇章
 *
 * @author suhq
 * @date 2019-09-10 10:10:52
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDExcludeInfo extends RDSuccess {
    private List<Integer> excludes;
}
