package com.bbw.god.activity.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 活动类型
 *
 * @author suhq
 * @date 2019年3月3日 下午11:24:29
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDActivityTypeList extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RDActivityType> types = null;// 内建福利列表

    /**
     * 活动类型信息
     *
     * @author suhq
     * @date 2019年3月3日 下午11:24:29
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RDActivityType implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer type = null;
        private Integer num = null;

        public RDActivityType(int type, int num) {
            this.type = type;
            this.num = num;
        }

    }

}
