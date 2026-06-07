package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.texture.SimpleTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import java.awt.image.BufferedImage;

public class TextureImageBased extends SimpleTexture {

	BufferedImage image;

	public TextureImageBased(ResourceLocation par1ResourceLocation, BufferedImage image) {
		super(par1ResourceLocation);
		this.image = image;
	}

	@Override
	public void loadTexture(ResourceManager par1ResourceManager) {
		TextureUtil.uploadTextureImage(getGlTextureId(), image);
	}

	public void reUploadImage() {
		TextureUtil.uploadTextureImage(getGlTextureId(), image);
	}

}
