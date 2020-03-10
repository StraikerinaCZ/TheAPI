package me.Straiker123;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.Straiker123.Utils.Error;

@SuppressWarnings("deprecation")
public class ItemCreatorAPI implements Cloneable {
	private ItemStack a;
	private String author = "", title = "",name,owner,url,text;
	private Color c;
	private boolean unb;
	private SkullType type;
	private List<SkullType> wd = Arrays.asList(SkullType.CREEPER,SkullType.DRAGON,SkullType.PLAYER,SkullType.SKELETON,SkullType.WITHER,SkullType.ZOMBIE);
	private int s = 1,model = -1, dur=-1;
	private MultiMap ef = TheAPI.getMultiMap(),enchs = TheAPI.getMultiMap(),w = TheAPI.getMultiMap();
	private List<Object> pages = new ArrayList<Object>(),lore = new ArrayList<Object>(),map = new ArrayList<Object>();
	public ItemCreatorAPI(ItemStack icon) {
		a=icon != null ? icon : new ItemStack(Material.AIR);
		unb=isUnbreakable();
		for(PotionEffect e : getPotionEffects()) {
			addPotionEffect(e.getType(), e.getDuration(),e.getAmplifier());
		}
		c=getPotionColor();
		name=getDisplayName();
		owner=getOwner();
		for(String s : getLore()) {
			addLore(s);
		}
		for(Enchantment e : getEnchantments().keySet())
		enchs.put(e, getEnchantments().get(e));
		s=getAmount();
		model=getCustomModelData();
		type=getSkullType();
		for(ItemFlag s : getItemFlags()) {
			map.add(s);
		}
		data=getMaterialData();
		dur=getDurability();
		for(Attribute s : getAttributeModifiers().keySet()) {
			addAttributeModifier(s, getAttributeModifiers().get(s));
		}
		if(hasBookAuthor())
		author=getBookAuthor();
		for(String s : getBookPages()) {
			addBookPage(s);
		}
		if(hasBookTitle())
		title=getBookTitle();
		if(hasBookGeneration())
		gen=getBookGeneration();
	}
	
