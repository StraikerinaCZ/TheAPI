package me.Straiker123;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import me.Straiker123.BlocksAPI.Shape;
import me.Straiker123.Utils.Error;
import me.Straiker123.Utils.Packets;

@SuppressWarnings("deprecation")
public class PlayerAPI {
	private Player s;
	private String name;
	private UUID uuid;
	public PlayerAPI(Player a) {
		s=a;
		name=a.getName();
		uuid=a.getUniqueId();
	}
	
	public Player getPlayer() {
		return s;
	}

	public boolean isOnline() {
		return TheAPI.getPlayer(name) != null && TheAPI.getPlayer(name).getName().equals(name);
	}
	
	public UUID getUUID() {
		return uuid;
	}
	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void addPotionEffect(PotionEffect e) {
		if(s == null) {
			Error.err("when adding PotionEffect", "Player is null");
			return;
		}
		try {
		s.addPotionEffect(e);
		}catch(Exception re) {
			Error.err("when adding PotionEffect to player "+getName(), "PotionEffect is null");
		}
	}
	public void addPotionEffect(PotionEffectType type, int duration) {
		try {
		addPotionEffect(new PotionEffect(type, duration, 1, false));
		}catch(Exception re) {
			Error.err("when creating PotionEffect", "PotionEffectType is null");
		}
	}
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		try {
		addPotionEffect(new PotionEffect(type, duration, amplifier, false));
	}catch(Exception re) {
		Error.err("when creating PotionEffect", "PotionEffectType is null");
	}
	}
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier, boolean hideParticles) {
		try {
		addPotionEffect(new PotionEffect(type, duration, amplifier, hideParticles));
	}catch(Exception re) {
		Error.err("when creating PotionEffect", "PotionEffectType is null");
	}
	}

	public void teleport(Location loc) {
		teleport(loc,TeleportCause.PLUGIN);
	}
	public void teleport(Entity entity) {
		teleport(entity,TeleportCause.PLUGIN);
	}
	public void teleport(Location loc,TeleportCause cause) {
		if(loc != null && loc.getWorld()!=null) {
			if(cause!=null)
		s.teleport(loc,cause);
			else {
				Error.err("teleporting "+s.getName(), "TeleportCause is null");
			}
		}else {
			Error.err("teleporting "+s.getName(), "Location is null");
		}
	}
	public void teleport(Entity entity,TeleportCause cause) {
		if(entity!=null) {
			teleport(entity.getLocation(),cause);
		}else {
			Error.err("teleporting "+s.getName(), "Entity is null");
		}
	}

	public void safeTeleport(Location loc) {
		safeTeleport(loc,TeleportCause.PLUGIN);
	}
	public void safeTeleport(Location b, TeleportCause cause) {
		if(b==null || b!=null && b.getWorld()==null) {
			Error.err("teleporting "+s.getName(), "Location is null");
			return;
		}
		String c = new Location(b.getWorld(),b.getX(),b.getY()-2,b.getZ()).getBlock().getType().name();
		String c1 = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ()).getBlock().getType().name();
		String c2 = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
		if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
		teleport(new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ(),b.getYaw(),b.getPitch()),cause);
	}else {
		 c = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ()).getBlock().getType().name();
		 c1 = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
		 c2 = new Location(b.getWorld(),b.getX(),b.getY()+1,b.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
		teleport(new Location(b.getWorld(),b.getX(),b.getY(),b.getZ(),b.getYaw(),b.getPitch()),cause);
	}else {
		 c = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
		 c1 = new Location(b.getWorld(),b.getX(),b.getY()+1,b.getZ()).getBlock().getType().name();
		 c2 = new Location(b.getWorld(),b.getX(),b.getY()+2,b.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
		teleport(new Location(b.getWorld(),b.getX(),b.getY()+1,b.getZ(),b.getYaw(),b.getPitch()),cause);
	}else {
		Location l = simpleLocation(new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()));
		if(l==null)l=searchLocation(new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()));
		if(l==null)l=new Location(b.getWorld(),b.getX(),b.getY(),b.getZ());
		 c = new Location(l.getWorld(),l.getX(),l.getY()-2,l.getZ()).getBlock().getType().name();
		 c1 = new Location(l.getWorld(),l.getX(),l.getY()-1,l.getZ()).getBlock().getType().name();
		 c2 = new Location(l.getWorld(),l.getX(),l.getY(),l.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
		teleport(new Location(l.getWorld(),l.getX(),l.getY()-1,l.getZ(),b.getYaw(),b.getPitch()),cause);
	}else {
		 c = new Location(l.getWorld(),l.getX(),l.getY()-1,l.getZ()).getBlock().getType().name();
		 c1 = new Location(l.getWorld(),l.getX(),l.getY(),l.getZ()).getBlock().getType().name();
		 c2 = new Location(l.getWorld(),l.getX(),l.getY()+1,l.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
		teleport(new Location(l.getWorld(),l.getX(),l.getY()-1,l.getZ(),b.getYaw(),b.getPitch()),cause);
	}else {
		 c = new Location(l.getWorld(),l.getX(),l.getY(),l.getZ()).getBlock().getType().name();
		 c1 = new Location(l.getWorld(),l.getX(),l.getY()+1,l.getZ()).getBlock().getType().name();
		 c2 = new Location(l.getWorld(),l.getX(),l.getY()+2,l.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
		teleport(new Location(l.getWorld(),l.getX(),l.getY(),l.getZ(),b.getYaw(),b.getPitch()),cause);
	}else
				teleport(new Location(l.getWorld(),l.getX(),l.getY(),l.getZ(),b.getYaw(),b.getPitch()),cause);

	}
	}
	}}
	}}
	private Location simpleLocation(Location loc) {
		Location l = null;
		for(Location b : TheAPI.getBlocksAPI().getBlocksLocation(Shape.Square, loc, 2)) {
			String c = new Location(b.getWorld(),b.getX(),b.getY()-2,b.getZ()).getBlock().getType().name();
			String c1 = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ()).getBlock().getType().name();
			String c2 = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
			l=b;
			break;
		}}
		return l != null ? l.add(0.5, 0.5, 0.5):null;
	}
	
	private Location searchLocation(Location d) {
		Location loc = new Location(d.getWorld(),d.getX(),d.getY(),d.getZ());
		Location l = null;
		for(Location b : TheAPI.getBlocksAPI().getBlocksLocation(Shape.Square, loc, 4)) {
			String c = new Location(b.getWorld(),b.getX(),b.getY()-2,b.getZ()).getBlock().getType().name();
			String c1 = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ()).getBlock().getType().name();
			String c2 = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
					l=b;
					break;
			}
		}
		if(l!=null)return l.add(0.5, 0.5, 0.5);
		for(Location b :TheAPI.getBlocksAPI().getBlocksLocation(Shape.Sphere, loc, 6)) {
			String c = new Location(b.getWorld(),b.getX(),b.getY()-2,b.getZ()).getBlock().getType().name();
			String c1 = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ()).getBlock().getType().name();
			String c2 = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
					l=b;
					break;
			}
		}
		if(l!=null)return l.add(0.5, 0.5, 0.5);
		for(Location b : TheAPI.getBlocksAPI().getBlocksLocation(Shape.Sphere, loc, 8)) {
			String c = new Location(b.getWorld(),b.getX(),b.getY()-2,b.getZ()).getBlock().getType().name();
			String c1 = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ()).getBlock().getType().name();
			String c2 = new Location(b.getWorld(),b.getX(),b.getY(),b.getZ()).getBlock().getType().name();
			if(acc(1,c) && acc(2,c1) && acc(3,c2)) {
					l=b;
					break;
					}
		}
		if(l!=null)return l.add(0.5, 0.5, 0.5);
		return d;
	}
	
	private boolean acc(int i, String c) {
		boolean d = false;
		switch(i) {
		case 1:
			if(!c.contains("AIR") && !c.contains("LAVA")&& !c.contains("BUTTON")
					&& !c.contains("DOOR")
			 && !c.contains("SIGN")
			 && !c.contains("TORCH")
			 && !c.contains("MUSHROOM") && Material.matchMaterial(c).isBlock())d=true;
			break;
		case 2:
			if(c.contains("AIR") || c.contains("BUTTON") || c.contains("DOOR")
					||c.contains("SIGN")
					||c.contains("TORCH")
					||c.contains("MUSHROOM") || c.contains("WATER"))d=true;
			break;
		case 3:
			if(c.contains("AIR") || c.contains("BUTTON") || c.contains("DOOR")
					||c.contains("SIGN")
					||c.contains("TORCH")
					||c.contains("MUSHROOM") || c.contains("WATER"))d=true;
			break;
		}
		return d;
	}
	
	public void setFreeze(boolean freeze) {
		LoaderClass.data.getConfig().set("data."+s.getName()+".freeze",true);
		TheAPI.getRunnable().runLater(new Runnable() {
			public void run() {
				LoaderClass.data.save();
			}
		}, 1);
	}
	public boolean isFreezen() {
		return LoaderClass.data.getConfig().getBoolean("data."+s.getName()+".freeze");
	}
	
	public static enum InvseeType {
		INVENTORY,
		ENDERCHEST
	}
	public void invsee(Player target, InvseeType type) {
		if(target==null) {
			Error.err("opening inventory to "+s.getName(), "Target is null");
			return;
		}
		if(type==null) {
			Error.err("opening inventory to "+s.getName(), "InvseeType is null");
			return;
		}
		try {
		switch(type) {
		case INVENTORY:
			s.openInventory(target.getInventory()); 
			break;
		case ENDERCHEST:
			s.openInventory(target.getEnderChest());
			break;
		}
		}catch(Exception e) {
			Error.err("opening inventory to "+s.getName(), "Uknown");
		}
	}
	public void msg(String message) {
		TheAPI.msg(message,s);
	}
	public void sendMessage(String message) {
		TheAPI.msg(message,s);
	}
	public void sendMsg(String message) {
		TheAPI.msg(message,s);
	}
	
	public void setHealth(double health) {
		try {
		if(0<health) {
			s.setMaxHealth(health);
		}
		s.setHealth(health);
		}catch(Exception e) {
			Error.err("setting health on "+s.getName(), "Health limit reached");
		}
	}
	
	public void setFood(int food) {
		try {
		s.setFoodLevel(food);
		}catch(Exception e) {
			Error.err("setting food on "+s.getName(), "Food limit reached");
		}
	}

	public void setFly(boolean allowFlying, boolean enableFlying) {
		if(allowFlying) {
			LoaderClass.data.getConfig().set("data."+s.getName()+".fly",true);
		s.setAllowFlight(true);
		s.setFlying(enableFlying);
		}else {
			LoaderClass.data.getConfig().set("data."+s.getName()+".fly",false);
			s.setFlying(enableFlying);
			s.setAllowFlight(false);
		}
		TheAPI.getRunnable().runLater(new Runnable() {
			public void run() {
				LoaderClass.data.save();
			}
		}, 1);
	}
	public boolean allowedFly() {
		return LoaderClass.data.getConfig().getBoolean("data."+s.getName()+".fly");
	}
	public void giveExp(int exp) {
			s.giveExp(exp);
	}

	public void takeExp(int exp) {
		s.giveExp(-exp);
	}
	
	public int getExp() {
		return s.getTotalExperience();
	}
	
	public void setExp(int exp) {
		if(getExp() < exp) {
			exp=getExp()-exp;
			giveExp(exp);
		}else
			if(getExp() > exp) {
				exp=exp-getExp();
				giveExp(exp);
			}
			
	}
	
	public void resetMaxHealth() {
		s.setMaxHealth(20);
	}
	public void resetTotalExp() {
		resetExp();
		resetLevel();
	}

	public void resetExp() {
		s.setExp(0);
		s.setTotalExperience(0);
	}
	public void resetLevel() {
		s.setLevel(0);
	}

	public void sendTitle(String firstLine, String nextLine) {
		try {
            
    	    Class<?> PacketPlayOutTitleClass = Packets.getNMSClass("PacketPlayOutTitle");
    	    Class<?> IChatBaseComponentClass = Packets.getNMSClass("IChatBaseComponent");
    	               
    	    Constructor<?> PacketPlayOutTitleConstructor = PacketPlayOutTitleClass.getConstructor(PacketPlayOutTitleClass.getDeclaredClasses()[0], IChatBaseComponentClass, int.class, int.class, int.class);
    	               
    	    Object titleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + TheAPI.colorize(firstLine)+ "\"}");
    	    Object subTitleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + TheAPI.colorize(nextLine) + "\"}");
    	               
    	    Object titlePacket = PacketPlayOutTitleConstructor.newInstance(PacketPlayOutTitleClass.getDeclaredClasses()[0].getField("TITLE").get(null), titleComponent, 20, 60, 20);
    	    Object subTitlePacket = PacketPlayOutTitleConstructor.newInstance(PacketPlayOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE").get(null), subTitleComponent, 20, 60, 20);
    	               
    	    Packets.sendPacket(s, titlePacket);
    	    Packets.sendPacket(s, subTitlePacket);
    	} catch (Exception e) {
			Error.err("sending title to "+s.getName(), "Line is null");
    	}
	}
	
	public void sendTitle(String firstLine, String nextLine, int fadeIn, int stay, int fadeOut) {
		try {
            
    	    Class<?> PacketPlayOutTitleClass = Packets.getNMSClass("PacketPlayOutTitle");
    	    Class<?> IChatBaseComponentClass = Packets.getNMSClass("IChatBaseComponent");
    	               
    	    Constructor<?> PacketPlayOutTitleConstructor = PacketPlayOutTitleClass.getConstructor(PacketPlayOutTitleClass.getDeclaredClasses()[0], IChatBaseComponentClass, int.class, int.class, int.class);
    	               
    	    Object titleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + TheAPI.colorize(firstLine)+ "\"}");
    	    Object subTitleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + TheAPI.colorize(nextLine) + "\"}");
    	               
    	    Object titlePacket = PacketPlayOutTitleConstructor.newInstance(PacketPlayOutTitleClass.getDeclaredClasses()[0].getField("TITLE").get(null), titleComponent, fadeIn, stay, fadeOut);
    	    Object subTitlePacket = PacketPlayOutTitleConstructor.newInstance(PacketPlayOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE").get(null), subTitleComponent, fadeIn, stay, fadeOut);
    	               
    	    Packets.sendPacket(s, titlePacket);
    	    Packets.sendPacket(s, subTitlePacket);
    	} catch (Exception e) {
			Error.err("sending title to "+s.getName(), "Line is null");
    	}
	}
	
	public boolean hasOpenGUI() {
		return LoaderClass.gui.containsKey(s);
	}

	public void giveLevel(int level) {
		s.setLevel(s.getLevel()+level);
	}
	
	public void takeLevel(int level) {
		if(s.getLevel() <=level)level=0;
		else level=s.getLevel()-level;
		s.setLevel(level);
	}
	
	public List<Block> getNearbyBlocks(int range){
		return TheAPI.getBlocksAPI().getBlocks(Shape.Sphere, s.getLocation(), range);
	}
	
	public List<Entity> getNearbyEntities(int range){
		return TheAPI.getBlocksAPI().getNearbyEntities(s.getLocation(), range);
	}

	public void closeOpenInventory() {
		s.getOpenInventory().close();
	}
	
	public String getAddress() {
	try {
		return TheAPI.getPunishmentAPI().getIP(s.getName());
	}catch(Exception e) {
		Error.err("getting ip address of "+s.getName(), "Address is null");
		return null;
	}
	}
	
	public void setScoreboard(Scoreboard sb) {
		try {
		if(sb!=null)
		s.setScoreboard(sb);
		else
			s.setScoreboard(s.getServer().getScoreboardManager().getNewScoreboard());
		}catch(Exception e) {
			Error.err("setting scoreboard on "+s.getName(), "Scoreboard is null");
		}
	}
	/**
	 * int from -10 to 10
	 * @param speed
	 */
	public void setFlySpeed(int speed) {
		if(speed<-10)speed=-10;
		if(speed>10)speed=10;
		s.setFlySpeed(speed/10);
	}

	/**
	 * int from -10 to 10
	 * @param speed
	 */
	public void setWalkSpeed(int speed) {
		if(speed<-10)speed=-10;
		if(speed>10)speed=10;
		s.setWalkSpeed(speed/10);
	}
	public void setMaxAir() {
		 s.setRemainingAir(s.getMaximumAir());
	}
	public void setAir(int air) {
		 s.setRemainingAir(air);
	}
	//1.5+
	public void setGodOnTime(int time) {
		if(time <= 0)time=1;
		try {
			
		 s.setNoDamageTicks(time*20);
		}catch(Exception ea) {
			setGod(true);
			TheAPI.getRunnable().runLater(new Runnable() {
				public void run() {
					setGod(false);
				}
			}, 20*time);
		}
	}

	public void setItemInHand(ItemStack i) {
		if(i==null)i=new ItemStack(Material.AIR);
		s.setItemInHand(i);
	}
	public void setHelmet(ItemStack i) {
		if(i==null)i=new ItemStack(Material.AIR);
		s.getEquipment().setHelmet(i);
	}
	public void setChestplate(ItemStack i) {
		if(i==null)i=new ItemStack(Material.AIR);
		s.getEquipment().setChestplate(i);
	}
	public void setLeggings(ItemStack i) {
		if(i==null)i=new ItemStack(Material.AIR);
		s.getEquipment().setLeggings(i);
	}
	public void setBoots(ItemStack i) {
		if(i==null)i=new ItemStack(Material.AIR);
		s.getEquipment().setBoots(i);
	}
	public void setItemInOffHand(ItemStack i) {
		if(i==null)i=new ItemStack(Material.AIR);
		try {
		s.getEquipment().setItemInOffHand(i);
		}catch(Exception er) {
			
		}
	}
	public ItemStack getItemInHand() {
		return s.getItemInHand();
	}
	public ItemStack getHelmet() {
		return s.getEquipment().getHelmet();
	}
	public ItemStack getChestplate() {
		return s.getEquipment().getChestplate();
	}
	public ItemStack getLeggings() {
		return s.getEquipment().getLeggings();
	}
	public ItemStack getBoots() {
		return s.getEquipment().getBoots();
	}
	public ItemStack getItemInOffHand() {
		try {
		return s.getEquipment().getItemInOffHand();
		}catch(Exception err) {
			return null;
		}
	}
	/**
	 * Kick player from serveer with reason
	 */
	public void kick(String reason) {
		if(reason==null)reason="Uknown";
		 s.kickPlayer(TheAPI.colorize(reason));
	}
	
	public void setGod(boolean enable) {
			LoaderClass.data.getConfig().set("data."+s.getName()+".god",enable);
			TheAPI.getRunnable().runLater(new Runnable() {
				public void run() {
					LoaderClass.data.save();
				}
			}, 1);
	}
	public boolean allowedGod() {
		return LoaderClass.data.getConfig().getBoolean("data."+s.getName()+".god");
	}
	
}
