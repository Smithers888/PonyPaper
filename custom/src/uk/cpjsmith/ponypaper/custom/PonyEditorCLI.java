package uk.cpjsmith.ponypaper.custom;

import java.io.File;

public class PonyEditorCLI {
    
    PonyEditor editor;
    
    public PonyEditorCLI() {
        editor = new PonyEditor();
    }
    
    private static void checkArgument(String[] args, int i) throws PonyEditor.GenericException {
        if (i + 1 >= args.length) throw new PonyEditor.GenericException("", "Option " + args[i] + " requires an argument.");
    }
    
    private static void checkArgument(String[] args, int i, int count) throws PonyEditor.GenericException {
        if (i + count >= args.length) throw new PonyEditor.GenericException("", "Option " + args[i] + " requires " + count + " arguments.");
    }
    
    public void processArguments(String[] args) {
        try {
            int currentAction = -1;
            
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-action":
                    {
                        checkArgument(args, i);
                        String actionName = args[++i];
                        currentAction = editor.findAction(actionName);
                        if (currentAction < 0) {
                            currentAction = editor.addAction(actionName);
                        }
                        break;
                    }
                    case "-load":
                        checkArgument(args, i);
                        editor.load(new File(args[++i]));
                        break;
                        
                    case "-next":
                    {
                        checkArgument(args, i, 2);
                        if (currentAction < 0) throw new PonyEditor.GenericException("", "No current action for " + args[i]);
                        String actionType = args[++i];
                        String actionNames = args[++i];
                        try {
                            editor.setActionNext(currentAction, actionType, actionNames);
                        } catch (IndexOutOfBoundsException e) {
                            throw new PonyEditor.GenericException("", "Can't set next actions for type " + actionType);
                        }
                        break;
                    }
                    case "-save":
                        checkArgument(args, i);
                        try {
                            editor.validate();
                        } catch (PonyEditor.GenericException e) {
                            for (String s : e.detail) System.err.println(s);
                        }
                        editor.save(new File(args[++i]));
                        break;
                        
                    case "-special":
                        checkArgument(args, i);
                        if (currentAction < 0) throw new PonyEditor.GenericException("", "No current action for " + args[i]);
                        editor.setActionSpecial(currentAction, args[++i]);
                        break;
                        
                    case "-sprite":
                    {
                        checkArgument(args, i, 2);
                        if (currentAction < 0) throw new PonyEditor.GenericException("", "No current action for " + args[i]);
                        String spriteDir = args[++i];
                        String spritePath = args[++i];
                        try {
                            editor.loadActionSprite(currentAction, spriteDir, new File(spritePath));
                        } catch (IndexOutOfBoundsException e) {
                            throw new PonyEditor.GenericException("", "Can't set sprite for direction " + spriteDir);
                        }
                        break;
                    }
                    case "-start":
                        checkArgument(args, i);
                        editor.setStartActions(args[++i]);
                        break;
                        
                    default:
                        throw new PonyEditor.GenericException("", "Invalid option: " + args[i]);
                }
            }
        } catch (PonyEditor.GenericException e) {
            for (String s : e.detail) System.err.println(s);
        }
    }
    
    public static void showArguments() {
        System.out.println("-load FILE");
        System.out.println("    Load a pony definition from the given file path.");
        System.out.println("-save FILE");
        System.out.println("    Save the pony definition to the given file path.");
        System.out.println("-start NAMES");
        System.out.println("    Set the starting actions.");
        System.out.println("-action NAME");
        System.out.println("    Switch to editing the named action, creating it if it does not exist.");
        System.out.println("-next TYPE NAMES");
        System.out.println("    Set the current action's next actions of the given type.");
        System.out.println("-special TYPE");
        System.out.println("    Set the current action's special type.");
        System.out.println("-sprite DIRECTION FILE");
        System.out.println("    Set the current action's sprite for the given direction.");
    }
    
}
