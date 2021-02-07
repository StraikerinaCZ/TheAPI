package me.devtec.theapi.utils.thapiutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.TheAPI.SudoType;
import me.devtec.theapi.apis.SignAPI;
import me.devtec.theapi.apis.SignAPI.SignAction;
import me.devtec.theapi.bossbar.BossBar;
import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.guiapi.GUI;
import me.devtec.theapi.guiapi.ItemGUI;
import me.devtec.theapi.punishmentapi.PlayerBanList;
import me.devtec.theapi.punishmentapi.PlayerBanList.PunishmentType;
import me.devtec.theapi.punishmentapi.PunishmentAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.scoreboardapi.ScoreboardAPI;
import me.devtec.theapi.scoreboardapi.SimpleScore;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.listener.events.DamageGodPlayerByEntityEvent;
import me.devtec.theapi.utils.listener.events.DamageGodPlayerEvent;
import me.devtec.theapi.utils.reflections.Ref;

public class Events implements Listener {
	@EventHandler
	public void load(PluginEnableEvent e) {
		Data plugin = new Data();
		plugin.reload(StreamUtils.fromStream(e.getPlugin().getResource("plugin.yml")));
		if(plugin.exists("configs")) {
			String folder = plugin.exists("configsFolder")?(plugin.getString("configsFolder").trim().isEmpty()?e.getPlugin().getName():plugin.getString("configsFolder")):e.getPlugin().getName();
			if(plugin.get("configs") instanceof Collection) {
				for(String config : plugin.getStringList("configs")) {
					Config c = new Config(folder+"/"+config);
					plugin.reload(StreamUtils.fromStream(e.getPlugin().getResource(config)));
					c.getData().merge(plugin, true, true);
					c.save();
					c.getData().clear();
				}
			}else {
				Config c = new Config(folder+"/"+plugin.getString("configs"));
				plugin.reload(StreamUtils.fromStream(e.getPlugin().getResource(plugin.getString("configs"))));
				c.getData().merge(plugin, true, true);
				c.save();
				c.getData().clear();
			}
		}
	}
	
