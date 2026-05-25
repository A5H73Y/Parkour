package io.github.a5h73y.parkour.configuration.serializable;

import de.leonhard.storage.internal.serialize.SimplixSerializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

public abstract class Base64Serializable<T> implements SimplixSerializable<T> {

	@Override
	public T deserialize(Object input) throws ClassCastException {
		T result = null;
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getMimeDecoder().decode(input.toString()));
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
			return Base64.getMimeEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save.", e);
		}
	}
}
