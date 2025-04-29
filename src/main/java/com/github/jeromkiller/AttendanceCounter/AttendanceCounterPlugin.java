package com.github.jeromkiller.AttendanceCounter;

import com.github.jeromkiller.AttendanceCounter.Panels.CaptureArea.CaptureCreationOptions;
import com.github.jeromkiller.AttendanceCounter.Util.AttendanceCounterSettings;
import com.github.jeromkiller.AttendanceCounter.game.CaptureArea;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;
import javax.swing.*;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "E.T. - Attendance Counter"
)
public class AttendanceCounterPlugin extends Plugin
{
	private static final String PLUGIN_NAME = "Event Tools - Attendance Counter";

	@Inject
	private Client client;

	@Inject
	private AttendanceCounterSceneOverlay sceneOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Gson gson;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Getter
	@Inject
	private ColorPickerManager colorPickerManager;

	@Getter
	@Inject
	private AttendanceCounterSettings settings;

	@Getter
	private AttendanceCounterPanel panel;
    private NavigationButton navButton;

	@Getter
	private LinkedHashSet<String> playerNames = new LinkedHashSet<>();

	@Getter
	private String localPlayerName = null;

	@Getter
	private final List<CaptureArea> captureAreas = new ArrayList<>();

	@Getter
	private final CaptureCreationOptions captureCreationOptions = new CaptureCreationOptions();

	@Getter
	@Setter
	public boolean startCapturing = false;

	@Override
	protected void startUp()
	{
		overlayManager.add(sceneOverlay);
        panel = new AttendanceCounterPanel(this);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		navButton = NavigationButton.builder()
				.tooltip(PLUGIN_NAME)
				.priority(5)
				.panel(panel)
				.icon(icon)
				.build();
		clientToolbar.addNavigation(navButton);

		loadSettings();

		panel.getAreaPanel().rebuild();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(sceneOverlay);
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		checkPlayersInRange();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{

	}

	@Subscribe
	public void onProfileChanged(ProfileChanged profileChanged)
	{
		loadSettings();
		SwingUtilities.invokeLater(panel.getAreaPanel()::rebuild);
	}

	private void checkPlayersInRange()
	{
		List<? extends  Player> playersList = client.getPlayers();
		Player localPlayer = client.getLocalPlayer();
		if(localPlayer == null) {
			return;
		}

		for(CaptureArea area : captureAreas)
		{
			WorldPoint playerLoc = localPlayer.getWorldLocation();
			if(playerLoc == null) {
				return;
			}

			if(area.notWorthChecking(playerLoc)) {
				continue;
			}
			for(Player player : playersList)
			{
				if(player == localPlayer)
				{
					continue;
				}

				final String playerName = player.getName();

				if(area.playerInArea(player.getWorldLocation()))
				{
					if(startCapturing) {
						playerNames.add(playerName);
						updatePlayers();
					}
				}
			}
		}
	}

	public void resetPlayers() {
		playerNames.clear();
		updatePlayers();
	}
	
	public void updatePlayers() {
		final int playerCount = playerNames.size();
		panel.getSetupPanel().setPlayerNames(playerNames);
		panel.getSetupPanel().setPlayerCount(playerCount);
	}

	public void startCaptureAreaCreation()
	{
		captureCreationOptions.setCurrentlyCreating(true);
		panel.getAreaPanel().rebuild();
	}

	public void finishCaptureAreaCreation()
	{
		final int width = (captureCreationOptions.getEast() + captureCreationOptions.getWest() + 1);
		final int height = (captureCreationOptions.getNorth() + captureCreationOptions.getSouth() + 1);
		final int xOffset = -captureCreationOptions.getWest();
		final int yOffset = -captureCreationOptions.getSouth();

		Player localPlayer = client.getLocalPlayer();
		if(null == localPlayer)
			return;
		WorldPoint playerLocation = localPlayer.getWorldLocation();
		WorldPoint swTile = playerLocation.dx(xOffset).dy(yOffset);
		CaptureArea setupArea = new CaptureArea(swTile, width, height,
				captureCreationOptions.getColor(),
				captureCreationOptions.getLabel(),
				captureCreationOptions.isLabelVisible());
		captureCreationOptions.resetOptions();

		addCaptureArea(setupArea);
		panel.getAreaPanel().rebuild();
	}

	public void cancelCaptureAreaCreation()
	{
		captureCreationOptions.resetOptions();
		panel.getAreaPanel().rebuild();
	}

	public void addCaptureArea(final CaptureArea captureArea)
	{
		captureAreas.add(captureArea);
		updateCaptureAreas();
	}

	public void deleteCaptureArea(final CaptureArea captureArea)
	{
		captureAreas.remove(captureArea);
		panel.getAreaPanel().rebuild();
		updateCaptureAreas();
	}

	public void updateCaptureAreas()
	{
		settings.setCaptureAreas(captureAreas);
	}

	public void loadSettings()
	{
		List<CaptureArea> areas = settings.getCaptureAreas();
		captureAreas.clear();
		captureAreas.addAll(areas);
	}

	public void copyCaptureAreaToClip(CaptureArea area)
	{
		final ArrayList<CaptureArea> exportAreas = new ArrayList<>();
		exportAreas.add(area);
		final String json = gson.toJson(exportAreas);
		final StringSelection selection = new StringSelection(json);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public void copyVisibleCaptureAreasToClip()
	{
		ArrayList<CaptureArea> exportAreas = new ArrayList<>(captureAreas);
		exportAreas.removeIf(area -> !area.isAreaVisible());
		final String json = gson.toJson(exportAreas);
		final StringSelection selection = new StringSelection(json);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public void importCaptureAreaFromClip() {
		String clipboardText;
		try {
			clipboardText = Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.getData(DataFlavor.stringFlavor)
					.toString();
		} catch (IOException | UnsupportedFlavorException ex) {
			sendChatMessage("Unable to read system clipboard.");
			log.warn("error reading clipboard", ex);
			return;
		}

		if (clipboardText.isEmpty())
			return;

		ArrayList<CaptureArea> importAreas;
		try {
			importAreas = gson.fromJson(clipboardText, new TypeToken<ArrayList<CaptureArea>>(){}.getType());
		}
		catch (JsonSyntaxException e) {
			sendChatMessage("You do not have any capture areas saved to the clipboard");
			return;
		}

		if(importAreas.isEmpty()) {
			sendChatMessage("You do not have any capture areas saved to the clipboard");
			return;
		}

		importAreas.removeIf(captureAreas::contains);
		captureAreas.addAll(importAreas);
		sendChatMessage("Imported " + importAreas.size() + " area(s) from clipboard");

		panel.getAreaPanel().rebuild();
		updateCaptureAreas();
	}

	private void sendChatMessage(final String message)
	{
		chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(message)
				.build());
	}
}
