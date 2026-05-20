package com.github.jeromkiller.AttendanceCounter.Panels;

import com.github.jeromkiller.AttendanceCounter.AttendanceCounterPlugin;
import com.github.jeromkiller.AttendanceCounter.Panels.Widgets.BlinklessToggleButton;
import com.github.jeromkiller.AttendanceCounter.Util.AttendanceCounterSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends BasePanel {
    private final BlinklessToggleButton showRenderDist;
    private final BlinklessToggleButton hideOverlay;
    private final BlinklessToggleButton filterAll;
    private final BlinklessToggleButton filterFriends;
    private final BlinklessToggleButton filterFriendsChat;
    private final BlinklessToggleButton filterClanChat;

    private final AttendanceCounterSettings settings;

    public SettingsPanel(AttendanceCounterPlugin plugin)
    {
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
        constraints.anchor = GridBagConstraints.WEST;


        JLabel filterLabel = new JLabel("Filters");
        filterLabel.setForeground(Color.WHITE);
        contents.add(filterLabel, constraints);
        constraints.gridy++;

        filterAll = new BlinklessToggleButton("Track any player near you");
        filterAll.setSelected(settings.getFilterAll());
        filterAll.addItemListener(() -> settings.setFilterAll(filterAll.isSelected()));
        addSettingRow("Track Anyone", filterAll, contents, constraints);

        filterFriends = new BlinklessToggleButton("Track players on your Friends List");
        filterFriends.setSelected(settings.getFilterFriends());
        filterFriends.addItemListener(() -> settings.setFilterFriends(filterFriends.isSelected()));
        addSettingRow("Track Friends", filterFriends, contents, constraints);

        filterFriendsChat = new BlinklessToggleButton("Track players in your Friends Chat");
        filterFriendsChat.setSelected(settings.getFilterFC());
        filterFriendsChat.addItemListener(() -> settings.setFilterFC(filterFriendsChat.isSelected()));
        addSettingRow("Track FC", filterFriendsChat, contents, constraints);

        filterClanChat = new BlinklessToggleButton("Track players in your Clan Chat");
        filterClanChat.setSelected(settings.getFilterCC());
        filterClanChat.addItemListener(() -> settings.setFilterCC(filterClanChat.isSelected()));
        addSettingRow("Track Clan", filterClanChat, contents, constraints);

        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        contents.add(new JLabel(" "), constraints);
        constraints.gridy++;
        JLabel filtersMisc = new JLabel("Misc");
        filtersMisc.setForeground(Color.WHITE);
        contents.add(filtersMisc, constraints);
        constraints.gridy++;

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
