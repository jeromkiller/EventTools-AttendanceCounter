package com.github.jeromkiller.AttendanceCounter.Panels;

import com.github.jeromkiller.AttendanceCounter.AttendanceCounterPlugin;
import com.github.jeromkiller.AttendanceCounter.Panels.Widgets.BlinklessToggleButton;
import lombok.Getter;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;

public class AttendancePanel extends JPanel {
    private final BlinklessToggleButton autoFillButton;
    private final JTextArea playerNames = new JTextArea();
    private final JLabel numPlayers;

    @Getter
    private final LinkedHashSet<String> playerNameList = new LinkedHashSet<>();

    private final AttendanceCounterPlugin plugin;

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

        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        contents.add(new JLabel("Participants:"), constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        numPlayers = new JLabel("0");
        contents.add(numPlayers, constraints);
        constraints.gridy++;

        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.WEST;

        final JLabel playerNameLabel = new JLabel("Participant Names:");
        contents.add(playerNameLabel, constraints);
        constraints.gridy++;

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        playerNames.setRows(10);
        Border border = BorderFactory.createLineBorder(ColorScheme.BORDER_COLOR );
        playerNames.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        contents.add(playerNames, constraints);
        playerNames.setEditable(false);
        constraints.gridy++;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {plugin.resetPlayers();});
        constraints.gridx = 1;
        contents.add(resetButton, constraints);

        add(contents, BorderLayout.NORTH);
    }

    private void changeAutoFill() {
        final boolean autofill = autoFillButton.isSelected();
        plugin.setStartCapturing(autofill);
    }

    public void setPlayerNames(LinkedHashSet<String> names)
    {
        playerNameList.clear();
        playerNameList.addAll(names);
        final String playerNameString = String.join(System.lineSeparator(), playerNameList);
        playerNames.setText(playerNameString);
    }

    public void setPlayerCount(int count){
        numPlayers.setText(String.valueOf(count));
    }

}
