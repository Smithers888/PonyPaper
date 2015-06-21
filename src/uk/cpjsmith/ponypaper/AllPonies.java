package uk.cpjsmith.ponypaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 * Contains the definitions of the available ponies.
 */
public class AllPonies {
    
    /**
     * Class is not instantiable.
     */
    private AllPonies() {
    }
    
    /**
     * Returns the complete list of ponies.
     * 
     * @param context the current application context
     * @param prefs   the user's preferences of which ponies to load
     * @return ponies, so many ponies
     */
    public static ArrayList<Pony> getPonies(Context context, SharedPreferences prefs) {
        ArrayList<Pony> result = new ArrayList<Pony>();
        
        Resources res = context.getResources();
        if (prefs.getBoolean("pref_ab", true)) result.add(makeAppleBloom(res));
        if (prefs.getBoolean("pref_aj", true)) result.add(makeApplejack(res));
        if (prefs.getBoolean("pref_bigmac", true)) result.add(makeBigMcIntosh(res));
        if (prefs.getBoolean("pref_derpy", true)) result.add(makeDerpyHooves(res));
        if (prefs.getBoolean("pref_doctor", true)) result.add(makeDoctorHooves(res));
        if (prefs.getBoolean("pref_fs", true)) result.add(makeFluttershy(res));
        if (prefs.getBoolean("pref_lyra", true)) result.add(makeLyraHeartstrings(res));
        if (prefs.getBoolean("pref_octavia", true)) result.add(makeOctavia(res));
        if (prefs.getBoolean("pref_pp", true)) result.add(makePinkiePie(res));
        if (prefs.getBoolean("pref_celestia", true)) result.add(makePrincessCelestia(res));
        if (prefs.getBoolean("pref_luna", true)) result.add(makePrincessLuna(res));
        if (prefs.getBoolean("pref_rd", true)) result.add(makeRainbowDash(res));
        if (prefs.getBoolean("pref_rarity", true)) result.add(makeRarity(res));
        if (prefs.getBoolean("pref_scootaloo", true)) result.add(makeScootaloo(res));
        if (prefs.getBoolean("pref_spike", true)) result.add(makeSpike(res));
        if (prefs.getBoolean("pref_ss", true)) result.add(makeSunsetShimmer(res));
        if (prefs.getBoolean("pref_sb", true)) result.add(makeSweetieBelle(res));
        if (prefs.getBoolean("pref_sd", true)) result.add(makeSweetieDrops(res));
        if (prefs.getBoolean("pref_ts", true)) result.add(makeTwilightSparkle(res));
        if (prefs.getBoolean("pref_vinyl", true)) result.add(makeVinylScratch(res));
        if (prefs.getBoolean("pref_custom", true)) loadCustomPonies(context, result);
        
        return result;
    }
    
    private static Pony makeDefaultPony(Resources res, int standId, int trotId) {
        PonyAction stand = new PonyAction(res, standId);
        PonyAction trot = new PonyAction(res, trotId);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justTrot = {trot};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        
        stand.setNextMoving(justTrot);
        trot.setNextMoving(justTrot);
        
        stand.setNextDrag(justTrot);
        trot.setNextDrag(justTrot);
        
        return new Pony(justTrot);
    }
    
    private static Pony makeAppleBloom(Resources res) {
        return makeDefaultPony(res, R.array.ab_stand, R.array.ab_trot);
    }
    
    private static Pony makeApplejack(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.aj_stand);
        PonyAction trot = new PonyAction(res, R.array.aj_trot);
        PonyAction drag = new PonyAction(res, R.array.aj_drag);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justTrot = {trot};
        PonyAction[] justDrag = {drag};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        drag.setNextWaiting(justStand);
        
        stand.setNextMoving(justTrot);
        trot.setNextMoving(justTrot);
        drag.setNextMoving(justTrot);
        
        stand.setNextDrag(justDrag);
        trot.setNextDrag(justDrag);
        drag.setNextDrag(justDrag);
        
