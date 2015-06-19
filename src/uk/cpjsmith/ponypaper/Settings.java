package uk.cpjsmith.ponypaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Settings extends PreferenceActivity {
    
    static final int SELECT_BACKGROUND = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
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
        if (requestCode == SELECT_BACKGROUND) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                
                StringBuilder hash = new StringBuilder();
                try {
                    InputStream in = getContentResolver().openInputStream(imageUri);
                    MessageDigest digester;
                    try {
                        digester = MessageDigest.getInstance("SHA-1");
                    } catch (NoSuchAlgorithmException e) {
                        digester = null;
                    }
                    
                    File dir = getExternalFilesDir(null);
                    OutputStream out = new FileOutputStream(new File(dir, "background"));
                    
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
                    for (int i = 0; i < digest.length; i++)
                        hash.append(String.format("%02x", (256 + digest[i]) % 256));
                } catch (IOException e) {
                }
                
                android.content.SharedPreferences sp = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
                android.content.SharedPreferences.Editor editor = sp.edit();
                editor.putString("pref_select_background", hash.toString());
                editor.commit();
            }
        }
    }
    
}
