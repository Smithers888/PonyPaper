package uk.cpjsmith.ponypaper;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import java.util.Random;

/**
 * Encapsulates a particular action for a pony. Ultimately, this unites the
 * {@code SpriteSheet} for the left- and right-facing modes of the action, as
 * well as information on possible next states.
 */
public class PonyAction {
    
    /**
     * Type constant for actions which follow the normal rules. I.e. if/when
     * used for moving, the pony travels at a fixed speed towards its
     * destination.
     */
    public static final int NORMAL = 0;
    /**
     * Type constant for actions which follow the teleport-out rules. I.e. when
     * used for moving, the pony remains stationary for one loop of the
     * action's animation, then jumps to the target and changes to the next
     * moving action.
     */
    public static final int PORT_O = 1;
    /**
     * Type constant for actions which follow the teleport-in rules. I.e. when
     * used for moving, the pony remains stationary for one loop of the
     * action's animation, then changes to the next stationary action.
     */
    public static final int PORT_I = 2;
    
    /** Represents motion towards the left (negative x) direction. */
    public static final int LEFT = 0;
    /** Represents motion towards the right (positive x) direction. */
    public static final int RIGHT = 1;
    
    /** The type of action; {@code NORMAL}, {@code PORT_O} or {@code PORT_I} */
    public final int type;
    
    private final SpriteSheet[] sprites;
    
    private PonyAction[] nextWaiting;
    private PonyAction[] nextMoving;
    private PonyAction[] nextDrag;
    
    /**
     * Constructs an action of type {@code NORMAL}.
     * 
     * @param res     the {@code Resources} object to load from
     * @param arrayId the ID of the action's main array resource
     * @see #PonyAction(Resources, int, int)
     */
    public PonyAction(Resources res, int arrayId) {
        this(res, arrayId, NORMAL);
    }
    
    /**
     * Constructs an action of the given type. The {@code arrayId} parameter
     * should be the ID of an array resouce containing 4 other resources - the
     * drawable for leftwards motion, the array of frame times for leftwards
     * motion, the drawable for rightwards motion and the array of frame times
     * for rightwards motion, respectively.
     * 
     * @param res     the {@code Resources} object to load from
     * @param arrayId the ID of the action's main array resource
     * @param type    the type of action
     */
    public PonyAction(Resources res, int arrayId, int type) {
        android.content.res.TypedArray array = res.obtainTypedArray(arrayId);
        
        int leftDrawableId = array.getResourceId(0, -1);
        int leftTimingId = array.getResourceId(1, -1);
        int rightDrawableId = array.getResourceId(2, -1);
        int rightTimingId = array.getResourceId(3, -1);
        
        this.sprites = new SpriteSheet[] {
            new SpriteSheet(res, leftDrawableId, leftTimingId),
            new SpriteSheet(res, rightDrawableId, rightTimingId)
        };
        this.type = type;
        
        array.recycle();
    }
    
    /**
     * Constructs an action.
     * 
     * @param definition the action definition extracted from XML
     */
    public PonyAction(PonyDefinition.Action definition) {
        this.sprites = new SpriteSheet[] {
            new SpriteSheet(Base64.decode(definition.images.get("left"), 0),
                            parseInts(definition.timings.get("left"))),
            new SpriteSheet(Base64.decode(definition.images.get("right"), 0),
                            parseInts(definition.timings.get("right")))
        };
        if (definition.specialType.equals("teleport-out")) {
            this.type = PORT_O;
        } else if (definition.specialType.equals("teleport-in")) {
            this.type = PORT_I;
        } else {
            this.type = NORMAL;
        }
    }
    
    private static int[] parseInts(String value) {
        String[] array = value.split(",");
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Integer.parseInt(array[i]);
        }
        return result;
    }
    
    public int getAnimationTime(int dir) {
        return sprites[dir].totalTime;
    }
    
    public void drawOn(Canvas c, int dir, int time, Point p, float scale) {
        SpriteSheet sprite = sprites[dir];
        
        int sW = sprite.frameWidth;
        int sH = sprite.frameHeight;
        float dW = sW * scale;
        float dH = sH * scale;
        
        RectF dstRect = new RectF(p.x - dW/2, p.y - dH/2, p.x + dW/2, p.y + dH/2);
        
        c.drawBitmap(sprite.bitmap, sprite.getRect(time), dstRect, null);
    }
    
    public void setNextWaiting(PonyAction[] states) {
        nextWaiting = states;
    }
    
    public void setNextMoving(PonyAction[] states) {
        nextMoving = states;
    }
    
    public void setNextDrag(PonyAction[] states) {
        nextDrag = states;
    }
    
    public PonyAction getNextWaiting(Random random) {
        return nextWaiting[random.nextInt(nextWaiting.length)];
    }
    
    public PonyAction getNextMoving(Random random) {
        return nextMoving[random.nextInt(nextMoving.length)];
    }
    
    public PonyAction getNextDrag(Random random) {
        return nextDrag[random.nextInt(nextDrag.length)];
    }
    
}
