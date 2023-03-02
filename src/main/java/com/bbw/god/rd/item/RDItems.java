package com.bbw.god.rd.item;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 列表返回外部统一
 * @date 2020/11/23 09:46
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDItems<T extends RDItem> extends RDSuccess {
    @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
    private List<T> items = new ArrayList<>();

    public static <T extends RDItem> RDItems<T> getInstance(List<T> items) {
        RDItems<T> rdItems = new RDItems<>();
        rdItems.addTasks(items);
        return rdItems;
    }

    public void addTasks(List<T> tasks){
        getItems().addAll(tasks);
    }

    public void addTask(T task){
        getItems().add(task);
    }
}
