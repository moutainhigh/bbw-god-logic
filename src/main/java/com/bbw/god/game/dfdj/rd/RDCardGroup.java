package com.bbw.god.game.dfdj.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 卡组
 * @date 2021/1/10 15:30
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardGroup extends RDSuccess implements Serializable {
    private List<Integer> cardIds = new ArrayList<>();
}
