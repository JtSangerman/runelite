/*
 * Copyright (c) 2018, SomeoneWithAnInternetConnection
 * Copyright (c) 2018, oplosthee <https://github.com/oplosthee>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.metronome;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.SoundEffectVolume;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Metronome",
	description = "Play a sound on a specified tick to aid in efficient skilling and prayer flicking",
	tags = {"skilling", "tick", "timers", "prayer", "flick"},
	enabledByDefault = false
)
public class MetronomePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MetronomePluginConfiguration config;

	@Inject
	private MetronomeOverlay ovr;

	@Inject
	private OverlayManager overlayManager;

	private int tickCounter = 0;
	private boolean shouldTock = false;
	private boolean currentColorGreen = false;

	@Provides
	MetronomePluginConfiguration provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MetronomePluginConfiguration.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(ovr);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(ovr);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		currentColorGreen = !currentColorGreen;

		if (config.tickCount() == 0)
		{
			return;
		}

		if (++tickCounter % config.tickCount() == 0)
		{

			if (config.enableTock() && shouldTock)
			{
				client.playSoundEffect(SoundEffectID.GE_DECREMENT_PLOP, SoundEffectVolume.MEDIUM_HIGH);
			}
			else
			{
				client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, SoundEffectVolume.MEDIUM_HIGH);
			}
			shouldTock = !shouldTock;
		}
	}

	public int getTickCounter() {
		return tickCounter;
	}

	public boolean getIsCurrentColorGreen() {
		return currentColorGreen;
	}
}
