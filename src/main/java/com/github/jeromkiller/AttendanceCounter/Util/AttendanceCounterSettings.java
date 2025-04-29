package com.github.jeromkiller.AttendanceCounter.Util;

import com.github.jeromkiller.AttendanceCounter.game.CaptureArea;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import joptsimple.internal.Strings;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.util.*;

public class AttendanceCounterSettings {

    public static final String CONFIG_GROUP = "AttendanceCounter";
    public static final String CAPTURE_AREA_KEY = "captureAreas";
    public static final String SHOW_RENDER_DIST = "AC_ShowRenderDist";
    public static final String HIDE_OVERLAY = "AC_HideOverlay";

    @Inject
    private ConfigManager configManager;
    @Inject
    private Gson gson;

    private void setValue(String key, Object value)
    {
        boolean isEmpty = false;
        if(value instanceof Collection)
        {
            isEmpty = ((Collection<?>) value).isEmpty();
        }
        else if (value instanceof Map)
        {
            isEmpty = ((Map<?, ?>) value).isEmpty();
        }

        if(isEmpty)
        {
            configManager.unsetConfiguration(CONFIG_GROUP, key);
            return;
        }

        final String json = gson.toJson(value);
        configManager.setConfiguration(CONFIG_GROUP, key, json);
    }

    public List<CaptureArea> getCaptureAreas() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, CAPTURE_AREA_KEY);
        if(Strings.isNullOrEmpty(json)){
            return new ArrayList<>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<CaptureArea>>(){}.getType());
    }

    public void setCaptureAreas(List<CaptureArea> captureAreas) {
        setValue(CAPTURE_AREA_KEY, captureAreas);
    }

    public boolean getShowRenderDist() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, SHOW_RENDER_DIST);
        if(Strings.isNullOrEmpty(json)){
            return false;
        }
        return gson.fromJson(json, new TypeToken<Boolean>(){}.getType());
    }

    public void setShowRenderDist(boolean show) {
        setValue(SHOW_RENDER_DIST, show);
    }

    public boolean getHideOverlay() {
        final String json = configManager.getConfiguration(CONFIG_GROUP, HIDE_OVERLAY);
        if(Strings.isNullOrEmpty(json)) {
            return false;
        }
        return gson.fromJson(json, new TypeToken<Boolean>(){}.getType());
    }

    public void setHideOverlay(boolean hide) {
        setValue(HIDE_OVERLAY, hide);
    }

}
