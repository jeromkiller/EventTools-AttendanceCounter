package com.github.jeromkiller.AttendanceCounter;

import com.github.jeromkiller.AttendanceCounter.Panels.CaptureArea.CaptureAreaManagementPanel;
import com.github.jeromkiller.AttendanceCounter.Panels.AttendancePanel;
import com.github.jeromkiller.AttendanceCounter.Panels.SettingsPanel;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class AttendanceCounterPanel extends PluginPanel {

    private final CaptureAreaManagementPanel areaPanel;
    private final AttendancePanel setupPanel;
    private final SettingsPanel settingsPanel;

    private static final ImageIcon COG_ICON;

    static {
        BufferedImage cogIcon = ImageUtil.loadImageResource(AttendanceCounterPlugin.class, "config_edit_icon.png");
        COG_ICON = new ImageIcon(cogIcon);
    }

    public AttendanceCounterPanel(AttendanceCounterPlugin plugin)
    {
        super(false);

        final int borderWidth = PluginPanel.BORDER_OFFSET;
        setBorder(new EmptyBorder(0, borderWidth, borderWidth, borderWidth));
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        areaPanel = new CaptureAreaManagementPanel(plugin);
        tabPane.addTab("Areas", new JScrollPane(areaPanel));


        setupPanel = new AttendancePanel(plugin);
        tabPane.addTab("Attendance", new JScrollPane(setupPanel));

        settingsPanel = new SettingsPanel(plugin);
        tabPane.addTab("Settings", COG_ICON, settingsPanel, "Change Plugin Settings");
        tabPane.setTabComponentAt(2, new JLabel(COG_ICON));

        add(tabPane);
    }
}
