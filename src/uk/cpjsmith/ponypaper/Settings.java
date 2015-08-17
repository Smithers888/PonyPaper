package uk.cpjsmith.ponypaper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class Settings extends PreferenceActivity {
    
    static final int SELECT_BACKGROUND = 0;
    static final int SELECT_CUSTOM = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        File dir = getExternalFilesDir(null);
        if (dir != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            
            File[] files = dir.listFiles(AllPonies.xmlFilter);
            Arrays.sort(files);
            PreferenceCategory customCat = (PreferenceCategory)findPreference("pref_custom");
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                String prefKey = "pref_custom_" + fileName;
                
                if (!prefs.contains(prefKey)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(prefKey, true);
                    editor.commit();
                }
                
                CheckBoxPreference checkbox = new CheckBoxPreference(this);
                checkbox.setKey(prefKey);
                checkbox.setTitle(fileName);
                customCat.addPreference(checkbox);
            }
        }
        
        findPreference("pref_add_custom").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select Custom Pony"), SELECT_CUSTOM);
                return true;
            }
        });
        
        findPreference("pref_background").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean)newValue) {
                    File dir = getExternalFilesDir(null);
                    if (!new File(dir, "background").exists()) {
                        selectBackground();
                    }
                }
                return true;
            }
        });
        
        findPreference("pref_select_background").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                selectBackground();
                return true;
            }
        });
    }
    
    private void selectBackground() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Background"), SELECT_BACKGROUND);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_BACKGROUND:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    
                    try {
                        String hash = copyToLocalAndGetHash(imageUri, "background");
                        
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("pref_select_background", hash.toString());
                        editor.commit();
                    } catch (IOException e) {
                        showAlertDialog("Failed to set background", "An I/O error occurred.");
                    }
                }
                break;
                
            case SELECT_CUSTOM:
                if (resultCode == RESULT_OK) {
                    Uri ponyUri = data.getData();
                    
                    // Validate the pony before storing it.
                    try {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
                        InputStream in = getContentResolver().openInputStream(ponyUri);
                        Document document = docBuilder.parse(in);
                        PonyDefinition definition = new PonyDefinition(document);
                        definition.validate();
                    } catch (Exception e) {
                        showAlertDialog("Failed to add pony", "Selected file was not a valid custom pony definition.");
                        break;
                    }
                    
                    try {
                        String fileName = getFileName(ponyUri);
                        if (!fileName.endsWith(".xml")) {
                            fileName += ".xml";
                        }
                        
                        String hash = copyToLocalAndGetHash(ponyUri, fileName);
                        
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sp.edit();
                        String prefKey = "pref_custom_" + fileName;
                        editor.putBoolean(prefKey, true);
                        editor.putString("pref_add_custom", hash.toString());
                        editor.commit();
                        if (findPreference(prefKey) == null) {
                            CheckBoxPreference checkbox = new CheckBoxPreference(this);
                            checkbox.setKey(prefKey);
                            checkbox.setTitle(fileName);
                            ((PreferenceCategory)findPreference("pref_custom")).addPreference(checkbox);
                        }
                    } catch (IOException e) {
                        showAlertDialog("Failed to add pony", "An I/O error occurred.");
                    }
                }
                break;
        }
    }
    
    private String copyToLocalAndGetHash(Uri sourceUri, String destName) throws IOException {
        InputStream in = getContentResolver().openInputStream(sourceUri);
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            digester = null;
        }
        
        File dir = getExternalFilesDir(null);
        OutputStream out = new FileOutputStream(new File(dir, destName));
        
        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
            if (digester != null) digester.update(buffer, 0, n);
        }
        
        out.close();
        in.close();
        
        byte[] digest;
        if (digester != null) {
            digest = digester.digest();
        } else {
            digest = new byte[20];
            new Random().nextBytes(digest);
        }
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < digest.length; i++)
            hash.append(String.format("%02x", (256 + digest[i]) % 256));
        return hash.toString();
    }
    
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
            }
            if (cursor != null) cursor.close();
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
    
}
