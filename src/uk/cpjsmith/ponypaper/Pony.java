package uk.cpjsmith.ponypaper;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import java.util.Random;

/**
 * Represents the current status of a pony. This class must be subclassed to
 * provide methods to create the initial state of the pony and to determine the
 * transition between states.
 */
public class Pony {
    
    private static final int MOTION_INIT = 0;
    private static final int MOTION_WAITING = 1;
    private static final int MOTION_MOVING = 2;
    private static final int MOTION_DRAGGED = 3;
    private static final int MOTION_SPECIAL = 4;
    
    private static final int LM_NORMAL = 0;
    private static final int LM_GOING = 1;
    private static final int LM_GONE = 2;
    
    private final PonyAction[] allActions;
    private final PonyAction[] startActions;
    
    private Random random;
    private Point targetPos;
    private int waitTimer;
    
    private int motion;
    private int leavingMode;
    
    private PonyAction currentAction;
    private Point currentPos;
    private int direction;
    private int frameTime = 0;
    
    private Rect screenBounds;
    
    /**
     * Creates a new {@code Pony} object.
     * 
     * @param allActions   all of the actions that this pony is comprised of
     * @param startActions the actions that the pony can enter the screen with
     */
    public Pony(PonyAction[] allActions, PonyAction[] startActions) {
        this.allActions = allActions;
        this.startActions = startActions;
        this.random = new Random();
        this.direction = random.nextBoolean() ? PonyAction.LEFT : PonyAction.RIGHT;
    }
    
    /**
     * Clears the current state of the pony.
     */
    public void reset() {
        waitTimer = 0;
        motion = MOTION_INIT;
        leavingMode = LM_NORMAL;
        currentAction = null;
        currentPos = null;
        frameTime = 0;
        for (int i = 0; i < allActions.length; i++) {
            allActions[i].unload();
        }
    }
    
    /**
     * Causes the state of the pony to be updated for the next frame.
     * 
     * @param clipBounds the bounds of the screen that the pony will be
     *                   positioned on
     */
    public void doUpdate(Rect clipBounds) {
        screenBounds = clipBounds;
        
        float scale = getScale();
        
        if (motion == MOTION_INIT) {
            for (int i = 0; i < allActions.length; i++) {
                allActions[i].load();
            }
            currentPos = randomOffScreen();
            changeAction(startActions[random.nextInt(startActions.length)]);
            motion = currentAction.type == PonyAction.NORMAL ? MOTION_MOVING : MOTION_SPECIAL;
            setRandomTarget();
        } else {
            frameTime += 4;
            if (frameTime >= currentAction.getAnimationTime(direction)) {
                frameTime -= currentAction.getAnimationTime(direction);
                switch (currentAction.type) {
                    case PonyAction.PORT_O:
                        moveTo(targetPos);
                        setMoving();
                        break;
                        
                    case PonyAction.PORT_I:
                        arriveTarget();
                        setWaiting();
                        break;
                }
            }
            
            switch (motion) {
                case MOTION_WAITING:
                    if (waitTimer > 0) {
                        waitTimer--;
                    } else {
                        setMoving();
                        motion = currentAction.type == PonyAction.NORMAL ? MOTION_MOVING : MOTION_SPECIAL;
                        setRandomTarget();
                    }
                    break;
                    
                case MOTION_MOVING:
                    moveTowardsTarget(3 * scale);
                    break;
            }
        }
    }
    
    public void drawOn(Canvas c) {
        currentAction.drawOn(c, direction, frameTime, currentPos, getScale());
    }
    
    /**
     * Determines whether this pony has left the scene to be replaced with
     * another.
     * 
     * @return {@code true} if the pony has completed an exit stage direction
     */
    public boolean goneOffScreen() {
        return leavingMode == LM_GONE;
    }
    
    /**
     * Returns the y-coordinate of the pony's position. This can be used to
     * sort ponies that are higher up the screen as being further away.
     * 
     * @return the screen y-coordinate
     */
    public int getY() {
        return currentPos.y;
    }
    
    /**
     * Tests whether a click at the given screen point should be considered to
     * be a click on the pony.
     * 
     * @param x the x-coordinate of the click
     * @param y the y-coordinate of the click
     * @return {@code true} iff the point is on top of this pony
     */
    public boolean testHitPoint(float x, float y) {
        float ponySize = 30 * getScale();
        
        float dX = x - currentPos.x;
        float dY = y - currentPos.y;
        float d2 = dX * dX + dY * dY;
        
        return d2 < ponySize * ponySize;
    }
    
    /**
     * Brings the pony into a dragged state. This means the pony will no longer
     * move on its own accord and will only move as directed with
     * {@link #moveTo(Point)} until {@link #stopDrag()} is called.
     */
    public void startDrag() {
        motion = MOTION_DRAGGED;
        targetPos = null;
        leavingMode = LM_NORMAL;
        setDragged();
    }
    
