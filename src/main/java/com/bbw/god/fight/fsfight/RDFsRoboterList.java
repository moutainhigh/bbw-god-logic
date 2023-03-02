package com.bbw.god.fight.fsfight;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 神仙大会匹配机器人的数据
 *
 * @author: suhq
 * @date: 2021/8/22 10:59 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFsRoboterList extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    /** uid:roboter */
    private Map<String, RDFsRoboter> roboters = new HashMap<>();

    public void addRoboter(long uid, RDFsRoboter roboter) {
        roboters.put(uid + "", roboter);
    }

}
