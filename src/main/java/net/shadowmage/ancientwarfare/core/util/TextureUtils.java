package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.Minecraft;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@OnlyIn(Dist.CLIENT)
public class TextureUtils {
	private TextureUtils() {}

	public static ResourceLocation getTextureLocation(String path) {
		String overridePath = AWCoreStatics.configPathForFiles + path;
		ResourceLocation locationOverride = ResourceLocation.fromNamespaceAndPath(net.shadowmage.ancientwarfare.core.net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID, overridePath);
		if (textureLoaded(locationOverride)) {
			return locationOverride;
		}
		ResourceLocation locationMain = ResourceLocation.fromNamespaceAndPath(net.shadowmage.ancientwarfare.core.net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID, path);
		if (textureLoaded(locationMain)) {
			return locationMain;
		}

		if (loadTexture(locationOverride, overridePath)) {
			return locationOverride;
		}

		if (loadTextureFromAssets(locationMain, path)) {
			return locationMain;
		}

		return net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation();
	}

	private static boolean textureLoaded(ResourceLocation loc) {
		//noinspection ConstantConditions - getTexture isn't marked as nullable but can return null
		return Minecraft.getInstance().getTextureManager().getTexture(loc) != null;
	}

	private static boolean loadTexture(ResourceLocation loc, String path) {
		File file = new File(path);
		return file.exists() && loadTexture(loc, file);
	}

	private static boolean loadTexture(ResourceLocation loc, File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			Minecraft.getInstance().getTextureManager().loadTexture(loc, new TextureImageBased(loc, image));

			return true;
		}
		catch (IOException e) {
			//noop
		}
		return false;
	}

	private static boolean loadTextureFromAssets(ResourceLocation loc, String path) {
		//noinspection ConstantConditions
		String fullPath = "assets/" + net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID + "/" + path;
		File source = ModList.get().getModFileById(net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID).getFile().getFilePath().toFile();
		if (source.isFile()) {
			try (FileSystem fs = FileSystems.newFileSystem(source.toPath(), null)) {
				InputStream inputstream = fs.provider().newInputStream(fs.getPath(fullPath));
				Minecraft.getInstance().getTextureManager().loadTexture(loc, new TextureImageBased(loc, ImageIO.read(inputstream)));
				return true;
			}
			catch (IOException e) {
				//noop
			}
		} else if (source.isDirectory()) {
			File file = source.toPath().resolve(fullPath).toFile();
			if (loadTexture(loc, file)) {
				return true;
			}
		}
		return false;
	}
}
