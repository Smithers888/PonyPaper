package uk.cpjsmith.ponypaper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;

public class SpriteSheetPreview extends JComponent implements MouseListener, MouseMotionListener {
    
    private Image image;
    private int frameCount;
    private int highlightIndex;
    
    public SpriteSheetPreview(Image image, int frameCount) {
        this.image = image;
        this.frameCount = frameCount;
        this.highlightIndex = -1;
        
        setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    private Rectangle getImageBounds() {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        int componentWidth = getWidth();
        int componentHeight = getHeight();
        
        Rectangle result = new Rectangle(0, 0, componentWidth, componentHeight);
        if (imageWidth < componentWidth) {
            result.x = (componentWidth - imageWidth) / 2;
            result.width = imageWidth;
        }
        if (imageHeight < componentHeight) {
            result.y = (componentHeight - imageHeight) / 2;
            result.height = imageHeight;
        }
        
        return result;
    }
    
    private void highlightFrame(MouseEvent e) {
        Rectangle bounds = getImageBounds();
        if (bounds.contains(e.getPoint())) {
            highlightIndex = (e.getX() - bounds.x) * frameCount / bounds.width;
        } else {
            highlightIndex = -1;
        }
        repaint();
    }
    
    private void highlightNone() {
        highlightIndex = -1;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics myG = g.create();
        
        Rectangle bounds = getImageBounds();
        
        myG.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
        if (highlightIndex != -1) {
            myG.setColor(new Color(0, 0, 0, 0x66));
            int highlightStart = bounds.x + bounds.width * highlightIndex / frameCount;
            int highlightEnd = bounds.x + bounds.width * (highlightIndex+1) / frameCount;
            myG.fillRect(bounds.x, bounds.y, highlightStart - bounds.x, bounds.height);
            myG.fillRect(highlightEnd, bounds.y, bounds.x + bounds.width - highlightEnd, bounds.height);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        highlightFrame(e);
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        highlightNone();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        highlightFrame(e);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        highlightFrame(e);
    }
    
}
