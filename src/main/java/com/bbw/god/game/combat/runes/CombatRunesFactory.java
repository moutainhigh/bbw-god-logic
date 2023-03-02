package com.bbw.god.game.combat.runes;

import com.bbw.god.game.combat.runes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author：lwb
 * @date: 2020/12/8 14:35
 * @version: 1.0
 */
@Service
public class CombatRunesFactory {
    @Lazy
    @Autowired
    private List<IInitStageRunes> initStageRunesServices;
    @Lazy
    @Autowired
    private List<IParamInitStageRunes> paramInitStageRunes;
    @Lazy
    @Autowired
    private List<AbstractAddSkillRunes> abstractAddSkillRunes;
    @Lazy
    @Autowired
    private List<IRoundStageRunes> roundStageRunesServices;
    @Lazy
    @Autowired
    private List<IRoundEndStageRunes> roundEndStageRunes;

    public IParamInitStageRunes matchParamInitStageRunes(int runesId) {
        for (IParamInitStageRunes service : paramInitStageRunes) {
            if (service.getRunesId() == runesId) {
                return service;
            }
        }
        return null;
    }

    public IInitStageRunes matchInitStageRunes(int runesId) {
        for (IInitStageRunes service : initStageRunesServices) {
            if (service.getRunesId() == runesId) {
                return service;
            }
        }
        return null;
    }

    public IRoundStageRunes matchRoundStageRunes(int runesId) {
        for (IRoundStageRunes service : roundStageRunesServices) {
            if (service.getRunesId() == runesId) {
                return service;
            }
        }
        return null;
    }

    public IRoundEndStageRunes matchRoundEndStageRunes(int runesId) {
        for (IRoundEndStageRunes service : roundEndStageRunes) {
            if (service.getRunesId() == runesId) {
                return service;
            }
        }
        return null;
    }


    public AbstractAddSkillRunes matchAddSkillRunes(int runesId) {
        for (AbstractAddSkillRunes service : abstractAddSkillRunes) {
            if (service.getRunesId() == runesId) {
                return service;
            }
        }
        return null;
    }

}
