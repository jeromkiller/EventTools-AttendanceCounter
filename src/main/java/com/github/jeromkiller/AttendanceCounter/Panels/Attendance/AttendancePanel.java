package com.github.jeromkiller.AttendanceCounter.Panels.Attendance;

import com.github.jeromkiller.AttendanceCounter.AttendanceCounterPlugin;
import com.github.jeromkiller.AttendanceCounter.Panels.Widgets.BlinklessToggleButton;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class AttendancePanel extends JPanel {
    private final BlinklessToggleButton autoFillButton;
    private final TrackingFrame allPlayerFrame;
    private final TrackingFrame friendsFrame;
    private final TrackingFrame friendsChatFrame;
    private final TrackingFrame clanChatFrame;

    @Getter
    private final LinkedHashSet<String> CapturedPlayers = new LinkedHashSet<>();
    private final AttendanceCounterPlugin plugin;
    private final HashSet<String> FriendPlayers = new HashSet<>();
    private final HashSet<String> FriendsChatPlayers = new HashSet<>();
    private final HashSet<String> ClanChatPlayers = new HashSet<>();

    public AttendancePanel(AttendanceCounterPlugin plugin)
    {
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);
        constraints.gridy = 0;
        constraints.weightx = 1;

        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        final JLabel autoFillLabel = new JLabel("Count Attendance");
        contents.add(autoFillLabel, constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        autoFillButton = new BlinklessToggleButton("Automatically add players to the participant list");
        autoFillButton.addItemListener(this::changeAutoFill);
        contents.add(autoFillButton, constraints);
        constraints.gridy++;

        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.WEST;

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        allPlayerFrame = new TrackingFrame("Anyone:", null);
        contents.add(allPlayerFrame, constraints);
        constraints.gridy++;

        friendsFrame = new TrackingFrame("Friends:", FriendPlayers);
        contents.add(friendsFrame, constraints);
        constraints.gridy++;

        friendsChatFrame = new TrackingFrame("Friends Chat:", FriendsChatPlayers);
        contents.add(friendsChatFrame, constraints);
        constraints.gridy++;

        clanChatFrame = new TrackingFrame("Clan Chat:", ClanChatPlayers);
        contents.add(clanChatFrame, constraints);
        constraints.gridy++;

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {plugin.resetPlayers();});
        constraints.gridx = 1;
        contents.add(resetButton, constraints);

        add(contents, BorderLayout.NORTH);
        updatePanels();
    }

    private void changeAutoFill() {
        final boolean autofill = autoFillButton.isSelected();
        plugin.setStartCapturing(autofill);
    }

    public void setPlayerNames(LinkedHashSet<String> names)
    {
        CapturedPlayers.clear();
        CapturedPlayers.addAll(names);
        allPlayerFrame.updatePlayers(CapturedPlayers);
        friendsFrame.updatePlayers(CapturedPlayers);
        friendsChatFrame.updatePlayers(CapturedPlayers);
        clanChatFrame.updatePlayers(CapturedPlayers);
        clanChatFrame.setVisible(plugin.getSettings().getFilterCC());
    }

    public void addFriend(String name) {
        final String cleanedName = name.replace((char) 160, ' '); // replace non breaking spaces with regular spaces
        if (FriendPlayers.add(cleanedName)) {
            friendsFrame.updatePlayers(CapturedPlayers);
        }
    }

    public void addFCMember(String name) {
        // people can freely join and leave friends chats,
        // to make life easier for the hosts keeping track, players remain tracked after leaving the fc
        final String cleanedName = name.replace((char) 160, ' '); // replace non breaking spaces with regular spaces
        if (FriendsChatPlayers.add(cleanedName)) {
            friendsChatFrame.updatePlayers(CapturedPlayers);
        }
    }

    public void setClanChatMembers(List<String> names) {
        ClanChatPlayers.clear();
        for(String name : names) {
            final String cleanedName = name.replace((char) 160, ' '); // replace non breaking spaces with regular spaces
            ClanChatPlayers.add(cleanedName);
        }
        clanChatFrame.updatePlayers(CapturedPlayers);
    }

    public void updatePanels() {
        allPlayerFrame.setVisible(plugin.getSettings().getFilterAll());
        friendsFrame.setVisible(plugin.getSettings().getFilterFriends());
        friendsChatFrame.setVisible(plugin.getSettings().getFilterFC());
        clanChatFrame.setVisible(plugin.getSettings().getFilterCC());
    }

}
