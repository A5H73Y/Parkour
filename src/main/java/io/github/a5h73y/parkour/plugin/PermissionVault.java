package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.PluginUtils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionVault extends PluginWrapper {

	public PermissionVault(Parkour parkour) {
		super(parkour);
	}

	private Permission permission;
	private static final String COURSE_PERMISSION = "parkour.course.";

	@Override
	protected void initialise() {
		super.initialise();

		if (isEnabled()) {
			RegisteredServiceProvider<Permission> rsp =
					getServer().getServicesManager().getRegistration(Permission.class);

			if (rsp == null) {
				PluginUtils.log("[Permission] Failed to connect to Vault's Permission service.", 2);
				setEnabled(false);
				return;
			}

			permission = rsp.getProvider();
		}
	}

	public Permission getPermissions() {
		return permission;
	}

	public boolean isPermissions() {
		return permission != null;
	}

	public boolean hasPaidOneTimeFee(Player player, String courseName) {
		return player.hasPermission(COURSE_PERMISSION + courseName);
	}

	public void setPaidOneTimeFee(Player player, String courseName) {
		permission.playerAdd(null, player, COURSE_PERMISSION + courseName);
	}

	@Override
	public String getPluginName() {
		return "Vault";
	}

	@Override
	public String getPluginDisplayName() {
		return "Vault (Permissions)";
	}
}
