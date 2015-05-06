package uk.cpjsmith.ponypaper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Encapsulates a linear sequence of images with associated timings. The images
 * are all stored in a single Bitmap.
 */
public class SpriteSheet {
    
    public Bitmap bitmap;
    public int totalTime;
    public int frameWidth;
    public int frameHeight;
    
    private static BitmapFactory.Options bfOpts;
    
    private int[] frameTimes;
    
    static {
        bfOpts = new BitmapFactory.Options();
        bfOpts.inScaled = false;
    }
    
    /**
     * Constructs a new SpriteSheet object from a drawable resource and an
     * integer array resource. The frame count is extracted from the length of
     * the frame time array.
     * 
     * @param res     the Resources object to use
     * @param drawId  the identifier of the drawable resource containing the
     *                frames
     * @param timesId the identifier of the integer array resource containing
     *                the frame times
     */
    public SpriteSheet(Resources res, int drawId, int timesId) {
        bitmap = BitmapFactory.decodeResource(res, drawId, bfOpts);
        frameTimes = res.getIntArray(timesId);
        setInternals();
    }
    
    /**
     * Constructs a new SpriteSheet object from an in-memory image file and an
     * array of frame times. The frame count is extracted from the length of
     * the frame time array.
     * 
     * @param bitmapData the image file as a byte array
     * @param frameTimes the integer array containing the frame times
     */
    public SpriteSheet(byte[] bitmapData, int[] frameTimes) {
        this.bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        this.frameTimes = frameTimes;
        setInternals();
    }
    
    /**
     * Return the boundary of the region of the complete image that should be
     * displayed at the given time. Requires {@code 0 <= time < totalTime}.
     * 
     * @param time the number of 10-millisecond intervals since the start of
     *             the animation
     * @return the rectangle to use as the {@code src} parameter to
     *         {@code android.graphics.canvas.drawBitmap()}
     * @throws IllegalArgumentException if {@code time} is invalid
     */
    public Rect getRect(int time) {
        if (time < 0) throw new IllegalArgumentException("Invalid frame time.");
        for (int frame = 0; frame < frameTimes.length; frame++) {
            if (time < frameTimes[frame]) return new Rect(frameWidth * frame, 0, frameWidth * (frame + 1), frameHeight);
            time -= frameTimes[frame];
        }
        throw new IllegalArgumentException("Invalid frame time.");
    }
    
    private void setInternals() {
        totalTime = 0;
        for (int x : frameTimes) totalTime += x;
        frameWidth = bitmap.getWidth() / frameTimes.length;
        frameHeight = bitmap.getHeight();
    }
    
}
