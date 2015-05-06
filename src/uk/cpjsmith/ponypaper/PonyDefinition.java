package uk.cpjsmith.ponypaper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents a definition of a pony that can be loaded from XML at runtime.
 */
public class PonyDefinition {
    
    public static class InvalidPonyException extends Exception {
        
        public List<String> errors;
        
        public InvalidPonyException(List<String> errors) {
            super("The pony definition was invalid.");
            this.errors = errors;
        }
        
    }
    
    public static class Action {
        
        public String name;
        public String specialType;
        public final Map<String, String> images = new HashMap<String, String>();
        public final Map<String, String> timings = new HashMap<String, String>();
        public final Map<String, String> nextActions = new HashMap<String, String>();
        
        public Action() {
            name = "";
            specialType = "";
            images.put("left", "");
            timings.put("left", "");
            images.put("right", "");
            timings.put("right", "");
            nextActions.put("waiting", "");
            nextActions.put("moving", "");
            nextActions.put("drag", "");
        }
        
        public Action(Element element) throws InvalidPonyException {
            List<String> errors = new ArrayList<String>();
            
            name = element.getAttribute("name");
            if (name.equals("")) {
                errors.add("An <action> must have a name.");
            }
            
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                switch (node.getNodeType()) {
                    case Node.ELEMENT_NODE:
                    {
                        String nodeName = node.getNodeName();
                        if (nodeName.equals("specialtype")) {
                            addSpecialType((Element)node, errors);
                        } else if (nodeName.equals("image")) {
                            addImage((Element)node, errors);
                        } else if (nodeName.equals("timings")) {
                            addTimings((Element)node, errors);
                        } else if (nodeName.equals("nextactions")) {
                            addNextActions((Element)node, errors);
                        } else {
                            errors.add("Unexpected " + node.getNodeName() + " element.");
                        }
                        break;
                    }
                    
                    case Node.TEXT_NODE:
                    {
                        String text = node.getNodeValue().trim();
                        if (!text.isEmpty()) {
                            errors.add("Unexpected text " + text + ".");
                        }
                        break;
                    }
                    
                    default:
                        errors.add("Unexpected " + node.getNodeName() + " node.");
                        break;
                }
            }
            
            if (!errors.isEmpty()) throw new InvalidPonyException(errors);
            
            if (specialType == null) specialType = "";
            if (!images.containsKey("left")) images.put("left", "");
            if (!timings.containsKey("left")) timings.put("left", "");
            if (!images.containsKey("right")) images.put("right", "");
            if (!timings.containsKey("right")) timings.put("right", "");
            if (!nextActions.containsKey("waiting")) nextActions.put("waiting", "");
            if (!nextActions.containsKey("moving")) nextActions.put("moving", "");
            if (!nextActions.containsKey("drag")) nextActions.put("drag", "");
        }
        
        private void addSpecialType(Element element, List<String> errors) {
            if (specialType != null) {
                errors.add("Too many <specialtype> elements.");
                return;
            }
            specialType = getContent(element, errors).replaceAll("\\s+", "");
        }
        
        private void addImage(Element element, List<String> errors) {
            String direction = element.getAttribute("direction");
            if (!(direction.equals("left") || direction.equals("right"))) {
                errors.add("<image> must have a direction of left or right.");
                return;
            }
            if (images.containsKey(direction)) {
                errors.add("Too many <image> elements with direction " + direction + ".");
                return;
            }
            images.put(direction, getContent(element, errors).replaceAll("\\s+", ""));
        }
        
        private void addTimings(Element element, List<String> errors) {
            String direction = element.getAttribute("direction");
            if (!(direction.equals("left") || direction.equals("right"))) {
                errors.add("<timings> must have a direction of left or right.");
                return;
            }
            if (timings.containsKey(direction)) {
                errors.add("Too many <timings> elements with direction " + direction + ".");
                return;
            }
            timings.put(direction, getContent(element, errors));
        }
        
