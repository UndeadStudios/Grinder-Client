package com.runescape.cache.graphics.widget.dynamicinterface;


import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.cache.graphics.widget.dynamicinterface.cons.*;

import java.util.ArrayList;

/**
 * A basic implementation of an interface creation system.
 * @author Simplex
 */
public abstract class DynamicInterface {

	/**
	 * Add interface instances to this list to hook into {@link Client} cycle
	 */
	public static ArrayList<DynamicInterface> onTickHooks = new ArrayList<DynamicInterface>();


	/**
	 * Instantiate all dynamic interfaces here.
	 */
	public static void populate() {
		dynamicInterfaces.add(new ConstructionLoading());
		dynamicInterfaces.add(new ConstructionFurnitureBuilder());
		dynamicInterfaces.add(new ConstructionRoomSelectionContent());
		dynamicInterfaces.add(new ConstructionRoomSelection());
	}

	protected static void addProcessHook(DynamicInterface di) {
		if(onTickHooks.contains(di))
			onTickHooks.remove(di);
		onTickHooks.add(di);
	}

	public static void processOnTickHooks() {
		onTickHooks.stream().forEach(i->i.onTick());
	}

	protected static DynamicInterface init(DynamicInterface dInt) {
		dInt.build();
		dInt.position();
		return dInt;
	}

	public void onTick() { }

    /**
     * A list of interfaces that will be called upon at all stages
     * of creation.
     */
    public static ArrayList<DynamicInterface> dynamicInterfaces = new ArrayList<>();

    /**
     * Unique cache index in master interface array.
     */
    public int baseIndex = -1;

    /**
     * Offset to be applied upon adding components
     */
    public int interfaceOffsetX = 0, interfaceOffsetY = 0;

    /**
     * If true, the interface will inherit the current interface within
     * the cache.
     */
    public boolean inheritParent = false;

    public static int getChildIndex(int parent, int interfaceId) {
        for(int k = 0; k < Widget.interfaceCache[parent].children.length; k++) {
            if(interfaceId == Widget.interfaceCache[parent].children[k])
                return k;
        }

        return -1;
    }

    public static Object get(int interfaceId) {
        for(DynamicInterface di : dynamicInterfaces) {
            if(di.baseIndex == interfaceId) {
                return di;
            }
        }

        return null;
    }

    static final int FIXED_WIDTH = 512;
	static final int FIXED_HEIGHT = 336;
	/**
	 * Automatically centers and adds the widget to this this
	 * DynamicInterface.
	 */
	public void addBackgroundSprite(Widget widget) {
		Sprite sprite = widget.disabledSprite;

		int x = FIXED_WIDTH - sprite.myWidth;
		int y = FIXED_HEIGHT - sprite.myHeight;

		if(x!=0) x/=2;
		if(y!=0) y/=2;

		add(widget, x, y);
	}


    private Widget widget;

    /**
     * Instantiate a new interface.
     * @param index in master.
     */
    public DynamicInterface(int index) {
        this.baseIndex = index;
		setIndex(index);
        this.initialize();
    }

    /**
     * Used for marking up existing interfaces.
     */
    public DynamicInterface(int index, boolean inheritParent) {
        this.setIndex(index);
        this.inheritParent = inheritParent;
        if(inheritParent)
            this.inherit();
        else initialize();
    }

    /**
     * Children contained in this interface.
     */
    public ArrayList<Child> interfaceChildren;


	/**
	 * Button click entry from Client.
	 */
	public static void buttonClick(int id) {
		Runnable singleton = null;
		for(ButtonActionListener b : buttonActionListeners) {

			if(b.id == id) {
				singleton = b.container;
				break;
			}
		}

		if(singleton != null) {
			singleton.run();
		}
	}

	/**
	 * Add a button action listener.
	 */
	public static void bindButtonActionListener(ButtonActionListener ba) {
		buttonActionListeners.add(ba);
	}
	public static void unbindButtonAction(int ba) {
		ArrayList<ButtonActionListener> toRemove = new ArrayList<ButtonActionListener>();

		// find all instances of this button (in case there is multiple)
		for(ButtonActionListener button: buttonActionListeners) {
			if (button.id == ba) {
				toRemove.add(button);
			}
		}

		for(ButtonActionListener button : toRemove) {
			buttonActionListeners.remove(button);
		}
	}

	/**
	 * Button action listeners
	 */
	public static ArrayList<ButtonActionListener> buttonActionListeners = new ArrayList<ButtonActionListener>();

	/**
	 * Client login entry.
	 */
	public static void clientLogin() {
		for(DynamicInterface di : dynamicInterfaces) {
			di.onLogin();
		}
	}

	/**
	 * Could be abstract; called upon logout of the client.
	 */
	public void onLogin() {}

	/**
     * @return The children of this interface.
     */
    public ArrayList<Child> getChildren() {
        return interfaceChildren;
    }

    /**
     * Inherits the existing interface within the cache.
     */
    public void inherit() {
        interfaceChildren = new ArrayList<>();
        Widget parent = Widget.interfaceCache[baseIndex];
        Widget child = null;

        // pass down all existing children
        for(int i = 0; i < parent.children.length; i++) {
            child = Widget.interfaceCache[parent.children[i]];
            this.getChildren().add(new Child(child.id,
                    parent.childX[i] + interfaceOffsetX,
                    parent.childY[i] + interfaceOffsetY));
        }
    }

    /**
     * Initialize local variables.
     */
    public void initialize() {
        Widget.addInterface(baseIndex);
        interfaceChildren = new ArrayList<>();
        this.setWidget(Widget.interfaceCache[this.baseIndex]);
    }

    /**
     * Set cache index, instantiate <@link Widget> rsi and set children
     */
    public void build() {}

    /**
     * Position all children.
     */
    public void position() {
        Widget rsi = Widget.interfaceCache[this.baseIndex] != null
                ? Widget.interfaceCache[this.baseIndex]
                : Widget.addInterface(this.baseIndex);
        rsi.totalChildren(interfaceChildren.size());

        Child itr = null;

        for(int i = 0; i < interfaceChildren.size(); i++) {
            itr = interfaceChildren.get(i);
            rsi.child(i, itr.getInterface(), itr.getX(), itr.getY());
        }
    }

    /**
     * Initializes the interface; for use in build method.
     * @param i
     */
    public void setIndex(int i) {
        this.baseIndex = i;
        //this.rsi = Widget.interfaceCache[i] != null ? Widget.interfaceCache[i] : Widget.addInterface(i);
    }

    /**
     * Build this interface by adding children.
     */
    public void add(Widget child, int x, int y) {
        this.getChildren().add(new Child(child.id, x + interfaceOffsetX, y + interfaceOffsetY));
        if(child.type != 0)
            child.parent = this.baseIndex;
        else
            child.parent = child.id;

    }

    /**
     * @return Parent Widget
     */
    public Widget parent() {
        return Widget.interfaceCache[baseIndex];
    }

	/**
	 * @return The parent interface.
	 */
	public Widget getWidget() {
        return widget;
    }

	/**
	 * @param widget The parent interface.
	 */
	public void setWidget(Widget widget) {
        this.widget = widget;
    }

	/**
	 * TODO rewire
	 * Client entry for logging out.
	 */
	public static void clientLogout() {
        for(DynamicInterface di : dynamicInterfaces) {
            di.onLogout();
        }
    }

	/**
	 * TODO rewire
	 * Called upon logout of Client.
	 */
	public void onLogout() {
    }
}
