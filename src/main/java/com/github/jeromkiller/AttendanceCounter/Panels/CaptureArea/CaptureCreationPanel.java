package com.github.jeromkiller.AttendanceCounter.Panels.CaptureArea;

import com.github.jeromkiller.AttendanceCounter.AttendanceCounterPlugin;
import com.github.jeromkiller.AttendanceCounter.Panels.BasePanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CaptureCreationPanel extends BasePanel {
    protected static final ImageIcon CREATE_ICON;
    protected static final ImageIcon CREATE_HOVER_ICON;
    protected static final ImageIcon CANCEL_ICON;
    protected static final ImageIcon CANCEL_HOVER_ICON;
    protected static final ImageIcon COMPASS_ICON;

    private final JLabel colorIndicator = new JLabel();
    private final JLabel labelIndicator = new JLabel();
    private final FlatTextField nameInput = new FlatTextField();
    private final JSpinner northSpinner = new JSpinner();
    private final JSpinner eastSpinner = new JSpinner();
    private final JSpinner southSpinner = new JSpinner();
    private final JSpinner westSpinner = new JSpinner();
    private final JLabel saveArea = new JLabel();
    private final JLabel cancelArea = new JLabel();
    private final JLabel warnLabel = new JLabel("⚠ Area can be skipped over");

    private final CaptureCreationOptions captureOptions;
    private final AttendanceCounterPlugin plugin;
    private boolean showLabel;

    static
    {
        final BufferedImage createImg = ImageUtil.loadImageResource(AttendanceCounterPlugin.class, "confirm_icon.png");
        final BufferedImage createImgHover = ImageUtil.alphaOffset(createImg, 0.5f);
        CREATE_ICON = new ImageIcon(createImg);
        CREATE_HOVER_ICON = new ImageIcon(createImgHover);

        final BufferedImage cancelImg = ImageUtil.loadImageResource(AttendanceCounterPlugin.class, "cancel_icon.png");
        final BufferedImage cancelImgHover = ImageUtil.alphaOffset(cancelImg, 0.5f);
        CANCEL_ICON = new ImageIcon(cancelImg);
        CANCEL_HOVER_ICON = new ImageIcon(cancelImgHover);

        final BufferedImage compassImg = ImageUtil.loadImageResource(AttendanceCounterPlugin.class, "compass.png");
        COMPASS_ICON = new ImageIcon(compassImg);
    }

    public CaptureCreationPanel(AttendanceCounterPlugin plugin)
    {
        this.plugin = plugin;
        this.captureOptions = plugin.getCaptureCreationOptions();
        this.showLabel = captureOptions.isLabelVisible();

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);

        JPanel nameActions = new JPanel(new BorderLayout(3,0));
        nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        nameInput.setText(captureOptions.getLabel());
        nameInput.setBorder(null);
        nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameInput.setPreferredSize(new Dimension(0, 24));
        nameInput.getTextField().setForeground(Color.WHITE);
        nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));

        nameWrapper.add(nameInput, BorderLayout.CENTER);
        nameWrapper.add(nameActions, BorderLayout.EAST);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
        centerContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        centerContainer.setLayout(new GridBagLayout());
        GridBagConstraints centerConstraints = new GridBagConstraints();

        final int MAX_VALUE = CaptureCreationOptions.MAX_AREA_SIZE;
        final int MIN_VALUE = 0;
        northSpinner.setModel(new SpinnerNumberModel(captureOptions.getNorth(), MIN_VALUE, MAX_VALUE, 1));
        northSpinner.addChangeListener(e -> {
            final int val = (int) northSpinner.getValue();
            captureOptions.setNorth(val);
            validateArea();
        }
        );
        southSpinner.setModel(new SpinnerNumberModel(captureOptions.getSouth(), MIN_VALUE, MAX_VALUE, 1));
        southSpinner.addChangeListener(e -> {
                final int val = (int) southSpinner.getValue();
                captureOptions.setSouth(val);
                validateArea();
        });
        eastSpinner.setModel(new SpinnerNumberModel(captureOptions.getEast(), MIN_VALUE, MAX_VALUE, 1));
        eastSpinner.addChangeListener(e -> {
                final int val = (int) eastSpinner.getValue();
                captureOptions.setEast(val);
                validateArea();
        });
        westSpinner.setModel(new SpinnerNumberModel(captureOptions.getWest(), MIN_VALUE, MAX_VALUE, 1));
        westSpinner.addChangeListener(e -> {
                final int val = (int) westSpinner.getValue();
                captureOptions.setWest(val);
                validateArea();
        });
        JLabel compass = new JLabel();
        compass.setIcon(COMPASS_ICON);
        centerConstraints.gridx = 1;
        centerConstraints.gridy = 0;
        centerContainer.add(northSpinner, centerConstraints);
        centerConstraints.gridx = 0;
        centerConstraints.gridy = 1;
        centerContainer.add(westSpinner, centerConstraints);
        centerConstraints.gridx = 1;
        centerContainer.add(compass, centerConstraints);
        centerConstraints.gridx = 2;
        centerContainer.add(eastSpinner, centerConstraints);
        centerConstraints.gridx = 1;
        centerConstraints.gridy = 2;
        centerContainer.add(southSpinner, centerConstraints);
        centerConstraints.gridy = 3;
        centerConstraints.gridx = 0;
        centerConstraints.gridwidth = 3;
        warnLabel.setVisible(false);
        centerContainer.add(warnLabel, centerConstraints);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
        bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        setupImageIcon(colorIndicator, "Edit area color", COLOR_ICON, COLOR_HOVER_ICON, this::openBorderColorPicker);


        labelIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                toggleLabelling(!showLabel);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                labelIndicator.setIcon(showLabel ? LABEL_HOVER_ICON : NO_LABEL_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                labelIndicator.setIcon(showLabel ? LABEL_ICON : NO_LABEL_ICON);
            }
        });

        leftActions.add(colorIndicator);
        leftActions.add(labelIndicator);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setupImageIcon(saveArea, "Save Area", CREATE_ICON, CREATE_HOVER_ICON, () -> {
            captureOptions.setLabel(nameInput.getText());
            plugin.finishCaptureAreaCreation();
        });

        setupImageIcon(cancelArea, "Cancel Area creation", CANCEL_ICON, CANCEL_HOVER_ICON, plugin::cancelCaptureAreaCreation);

        rightActions.add(saveArea);
        rightActions.add(cancelArea);

        bottomContainer.add(leftActions, BorderLayout.WEST);
        bottomContainer.add(rightActions, BorderLayout.EAST);
        bottomContainer.setPreferredSize(new Dimension(0, 37));

        add(nameWrapper, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);

        updateBorder();
        updateLabelling();
    }

    private void toggleLabelling(boolean on)
    {
        showLabel = on;
        captureOptions.setLabelVisible(on);
        plugin.updateCaptureAreas();
        updateLabelling();
    }

    private void updateLabelling()
    {
        labelIndicator.setIcon(showLabel ? LABEL_ICON : NO_LABEL_ICON);
        labelIndicator.setToolTipText(showLabel ? "Hide label" : "Show label");
    }

    private void updateBorder()
    {
        Color color = captureOptions.getColor();
        colorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, ColorUtil.colorWithAlpha(color, MAX_ALPHA)));

        colorIndicator.setIcon(COLOR_ICON);
    }

    private void openBorderColorPicker()
    {
        final Color color = captureOptions.getColor();
        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                color,
                captureOptions.getLabel() + " Border",
                false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnColorChange(c ->
        {
            captureOptions.setColor(c);
            updateBorder();
        });
        colorPicker.setOnClose(c -> plugin.updateCaptureAreas());
        colorPicker.setVisible(true);
    }

    private void validateArea()
    {
        final int width = captureOptions.getWest() + captureOptions.getEast() + 1;
        final int height = captureOptions.getNorth() + captureOptions.getSouth() + 1;
        final boolean tooSmall = (width == 1 || height == 1);
        warnLabel.setVisible(tooSmall);
    }
}