    /**
     * Brings the pony back out of the dragged state. If the pony has been
     * dragged to the edge of the screen, it will immediately walk (fly, etc.)
     * off screen. Otherwise it will resume normal behaviour.
     */
    public void stopDrag() {
        int s = (int)(30 * getScale());
        
        if (currentPos.x < screenBounds.left + s) {
            motion = MOTION_MOVING;
            leavingMode = LM_GOING;
            targetPos = new Point(screenBounds.left - s, currentPos.y);
            setMoving();
        } else if (currentPos.x >= screenBounds.right - s) {
            motion = MOTION_MOVING;
            leavingMode = LM_GOING;
            targetPos = new Point(screenBounds.right + s, currentPos.y);
            setMoving();
        } else {
            motion = MOTION_WAITING;
            waitTimer = 25 + random.nextInt(250);
            setWaiting();
        }
    }
    
    /**
     * Moves the pony to a position.
     * 
     * @param pos the new position for the pony
     */
    public void moveTo(Point pos) {
        setDirection(pos);
        currentPos = pos;
    }
    
    private void setWaiting() {
        changeAction(currentAction.getNextWaiting(random));
    }
    
    private void setMoving() {
        changeAction(currentAction.getNextMoving(random));
    }
    
    private void setDragged() {
        changeAction(currentAction.getNextDrag(random));
    }
    
    private void changeAction(PonyAction newAction) {
        if (newAction != currentAction) {
            currentAction = newAction;
            frameTime = 0;
        }
    }
    
    private void arriveTarget() {
        motion = MOTION_WAITING;
        targetPos = null;
        waitTimer = 25 + random.nextInt(250);
        if (leavingMode == LM_GOING) leavingMode = LM_GONE;
    }
    
    private void setRandomTarget() {
        if (random.nextInt(8) < 1) {
            if (motion == MOTION_MOVING) {
                targetPos = randomOffScreenHoriz();
            } else {
                targetPos = randomOffScreen();
            }
            leavingMode = LM_GOING;
        } else {
            if (motion == MOTION_MOVING) {
                targetPos = randomOnScreenHoriz();
            } else {
                targetPos = randomOnScreen();
            }
        }
    }
    
    /**
     * Moves the pony towards its target by a given number of pixels.
     * 
     * @param speed the number of pixels to move (i.e. the speed in
     *              pixels/call)
     */
    private void moveTowardsTarget(float speed) {
        setDirection(targetPos);
        int dX = targetPos.x - currentPos.x;
        int dY = targetPos.y - currentPos.y;
        float f = speed / (float)Math.sqrt(dX * dX + dY * dY);
        if (f >= 1) {
            currentPos = targetPos;
            arriveTarget();
            setWaiting();
        } else {
            currentPos.x += (int)(dX * f);
            currentPos.y += (int)(dY * f);
        }
    }
    
    private float getScale() {
        return Math.min(screenBounds.width(), screenBounds.height()) / 200.0f;
    }
    
    /**
     * Chooses a random point on the screen.
     * 
     * @return the chosen point.
     */
    private Point randomOnScreen() {
        int s = (int)(30 * getScale());
        return new Point(screenBounds.left + s + random.nextInt(screenBounds.width() - 2*s),
                         screenBounds.top + s + random.nextInt(screenBounds.height() - 2*s));
    }
    
    /**
     * Chooses a random point on the screen, restricted to areas roughly
     * horizontal with the current position
     * 
     * @return the chosen point.
     */
    private Point randomOnScreenHoriz() {
        Point newPoint = null;
        for (int i = 0; i < 100; i++) {
            newPoint = randomOnScreen();
            if (newPoint.x != currentPos.x &&
                Math.atan(Math.abs(newPoint.y - currentPos.y) /
                Math.abs(newPoint.x - currentPos.x)) < Math.PI / 6) {
                break;
            }
        }
        return newPoint;
    }

    /**
     * Chooses a random point just to the side of the screen.
     * 
     * @return the chosen point.
     */
    private Point randomOffScreen() {
        int s = (int)(30 * getScale());
        return new Point(random.nextBoolean() ? screenBounds.left - s : screenBounds.right + s,
                         screenBounds.top + s + random.nextInt(screenBounds.height() - 2*s));
    }

    /**
     * Chooses a random point just to the side of the screen, 
     * restricted to areas roughly horizontal with the current position
     * 
     * @return the chosen point.
     */
    private Point randomOffScreenHoriz() {
        Point newPoint = null;
        for (int i = 0; i < 100; i++) {
            newPoint = randomOffScreen();
            if (newPoint.x != currentPos.x &&
                Math.atan(Math.abs(newPoint.y - currentPos.y) /
                Math.abs(newPoint.x - currentPos.x)) < Math.PI / 6) {
                break;
            }
        }
        return newPoint;
    }
    
    private void setDirection(Point targetPos) {
        int dX = targetPos.x - currentPos.x;
        if (dX > 0 && direction != PonyAction.RIGHT) {
            direction = PonyAction.RIGHT;
            frameTime = 0;
        }
        if (dX < 0 && direction != PonyAction.LEFT) {
            direction = PonyAction.LEFT;
            frameTime = 0;
        }
    }
    
}
