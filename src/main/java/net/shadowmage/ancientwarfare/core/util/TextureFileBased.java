package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.texture.SimpleTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureFileBased extends SimpleTexture {

	private File file;

	public TextureFileBased(ResourceLocation par1ResourceLocation, File file) {
		super(par1ResourceLocation);
		this.file = file;
	}

	@Override
	public void loadTexture(ResourceManager par1ResourceManager) {
		BufferedImage image;
		try {
			image = ImageIO.read(file);
			TextureUtil.uploadTextureImage(getGlTextureId(), image);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
