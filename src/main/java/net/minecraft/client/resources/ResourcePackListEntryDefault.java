package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackListEntryDefault extends ResourcePackListEntry {
    public static Logger logger = LogManager.getLogger();
    public IResourcePack pack = this.client.getResourcePackRepository().rprDefaultResourcePack;
    public ResourceLocation identifier;

    public ResourcePackListEntryDefault(GuiScreenResourcePacks guiScreenResourcePacks) throws IOException {
        super(guiScreenResourcePacks);
        DynamicTexture dynamicTexture = new DynamicTexture(this.pack.getPackImage());
        this.identifier = this.client.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamicTexture);
    }

    @Override
    public String createStringBuilder() {
        try {
            PackMetadataSection packMetadata = (PackMetadataSection)this.pack.getPackMetadata(this.client.getResourcePackRepository().rprMetadataSerializer, "pack");
            if (packMetadata != null) {
                return packMetadata.getDescription().getFormattedText();
            }
        } catch (JsonParseException e) {
            logger.error("Couldn't load metadata info", e);
        } catch (IOException e) {
            logger.error("Couldn't load metadata info", e);
        }

        return EnumChatFormatting.RED + "Missing pack.mcmeta :(";
    }

    @Override
    public boolean hasResourcePackEntry() {
        return false;
    }

    @Override
    public boolean hasResourcePackEntry2() {
        return false;
    }

    @Override
    public boolean canSortUp() {
        return false;
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public String getResourcePackName() {
        return "Default";
    }

    @Override
    public void run() {
        this.client.getTextureManager().bindTexture(this.identifier);
    }

    @Override
    public boolean alwaysTrue() {
        return false;
    }
}
