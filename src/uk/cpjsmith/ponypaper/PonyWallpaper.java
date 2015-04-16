package uk.cpjsmith.ponypaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class PonyWallpaper extends WallpaperService {
    
    private class PonyEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        
        private Ponies ponies = null;
        
        private boolean isVisible = false;
        private final Runnable drawFrameCallback = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        
        private PonyEngine() {
            getPreferences().registerOnSharedPreferenceChangeListener(this);
        }
        
        private SharedPreferences getPreferences() {
            return PreferenceManager.getDefaultSharedPreferences(PonyWallpaper.this);
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            ponies = null;
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawFrameCallback);
        }
        
        @Override
        public void onVisibilityChanged(boolean visible) {
            isVisible = visible;
            if (visible) drawFrame();
            else handler.removeCallbacks(drawFrameCallback);
        }
        
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (ponies != null) ponies.reset();
            drawFrame();
        }
        
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            isVisible = false;
            handler.removeCallbacks(drawFrameCallback);
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (ponies != null) ponies.onTouchEvent(event);
        }
        
        private void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            
            Canvas c = null;
            try {
                if (ponies == null) ponies = new Ponies(getResources(), getPreferences());
                c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(0xff3333ee);
                    ponies.drawAndUpdate(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }
            
            // Reschedule the next redraw
            handler.removeCallbacks(drawFrameCallback);
            if (isVisible) handler.postDelayed(drawFrameCallback, 1000 / 25);
        }
        
    }
    
    private final Handler handler = new Handler();
    
    @Override
    public Engine onCreateEngine() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        return new PonyEngine();
    }
    
}
