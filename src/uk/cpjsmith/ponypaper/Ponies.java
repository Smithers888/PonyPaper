package uk.cpjsmith.ponypaper;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Class to hold the collection of ponies and coordinate their overall motion.
 */
public class Ponies {
    
    private static final Comparator<Pony> compareY = new Comparator<Pony>() {
        @Override
        public int compare(Pony lhs, Pony rhs) {
            int yL = lhs.getY();
            int yR = rhs.getY();
            return yL < yR ? -1 : yL > yR ? 1 : 0;
        }
    };
    
    private int activeCount;
    
    private Random random;
    
    private ArrayList<Pony> inactivePonies;
    private Pony[] activePonies;
    
    private int initialPointerId = -1;
    private Pony draggedPony = null;
    
    /**
     * Creates a new {@code Ponies} instance.
     * 
     * @param res   the resources object to load the pony sprites from
     * @param prefs the users preferences of which ponies to load
     */
    public Ponies(Resources res, SharedPreferences prefs) {
        inactivePonies = AllPonies.getPonies(res, prefs);
        
        activeCount = Math.min(inactivePonies.size(), 4);
        
        random = new Random();
        activePonies = new Pony[activeCount];
        for (int i = 0; i < activeCount; i++) {
            int j = random.nextInt(inactivePonies.size());
            activePonies[i] = inactivePonies.remove(j);
        }
    }
    
    /**
     * Resets the position of all active (on-screen) ponies.
     */
    public void reset() {
        for (Pony pony : activePonies) pony.reset();
    }
    
    /**
     * Updates all active ponies for one frame of motion and draws them on the
     * given canvas.
     * 
     * @param c the canvas to draw on
     */
    public void drawAndUpdate(Canvas c) {
        for (int i = 0; i < activePonies.length; i++) {
            activePonies[i].doUpdate(c.getClipBounds());
            if (activePonies[i].goneOffScreen()) {
                Pony temp = activePonies[i];
                temp.reset();
                if (inactivePonies.size() != 0) {
                    int j = random.nextInt(inactivePonies.size());
                    activePonies[i] = inactivePonies.remove(j);
                    inactivePonies.add(temp);
                }
                activePonies[i].doUpdate(c.getClipBounds());
            }
        }
        Arrays.sort(activePonies, compareY);
        for (int i = 0; i < activePonies.length; i++) {
            activePonies[i].drawOn(c);
        }
    }
    
    /**
     * Handles a touch event on the screen. This allows the user a means of
     * dragging ponies around the screen.
     * 
     * @param event the touch event that was performed by the user
     */
    public void onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (draggedPony != null) draggedPony.stopDrag();
                
                initialPointerId = event.getPointerId(0);
                draggedPony = null;
                for (Pony pony : activePonies) {
                    if (pony.testHitPoint(event.getX(), event.getY())) draggedPony = pony;
                }
                if (draggedPony != null) {
                    draggedPony.startDrag();
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (draggedPony != null) draggedPony.stopDrag();
                
                initialPointerId = -1;
                draggedPony = null;
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (draggedPony != null) {
                    draggedPony.moveTo(new Point(Math.round(event.getX()), Math.round(event.getY())));
                }
                break;
                
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerId(event.getActionIndex()) == initialPointerId) {
                    if (draggedPony != null) draggedPony.stopDrag();
                    
                    initialPointerId = -1;
                    draggedPony = null;
                }
                break;
        }
    }
    
}