	public Material getMaterial() {
		return a.getType();
	}
	public boolean isItem(boolean canBeLegacy) {
		String s = a.getType().name();
		return !s.contains("WALL_")&&!s.contains("AIR")&&!s.contains("VOID")
				&&!s.contains("_STEM")&&!s.contains("POTTED_")&&(canBeLegacy ? true : !s.contains("LEGACY_"))
				&&!s.equals("END_PORTAL")&&!s.equals("END_GATEWAY")&&!s.equals("NETHER_PORTAL")
				|| a.getType().isBlock() && a.getType().isOccluding();
	}
	public boolean isBlock() {
		return a.getType().isBlock();
	}
	public void setOwnerFromWeb(String web) {
		if(web!=null)
			this.url=web;
	}
	public void setOwnerFromValues(String values) {
		if(values!=null)
		text=values;
	}
	public void setMaterial(String byName) {
		try {
			a.setType(Material.matchMaterial(byName));
		}catch(Exception e) {
			Error.err("set material in ItemCreatorAPI", "Uknown Material");
		}
	}
	public List<PotionEffect> getPotionEffects() {
		if(a.getItemMeta() instanceof PotionMeta)
	 return ((PotionMeta)a.getItemMeta()).getCustomEffects();
		return new ArrayList<PotionEffect>();
	}
	public ItemMeta getItemMeta() {
		return a.getItemMeta();
	}
	public boolean hasPotionEffects() {
		if(a.getItemMeta() instanceof PotionMeta)
	 return ((PotionMeta)a.getItemMeta()).hasCustomEffects();
		return false;
	}
	public boolean hasPotionEffect(PotionEffectType type) {
		if(a.getItemMeta() instanceof PotionMeta)
	 return ((PotionMeta)a.getItemMeta()).hasCustomEffect(type);
		return false;
	}
	public boolean hasPotionColor() {
		if(a.getItemMeta() instanceof PotionMeta)
	 return ((PotionMeta)a.getItemMeta()).hasColor();
		return false;
	}
	public void addPotionEffect(PotionEffectType potionEffect, int duration, int amlifier) {
		if(potionEffect!=null)
		ef.put(potionEffect, duration,amlifier);
	}
	public void addPotionEffect(String potionEffect, int duration, int amlifier) {
		addPotionEffect(PotionEffectType.getByName(potionEffect),duration,amlifier);
	}
	public Color getPotionColor() {
		if(a.getItemMeta() instanceof PotionMeta)
	 return ((PotionMeta)a.getItemMeta()).getColor();
		return null;
	}
	public void setPotionColor(Color color) {
		if(color != null)
		c=color;
	}
	public void setDisplayName(String newName) {
		if(newName!=null)
		name=TheAPI.colorize(newName);
	}
	public String getDisplayName() {
		return a.getItemMeta().getDisplayName();
	}
	public void addLore(String line) {
		if(line!=null)
		lore.add(TheAPI.colorize(line));
	}
	public List<String> getLore() {
		return a.getItemMeta().getLore();
	}
	public String getOwner() {
		if(a.getItemMeta() instanceof SkullMeta)
		return ((SkullMeta)a.getItemMeta()).getOwner();
		return null;
	}
	public void setOwner(String owner) {
		if(owner!=null)
			this.owner=owner;
	}
	public HashMap<Enchantment, Integer> getEnchantments() {
		HashMap<Enchantment, Integer> e= new HashMap<Enchantment, Integer>();
		Map<Enchantment, Integer> map = a.getItemMeta().getEnchants();
		for(Enchantment a : map.keySet())e.put(a,map.get(a));
		return e;
	}
	public void addEnchantment(Enchantment e, int level) {
		if(e!= null)enchs.put(e, level);
	}
	public void addEnchantment(String e, int level) {
		if(e!= null)enchs.put(TheAPI.getEnchantmentAPI().getByName(e), level);
	}
	public int getAmount() {
		return a.getAmount();
	}
	public void setAmount(int amount) {
		if(amount > 64)amount=64;
		s=amount;
	}
	public void setLore(List<String> lore) {
		if(lore!=null && lore.isEmpty()==false)
			for(String s:lore)
				addLore(s);
	}
	public int getCustomModelData() {
		try {
			return a.getItemMeta().getCustomModelData();
		}catch(Exception er) {
			return -1;
		}
	}
	public void setCustomModelData(int i) {
		model=i;
	}
	public boolean isUnbreakable() {
		try {
		return a.getItemMeta().isUnbreakable();
		}catch(Exception er) {
			return hasLore() && getLore().contains("") && getLore().contains("&9UNBREAKABLE");
		}
	}
	public void setUnbreakable(boolean unbreakable) {
		unb=unbreakable;
	}
	public SkullType getSkullType() {
		if(a.getItemMeta() instanceof SkullMeta) {
			return wd.get((int)a.getDurability());
		}
		return null;
	}
	public void setSkullType(SkullType t) {
		if(t!=null)
			type=t;
	}
	public List<ItemFlag> getItemFlags(){
		List<ItemFlag> items = new ArrayList<ItemFlag>();
		for(ItemFlag f : a.getItemMeta().getItemFlags())items.add(f);
		return items;
	}
	public void addItemFlag(ItemFlag itemflag) {
		if(itemflag!=null)
		map.add(itemflag);
	}
	@SuppressWarnings("unchecked")
	public HashMap<Attribute, AttributeModifier> getAttributeModifiers(){
		HashMap<Attribute, AttributeModifier> h = new HashMap<Attribute, AttributeModifier>();
		try {
			if(hasAttributeModifiers()) {
			HashMap<Attribute, AttributeModifier> map = (HashMap<Attribute, AttributeModifier>) a.getItemMeta().getAttributeModifiers();
			for(Attribute a : map.keySet())h.put(a, map.get(a));
			}
		return h;
		}catch(Exception er) {
			return h;
		}
	}
	public void addAttributeModifier(Attribute a, AttributeModifier s) {
		if(TheAPI.isNewVersion()&&!TheAPI.getServerVersion().equals("v1_13_R1") && a != null && s!=null)
		w.put(a, s);
	}
	public void addAttributeModifiers(HashMap<Attribute, AttributeModifier> s) {
		if(TheAPI.isNewVersion()&&!TheAPI.getServerVersion().equals("v1_13_R1") && s!=null)
		for(Attribute r : s.keySet()) {
			addAttributeModifier(r,s.get(r));
		}
	}
	
