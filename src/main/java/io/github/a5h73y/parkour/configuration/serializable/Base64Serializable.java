package io.github.a5h73y.parkour.configuration.serializable;

import de.leonhard.storage.internal.serialize.SimplixSerializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public abstract class Base64Serializable<T> implements SimplixSerializable<T> {

	@Override
	public T deserialize(Object input) throws ClassCastException {
		T result = null;
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(input.toString()));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			result = (T) dataInput.readObject();
			dataInput.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String serialize(@NotNull T value) throws ClassCastException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(value);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save.", e);
		}
	}
}
