package com.bbw.god.gameuser.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡组
 *
 * @author: suhq
 * @date: 2021/11/17 1:57 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardGroup implements Serializable {
    private static final long serialVersionUID = -4492507071675923604L;
    private List<Integer> cardIds = new ArrayList<>();
    private Integer fuCeId = 0;
    private Integer cardGroupType;
}
