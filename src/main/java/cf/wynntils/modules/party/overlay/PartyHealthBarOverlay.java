package cf.wynntils.modules.party.overlay;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.AssetsTexture;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.modules.party.configs.PartyConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.Sys;

import java.util.*;

public class PartyHealthBarOverlay extends Overlay {
    public PartyHealthBarOverlay() {
        super("Party Health Bars", 120, 96, true, 1, 0, -125, 10, OverlayGrowFrom.TOP_LEFT);
    }

    private static HashSet partyHealthMap = new HashSet();
    private static TreeMap<String, ResourceLocation> partyList = new TreeMap<>();

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(27,3);

    @Setting(displayName = "Health Position", description = "The position offset of the health")
    public Pair<Integer,Integer> healthPositionOffset = new Pair<>(37,20);

    @Setting(displayName = "Colour", description = "The color of the names")
    public CustomColor nameColour = CommonColors.YELLOW;

    @Setting(displayName = "Colour", description = "The color of the names")
    public CustomColor healthColour = CommonColors.RED;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getCurrentHealth() != -1 && !Reference.onLobby))) return;

        partyList = PlayerInfo.getPlayerInfo().getPartyList();
        partyHealthMap.clear();

        if (PartyConfig.INSTANCE.partyOverlay && partyList != null) {
            for (Map.Entry<String, ResourceLocation> entry: partyList.entrySet()) {
                try {
                    if (partyHealthMap.size() < PartyConfig.INSTANCE.shownAmnt) {
                        EntityPlayer ep = ModCore.mc().world.getPlayerEntityByName(entry.getKey());
                        if (ep != null) {
                            partyHealthMap.add(ep);
                        }
                    }
                } catch (Exception e) {
                    //Player left vicinity, We could remove o from the list though after each teleport we'll have to wait a minute.
                }
            }
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
       if (partyHealthMap != null) {
           int i = 0;
           for (Object o: partyHealthMap) {
               drawDefaultBar(0, 24, 0, 72, i, (EntityPlayer) o);
               i++;
           }
       }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, int i, EntityPlayer pl) {
        drawProgressBar(Textures.Overlays.party_health_bar, 0, y1 + i*(y2-y1 +2), 120, y2 + i *(y2-y1 +2), ty1, ty2, 0.361f + 0.622f * (pl.getHealth()/pl.getMaxHealth()));
        drawString(pl.getName(), textPositionOffset.a, textPositionOffset.b + i*(y2-y1+2), nameColour, SmartFontRenderer.TextAlignment.LEFT_RIGHT, PartyConfig.INSTANCE.textShadow);
        scale(.7f);
        drawString(Math.min((int)((pl.getHealth()/pl.getMaxHealth())*100), 100)+ "%", healthPositionOffset.a, healthPositionOffset.b + i*((ty2-ty1)/2 +1), healthColour, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        resetScale();
        drawRect(new AssetsTexture(partyList.get(pl.getName())), 10, 100, 180, 180, 16, 16);
    }
}