        return new Pony(justTrot);
    }
    
    private static Pony makeBigMcIntosh(Resources res) {
        return makeDefaultPony(res, R.array.bigmac_stand, R.array.bigmac_trot);
    }
    
    private static Pony makeDerpyHooves(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.derpy_stand);
        PonyAction trot = new PonyAction(res, R.array.derpy_trot);
        PonyAction hover = new PonyAction(res, R.array.derpy_hover);
        PonyAction hoverud = new PonyAction(res, R.array.derpy_hoverud);
        PonyAction fly = new PonyAction(res, R.array.derpy_fly);
        PonyAction flyud = new PonyAction(res, R.array.derpy_flyud);
        PonyAction drag = new PonyAction(res, R.array.derpy_drag);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justFly = {fly};
        PonyAction[] justFlyud = {flyud};
        PonyAction[] justDrag = {drag};
        PonyAction[] waitStatesnorm = {stand, hover};
        PonyAction[] waitStatesud = {stand, hoverud};
        PonyAction[] waitStates = {stand, hover, hoverud};
        PonyAction[] moveStates = {trot, fly, flyud};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        hover.setNextWaiting(waitStatesnorm);
        hoverud.setNextWaiting(waitStatesud);
        fly.setNextWaiting(waitStatesnorm);
        flyud.setNextWaiting(waitStatesud);
        drag.setNextWaiting(waitStates);
        
        stand.setNextMoving(moveStates);
        trot.setNextMoving(moveStates);
        hover.setNextMoving(justFly);
        hoverud.setNextMoving(justFlyud);
        fly.setNextMoving(justFly);
        flyud.setNextMoving(justFlyud);
        drag.setNextMoving(moveStates);
        
        stand.setNextDrag(justDrag);
        trot.setNextDrag(justDrag);
        hover.setNextDrag(justDrag);
        hoverud.setNextDrag(justDrag);
        fly.setNextDrag(justDrag);
        flyud.setNextDrag(justDrag);
        drag.setNextDrag(justDrag);
        
        return new Pony(moveStates);
    }
    
    private static Pony makeDoctorHooves(Resources res) {
        return makeDefaultPony(res, R.array.doctor_stand, R.array.doctor_trot);
    }
    
    private static Pony makeFluttershy(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.fs_stand);
        PonyAction trot = new PonyAction(res, R.array.fs_trot);
        PonyAction fly = new PonyAction(res, R.array.fs_fly);
        PonyAction drag = new PonyAction(res, R.array.fs_drag);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justFly = {fly};
        PonyAction[] justDrag = {drag};
        PonyAction[] waitStates = {stand, stand, stand, fly};
        PonyAction[] moveStates = {trot, trot, trot, fly};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        fly.setNextWaiting(waitStates);
        drag.setNextWaiting(waitStates);
        
        stand.setNextMoving(moveStates);
        trot.setNextMoving(moveStates);
        fly.setNextMoving(justFly);
        drag.setNextMoving(moveStates);
        
        stand.setNextDrag(justDrag);
        trot.setNextDrag(justDrag);
        fly.setNextDrag(justDrag);
        drag.setNextDrag(justDrag);
        
        return new Pony(moveStates);
    }
    
    private static Pony makeLyraHeartstrings(Resources res) {
        PonyAction sit = new PonyAction(res, R.array.lyra_sit);
        PonyAction stand = new PonyAction(res, R.array.lyra_stand);
        PonyAction trot = new PonyAction(res, R.array.lyra_trot);
        
        PonyAction[] justTrot = {trot};
        PonyAction[] waitStates = {stand, stand, stand, sit};
        
        sit.setNextWaiting(waitStates);
        stand.setNextWaiting(waitStates);
        trot.setNextWaiting(waitStates);
        
        sit.setNextMoving(justTrot);
        stand.setNextMoving(justTrot);
        trot.setNextMoving(justTrot);
        
        sit.setNextDrag(justTrot);
        stand.setNextDrag(justTrot);
        trot.setNextDrag(justTrot);
        
        return new Pony(justTrot);
    }
    
    private static Pony makeOctavia(Resources res) {
        return makeDefaultPony(res, R.array.octavia_stand, R.array.octavia_trot);
    }
    
    private static Pony makePinkiePie(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.pp_stand);
        PonyAction trot = new PonyAction(res, R.array.pp_trot);
        PonyAction bounce = new PonyAction(res, R.array.pp_bounce);
        PonyAction drag = new PonyAction(res, R.array.pp_drag);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justDrag = {drag};
        PonyAction[] moveStates = {trot, bounce};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        bounce.setNextWaiting(justStand);
        drag.setNextWaiting(justStand);
        
        stand.setNextMoving(moveStates);
        trot.setNextMoving(moveStates);
        bounce.setNextMoving(moveStates);
        drag.setNextMoving(moveStates);
        
        stand.setNextDrag(justDrag);
        trot.setNextDrag(justDrag);
        bounce.setNextDrag(justDrag);
        drag.setNextDrag(justDrag);
        
        return new Pony(moveStates);
    }
    
    private static Pony makePrincessCelestia(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.celestia_stand);
        PonyAction walk = new PonyAction(res, R.array.celestia_walk);
        PonyAction fly = new PonyAction(res, R.array.celestia_fly);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justFly = {fly};
        PonyAction[] waitStates = {stand, fly};
        PonyAction[] moveStates = {walk, fly};
        
        stand.setNextWaiting(justStand);
        walk.setNextWaiting(justStand);
        fly.setNextWaiting(waitStates);
        
        stand.setNextMoving(moveStates);
        walk.setNextMoving(moveStates);
        fly.setNextMoving(justFly);
        
        stand.setNextDrag(justFly);
        walk.setNextDrag(justFly);
        fly.setNextDrag(justFly);
        
        return new Pony(moveStates);
    }
    
    private static Pony makePrincessLuna(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.luna_stand);
        PonyAction walk = new PonyAction(res, R.array.luna_walk);
        PonyAction fly = new PonyAction(res, R.array.luna_fly);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justFly = {fly};
        PonyAction[] waitStates = {stand, fly};
        PonyAction[] moveStates = {walk, fly};
        
        stand.setNextWaiting(justStand);
        walk.setNextWaiting(justStand);
        fly.setNextWaiting(waitStates);
        
        stand.setNextMoving(moveStates);
        walk.setNextMoving(moveStates);
        fly.setNextMoving(justFly);
        
        stand.setNextDrag(justFly);
        walk.setNextDrag(justFly);
        fly.setNextDrag(justFly);
        
        return new Pony(moveStates);
    }
    
    private static Pony makeRainbowDash(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.rd_stand);
        PonyAction trot = new PonyAction(res, R.array.rd_trot);
        PonyAction fly = new PonyAction(res, R.array.rd_fly);
        PonyAction drag = new PonyAction(res, R.array.rd_drag);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justFly = {fly};
        PonyAction[] justDrag = {drag};
        PonyAction[] waitStates = {stand, fly, fly, fly};
        PonyAction[] moveStates = {trot, fly, fly, fly};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        fly.setNextWaiting(waitStates);
        drag.setNextWaiting(waitStates);
        
        stand.setNextMoving(moveStates);
        trot.setNextMoving(moveStates);
        fly.setNextMoving(justFly);
        drag.setNextMoving(moveStates);
        
        stand.setNextDrag(justDrag);
        trot.setNextDrag(justDrag);
        fly.setNextDrag(justDrag);
        drag.setNextDrag(justDrag);
        
        return new Pony(moveStates);
    }
    
    private static Pony makeRarity(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.rarity_stand);
        PonyAction trot = new PonyAction(res, R.array.rarity_trot);
        PonyAction drag = new PonyAction(res, R.array.rarity_drag);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justTrot = {trot};
        PonyAction[] justDrag = {drag};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        drag.setNextWaiting(justStand);
        
        stand.setNextMoving(justTrot);
        trot.setNextMoving(justTrot);
        drag.setNextMoving(justTrot);
        
        stand.setNextDrag(justDrag);
        trot.setNextDrag(justDrag);
        drag.setNextDrag(justDrag);
        
        return new Pony(justTrot);
    }
    
    private static Pony makeScootaloo(Resources res) {
        return makeDefaultPony(res, R.array.scootaloo_stand, R.array.scootaloo_trot);
    }
    
    private static Pony makeSpike(Resources res) {
        return makeDefaultPony(res, R.array.spike_stand, R.array.spike_walk);
    }
    
    private static Pony makeSunsetShimmer(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.ss_stand);
        PonyAction trot = new PonyAction(res, R.array.ss_trot);
        PonyAction teleportOut = new PonyAction(res, R.array.ss_teleportout, PonyAction.PORT_O);
        PonyAction teleportIn = new PonyAction(res, R.array.ss_teleportin, PonyAction.PORT_I);
        
        PonyAction[] justStand = {stand};
        PonyAction[] justTrot = {trot};
        PonyAction[] moveStates = {trot, trot, trot, teleportOut};
        
        stand.setNextWaiting(justStand);
        trot.setNextWaiting(justStand);
        teleportOut.setNextWaiting(justStand);
        teleportIn.setNextWaiting(justStand);
        
        stand.setNextMoving(moveStates);
        trot.setNextMoving(moveStates);
        teleportOut.setNextMoving(new PonyAction[] {teleportIn});
        teleportIn.setNextMoving(moveStates);
        
        stand.setNextDrag(justTrot);
        trot.setNextDrag(justTrot);
        teleportOut.setNextDrag(justTrot);
        teleportIn.setNextDrag(justTrot);
        
        return new Pony(moveStates);
    }
    
    private static Pony makeSweetieBelle(Resources res) {
        return makeDefaultPony(res, R.array.sb_stand, R.array.sb_trot);
    }
    
    private static Pony makeSweetieDrops(Resources res) {
        return makeDefaultPony(res, R.array.sd_stand, R.array.sd_trot);
    }
    
    private static Pony makeTwilightSparkle(Resources res) {
        PonyAction standA = new PonyAction(res, R.array.pts_stand);
        PonyAction trotA = new PonyAction(res, R.array.pts_trot);
        PonyAction flyA = new PonyAction(res, R.array.pts_fly);
        PonyAction teleportOutA = new PonyAction(res, R.array.pts_teleportout, PonyAction.PORT_O);
        PonyAction teleportInA = new PonyAction(res, R.array.pts_teleportin, PonyAction.PORT_I);
        PonyAction standU = new PonyAction(res, R.array.ts_stand);
        PonyAction trotU = new PonyAction(res, R.array.ts_trot);
        PonyAction teleportOutU = new PonyAction(res, R.array.ts_teleportout, PonyAction.PORT_O);
        PonyAction teleportInU = new PonyAction(res, R.array.ts_teleportin, PonyAction.PORT_I);
        PonyAction dragU = new PonyAction(res, R.array.ts_drag);
        
        PonyAction[] justStandA = {standA};
        PonyAction[] justFlyA = {flyA};
        PonyAction[] waitStatesA = {standA, standA, standA, flyA};
        PonyAction[] moveStatesA = {trotA, trotA, flyA, teleportOutA};
        PonyAction[] justStandU = {standU};
        PonyAction[] justDragU = {dragU};
        PonyAction[] moveStatesU = {trotU, trotU, trotU, teleportOutU};
        
        standA.setNextWaiting(justStandA);
        trotA.setNextWaiting(justStandA);
        flyA.setNextWaiting(waitStatesA);
        teleportOutA.setNextWaiting(justStandA);
        teleportInA.setNextWaiting(justStandA);
        standU.setNextWaiting(justStandU);
        trotU.setNextWaiting(justStandU);
        teleportOutU.setNextWaiting(justStandU);
        teleportInU.setNextWaiting(justStandU);
        dragU.setNextWaiting(justStandU);
        
        standA.setNextMoving(moveStatesA);
        trotA.setNextMoving(moveStatesA);
        flyA.setNextMoving(justFlyA);
        teleportOutA.setNextMoving(new PonyAction[] {teleportInA});
        teleportInA.setNextMoving(moveStatesA);
        standU.setNextMoving(moveStatesU);
        trotU.setNextMoving(moveStatesU);
        teleportOutU.setNextMoving(new PonyAction[] {teleportInU});
        teleportInU.setNextMoving(moveStatesU);
        dragU.setNextMoving(moveStatesU);
        
        standA.setNextDrag(justFlyA);
        trotA.setNextDrag(justFlyA);
        flyA.setNextDrag(justFlyA);
        teleportOutA.setNextDrag(justFlyA);
        teleportInA.setNextDrag(justFlyA);
        standU.setNextDrag(justDragU);
        trotU.setNextDrag(justDragU);
        teleportOutU.setNextDrag(justDragU);
        teleportInU.setNextDrag(justDragU);
        dragU.setNextDrag(justDragU);
        
        return new Pony(new PonyAction[] {trotA, trotA, flyA, teleportOutA, trotU, trotU, trotU, teleportOutU});
    }
    
    private static Pony makeVinylScratch(Resources res) {
        PonyAction stand = new PonyAction(res, R.array.vinyl_stand);
        PonyAction trot = new PonyAction(res, R.array.vinyl_trot);
        PonyAction dance = new PonyAction(res, R.array.vinyl_dance);
        PonyAction moonwalk = new PonyAction(res, R.array.vinyl_moonwalk);
        
        PonyAction[] justTrot = {trot};
        PonyAction[] waitStates = {stand, dance};
        PonyAction[] moveStates = {trot, trot, trot, moonwalk};
        
        stand.setNextWaiting(waitStates);
        trot.setNextWaiting(waitStates);
        dance.setNextWaiting(waitStates);
        moonwalk.setNextWaiting(waitStates);
        
        stand.setNextMoving(moveStates);
        trot.setNextMoving(moveStates);
        dance.setNextMoving(moveStates);
        moonwalk.setNextMoving(moveStates);
        
        stand.setNextDrag(justTrot);
        trot.setNextDrag(justTrot);
        dance.setNextDrag(justTrot);
        moonwalk.setNextDrag(justTrot);
        
        return new Pony(moveStates);
    }
    
    private static PonyAction[] getActions(HashMap<String, PonyAction> actions, String[] actionNames) {
        PonyAction[] result = new PonyAction[actionNames.length];
        for (int j = 0; j < actionNames.length; j++) {
            result[j] = actions.get(actionNames[j]);
        }
        return result;
    }
    
    private static void loadCustomPonies(Context context, ArrayList<Pony> ponies) {
        File dir = context.getExternalFilesDir(null);
        try {
            new File(dir, "custom-ponies-go-here").createNewFile();
        } catch (Exception e) {
        }
        
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".xml");
            }
        });
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        for (int i = 0; i < files.length; i++) {
            try {
                DocumentBuilder docBuilder = dbf.newDocumentBuilder();
                Document document = docBuilder.parse(files[i]);
                PonyDefinition definition = new PonyDefinition(document);
                definition.validate();
                ponies.add(makeCustomPony(definition));
            } catch (Exception e) {
                android.util.Log.e("PonyPaper", e.toString());
                continue;
            }
        }
    }
    
    private static Pony makeCustomPony(PonyDefinition definition) {
        HashMap<String, PonyAction> actions = new HashMap<String, PonyAction>();
        
        final int actionCount = definition.actions.length;
        
        for (int i = 0; i < actionCount; i++) {
            actions.put(definition.actions[i].name, new PonyAction(definition.actions[i]));
        }
        
        for (int i = 0; i < actionCount; i++) {
            PonyDefinition.Action actionDef = definition.actions[i];
            PonyAction action = actions.get(actionDef.name);
            action.setNextWaiting(getActions(actions, actionDef.nextActions.get("waiting").split(",")));
            action.setNextMoving(getActions(actions, actionDef.nextActions.get("moving").split(",")));
            action.setNextDrag(getActions(actions, actionDef.nextActions.get("drag").split(",")));
        }
        
        return new Pony(getActions(actions, definition.startActions.split(",")));
    }
    
}
