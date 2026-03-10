package com.game.client.window.screen.unit;

import com.game.client.window.model.obj.Model;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScreenUnit {
    private SharedUnitState sharedUnitState;
    private Model model;
    /**
     * Units which can be selected
     */
    @Builder.Default
    private boolean isSurface = true;
    /**
     * Some units should not have shadows, for ex. the sky
     */
    @Builder.Default
    private boolean useShading = true;
}
