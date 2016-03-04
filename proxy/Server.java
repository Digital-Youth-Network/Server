package com.dyn.server.proxy;

import java.util.List;

import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.CheckDynUsernameMessage;
import com.dyn.server.packets.client.TeacherSettingsMessage;
//import com.forgeessentials.api.APIRegistry;
import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;

public class Server implements Proxy {

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Actions on render GUI for the server (logging)

	}

	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	/**
	 * Returns the current thread based on side during message handling,
	 * used for ensuring that the message is being handled by the main thread
	 */
	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.getServerForPlayer();
	}
	
	@Override
	public void init() {
		FMLCommonHandler.instance().bus().register(this);

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		if (getOpLevel(event.player.getGameProfile()) > 0) {
			PacketDispatcher.sendTo(new TeacherSettingsMessage(getServerUserlist(), true), (EntityPlayerMP) event.player);
		}

		PacketDispatcher.sendTo(new CheckDynUsernameMessage(), (EntityPlayerMP) event.player);

	}

	@SubscribeEvent
	public void changedDim(PlayerEvent.PlayerChangedDimensionEvent event) {
		//event.player.addChatMessage(new ChatComponentText("Player Warped from " + event.fromDim + " to Dimension" + event.toDim));
		/*PacketDispatcher.sendTo(
				new SyncWorldMessage(
						APIRegistry.namedWorldHandler.getWorldName(event.player.worldObj.provider.dimensionId)),
				(EntityPlayerMP) event.player);*/

	}

	@Override
	public String[] getServerUserlist() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	@Override
	public List<EntityPlayerMP> getServerUsers() {
		return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
	}
	
	@Override
	public int getOpLevel(GameProfile profile) {
		// does the configuration manager return null on the client side?
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		if (minecraftServer == null)
			return 0;
		if (!minecraftServer.getConfigurationManager().canSendCommands(profile))
			return 0;
		UserListOpsEntry entry = (UserListOpsEntry) minecraftServer.getConfigurationManager().getOppedPlayers().getEntry(profile);
		return entry != null ? entry.getPermissionLevel() : MinecraftServer.getServer().getOpPermissionLevel();
	}

}