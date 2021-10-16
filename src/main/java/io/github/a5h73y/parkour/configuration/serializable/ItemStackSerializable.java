package io.github.a5h73y.parkour.configuration.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import de.leonhard.storage.internal.serialize.LightningSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemStackSerializable implements LightningSerializable<ItemStack[]> {

	@Override
	public ItemStack[] deserialize(Object input) throws ClassCastException {
		ItemStack[] result = null;
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(input.toString()));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			result = (ItemStack[]) dataInput.readObject();
			dataInput.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String serialize(ItemStack @NotNull [] itemStacks) throws ClassCastException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(itemStacks);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stack.", e);
		}
	}

	@Override
	public Class<ItemStack[]> getClazz() {
		return ItemStack[].class;
	}
}
