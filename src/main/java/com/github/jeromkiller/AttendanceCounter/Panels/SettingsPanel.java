package com.github.jeromkiller.AttendanceCounter.Panels;

import com.github.jeromkiller.AttendanceCounter.AttendanceCounterPlugin;
import com.github.jeromkiller.AttendanceCounter.Panels.Widgets.BlinklessToggleButton;
import com.github.jeromkiller.AttendanceCounter.Util.AttendanceCounterSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends BasePanel {
    private final JSpinner tickLeniency = new JSpinner(new SpinnerNumberModel(2, 0, 100, 1));
    private final BlinklessToggleButton showRenderDist;
    private final BlinklessToggleButton hideOverlay;

    private final AttendanceCounterPlugin plugin;
    private final AttendanceCounterSettings settings;

    public SettingsPanel(AttendanceCounterPlugin plugin)
    {
        this.plugin = plugin;
        this.settings = plugin.getSettings();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;

        hideOverlay = new BlinklessToggleButton("Hide the in game overlay. \nCapture areas previously set to 'visible' are still enabled");
        hideOverlay.setSelected(settings.getHideOverlay());
        hideOverlay.addItemListener(() -> {
            settings.setHideOverlay(hideOverlay.isSelected());
            updateDisabledButtons();
        });
        addSettingRow("Hide Overlay", hideOverlay, contents, constraints);

        showRenderDist = new BlinklessToggleButton("Show Render Distance");
        showRenderDist.setSelected(settings.getShowRenderDist());
        showRenderDist.addItemListener(() -> settings.setShowRenderDist(showRenderDist.isSelected()));
        addSettingRow("Show Render Distance", showRenderDist, contents, constraints);

        add(contents, BorderLayout.NORTH);
        loadSettings();
    }

    private void addSettingRow(String text, JComponent component, JPanel container, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        container.add(new JLabel(text), constraints);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        container.add(component, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
    }

    public void loadSettings() {
        final boolean showRenderDistSetting = settings.getShowRenderDist();
        showRenderDist.setSelected(showRenderDistSetting);

        updateDisabledButtons();
    }

    public void updateDisabledButtons() {
        showRenderDist.setEnabled(!settings.getHideOverlay());
    }
}