        private void addNextActions(Element element, List<String> errors) {
            String type = element.getAttribute("type");
            if (!(type.equals("waiting") || type.equals("moving") || type.equals("drag"))) {
                errors.add("<nextactions> must have a type of waiting, moving or drag.");
                return;
            }
            if (nextActions.containsKey(type)) {
                errors.add("Too many <nextactions> elements with type " + type + ".");
                return;
            }
            nextActions.put(type, getContent(element, errors));
        }
        
    }
    
    public Action[] actions;
    public String startActions;
    
    public PonyDefinition() {
        actions = new Action[0];
        startActions = "";
    }
    
    public PonyDefinition(Document document) throws InvalidPonyException {
        List<String> errors = new ArrayList<String>();
        
        Element element = document.getDocumentElement();
        
        if (!element.getTagName().equals("pony")) {
            errors.add("The root element must be <pony>.");
            throw new InvalidPonyException(errors);
        }
        
        List<Action> actions = new ArrayList<Action>();
        
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                {
                    String nodeName = node.getNodeName();
                    if (nodeName.equals("action")) {
                        try {
                            actions.add(new Action((Element)node));
                        } catch (InvalidPonyException e) {
                            errors.addAll(e.errors);
                        }
                    } else if (nodeName.equals("startactions")) {
                        if (startActions != null) {
                            errors.add("Too many <startactions> elements.");
                        } else {
                            startActions = getContent((Element)node, errors);
                        }
                    } else {
                        errors.add("Unexpected " + node.getNodeName() + " element.");
                    }
                    break;
                }
                
                case Node.TEXT_NODE:
                {
                    String text = node.getNodeValue().trim();
                    if (!text.isEmpty()) {
                        errors.add("Unexpected text " + text + ".");
                    }
                    break;
                }
                
                default:
                    errors.add("Unexpected " + node.getNodeName() + " node.");
                    break;
            }
        }
        
        if (!errors.isEmpty()) throw new InvalidPonyException(errors);
        
        this.actions = actions.toArray(new Action[actions.size()]);
    }
    
    private static String getContent(Element container, List<String> errors) {
        String result = "";
        boolean valid = true;
        
        for (Node node = container.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    result += node.getNodeValue();
                    break;
                    
                default:
                    errors.add("Unexpected " + node.getNodeName() + " node.");
                    valid = false;
                    break;
            }
        }
        
        return valid ? result.trim() : null;
    }
    
    private boolean hasAction(String name) {
        for (int i = 0; i < actions.length; i++) {
            if (actions[i].name.equals(name)) return true;
        }
        return false;
    }
    
    private void validateActionList(String value, String field1, String field2, List<String> errors) {
        if (value.length() == 0) {
            errors.add("Missing " + field1 + field2 + ".");
        } else {
            String[] names = value.split(",");
            for (int i = 0; i < names.length; i++) {
                if (!hasAction(names[i])) {
                    errors.add("Action " + names[i] + " not defined.");
                }
            }
        }
    }
    
    private void validateIntegerList(String value, String field1, String field2, List<String> errors) {
        if (value.length() == 0) {
            errors.add("Missing " + field1 + field2 + ".");
        } else {
            String[] valArray = value.split(",");
            try {
                for (int i = 0; i < valArray.length; i++) {
                    Integer.parseInt(valArray[i]);
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid integer in " + field1 + field2 + ".");
            }
        }
    }
    
    public void validate() throws InvalidPonyException {
        List<String> errors = new ArrayList<String>();
        
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            String name = action.name;
            
            for (int j = 0; j < actions.length; j++) {
                if (i != j && name.equals(actions[j].name)) {
                    errors.add("Multiple actions with name " + name);
                }
            }
            
            String specialType = action.specialType;
            if (!(specialType.equals("") || specialType.equals("teleport-out") || specialType.equals("teleport-in"))) {
                errors.add("Invalid specialtype for " + name + ".");
            }
            
            if (action.images.get("left").isEmpty()) {
                errors.add("Missing left image for " + name + ".");
            }
            
            validateIntegerList(action.timings.get("left"), "left timings for ", name, errors);
            
            if (action.images.get("right").isEmpty()) {
                errors.add("Missing right image for " + name + ".");
            }
            
            validateIntegerList(action.timings.get("right"), "right timings for ", name, errors);
            
            validateActionList(action.nextActions.get("waiting"), "waiting actions for ", name, errors);
            validateActionList(action.nextActions.get("moving"), "moving actions for ", name, errors);
            validateActionList(action.nextActions.get("drag"), "drag actions for ", name, errors);
        }
        
        validateActionList(startActions, "start actions", "", errors);
        
        if (!errors.isEmpty()) throw new InvalidPonyException(errors);
    }
    
    private static void writeAttribute(PrintWriter writer, String name, String value) {
        writer.print(" ");
        writer.print(name);
        writer.print("=\"");
        writer.print(value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;"));
        writer.print("\"");
    }
    
    private static void writeCharacters(PrintWriter writer, String value) {
        writer.print(value.replaceAll("&", "&amp;").replaceAll("<", "&lt;"));
    }
    
    private static void writeSplit(PrintWriter writer, String value, String indent) {
        final int N = 128;
        for (int i = 0; i < value.length(); i += N) {
            writer.print(indent);
            writeCharacters(writer, value.substring(i, Math.min(i + N, value.length())));
            writer.println();
        }
    }
    
    public void writeDefinition(PrintWriter writer) {
        writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        writer.println("<pony>");
        
        for (int i = 0; i < actions.length; i++) {
            Action action = actions[i];
            
            writer.print("    <action");
            writeAttribute(writer, "name", action.name);
            writer.println(">");
            
            if (!action.specialType.isEmpty()) {
                writer.print("        <specialtype>");
                writeCharacters(writer, action.specialType);
                writer.println("</specialtype>");
            }
            
            writer.println("        <image direction=\"left\">");
            writeSplit(writer, action.images.get("left"), "            ");
            writer.println("        </image>");
            
            writer.print("        <timings direction=\"left\">");
            writeCharacters(writer, action.timings.get("left"));
            writer.println("</timings>");
            
            writer.println("        <image direction=\"right\">");
            writeSplit(writer, action.images.get("right"), "            ");
            writer.println("        </image>");
            
            writer.print("        <timings direction=\"right\">");
            writeCharacters(writer, action.timings.get("right"));
            writer.println("</timings>");
            
            writer.print("        <nextactions type=\"waiting\">");
            writeCharacters(writer, action.nextActions.get("waiting"));
            writer.println("</nextactions>");
            
            writer.print("        <nextactions type=\"moving\">");
            writeCharacters(writer, action.nextActions.get("moving"));
            writer.println("</nextactions>");
            
            writer.print("        <nextactions type=\"drag\">");
            writeCharacters(writer, action.nextActions.get("drag"));
            writer.println("</nextactions>");
            
            writer.println("    </action>");
        }
        
        writer.print("    <startactions>");
        writeCharacters(writer, startActions);
        writer.println("</startactions>");
        
        writer.println("</pony>");
    }
    
}
