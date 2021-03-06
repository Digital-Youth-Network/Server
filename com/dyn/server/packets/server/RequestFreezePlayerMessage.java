package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.FreezePlayerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestFreezePlayerMessage extends AbstractServerMessage<RequestFreezePlayerMessage> {

	private String username;
	private boolean freeze;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestFreezePlayerMessage() {
	}

	public RequestFreezePlayerMessage(String playerName, boolean frozen) {
		username = playerName;
		freeze = frozen;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			if (freeze) {
				ServerMod.frozenPlayers.add(username);
			} else {
				ServerMod.frozenPlayers.remove(username);
			}
			PacketDispatcher.sendTo(new FreezePlayerMessage(freeze),
					MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username));
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		username = buffer.readStringFromBuffer(buffer.readableBytes());
		freeze = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(username);
		buffer.writeBoolean(freeze);
	}
}
