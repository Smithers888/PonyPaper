package uk.cpjsmith.ponypaper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PonyEditorGUI extends JPanel {
    
    private static abstract class MyDocumentListener implements DocumentListener {
        
        public abstract void update(DocumentEvent e);
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            update(e);
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            update(e);
        }
        
    }
    
    private class ActionPanel extends JPanel {
        
        DocumentListener specialTypeListener = new MyDocumentListener() {
            public void update(DocumentEvent e) {
                if (currentIndex >= 0) {
                    editor.setActionSpecial(currentIndex, specialTypeField.getText());
                    hasChanges = true;
                }
            }
        };
        
        ActionListener previewLeftListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previewImage(editor.getActionImage(currentIndex, "left"), editor.getActionTimings(currentIndex, "left"));
            }
        };
        
        ActionListener importLeftListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importImage("left");
            }
        };
        
        DocumentListener timingsLeftListener = new MyDocumentListener() {
            public void update(DocumentEvent e) {
                if (currentIndex >= 0) {
                    editor.setActionTimings(currentIndex, "left", timingsLeftField.getText());
                    hasChanges = true;
                }
            }
        };
        
        ActionListener previewRightListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previewImage(editor.getActionImage(currentIndex, "right"), editor.getActionTimings(currentIndex, "right"));
            }
        };
        
        ActionListener importRightListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importImage("right");
            }
        };
        
        DocumentListener timingsRightListener = new MyDocumentListener() {
            public void update(DocumentEvent e) {
                if (currentIndex >= 0) {
                    editor.setActionTimings(currentIndex, "right", timingsRightField.getText());
                    hasChanges = true;
                }
            }
        };
        
        DocumentListener nextWaitingListener = new MyDocumentListener() {
            public void update(DocumentEvent e) {
                if (currentIndex >= 0) {
                    editor.setActionNext(currentIndex, "waiting", nextWaitingField.getText());
                    hasChanges = true;
                }
            }
        };
        
        DocumentListener nextMovingListener = new MyDocumentListener() {
            public void update(DocumentEvent e) {
                if (currentIndex >= 0) {
                    editor.setActionNext(currentIndex, "moving", nextMovingField.getText());
                    hasChanges = true;
                }
            }
        };
        
        DocumentListener nextDragListener = new MyDocumentListener() {
            public void update(DocumentEvent e) {
                if (currentIndex >= 0) {
                    editor.setActionNext(currentIndex, "drag", nextDragField.getText());
                    hasChanges = true;
                }
            }
        };
        
        JTextField specialTypeField;
        JTextField imageLeftField;
        JButton imageLeftPreview;
        JButton imageLeftImport;
        JTextField timingsLeftField;
        JTextField imageRightField;
        JButton imageRightPreview;
        JButton imageRightImport;
        JTextField timingsRightField;
        JTextField nextWaitingField;
        JTextField nextMovingField;
        JTextField nextDragField;
        
        int currentIndex;
        
        ActionPanel() {
            super(new GridBagLayout());
            
            ((GridBagLayout)getLayout()).columnWeights = new double[] { 0.0, 1.0 };
            GridBagConstraints c;
            
            JLabel specialTypeLabel = new JLabel("Special type:");
            c = getConstraints(0, 0);
            c.anchor = GridBagConstraints.WEST;
            add(specialTypeLabel, c);
            
            specialTypeField = new JTextField();
            specialTypeField.getDocument().addDocumentListener(specialTypeListener);
            c = getConstraints(1, 0);
            c.weighty = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(specialTypeField, c);
            
            JLabel imageLeftLabel = new JLabel("Left sprite:");
            c = getConstraints(0, 1);
            c.anchor = GridBagConstraints.SOUTHWEST;
            add(imageLeftLabel, c);
            
            imageLeftField = new JTextField();
            imageLeftField.setEditable(false);
            c = getConstraints(1, 1);
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.SOUTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(imageLeftField, c);
            
            imageLeftPreview = new JButton("Preview");
            imageLeftPreview.addActionListener(previewLeftListener);
            c = getConstraints(1, 2);
            c.fill = GridBagConstraints.HORIZONTAL;
            add(imageLeftPreview, c);
            
            imageLeftImport = new JButton("Import image");
            imageLeftImport.addActionListener(importLeftListener);
            c = getConstraints(1, 3);
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(imageLeftImport, c);
            
            JLabel timingsLeftLabel = new JLabel("Left timings:");
            c = getConstraints(0, 4);
            c.anchor = GridBagConstraints.WEST;
            add(timingsLeftLabel, c);
            
            timingsLeftField = new JTextField();
            timingsLeftField.getDocument().addDocumentListener(timingsLeftListener);
            c = getConstraints(1, 4);
            c.weighty = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(timingsLeftField, c);
            
            JLabel imageRightLabel = new JLabel("Right sprite:");
            c = getConstraints(0, 5);
            c.anchor = GridBagConstraints.SOUTHWEST;
            add(imageRightLabel, c);
            
            imageRightField = new JTextField();
            imageRightField.setEditable(false);
            c = getConstraints(1, 5);
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.SOUTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(imageRightField, c);
            
            imageRightPreview = new JButton("Preview");
            imageRightPreview.addActionListener(previewRightListener);
            c = getConstraints(1, 6);
            c.fill = GridBagConstraints.HORIZONTAL;
            add(imageRightPreview, c);
            
            imageRightImport = new JButton("Import image");
            imageRightImport.addActionListener(importRightListener);
            c = getConstraints(1, 7);
            c.weighty = 0.5;
            c.anchor = GridBagConstraints.NORTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(imageRightImport, c);
            
            JLabel timingsRightLabel = new JLabel("Right timings:");
            c = getConstraints(0, 8);
            c.anchor = GridBagConstraints.WEST;
            add(timingsRightLabel, c);
            
            timingsRightField = new JTextField();
            timingsRightField.getDocument().addDocumentListener(timingsRightListener);
            c = getConstraints(1, 8);
            c.weighty = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(timingsRightField, c);
            
            JLabel nextWaitingLabel = new JLabel("Next waiting actions:");
            c = getConstraints(0, 9);
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.WEST;
            add(nextWaitingLabel, c);
            
            nextWaitingField = new JTextField();
            nextWaitingField.getDocument().addDocumentListener(nextWaitingListener);
            c = getConstraints(1, 9);
            c.fill = GridBagConstraints.HORIZONTAL;
            add(nextWaitingField, c);
            
            JLabel nextMovingLabel = new JLabel("Next moving actions:");
            c = getConstraints(0, 10);
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.WEST;
            add(nextMovingLabel, c);
            
            nextMovingField = new JTextField();
            nextMovingField.getDocument().addDocumentListener(nextMovingListener);
            c = getConstraints(1, 10);
            c.fill = GridBagConstraints.HORIZONTAL;
            add(nextMovingField, c);
            
            JLabel nextDragLabel = new JLabel("Next drag actions:");
            c = getConstraints(0, 11);
            c.weighty = 1.0;
            c.anchor = GridBagConstraints.WEST;
            add(nextDragLabel, c);
            
            nextDragField = new JTextField();
            nextDragField.getDocument().addDocumentListener(nextDragListener);
            c = getConstraints(1, 11);
            c.fill = GridBagConstraints.HORIZONTAL;
            add(nextDragField, c);
            
            setAction(-1);
        }
        
        void setAction(int index) {
            currentIndex = -1;
            
            if (index >= 0) {
                specialTypeField.setText(editor.getActionSpecial(index));
                imageLeftField.setText(editor.getActionImage(index, "left").isEmpty() ? "" : "<image>");
                timingsLeftField.setText(editor.getActionTimings(index, "left"));
                imageRightField.setText(editor.getActionImage(index, "right").isEmpty() ? "" : "<image>");
                timingsRightField.setText(editor.getActionTimings(index, "right"));
                nextWaitingField.setText(editor.getActionNext(index, "waiting"));
                nextMovingField.setText(editor.getActionNext(index, "moving"));
                nextDragField.setText(editor.getActionNext(index, "drag"));
                
                setEnabled(true);
            } else {
                specialTypeField.setText("");
                imageLeftField.setText("");
                timingsLeftField.setText("");
                imageRightField.setText("");
                timingsRightField.setText("");
                nextWaitingField.setText("");
                nextMovingField.setText("");
                nextDragField.setText("");
                
                setEnabled(false);
            }
            
            currentIndex = index;
        }
        
        void previewImage(String b64Image, String timings) {
            try {
                Base64.Decoder b64Dec = Base64.getDecoder();
                byte[] rawImage = b64Dec.decode(b64Image);
                Image image = ImageIO.read(new ByteArrayInputStream(rawImage));
                if (image == null) throw new IllegalArgumentException();
                JOptionPane.showMessageDialog(this, new SpriteSheetPreview(image, timings.split(",").length), "Image Preview", JOptionPane.PLAIN_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "The image could not be decoded. Please load a new image.", "Image Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "The image could not be decoded. Please load a new image.", "Image Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        void importImage(String direction) {
            fc.setFileFilter(new FileNameExtensionFilter("All Supported Formats", "png", "gif"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("GIF Animations", "gif"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    editor.loadActionSprite(currentIndex, direction, file);
                    setAction(currentIndex);
                    hasChanges = true;
                } catch (PonyEditor.GenericException e) {
                    JOptionPane.showMessageDialog(this, e.detail, e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
            }
            fc.resetChoosableFileFilters();
        }
        
        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            
            specialTypeField.setEnabled(enabled);
            imageLeftField.setEnabled(enabled);
            imageLeftPreview.setEnabled(enabled);
            imageLeftImport.setEnabled(enabled);
            timingsLeftField.setEnabled(enabled);
            imageRightField.setEnabled(enabled);
            imageRightPreview.setEnabled(enabled);
            imageRightImport.setEnabled(enabled);
            timingsRightField.setEnabled(enabled);
            nextWaitingField.setEnabled(enabled);
            nextMovingField.setEnabled(enabled);
            nextDragField.setEnabled(enabled);
        }
        
    }
    
    private WindowListener windowListener = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            if (!checkChanges()) return;
            e.getWindow().dispose();
        }
    };
    
    private ActionListener fileNewListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (!checkChanges()) return;
            createNewPony();
        }
    };
    
    private ActionListener fileOpenListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (!checkChanges()) return;
            fc.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
            if (fc.showOpenDialog(PonyEditorGUI.this) == JFileChooser.APPROVE_OPTION) {
                loadPony(fc.getSelectedFile());
            }
            fc.resetChoosableFileFilters();
        }
    };
    
    private ActionListener fileSaveListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            defaultSave();
        }
    };
    
    private ActionListener fileSaveAsListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            fc.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
            if (fc.showSaveDialog(PonyEditorGUI.this) == JFileChooser.APPROVE_OPTION) {
                savePony(fc.getSelectedFile());
            }
            fc.resetChoosableFileFilters();
        }
    };
    
    private ListSelectionListener actionListSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            actionSettingsPane.setAction(actionList.getSelectedIndex());
        }
    };
    
    private ActionListener newActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String actionName = JOptionPane.showInputDialog(PonyEditorGUI.this, "Enter a name for the new action:", "New Action", JOptionPane.PLAIN_MESSAGE);
            if (actionName != null && !actionName.equals("")) {
                int i = editor.addAction(actionName);
                hasChanges = true;
                
                actionListModel.addElement(actionName);
                actionList.setSelectedIndex(i);
            }
        }
    };
    
    private ActionListener deleteActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int i = actionList.getSelectedIndex();
            if (i != -1) {
                editor.removeAction(i);
                hasChanges = true;
                
                actionListModel.remove(i);
                actionList.setSelectedIndex(i < editor.getActionCount() ? i : i - 1);
            }
        }
    };
    
    private DocumentListener startActionsListener = new MyDocumentListener() {
        public void update(DocumentEvent e) {
            if (!startActionsField.getText().equals(editor.getStartActions())) {
                editor.setStartActions(startActionsField.getText());
                hasChanges = true;
            }
        }
    };
    
    private JFrame parentFrame;
    private DefaultListModel<String> actionListModel;
    private JList<String> actionList;
    private ActionPanel actionSettingsPane;
    private JTextField startActionsField;
    
    private JFileChooser fc;
    
    private PonyEditor editor;
    private File currentFile;
    private boolean hasChanges;
    
    private PonyEditorGUI(JFrame parentFrame) {
        super(new GridBagLayout());
        
        this.parentFrame = parentFrame;
        
        fc = new JFileChooser(".");
        fc.setAcceptAllFileFilterUsed(false);
        
        GridBagConstraints c;
        
        JComponent actionListPane = createActionListPane();
        c = getConstraints(0, 0);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(actionListPane, c);
        
        actionSettingsPane = new ActionPanel();
        c = getConstraints(1, 0);
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(actionSettingsPane, c);
        
        JComponent startActionsPane = createStartActionsPane();
        c = getConstraints(0, 1);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        add(startActionsPane, c);
        
        editor = new PonyEditor();
        setFile(null);
        hasChanges = false;
    }
    
    private void setFile(File file) {
        currentFile = file;
        if (file != null) {
            parentFrame.setTitle("PonyPaper Custom Pony Editor - " + file.getName());
        } else {
            parentFrame.setTitle("PonyPaper Custom Pony Editor");
        }
    }
    
    private void setUIFromPony() {
        actionListModel.clear();
        for (int i = 0; i < editor.getActionCount(); i++) {
            actionListModel.addElement(editor.getActionName(i));
        }
        startActionsField.setText(editor.getStartActions());
        
        if (editor.getActionCount() > 0) {
            actionList.setSelectedIndex(0);
        }
    }
    
    private void createNewPony() {
        editor.reset();
        
        setUIFromPony();
        
        setFile(null);
        hasChanges = false;
    }
    
    private void loadPony(File file) {
        try {
            editor.load(file);
        } catch (PonyEditor.GenericException e) {
            JOptionPane.showMessageDialog(this, e.detail, e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setUIFromPony();
        
        setFile(file);
        hasChanges = false;
    }
    
    private boolean savePony(File file) {
        try {
            editor.validate();
        } catch (PonyEditor.GenericException e) {
            int len = e.detail.length;
            String[] messages = Arrays.copyOf(e.detail, len + 2);
            messages[len + 0] = "";
            messages[len + 1] = "Save anyway?";
            if (JOptionPane.showConfirmDialog(this, messages, "Invalid Pony", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return false;
        }
        try {
            editor.save(file);
        } catch (PonyEditor.GenericException e) {
            JOptionPane.showMessageDialog(this, e.detail, e.getMessage(), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        setFile(file);
        hasChanges = false;
        return true;
    }
    
    /**
     * Saves the pony, prompting for file path only if there is none currently.
     * 
     * @return {@code true} if the pony was saved, {@code false} if the user
     *         cancelled the 'Save as' dialog or some other error occurred
     */
    private boolean defaultSave() {
        File file = currentFile;
        if (file == null) {
            fc.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
            }
            fc.resetChoosableFileFilters();
        }
        
        if (file != null) {
            return savePony(file);
        } else {
            return false;
        }
    }
    
    /**
     * Offers to save changes to the pony, if any. The user may chose to save
     * the changes (which is performed by this method), abandon them or cancel
     * whatever operation would cause them to be lost.
     * 
     * @return {@code true} iff the operation should proceed (i.e. either the
     *         changes have been saved or the user has opted to discard them)
     */
    private boolean checkChanges() {
        if (!hasChanges) return true;
        
        switch (JOptionPane.showConfirmDialog(this, "The current pony has unsaved changes. Save now?", "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION)) {
            case JOptionPane.YES_OPTION:
                return defaultSave();
                
            case JOptionPane.NO_OPTION:
                return true;
                
            case JOptionPane.CANCEL_OPTION:
            default:
                return false;
        }
    }
    
    private static GridBagConstraints getConstraints(int gridx, int gridy) {
        GridBagConstraints result = new GridBagConstraints();
        result.gridx = gridx;
        result.gridy = gridy;
        return result;
    }
    
    private JComponent createActionListPane() {
        JPanel result = new JPanel(new GridBagLayout());
        
        GridBagConstraints c;
        
        actionListModel = new DefaultListModel<String>();
        actionList = new JList<String>(actionListModel);
        actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionList.setVisibleRowCount(-1);
        actionList.getSelectionModel().addListSelectionListener(actionListSelectionListener);
        JScrollPane actionListScroller = new JScrollPane(actionList);
        c = getConstraints(0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridwidth = 2;
        result.add(actionListScroller, c);
        
        JButton newAction = new JButton("New action");
        newAction.addActionListener(newActionListener);
        c = getConstraints(0, 1);
        result.add(newAction, c);
        
        JButton deleteAction = new JButton("Delete action");
        deleteAction.addActionListener(deleteActionListener);
        c = getConstraints(1, 1);
        result.add(deleteAction, c);
        
        return result;
    }
    
    private JComponent createStartActionsPane() {
        JPanel result = new JPanel(new GridBagLayout());
        
        GridBagConstraints c;
        
        JLabel startActionsLabel = new JLabel("Start actions:");
        c = getConstraints(0, 0);
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.WEST;
        result.add(startActionsLabel, c);
        
        startActionsField = new JTextField();
        startActionsField.getDocument().addDocumentListener(startActionsListener);
        c = getConstraints(1, 0);
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        result.add(startActionsField, c);
        
        return result;
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar result = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newPony = new JMenuItem("New");
        newPony.setMnemonic(KeyEvent.VK_N);
        newPony.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newPony.addActionListener(fileNewListener);
        fileMenu.add(newPony);
        
        JMenuItem open = new JMenuItem("Open...");
        open.setMnemonic(KeyEvent.VK_O);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        open.addActionListener(fileOpenListener);
        fileMenu.add(open);
        
        JMenuItem save = new JMenuItem("Save");
        save.setMnemonic(KeyEvent.VK_S);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        save.addActionListener(fileSaveListener);
        fileMenu.add(save);
        
        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.setMnemonic(KeyEvent.VK_A);
        saveAs.addActionListener(fileSaveAsListener);
        fileMenu.add(saveAs);
        
        result.add(fileMenu);
        
        return result;
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame();
        frame.setMinimumSize(new Dimension(600, 450));
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        PonyEditorGUI contentPane = new PonyEditorGUI(frame);
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);
        frame.addWindowListener(contentPane.windowListener);
        
        frame.setJMenuBar(contentPane.createMenuBar());
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void start() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
}
