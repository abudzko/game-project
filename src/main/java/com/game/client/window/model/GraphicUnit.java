package com.game.client.window.model;

import com.game.client.window.model.obj.Model;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the game unit:
 * - positions
 * - rotation
 * - scale
 */
@Getter
@Builder
public class GraphicUnit {
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
