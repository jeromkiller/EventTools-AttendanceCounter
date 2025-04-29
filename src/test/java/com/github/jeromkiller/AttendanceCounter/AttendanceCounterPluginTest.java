package com.github.jeromkiller.AttendanceCounter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AttendanceCounterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AttendanceCounterPlugin.class);
		RuneLite.main(args);
	}
}