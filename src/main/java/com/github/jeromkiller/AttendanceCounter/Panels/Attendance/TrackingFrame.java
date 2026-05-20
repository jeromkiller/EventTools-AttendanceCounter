package com.github.jeromkiller.AttendanceCounter.Panels.Attendance;

import com.github.jeromkiller.AttendanceCounter.Panels.BasePanel;
import net.runelite.client.ui.ColorScheme;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class TrackingFrame extends BasePanel {
    private final String title;
    private final HashSet<String> filter;
    private final JLabel numPlayers;
    private final JTextArea playerNames = new JTextArea();

    public TrackingFrame(String title, @Nullable HashSet<String> filter) {
        this.title = title;
        this.filter = filter;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 2, 5, 2);
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;

        contents.add(new JLabel(this.title));
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        numPlayers = new JLabel("0");
        contents.add(numPlayers, constraints);
        constraints.gridy++;

        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.WEST;

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

        add(contents, BorderLayout.NORTH);
    }

    public void updatePlayers(LinkedHashSet<String> names) {
        LinkedHashSet<String> filteredNames = new LinkedHashSet<>(names);
        if (filter != null) {
            filteredNames.retainAll(filter);
        }
        final String nameString = String.join(System.lineSeparator(), filteredNames);
        playerNames.setText(nameString);
        numPlayers.setText(String.valueOf(filteredNames.size()));
    }
}