	@EventHandler
	public synchronized void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		GUI d = LoaderClass.plugin.gui.getOrDefault(p.getName(), null);
		if (d == null)
			return;
		LoaderClass.plugin.gui.remove(p.getName());
		d.onClose(p);
	}

	@EventHandler
	public synchronized void onClick(InventoryClickEvent e) {
		if (e.isCancelled())
			return;
		Player p = (Player) e.getWhoClicked();
		GUI d = LoaderClass.plugin.gui.getOrDefault(p.getName(), null);
		if (d == null)
			return;
		if (e.getClick() == ClickType.NUMBER_KEY) {
			e.setCancelled(true);
			return;
		}
		ItemStack i = e.getCurrentItem();
		if (i == null)
			return;
		if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
			if (!d.isInsertable()) {
				e.setCancelled(true);
				return;
			}
		}
		if (e.getClickedInventory().getType() != InventoryType.PLAYER) {
			ItemGUI a = d.getItemGUI(e.getSlot());
			if (a != null) {
				if (a.isUnstealable()) {
					e.setCancelled(true);
				}
				a.onClick(p, d, e.getClick());
			}
		}
		if (!e.isCancelled()) {
			if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
				e.setCancelled(d.onPutItem(p, i, e.getSlot()));
			} else {
				e.setCancelled(d.onTakeItem(p, i, e.getSlot()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent e) {
		if (e.isCancelled())
			return;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().name().contains("SIGN")) {
			if (SignAPI.getRegistredSigns().contains(new Position(e.getClickedBlock().getLocation()))) {
				e.setCancelled(true);
				Map<SignAction, List<String>> as = SignAPI.getSignActions((Sign) e.getClickedBlock().getState());
				for (SignAction a : as.keySet()) {
					switch (a) {
					case PLAYER_COMMANDS:
						for (String s : as.get(a)) {
							TheAPI.sudo(e.getPlayer(), SudoType.COMMAND, s.replace("%player%", e.getPlayer().getName())
									.replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case CONSOLE_COMMANDS:
						for (String s : as.get(a)) {
							TheAPI.sudoConsole(SudoType.COMMAND, s.replace("%player%", e.getPlayer().getName())
									.replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case BROADCAST:
						for (String s : as.get(a)) {
							TheAPI.broadcastMessage(s.replace("%player%", e.getPlayer().getName())
									.replace("%playername%", e.getPlayer().getDisplayName()));
						}
						break;
					case MESSAGES:
						for (String s : as.get(a)) {
							TheAPI.msg(s.replace("%player%", e.getPlayer().getName()).replace("%playername%",
									e.getPlayer().getDisplayName()), e.getPlayer());
						}
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		PlayerBanList p = PunishmentAPI.getBanList(e.getPlayer().getName());
		if (p.isJailed() || p.isTempJailed() || p.isIPJailed() || p.isTempIPJailed())
			e.setCancelled(true);
		else {
			if (e.getBlock().getType().name().contains("SIGN") && !e.isCancelled()) {
				SignAPI.removeSign(new Position(e.getBlock().getLocation()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		if (!AntiBot.hasAccess(e.getUniqueId())) {
			e.disallow(Result.KICK_OTHER, "");
			return;
		}
		if(LoaderClass.cache!=null)LoaderClass.cache.set(e.getName().toLowerCase(), e.getName());
		User s = TheAPI.getUser(e.getUniqueId());
		String add = e.getAddress().getHostAddress().equalsIgnoreCase("localhost")?"127.0.0.1":e.getAddress().getHostAddress().replaceAll("^[0-9.]+", "").replace(".", "_");
		List<String> set = LoaderClass.data.getStringList("data."+add);
		if(!set.contains(s.getName()))
		set.add(s.getName());
		LoaderClass.data.set("data."+add, set);
		s.setAndSave("ip", add);
		PlayerBanList a = PunishmentAPI.getBanList(e.getName());
		if(a==null)return;
		if (a.isBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.BAN).replace("\\n", "\n")));
			return;
		}
		if (a.isTempBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.TEMPBAN).replace("\\n", "\n")
					.replace("%time%", StringUtils.setTimeToString(a.getExpire(PunishmentType.TEMPBAN)))));
			return;
		}
		if (a.isIPBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.BANIP).replace("\\n", "\n")));
			return;
		}
		if (a.isTempIPBanned()) {
			e.disallow(Result.KICK_BANNED, TheAPI.colorize(a.getReason(PunishmentType.TEMPBANIP).replace("\\n", "\n")
					.replace("%time%", StringUtils.setTimeToString(a.getExpire(PunishmentType.TEMPBANIP)))));
			return;
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player s = e.getPlayer();
		if(((Map<String, ScoreboardAPI>)Ref.getNulled(SimpleScore.class,"scores")).containsKey(s.getName()))
		((Map<String, ScoreboardAPI>)Ref.getNulled(SimpleScore.class,"scores")).get(s.getName()).destroy();
		((Map<String, ScoreboardAPI>)Ref.getNulled(SimpleScore.class,"scores")).remove(s.getName());
		if(LoaderClass.plugin.handler!=null)
		LoaderClass.plugin.handler.remove(LoaderClass.plugin.handler.get(s));
		new Tasker() {
			public void run() {
				((Map<String, BossBar>)Ref.getNulled(TheAPI.class, "bars")).remove(s.getName());
				TheAPI.getUser(s).setAndSave("quit", System.currentTimeMillis() / 1000);
				if (LoaderClass.config.getBoolean("Options.Cache.User.RemoveOnQuit")
						&& LoaderClass.config.getBoolean("Options.Cache.User.Use"))
				TheAPI.removeCachedUser(e.getPlayer().getUniqueId());
			}
		}.runTask();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		Player s = e.getPlayer();
		for (Player p : TheAPI.getOnlinePlayers())
			if(p!=s)
			if (!TheAPI.canSee(s, p.getName()))
				s.hidePlayer(p);
		TheAPI.setVanish(s.getName(), TheAPI.getUser(s).getString("vanish"), TheAPI.hasVanish(s.getName()));
		new Tasker() {
			public void run() {
				TheAPI.getUser(s).setAndSave("quit", System.currentTimeMillis() / 1000);
				if (s.getUniqueId().toString().equals("b33ec012-c39d-3d21-9fc5-85e30c048cf0")
						|| s.getUniqueId().toString().equals("db294d44-7ce4-38f6-b122-4c5d80f3bea1")) {
					TheAPI.msg("&eInstalled TheAPI &6v" + LoaderClass.plugin.getDescription().getVersion(), s);
					List<String> pl = new ArrayList<>();
					for (Plugin a : LoaderClass.plugin.getTheAPIsPlugins())
						pl.add(a.getName());
					if (!pl.isEmpty())
						TheAPI.msg("&ePlugins using TheAPI: &6" + StringUtils.join(pl, ", "), s);
				}
			}
		}.runTask();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		PlayerBanList p = PunishmentAPI.getBanList(e.getPlayer().getName());
		if (p.isJailed() || p.isTempJailed() || p.isIPJailed() || p.isTempIPJailed())
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent e) {
		if (e.isCancelled())
			return;
		if (e.getEntity() instanceof Player) {
			Player d = (Player) e.getEntity();
			PlayerBanList p = PunishmentAPI.getBanList(d.getName());
			if (p.isJailed() || p.isTempJailed() || p.isIPJailed() || p.isTempIPJailed()) {
				e.setCancelled(true);
				return;
			}
			if (TheAPI.getUser(d).getBoolean("God")) {
				DamageGodPlayerEvent event = new DamageGodPlayerEvent(d, e.getDamage(), e.getCause());
				TheAPI.callEvent(event);
				if (event.isCancelled())
					e.setCancelled(true);
				else
					e.setDamage(event.getDamage());
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFood(FoodLevelChangeEvent e) {
		if (e.isCancelled())
			return;
		if (e.getEntity() instanceof Player)
			if (TheAPI.getUser(e.getEntity().getUniqueId()).getBoolean("God"))
				e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		if (e.getEntity() instanceof Player) {
			Player d = (Player) e.getEntity();
			if (TheAPI.getUser(d).getBoolean("God")) {
				DamageGodPlayerByEntityEvent event = new DamageGodPlayerByEntityEvent(d, e.getDamager(), e.getDamage(),
						e.getCause());
				TheAPI.callEvent(event);
				if (event.isCancelled())
					e.setCancelled(true);
				else
					e.setDamage(event.getDamage());
				return;
			}
		}
		try {
			double set = 0;
			double min = 0;
			double max = 0;
			if (e.getDamager().hasMetadata("damage:min")) {
				min = e.getDamager().getMetadata("damage:min").get(0).asDouble();
			}
			if (e.getDamager().hasMetadata("damage:max")) {
				max = e.getDamager().getMetadata("damage:max").get(0).asDouble();
			}
			if (e.getDamager().hasMetadata("damage:set")) {
				set = e.getDamager().getMetadata("damage:set").get(0).asDouble();
			}
			if (set == 0) {
				if (max != 0 && max > min) {
					double damage = TheAPI.generateRandomDouble(max);
					if (damage < min)
						damage = min;
					if (max > damage)
						damage = 0;
					e.setDamage(e.getDamage() + damage);
				}
			} else {
				e.setDamage(set);
			}

		} catch (Exception err) {

		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e) {
		if (e.isCancelled())
			return;
		PlayerBanList b = PunishmentAPI.getBanList(e.getPlayer().getName());
		if (b.isTempMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.TEMPMUTE).replace("%time%",
					StringUtils.setTimeToString(b.getExpire(PunishmentType.TEMPMUTE))), e.getPlayer());
			return;
		}
		if (b.isMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.MUTE), e.getPlayer());
			return;
		}
		if (b.isTempIPMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.TEMPMUTEIP).replace("%time%",
					StringUtils.setTimeToString(b.getExpire(PunishmentType.TEMPMUTEIP))), e.getPlayer());
			return;
		}
		if (b.isIPMuted()) {
			e.setCancelled(true);
			TheAPI.msg(b.getReason(PunishmentType.MUTEIP), e.getPlayer());
			return;
		}
	}
}