package com.game.client.window.screen.world.engine.unit;

import com.game.client.window.screen.world.engine.action.GameUnitAction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Queue;

@Builder
@Getter
@Setter
public class GameUnit {
    private final long id;
    private final boolean dynamic;
    private Vector3f position;
    private GameUnitAction gameUnitAction;
    private Queue<ChangeType> changeQueue;

    public void step() {
        var action = gameUnitAction;
        if (action != null) {
            if (!action.act(this)) {
                gameUnitAction = null;
            }
        }
    }

    public void setAction(GameUnitAction gameUnitAction) {
        this.gameUnitAction = gameUnitAction;
    }

    public void addChange(ChangeType changeType) {
        if (changeQueue == null) {
            changeQueue = new LinkedList<>();
        }
        changeQueue.add(changeType);
    }
}
