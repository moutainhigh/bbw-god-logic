package com.bbw.god.server.maou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @date 2019年12月24日 上午10:57:04
 */
@Service
public class MaouProcessorFactory {
    @Autowired
    @Lazy
    private List<IServerMaouProcessor> maouProcessors;

    public IServerMaouProcessor getMaouProcessor(int sid, int maouType) {
        List<IServerMaouProcessor> processors = this.maouProcessors.stream().filter(mp ->
                mp.isMatch(sid)).collect(Collectors.toList());
        return processors.stream().filter(tmp -> tmp.isMatchByMaouKind(maouType))
                .findFirst().orElse(null);
    }

    public IServerMaouProcessor getMaouProcessorAsMaouKind(int maouKind) {
        return this.maouProcessors.stream().filter(mp -> mp.isMatchByMaouKind(maouKind)).findFirst().orElse(null);
    }

}