	public short getDurability() {
		return a.getDurability();
	}
	
	public void setDurability(int amount) {
		dur=amount;
	}
	
	public MaterialData getMaterialData() {
		try {
			return a.getData();
		}catch(Exception er) {
			return null;
		}
	}
	
	private MaterialData data = null;
	public void setMaterialData(MaterialData data) {
		this.data=data;
	}
	
	public boolean hasDisplayName() {
		return a.getItemMeta().hasDisplayName();
	}
	public boolean hasLore() {
		return a.getItemMeta().hasLore();
	}
	public boolean hasEnchants() {
		return a.getItemMeta().hasEnchants();
	}
	public boolean hasCustomModelData() {
		return a.getItemMeta().hasCustomModelData();
	}
	public boolean hasAttributeModifiers() {
		try {
		return a.getItemMeta().hasAttributeModifiers();
		}catch(Exception err) {
			return false;
		}
	}
	public boolean hasItemFlag(ItemFlag flag) {
		return a.getItemMeta().hasItemFlag(flag);
	}
	public boolean hasConflictingEnchant(Enchantment ench) {
		return a.getItemMeta().hasConflictingEnchant(ench);
	}
	public boolean hasEnchant(Enchantment ench) {
		return a.getItemMeta().hasEnchant(ench);
	}

