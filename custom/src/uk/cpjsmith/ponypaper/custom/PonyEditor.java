package uk.cpjsmith.ponypaper.custom;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.cpjsmith.ponypaper.PonyDefinition;

/**
 * Wraps a {@code PonyDefinition} with the operational functions needed to
 * provide an editor. Also contains the {@code main(String[])} function to
 * start a graphical or command-line editor.
 */
public class PonyEditor {
    
    /**
     * Represents an error that should be displayed to the user.
     */
    public static class GenericException extends Exception {
        
        /** Contains one or more lines of text to show to the user. */
        public String[] detail;
        
        /**
         * Creates a new {@code GenericException} object.
         * 
         * @param message a brief description of the error
         * @param detail  contains enough the information that the user just
         *                does know what went wrong
         */
        public GenericException(String message, String... detail) {
            super(message);
            this.detail = detail;
        }
        
    }
    
    private PonyDefinition ponyDefinition;
    
    /**
     * Creates a new editor instance with a blank pony.
     */
    public PonyEditor() {
        ponyDefinition = new PonyDefinition();
    }
    
    /**
     * Replaces the current pony with a blank slate.
     */
    public void reset() {
        ponyDefinition = new PonyDefinition();
    }
    
    /**
     * Loads a pony definition from the given file. If loading fails, a {@code
     * GenericException} is thrown and the current pony is unchanged.
     * 
     * @param file the file to load from
     * @throws GenericException if the file cannot be opened or is invalid
     */
    public void load(File file) throws GenericException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("An internal error occurred, cannot load file", e);
        }
        
        Document document;
        try {
            document = docBuilder.parse(file);
        } catch (IOException e) {
            throw new GenericException("Invalid File", "Failed to read " + file);
        } catch (SAXException e) {
            throw new GenericException("Invalid Pony", "Failed to load " + file + " due to XML errors.");
        }
        
        try {
            ponyDefinition = new PonyDefinition(document);
        } catch (PonyDefinition.InvalidPonyException e) {
            String[] messages = new String[e.errors.size() + 1];
            messages[0] = "Failed to load " + file + " due to the following errors:";
            for (int i = 0; i < e.errors.size(); i++) messages[i + 1] = e.errors.get(i);
            throw new GenericException("Invalid Pony", messages);
        }
    }
    
    /**
     * Saves the current pony to the given file.
     * 
     * @param file the file to save to
     * @throws GenericException if the file cannot be written
     */
    public void save(File file) throws GenericException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            ponyDefinition.writeDefinition(writer);
        } catch (IOException e) {
            throw new GenericException("File Error", "An error occurred writing " + file + ".");
        } finally {
            if (writer != null) writer.close();
        }
    }
    
    /**
     * Checks that the pony is currently valid. If this method raises an
     * exception, the pony will not be usable in PonyPaper; the user should be
     * usually be warned of this prior to saving.
     * 
     * @throws GenericException if the pony is invalid
     */
    public void validate() throws GenericException {
        try {
            ponyDefinition.validate();
        } catch (PonyDefinition.InvalidPonyException e) {
            String[] messages = new String[e.errors.size() + 1];
            messages[0] = "The current pony fails to validate, it will not be usable in the app due to the following errors:";
            for (int i = 0; i < e.errors.size(); i++) messages[i + 1] = e.errors.get(i);
            throw new GenericException("Invalid Pony", messages);
        }
    }
    
    /**
     * Returns the pony's start actions.
     * 
     * @return the start actions as a comma-separated string
     */
    public String getStartActions() {
        return ponyDefinition.startActions;
    }
    
    /**
     * Changes the pony's start actions.
     * 
     * @param actionNames the start actions as a comma-separated string
     */
    public void setStartActions(String actionNames) {
        ponyDefinition.startActions = actionNames;
    }
    
    /**
     * Returns the number of actions that the pony has. All methods that take
     * an action index require it to be at least {@code 0} and strictly less
     * than {@code getActionCount()}; they throw {@code
     * IndexOutOfBoundsException} otherwise.
     * 
     * @return the number of actions
     */
    public int getActionCount() {
        return ponyDefinition.actions.length;
    }
    
    /**
     * Finds an action with the given name.
     * 
     * @param name the name of the action
     * @return the index of the action or {@code -1} if the name was not found
     */
    public int findAction(String name) {
        for (int i = 0; i < ponyDefinition.actions.length; i++) {
            if (ponyDefinition.actions[i].name == name) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Creates a new action with the given name.
     * 
     * @param name the name of the action
     * @return the index of the newly created action
     */
    public int addAction(String name) {
        PonyDefinition.Action[] oldActions = ponyDefinition.actions;
        int oldCount = oldActions.length;
        int newCount = oldCount + 1;
        PonyDefinition.Action[] newActions = new PonyDefinition.Action[newCount];
        for (int i = 0; i < oldCount; i++) newActions[i] = oldActions[i];
        
        newActions[oldCount] = new PonyDefinition.Action();
        newActions[oldCount].name = name;
        
        ponyDefinition.actions = newActions;
        
        return oldCount;
    }
    
    /**
     * Removes an action.
     * 
     * @param index the index of the action to remove
     * @throws IndexOutOfBoundsException if {@code index < 0 || index >=
     *                                   getActionCount()}
     */
    public void removeAction(int index) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        
        PonyDefinition.Action[] oldActions = ponyDefinition.actions;
        int oldCount = oldActions.length;
        int newCount = oldCount - 1;
        PonyDefinition.Action[] newActions = new PonyDefinition.Action[newCount];
        
        for (int i = 0; i < index; i++) newActions[i] = oldActions[i];
        for (int i = index; i < newCount; i++) newActions[i] = oldActions[i + 1];
        
        ponyDefinition.actions = newActions;
    }
    
    public String getActionName(int index) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        return ponyDefinition.actions[index].name;
    }
    
    public void setActionName(int index, String name) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        ponyDefinition.actions[index].name = name;
    }
    
    public String getActionSpecial(int index) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        return ponyDefinition.actions[index].specialType;
    }
    
    public void setActionSpecial(int index, String specialType) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        ponyDefinition.actions[index].specialType = specialType;
    }
    
    public String getActionImage(int index, String direction) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        if (!ponyDefinition.actions[index].images.containsKey(direction)) throw new IndexOutOfBoundsException();
        return ponyDefinition.actions[index].images.get(direction);
    }
    
    public String getActionTimings(int index, String direction) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        if (!ponyDefinition.actions[index].timings.containsKey(direction)) throw new IndexOutOfBoundsException();
        return ponyDefinition.actions[index].timings.get(direction);
    }
    
    public void setActionTimings(int index, String direction, String timings) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        if (!ponyDefinition.actions[index].timings.containsKey(direction)) throw new IndexOutOfBoundsException();
        ponyDefinition.actions[index].timings.put(direction, timings);
    }
    
    /**
     * Loads a new sprite for an action. If the loaded image is an animation,
     * it is converted into a spritesheet and both the image and timings are
     * set, otherwise, only the image is set.
     * 
     * @param index      the index of the action
     * @param direction  the direction of the sprite to set
     * @param spriteFile the file to load the sprite from
     * @throws IndexOutOfBoundsException if {@code index < 0 || index >=
     *                                   getActionCount()}
     * @throws IndexOutOfBoundsException if {@code direction} is not a valid
     *                                   direction ("left" or "right")
     * @throws GenericException if the file cannot be loaded as an image
     */
    public void loadActionSprite(int index, String direction, File spriteFile) throws GenericException {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        if (!ponyDefinition.actions[index].images.containsKey(direction)) throw new IndexOutOfBoundsException();
        
        try {
            ImageImport imported = ImageImport.load(spriteFile);
            ponyDefinition.actions[index].images.put(direction, Base64.getEncoder().encodeToString(imported.loadedImage));
            if (imported.timings != null) {
                ponyDefinition.actions[index].timings.put(direction, imported.timings);
            }
        } catch (IOException e) {
            throw new GenericException("", "Failed to read " + spriteFile + ".");
        }
    }
    
    public String getActionNext(int index, String type) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        if (!ponyDefinition.actions[index].nextActions.containsKey(type)) throw new IndexOutOfBoundsException();
        return ponyDefinition.actions[index].nextActions.get(type);
    }
    
    public void setActionNext(int index, String type, String actionNames) {
        if (index < 0 || index >= ponyDefinition.actions.length) throw new IndexOutOfBoundsException();
        if (!ponyDefinition.actions[index].nextActions.containsKey(type)) throw new IndexOutOfBoundsException();
        ponyDefinition.actions[index].nextActions.put(type, actionNames);
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            PonyEditorGUI.start();
        } else if (args.length == 1 && args[0] == "-help") {
            System.out.println("PonyPaper custom pony editor");
            System.out.println("With no arguments, run a graphical user interface.");
            System.out.println("With -help, print this help.");
            System.out.println("With other arguments, process them in turn as follows:");
            System.out.println("");
            PonyEditorCLI.showArguments();
        } else {
            new PonyEditorCLI().processArguments(args);
        }
    }
    
}
