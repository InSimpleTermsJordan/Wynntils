package cf.wynntils.modules.party.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.rendering.textures.AssetsTexture;
import cf.wynntils.modules.capes.managers.ImageDownloader;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.party.configs.PartyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.HashSet;
import java.util.TreeMap;

public class ClientEvents implements Listener {

    private static long tickcounter = 0;

    @SubscribeEvent
    public void partyUpdate(TickEvent.ClientTickEvent e) {
        if (Reference.onWorld && e.phase == TickEvent.Phase.END) {
            if (PartyConfig.INSTANCE.partyOverlay && tickcounter % PartyConfig.INSTANCE.updateRate == 0) {

            }

            if ((PartyConfig.INSTANCE.characterBar || PartyConfig.INSTANCE.partyOverlay) && tickcounter % (PartyConfig.INSTANCE.partyListUpdateRate * 20) == 0) { //Update party list every minute, since Wynncraft doesn't update often.
                TreeMap<String, ResourceLocation> partyList = new TreeMap<>();

                ModCore.mc().getConnection().getPlayerInfoMap().forEach(networkPlayerInfo -> {
                    String playerName = ModCore.mc().ingameGUI.getTabList().getPlayerName(networkPlayerInfo);
                    if (!playerName.equals("")) {
                        if (playerName.matches("§(c|e)[A-Za-z0-9_ ]+§r") && !PlayerInfo.getPlayerInfo().getName().equals(Utils.stripColor(playerName))) {
                            try {
                                ResourceLocation rl = new ResourceLocation("wynntils:helm/" + playerName);
                                TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
                                ImageDownloader id = new ImageDownloader(null, "https://minotar.net/halm/" + Utils.stripColor(playerName) + "/16.png", null);
                                textureManager.loadTexture(rl, id);
                                partyList.put(Utils.stripColor(playerName), rl);
                            } catch (Exception ex) {ex.printStackTrace();}
                        }
                    }
                });

                PlayerInfo.getPlayerInfo().setPartyList(partyList);
            }
            tickcounter++;
        }
    }
}