	public String getBookAuthor() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).getAuthor();	
		}
		return null;
	}

	public boolean hasBookAuthor() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).hasAuthor();	
		}
		return false;
	}
	
	public void setBookAuthor(String author) {
		if(author!=null)
		this.author=TheAPI.colorize(author);
	}

	public boolean hasBookTitle() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).hasTitle();	
		}
		return false;
	}
	public String getBookTitle() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).getTitle();	
		}
		return null;
	}
	
	public void setBookTitle(String title) {
		if(title!=null)
		this.title=TheAPI.colorize(title);
	}
	public List<String> getBookPages() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).getPages();	
		}
		return new ArrayList<String>();
	}
	public String getBookPage(int page) {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).getPage(page);	
		}
		return null;
	}
	public int getBookPageCount() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).getPageCount();	
		}
		return 0;
	}
	public void addBookPage(String lines) {
		if(lines==null)lines="";
		pages.add(TheAPI.colorize(lines));
	}
	public void addBookPage(int page, String lines) {
		if(lines==null && pages.get(page)!=null)pages.remove(page);
		else
		pages.set(page,TheAPI.colorize(lines));
	}
	public void setBookPages(List<String> lines) {
		if(lines!=null)
		for(String s : lines)
		addBookPage(s);
	}
	public boolean hasBookGeneration() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).hasGeneration();	
		}
		return false;
	}
	public Generation getBookGeneration() {
		if(a.getItemMeta() instanceof BookMeta) {
		return ((BookMeta)a.getItemMeta()).getGeneration();	
		}
		return null;
	}
	Generation gen = Generation.ORIGINAL;
	public void setBookGeneration(Generation generation) {
		try {
		if(generation!=null)
			gen=generation;
		}catch(Exception e) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack create() {
		ItemStack i = a;
		try {
		if(type!=null) {
			a.setDurability((short)type.ordinal());
		}else {
			if(dur!=-1)
			a.setDurability((short)dur);
		}
		i.setAmount(s);
		ItemMeta mf=i.getItemMeta();
		if(data != null)
			i.setData(data);
		if(!i.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
			if(enchs != null && enchs.getKeySet().isEmpty())
				for(Object t : enchs.getKeySet())i.addUnsafeEnchantment((Enchantment)t,TheAPI.getStringUtils().getInt(enchs.getValues(t).get(0).toString()));
		}
			if(name!=null)
			mf.setDisplayName(name);
			if(model != -1 && TheAPI.isNewVersion() //1.13+
					 &&!TheAPI.getServerVersion().contains("v1_13"))
			mf.setCustomModelData(model);
			if(!TheAPI.isOlder1_9()
					 &&!TheAPI.getServerVersion().contains("v1_9")
					 &&!TheAPI.getServerVersion().contains("v1_10"))
			mf.setUnbreakable(unb);
			 else {
				 addLore("");
				 addLore("&9UNBREAKABLE");
			 }
				if(lore!=null && !lore.isEmpty()) {
					List<String> lor = new ArrayList<String>();
					for(Object o : lore)lor.add(o.toString());
					mf.setLore(lor);
				}
			if(map != null && !map.isEmpty())
			for(Object f: map)
			mf.addItemFlags((ItemFlag)f);
			if(w!=null && !w.getKeySet().isEmpty() && TheAPI.isNewVersion()//(Multimap<Attribute, AttributeModifier>) 
					 &&!TheAPI.getServerVersion().equals("v1_13_R1")) {//1.13+
				HashMap<Attribute, AttributeModifier> v = new HashMap<Attribute, AttributeModifier>();
				for(Object o : w.getKeySet())
				v.put((Attribute)o, (AttributeModifier)w.getValues(o).get(0));
			mf.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) v);
			}
			i.setItemMeta(mf);
		if(i.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
		EnchantmentStorageMeta m = (EnchantmentStorageMeta) i.getItemMeta();
		if(enchs != null && !enchs.getKeySet().isEmpty())
			for(Object e : enchs.getKeySet())
			m.addStoredEnchant((Enchantment)e, TheAPI.getStringUtils().getInt(enchs.getValues(e).get(0).toString()), true);
		i.setItemMeta(m);
		a=i;
		}else
		if(i.getType().name().equalsIgnoreCase("WRITABLE_BOOK")||i.getType().name().equalsIgnoreCase("BOOK_AND_QUILL")) {
			BookMeta m = (BookMeta)i.getItemMeta();
			m.setAuthor(author);
			List<String> page = new ArrayList<String>();
			for(Object o : pages)page.add(o.toString());
			m.setPages(page);
			m.setTitle(title);
			try {
			m.setGeneration(gen);
			}catch(Exception e) {}
			i.setItemMeta(m);
		}else
			if(i.getType().name().startsWith("LINGERING_POTION_OF_")||i.getType().name().startsWith("SPLASH_POTION_OF_")||i.getType().name().startsWith("POTION_OF_")) {
				PotionMeta meta = (PotionMeta)i.getItemMeta();
				meta.setColor(c);
				for(Object o : ef.getKeySet()) {
					Object[] f = ef.getValues(o).toArray();
					PotionEffectType t = PotionEffectType.getByName(o.toString());
					if(t==null) {
						Error.err("creating ItemStack in ItemCreatorAPI", "Uknown PotionEffectType "+o.toString());
						continue;
					}
					int dur = TheAPI.getStringUtils().getInt(f[0].toString());
					int amp = TheAPI.getStringUtils().getInt(f[1].toString());
					meta.addCustomEffect(new PotionEffect(t, dur, (amp <= 0 ? 1 : amp)), true);
				}
				i.setItemMeta(meta);
			}else
			if(type!=null) {
				SkullMeta m=(SkullMeta)i.getItemMeta();	
					if(owner!=null) {
						m.setOwner(owner);
					}else if (url != null || url ==null && text != null){
						GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
			        profile.getProperties().put("textures", new Property("textures",url == null && text != null ? text :  new String(encodedData)));
			        Field profileField = null;
			        try {
			            profileField = m.getClass().getDeclaredField("profile");
			            profileField.setAccessible(true);
			            profileField.set(m, profile);
			        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			        }}
					i.setItemMeta(m);
			}
		}catch(Exception err) {
			Error.err("creating ItemStack in ItemCreatorAPI", "Uknown Material/ItemStack");
		}
		a=i;
		return a;
	}
	
}
