package com.runescape.cache.graphics.widget.component;

import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.widget.GameButtonListener;
import com.runescape.cache.graphics.widget.GameScreenAdapter;

import static com.runescape.cache.graphics.widget.component.DynamicComponent.State.NONE;

/**
 * Created by Stan van der Bend for Empyrean at 09/08/2018.
 *
 * @author https://www.rune-server.ee/members/StanDev/
 */
public abstract class DynamicComponent extends Component implements GameButtonListener, GameScreenAdapter {

    private State state;
    private boolean hovering;

    public DynamicComponent(Sprite sprite, int x, int y) {
        super(sprite, x, y);
    }

    @Override
    public boolean onClick(Client instance) {
        if(state == State.CLICKED)
            state = NONE;
        else
            state = State.CLICKED;
        return true;
    }

    @Override
    public boolean containsMouse(Client client) {
        if(super.containsMouse(client)){
            hovering = true;
            return true;
        } else if(hovering)
            hovering = false;
        return false;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public boolean isHovering() {
        return hovering;
    }

    public State getState() {
        return state;
    }

    protected enum State {
        NONE,
        CLICKED
    }
}
