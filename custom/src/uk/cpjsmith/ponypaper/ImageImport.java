package uk.cpjsmith.ponypaper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageImport {
    
    private static class GIFFrame {
        
        BufferedImage image;
        Rectangle bounds;
        String disposal;
        String delay;
        
        GIFFrame(IIOImage frame) {
            image = (BufferedImage)frame.getRenderedImage();
            
            IIOMetadata imageMetadata = frame.getMetadata();
            String metaFormatName = imageMetadata.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode)imageMetadata.getAsTree(metaFormatName);
            
            IIOMetadataNode idNode = getNode(root, "ImageDescriptor");
            if (idNode != null) {
                int x = Integer.parseInt(idNode.getAttribute("imageLeftPosition")) / 2;
                int y = Integer.parseInt(idNode.getAttribute("imageTopPosition")) / 2;
                int w = Integer.parseInt(idNode.getAttribute("imageWidth")) / 2;
                int h = Integer.parseInt(idNode.getAttribute("imageHeight")) / 2;
                bounds = new Rectangle(x, y, w, h);
            } else {
                bounds = new Rectangle(0, 0, image.getWidth() / 2, image.getHeight() / 2);
            }
            
            IIOMetadataNode gceNode = getNode(root, "GraphicControlExtension");
            if (gceNode != null) {
                disposal = gceNode.getAttribute("disposalMethod");
                delay = gceNode.getAttribute("delayTime");
                if (delay.equals("0")) {
                    delay = "4";
                }
            } else {
                disposal = "";
                delay = "4";
            }
        }
        
    }
    
    public byte[] loadedImage;
    public String timings;
    
    private ImageImport(byte[] loadedImage, String timings) {
        this.loadedImage = loadedImage;
        this.timings = timings;
    }
    
    private static IIOMetadataNode getNode(IIOMetadataNode parent, String name) {
        NodeList nodes = parent.getElementsByTagName(name);
        if (nodes.getLength() > 0) {
            return (IIOMetadataNode)nodes.item(0);
        } else {
            return null;
        }
    }
    
    private static ImageImport loadGIF(File file) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(file));
        
        IIOMetadata streamMetadata = reader.getStreamMetadata();
        String metaFormatName = streamMetadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode)streamMetadata.getAsTree(metaFormatName);
        
        int frameWidth = 0;
        int frameHeight = 0;
        
        IIOMetadataNode lsdNode = getNode(root, "LogicalScreenDescriptor");
        if (lsdNode != null) {
            frameWidth = Integer.parseInt(lsdNode.getAttribute("logicalScreenWidth")) / 2;
            frameHeight = Integer.parseInt(lsdNode.getAttribute("logicalScreenHeight")) / 2;
        }
        
        List<GIFFrame> frames = new ArrayList<GIFFrame>();
        
        Iterator<IIOImage> it = reader.readAll(null);
        while (it.hasNext()) {
            frames.add(new GIFFrame(it.next()));
        }
        
        BufferedImage sheet = new BufferedImage(frames.size() * frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
        StringBuilder timings = new StringBuilder();
        
        Graphics2D sheetG = sheet.createGraphics();
        BufferedImage currentFrame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D frameG = currentFrame.createGraphics();
        frameG.setBackground(new Color(0x00000000, true));
        for (int i = 0; i < frames.size(); i++) {
            GIFFrame frame = frames.get(i);
            
            BufferedImage previousFrame = null;
            if (frame.disposal.equals("restoreToPrevious")) {
                previousFrame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
                previousFrame.createGraphics().drawImage(currentFrame, 0, 0, frameWidth, frameHeight, null);
            }
            
            frameG.drawImage(frame.image, frame.bounds.x, frame.bounds.y, frame.bounds.width, frame.bounds.height, null);
            sheetG.drawImage(currentFrame, i * frameWidth, 0, frameWidth, frameHeight, null);
            
            if (frame.disposal.equals("restoreToBackgroundColor")) {
                frameG.clearRect(frame.bounds.x, frame.bounds.y, frame.bounds.width, frame.bounds.height);
            } else if (frame.disposal.equals("restoreToPrevious")) {
                frameG.clearRect(0, 0, frameWidth, frameHeight);
                frameG.drawImage(previousFrame, 0, 0, frameWidth, frameHeight, null);
            }
            
            if (i != 0) timings.append(",");
            timings.append(frame.delay);
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(sheet, "png", out);
        
        return new ImageImport(out.toByteArray(), timings.toString());
    }
    
    public static ImageImport load(File file) throws IOException {
        String filename = file.getName().toLowerCase();
        if (filename.endsWith(".gif")) {
            return loadGIF(file);
        } else {
            return new ImageImport(Files.readAllBytes(file.toPath()), null);
        }
    }
    
}
