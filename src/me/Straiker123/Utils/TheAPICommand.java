package me.Straiker123.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import me.Straiker123.BlockSave;
import me.Straiker123.BlocksAPI;
import me.Straiker123.BlocksAPI.Shape;
import me.Straiker123.ConfigAPI;
import me.Straiker123.LoaderClass;
import me.Straiker123.ScoreboardAPI;
import me.Straiker123.TheAPI;

public class TheAPICommand implements CommandExecutor, TabCompleter {
	
	private String getPlugin(Plugin a) {
		if(a.isEnabled())return "&a"+a.getName();
		return "&c"+a.getName()+" &7(Disabled)";
	}
	String[] args;
	private boolean eq(int i, String s) {
		return args[i].equalsIgnoreCase(s);
	}
	public static boolean r;
	CommandSender s;
	private boolean perm(String p) {
		if(s.hasPermission("TheAPI.Command."+p))return true;
		s.sendMessage(TheAPI.colorize("&cYou do not have permission '&4TheAPI.Command."+p+"&c' to do that!"));
		return false;
	}

	private void regWorld(String w, String gen) {
		LoaderClass.config.getConfig().set("WorldsSetting."+w+".Generator", gen);
		LoaderClass.config.getConfig().set("WorldsSetting."+w+".GenerateStructures", true);
	}
	private void unregWorld(String w) {
		LoaderClass.config.getConfig().set("WorldsSetting."+w, null);
	}
	//Info, Reload, ClearCache, WorldsManager
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		this.args=args;
		this.s=s;
		if(args.length==0) {
			s.sendMessage(TheAPI.colorize("&7-----------------"));
			if(s.hasPermission("TheAPI.Command.Info"))
			s.sendMessage(TheAPI.colorize("&6/TheAPI Info"));
			if(s.hasPermission("TheAPI.Command.Reload"))
			s.sendMessage(TheAPI.colorize("&6/TheAPI Reload"));
			if(s.hasPermission("TheAPI.Command.WorldsManager"))
			s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager"));
			if(s.hasPermission("TheAPI.Command.ClearCache"))
			s.sendMessage(TheAPI.colorize("&6/TheAPI ClearCache"));
			if(s.isOp())
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test"));
			s.sendMessage(TheAPI.colorize("&6Credits: TheAPI created by DevTec"));
			s.sendMessage(TheAPI.colorize("&7-----------------"));
			return true;
		}
		if(eq(0,"test")) {
			if(!s.isOp() || !(s instanceof Player))return true;
			Player p = (Player)s;
			if(args.length==1) {
			s.sendMessage(TheAPI.colorize("&7-----------------"));
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test BossBar"));
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test ActionBar"));
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test Title"));
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test TabList"));
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test Scoreboard"));
			s.sendMessage(TheAPI.colorize("&6/TheAPI Test BlocksAPI"));
			s.sendMessage(TheAPI.colorize("&7-----------------"));
			return true;
			}
			if(eq(1,"bossbar")) {
				TheAPI.sendBossBar(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"), 0.5, 40);
				return true;
			}
			if(eq(1,"ActionBar")) {
				TheAPI.sendActionBar(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				return true;
			}
			if(eq(1,"Title")) {
				TheAPI.sendTitle(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"),"");
				return true;
			}
			if(eq(1,"TabList")) {
				TheAPI.getTabListAPI().setHeaderFooter(p, "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"), "&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				return true;
			}
			if(eq(1,"Scoreboard")) {
				ScoreboardAPI a = TheAPI.getScoreboardAPI(p);
				a.setTitle("&eTheAPI v"+TheAPI.getPluginsManagerAPI().getVersion("TheAPI"));
				a.addLine("&aBy DevTec", 0);
				a.create();
				Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
					@Override
					public void run() {
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
					}
				}, 40);
				return true;
			}
			if(eq(1,"BlocksAPI")) {
				if(!r) {
					r=true;
				BlocksAPI a = TheAPI.getBlocksAPI();
				List<Block> d =a.getBlocks(Shape.Sphere, p.getLocation(), 5,Material.AIR);
				List<BlockSave> save = new ArrayList<BlockSave>();
				for(Block b : d) {
					save.add(a.getBlockSave(b));
					b.getWorld().getBlockAt(b.getLocation()).setType(Material.DIAMOND_BLOCK);
				}
				d.clear();
				Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() { // undo command ?
					@Override
					public void run() {
						if(save.isEmpty()==false)
						a.setBlockSaves(save);
						r=false;
					}
				}, 40);
				return true;
				}
				return true;
			}
		}
		if(eq(0,"cc")||eq(0,"clear")||eq(0,"clearcache")) {
			if(perm("ClearCache")) {
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				s.sendMessage(TheAPI.colorize("&6Clearing cache.."));
				for(Player p : Bukkit.getOnlinePlayers()) {
					if( p.getOpenInventory()!=null) {
						if(LoaderClass.gui.get(p)!=null)
							p.getOpenInventory().close();
				}}
				LoaderClass.data.getConfig().set("guis", null);
				LoaderClass.data.getConfig().set("entities", null);
				LoaderClass.data.save();
				LoaderClass.gui.clear();
				s.sendMessage(TheAPI.colorize("&6Cache cleared."));
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				return true;
			}
			return true;
		}
		if(eq(0,"reload")||eq(0,"rl")) {
		if(perm("Reload")) {
			s.sendMessage(TheAPI.colorize("&7-----------------"));
			s.sendMessage(TheAPI.colorize("&6Reloading configs.."));
			for(ConfigAPI a : LoaderClass.list) {
				a.reload();
			}
			s.sendMessage(TheAPI.colorize("&6Configs reloaded."));
			s.sendMessage(TheAPI.colorize("&7-----------------"));
			
			return true;
			}
		return true;
		}
		if(eq(0,"inf")||eq(0,"info")) {
			if(perm("Info")) {
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				s.sendMessage(TheAPI.colorize("&6Version: &cv"+LoaderClass.plugin.getDescription().getVersion()));
				if(TheAPI.getCountingAPI().getPluginsUsingTheAPI().size()!=0) {
				s.sendMessage(TheAPI.colorize("&6Plugins using TheAPI:"));
				for(Plugin a: TheAPI.getCountingAPI().getPluginsUsingTheAPI())
				s.sendMessage(TheAPI.colorize("&7 - "+getPlugin(a)));
				}
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				return true;
				}return true;
		}
		if(eq(0,"worldsmanager")||eq(0,"world")||eq(0,"worlds")||eq(0,"wm")||eq(0,"worldmanager")) {
			if(perm("WorldsManager")) {
				if(args.length==1) {
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Create <world> <generator>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Delete <world>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Unload <world>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Load <world> <generator>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Save <world>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager SaveAll"));
				s.sendMessage(TheAPI.colorize("&6Worlds:"));
				for(World w : Bukkit.getWorlds())
				s.sendMessage(TheAPI.colorize("&7 - &a"+w.getName()));
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				return true;
				}
				if(eq(1,"saveall")) {
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6TheAPI WorldsManager saving all worlds.."));
					for(World w:Bukkit.getWorlds())w.save();
					s.sendMessage(TheAPI.colorize("&6All worlds saved."));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
				}
				if(eq(1,"save")) {
					if(args.length==2) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Save <world>"));
						s.sendMessage(TheAPI.colorize("&6Worlds:"));
						for(World w : Bukkit.getWorlds())
						s.sendMessage(TheAPI.colorize("&7 - &a"+w.getName()));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					
					if(Bukkit.getWorld(args[2])==null) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' doesn't exists."));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6TheAPI WorldsManager saving world with name '"+args[2]+"'.."));
					Bukkit.getWorld(args[2]).save();
					s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' saved."));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
				}
				if(eq(1,"unload")) {
					if(args.length==2) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Unload <world>"));
						s.sendMessage(TheAPI.colorize("&6Worlds:"));
						for(World w : Bukkit.getWorlds())
						s.sendMessage(TheAPI.colorize("&7 - &a"+w.getName()));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					if(Bukkit.getWorld(args[2])==null) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' doesn't exists."));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6TheAPI WorldsManager unloading world with name '"+args[2]+"'.."));
					TheAPI.getWorldsManager().unloadWorld(args[2], true);

					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.remove(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' unloaded."));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
				}
				if(eq(1,"load")) {
					if(args.length==2) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Load <world> <generator>"));
						s.sendMessage(TheAPI.colorize("&6Generators:"));
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
						s.sendMessage(TheAPI.colorize("&7 - &a"+w));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					if(Bukkit.getWorld(args[2])!=null) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' already exists."));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					if(args.length==3) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Load <world> <generator>"));
						s.sendMessage(TheAPI.colorize("&6Generators:"));
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
						s.sendMessage(TheAPI.colorize("&7 - &a"+w));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					String generator = args[3];
					String type="Default";
					boolean i=false;
					for(String w:Arrays.asList("Default","Normal","Nether","The_End"
							,"End","The_Void","Void","Empty","Flat")) {
					if(generator.equalsIgnoreCase(w)) {
						i=true;
						if(w.equals("Flat"))type="Flat";
						if(w.equals("Nether"))type="Nether";
						if(w.equals("The_End")||w.equals("End"))type="The_End";
						if(w.equals("The_Void")||w.equals("Void")||w.equals("Empty"))type="The_Void";
						break;
					}
					}
					if(i) {
						if(new File(Bukkit.getWorldContainer().getPath()+"/"+args[2]+"/session.lock").exists()) {
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6TheAPI WorldsManager loading world with name '"+args[2]+"'.."));
					Environment env = Environment.NORMAL;
					WorldType wt= WorldType.NORMAL;

					if(type.equals("Flat"))wt=WorldType.FLAT;
					if(type.equals("The_Void"))env=null;
					if(type.equals("The_End")) {
						try {
						env=Environment.valueOf("THE_END");
						}catch(Exception e) {
							env=Environment.valueOf("END");
						}
					}
					if(type.equals("Nether"))env=Environment.NETHER;
					TheAPI.getWorldsManager().load(args[2], env, wt);
					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.add(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					regWorld(args[2],type);
					s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' loaded."));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
						}
							s.sendMessage(TheAPI.colorize("&7-----------------"));
							s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' doesn't exists."));
							s.sendMessage(TheAPI.colorize("&7-----------------"));
							return true;
					}
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Load "+args[2]+" <generator>"));
					s.sendMessage(TheAPI.colorize("&6Generators:"));
					for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
					s.sendMessage(TheAPI.colorize("&7 - &a"+w));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
				}
				if(eq(1,"delete")) {
					if(args.length==2) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Delete <world>"));
						s.sendMessage(TheAPI.colorize("&6Worlds:"));
						for(World w : Bukkit.getWorlds())
						s.sendMessage(TheAPI.colorize("&7 - &a"+w.getName()));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					if(Bukkit.getWorld(args[2])==null) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' doesn't exists."));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6TheAPI WorldsManager deleting world with name '"+args[2]+"'.."));
					TheAPI.getWorldsManager().delete(Bukkit.getWorld(args[2]), true);
					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.remove(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					unregWorld(args[2]);
					s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' deleted."));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
				}
				if(eq(1,"create")) {
					if(args.length==2) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Create <world> <generator>"));
						s.sendMessage(TheAPI.colorize("&6Generators:"));
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void"))
						s.sendMessage(TheAPI.colorize("&7 - &a"+w));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					if(Bukkit.getWorld(args[2])!=null) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' already exists."));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					
					if(args.length==3) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Create "+args[2]+" <generator>"));
						s.sendMessage(TheAPI.colorize("&6Generators:"));
						for(String w:Arrays.asList("Default","Nether","The_End","The_Void","Flat"))
						s.sendMessage(TheAPI.colorize("&7 - &a"+w));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					String generator = args[3];
					String type="Default";
					boolean i=false;
					for(String w:Arrays.asList("Default","Normal","Nether","The_End"
							,"End","The_Void","Void","Empty","Flat")) {
					if(generator.equalsIgnoreCase(w)) {
						i=true;
						if(w.equals("Flat"))type="Flat";
						if(w.equals("Nether"))type="Nether";
						if(w.equals("The_End")||w.equals("End"))type="The_End";
						if(w.equals("The_Void")||w.equals("Void")||w.equals("Empty"))type="The_Void";
						break;
					}
					}
					if(i) {
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						s.sendMessage(TheAPI.colorize("&6TheAPI WorldsManager creating new world with name '"+args[2]+"' using generator '"+type+"'.."));
						Environment env = Environment.NORMAL;
						WorldType wt= WorldType.NORMAL;

						if(type.equals("Flat"))wt=WorldType.FLAT;
						if(type.equals("The_Void"))env=null;
						if(type.equals("The_End")) {
							try {
							env=Environment.valueOf("THE_END");
							}catch(Exception e) {
								env=Environment.valueOf("END");
							}
						}
						if(type.equals("Nether"))env=Environment.NETHER;
						
						TheAPI.getWorldsManager().create(args[2], env, wt, true, 0);
						List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
						a.add(args[2]);
						LoaderClass.config.getConfig().set("Worlds", a);
						regWorld(args[2],type);
						s.sendMessage(TheAPI.colorize("&6World with name '"+args[2]+"' created."));
						s.sendMessage(TheAPI.colorize("&7-----------------"));
						return true;
					}
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Create "+args[2]+" <generator>"));
					s.sendMessage(TheAPI.colorize("&6Generators:"));
					for(String w:Arrays.asList("Default","Nether","The_End","The_Void","Flat"))
					s.sendMessage(TheAPI.colorize("&7 - &a"+w));
					s.sendMessage(TheAPI.colorize("&7-----------------"));
					return true;
				}
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Create <world> <generator>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Delete <world>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Unload <world>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Load <world> <generator>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager Save <world>"));
				s.sendMessage(TheAPI.colorize("&6/TheAPI WorldsManager SaveAll"));
				s.sendMessage(TheAPI.colorize("&6Worlds:"));
				for(World w : Bukkit.getWorlds())
				s.sendMessage(TheAPI.colorize("&7 - &a"+w.getName()));
				s.sendMessage(TheAPI.colorize("&7-----------------"));
				return true;
				
				}return true;
		}
		return false;
	}
	List<String> getWorlds(){
		List<String> list = new ArrayList<String>();
		for(World w :Bukkit.getWorlds())list.add(w.getName());
		return list;
	}
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = new ArrayList<>();
		if(args.length==1) {
			if(s.hasPermission("TheAPI.Command.Info"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Info"), new ArrayList<>()));
			if(s.hasPermission("TheAPI.Command.Reload"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Reload"), new ArrayList<>()));
			if(s.hasPermission("TheAPI.Command.ClearCache"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("ClearCache"), new ArrayList<>()));
			if(s.hasPermission("TheAPI.Command.WorldsManager"))
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("WorldsManager"), new ArrayList<>()));
			if(s.isOp())
		    	c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Test"), new ArrayList<>()));
		}
		if(args[0].equalsIgnoreCase("Test") && s.isOp()) {
			if(args.length==2) {
				c.addAll(StringUtil.copyPartialMatches(args[1], Arrays.asList("TabList","Scoreboard","Title","Actionbar","Bossbar","BlocksAPI"), new ArrayList<>()));
				}
		}
		if(args[0].equalsIgnoreCase("WorldsManager") && s.hasPermission("TheAPI.Command.WorldsManager")) {
			if(args.length==2) {
		    	c.addAll(StringUtil.copyPartialMatches(args[1], Arrays.asList("Create","Delete","Load","Unload","Save","SaveAll"), new ArrayList<>()));
			}
			if(args.length>=3) {
			if(args[1].equalsIgnoreCase("Create")||args[1].equalsIgnoreCase("Load")) {
				if(args.length==3)
				return Arrays.asList("?");
				if(args.length==4)
				 	c.addAll(StringUtil.copyPartialMatches(args[3], Arrays.asList("Default","Nether","The_End","The_Void","Flat"), new ArrayList<>()));
			}
			if(args[1].equalsIgnoreCase("Unload")||args[1].equalsIgnoreCase("Delete")||args[1].equalsIgnoreCase("Save")) {
				if(args.length==3)
					c.addAll(StringUtil.copyPartialMatches(args[1], getWorlds(), new ArrayList<>()));
				}}
		}
		return c;
	}

}
