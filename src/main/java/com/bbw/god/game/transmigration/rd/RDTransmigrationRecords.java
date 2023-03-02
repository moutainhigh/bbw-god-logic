package com.bbw.god.game.transmigration.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 轮回挑战记录列表(各城最好挑战记录)
 *
 * @author: suhq
 * @date: 2021/9/15 10:34 上午
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTransmigrationRecords extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -4714532554643883373L;
    private List<RDTransmigrationRecord> records;
}
