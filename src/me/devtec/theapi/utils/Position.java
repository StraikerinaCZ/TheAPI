package me.devtec.theapi.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class Position implements Cloneable {

	public Position() {
	}

	public Position(World world) {
		w = world.getName();
	}

	public Position(String world) {
		w = world;
	}

	public Position(World world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(World world, double x, double y, double z, float yaw, float pitch) {
		w = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Position(String world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}

	public Position(String world, double x, double y, double z, float yaw, float pitch) {
		w = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Position(Location location) {
		w = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	public Position(Block b) {
		this(b.getLocation());
	}

	private String w;
	private double x, y, z;
	private float yaw, pitch;

	@Override
	public String toString() {
		return ("[Position:" + w + "/" + x + "/" + y + "/" + z + "/" + yaw + "/" + pitch + "]").replace(".", ":");
	}

	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + w.hashCode();
		hashCode = (int) (31 * hashCode + x);
		hashCode = (int) (31 * hashCode + y);
		hashCode = (int) (31 * hashCode + z);
		hashCode = (int) (31 * hashCode + yaw);
		hashCode = (int) (31 * hashCode + pitch);
		return hashCode;
	}

	public Biome getBiome() {
		return getBlock().getBiome();
	}

	public int getData() {
		return getType().getData();
	}

	public Material getBukkitType() {
		return getType().getType();
	}

	private static Method getter, getdata;
	static {
		if(TheAPI.isOlderThan(8)) {
			getdata=Ref.method(Ref.nms("Chunk"), "getData", int.class, int.class, int.class);
			getter = Ref.method(Ref.nms("Chunk"), "getType", int.class, int.class, int.class);
		}else
		getter = Ref.method(Ref.nms("Chunk"), "getBlockData", Ref.nms("BlockPosition"));
	}
	
	public TheMaterial getType() {
		if(TheAPI.isOlderThan(8)) //1.7.10
			return TheMaterial.fromData(Ref.invoke(getNMSChunk(), getter, (int)x&0xF, (int)y, (int)z&0xF), (byte)Ref.invoke(getNMSChunk(), getdata, (int)x & 0xF, (int)y & 0xFF, (int)z & 0xF));
		return TheMaterial.fromData(Ref.invoke(getNMSChunk(), getter, getBlockPosition()));
	}

	public Position subtract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Position subtract(Position position) {
		this.x -= position.getX();
		this.y -= position.getY();
		this.z -= position.getZ();
		yaw -= position.getYaw();
		pitch -= position.getPitch();
		return this;
	}

	public Position subtract(Location location) {
		this.x -= location.getX();
		this.y -= location.getY();
		this.z -= location.getZ();
		yaw -= location.getYaw();
		pitch -= location.getPitch();
		return this;
	}

	public String getWorldName() {
		return w;
	}

	public void setWorld(World world) {
		w = world.getName();
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public static Position fromString(String stored) {
		if (stored.startsWith("[Position:")) {
			stored = stored.substring(0, stored.length() - 1).replaceFirst("\\[Position:", "");
			String[] part = stored.replace(":", ".").split("/");
			return new Position(part[0], StringUtils.getDouble(part[1]), StringUtils.getDouble(part[2]),
					StringUtils.getDouble(part[3]), StringUtils.getFloat(part[4]), StringUtils.getFloat(part[5]));
		}
		Location loc = StringUtils.getLocationFromString(stored);
		if (loc != null)
			return new Position(loc);
		return null;
	}

	public double distance(Location location) {
		return Math.sqrt(distanceSquared(location));
	}

	public double distance(Position position) {
		return Math.sqrt(distanceSquared(position));
	}

	public Position multiply(double m) {
		x *= m;
		y *= m;
		z *= m;
		return this;
	}

	public Position zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public double lengthSquared() {
		return square(x) + square(y) + square(z);
	}

	public double distanceSquared(Location location) {
		return square(this.x - location.getX()) + square(this.y - location.getY()) + square(this.z - location.getZ());
	}

	public double distanceSquared(Position position) {
		return square(this.x - position.x) + square(this.y - position.y) + square(this.z - position.z);
	}

	private double square(double d) {
		return d * d;
	}

	public Chunk getChunk() {
		return (Chunk) Ref.get(getNMSChunk(), "bukkitChunk");
	}

	private static int wf = StringUtils.getInt(TheAPI.getServerVersion().split("_")[1]);

	public Object getNMSChunk() {
		try {
			return Ref.handle(
					Ref.cast(Ref.craft("CraftChunk"), getWorld().getChunkAt(getBlockX() >> 4, getBlockZ() >> 4)));
		} catch (Exception er) {
			return Ref.invoke(
					Ref.get(Ref.cast(Ref.nms("ChunkProviderServer"),
							Ref.invoke(Ref.world(getWorld()), "getChunkProvider")), "chunkGenerator"),
					Ref.method(Ref.nms("ChunkGenerator"), "getOrCreateChunk", int.class, int.class), getBlockX() >> 4,
					getBlockZ() >> 4);
		}
	}
	
	public Object getBlockPosition() {
		return (wf <= 7 ? Ref.newInstance(old, (int) x, (int) y, (int) z) : Ref.blockPos((int) x, (int) y, (int) z));
	}

	public ChunkSnapshot getChunkSnapshot() {
		return getChunk().getChunkSnapshot();
	}

	public Block getBlock() {
		return getWorld().getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}

	public World getWorld() {
		return Bukkit.getWorld(w);
	}

	public Position add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Position add(Position position) {
		this.x += position.getX();
		this.y += position.getY();
		this.z += position.getZ();
		return this;
	}

	public Position add(Location location) {
		this.x += location.getX();
		this.y += location.getY();
		this.z += location.getZ();
		return this;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockX() {
		return (int) x;
	}

	public int getBlockY() {
		return (int) y;
	}

	public int getBlockZ() {
		return (int) z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Location toLocation() {
		return new Location(Bukkit.getWorld(w), x, y, z, yaw, pitch);
	}

	public long setType(Material with) {
		return setType(with, 0);
	}

	public long setType(TheMaterial with) {
		return set(this, with);
	}

	public long setType(Material with, int data) {
		return setType(new TheMaterial(with, data));
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof Position) {
			Position s = (Position) a;
			return w.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y && s.getZ() == z
					&& s.getPitch() == pitch && s.getYaw() == yaw;
		}
		if (a instanceof Location) {
			Location s = (Location) a;
			return w.equals(s.getWorld().getName()) && s.getX() == x && s.getY() == y && s.getZ() == z
					&& s.getPitch() == pitch && s.getYaw() == yaw;
		}
		return false;
	}

	private static Constructor<?> c;
	private static int t;
	static {
		c=Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("IBlockAccess"), Ref.nms("BlockPosition"));
		if(c==null) {
			c=Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), Ref.nms("World"), Ref.nms("BlockPosition"));
			if(c==null) {
				c=Ref.constructor(Ref.nms("PacketPlayOutBlockChange"), int.class, int.class, int.class, Ref.nms("World"));
				t=1;
			}
		}
	}
	
	public static void updateBlockAt(Position pos, Object oldBlock) {
		NMSAPI.refleshBlock(pos, oldBlock);
		Object packet = t==0?Ref.newInstance(c, Ref.world(pos.getWorld()), pos.getBlockPosition()):Ref.newInstance(c, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), Ref.world(pos.getWorld()));
		for (Player p : TheAPI.getOnlinePlayers())
			Ref.sendPacket(p, packet);
	}

	private static boolean aww = TheAPI.isOlderThan(8);

	public static void updateLightAt(Position pos) {
		if (aww)
			Ref.invoke(pos.getNMSChunk(), "initLighting");
		else
			Ref.invoke(Ref.invoke(pos.getNMSChunk(), "e"), "a", pos.getBlockPosition());
	}

	public static long set(Position pos, TheMaterial mat) {
		if (wf <= 7)
			setOld(pos, mat.getType().getId(), mat.getData());
		else
			set(pos, wf >= 9, wf >= 14, mat.getIBlockData());
		return pos.getChunkKey();
	}

	public long getChunkKey() {
		long k = (getBlockX() >> 4 & 0xFFFF0000L) << 16L | (getBlockX() >> 4 & 0xFFFFL) << 0L;
		k |= (getBlockZ() >> 4 & 0xFFFF0000L) << 32L | (getBlockZ() >> 4 & 0xFFFFL) << 16L;
		return k;
	}

	public static long set(Location pos, int id, int data) {
		return set(new Position(pos), new TheMaterial(id, data));
	}

	/**
	 * 
	 * @param pos Location
	 * @param id  int id of Material
	 * @return long ChunkKey
	 */
	private static synchronized void setOld(Position pos, int id, int data) { // Uknown - 1.7.10
		Object c = pos.getNMSChunk();
		Object sc = ((Object[]) Ref.invoke(c, get))[pos.getBlockY() >> 4];
		if (sc == null) {
			sc = Ref.newInstance(aw,pos.getBlockY() >> 4 << 4, true);
			((Object[]) Ref.invoke(c, "getSections"))[pos.getBlockY() >> 4] = sc;
		}
		Ref.invoke(sc, setId, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, id);
		Ref.invoke(sc, setData, pos.getBlockX() & 0xF, pos.getBlockY() & 0xF, pos.getBlockZ() & 0xF, data);
	}

	private static Constructor<?> aw = Ref.constructor(Ref.nms("ChunkSection"), int.class),
			old = Ref.constructor(Ref.nms("Position"), double.class, double.class, double.class);
	private static Method a, get = Ref.method(Ref.nms("Chunk"), "getSections"), setId, setData;
	static {
		a = Ref.method(Ref.nms("ChunkSection"), "setType", int.class, int.class, int.class, Ref.nms("IBlockData"), boolean.class);
		if(a==null)
			a = Ref.method(Ref.nms("ChunkSection"), "setType", int.class, int.class, int.class, Ref.nms("IBlockData"));
		if (aw == null)
			aw = Ref.constructor(Ref.nms("ChunkSection"), int.class, boolean.class);
		if(TheAPI.isNewerThan(8)) {
			setId=Ref.method(Ref.nms("ChunkSection"), "setTypeId", int.class, int.class, int.class, Ref.nms("Block"));
			setData=Ref.method(Ref.nms("ChunkSection"), "setData", int.class, int.class, int.class, int.class);
		}
	}

	/**
	 * 
	 * @param pos   Location
	 * @param palet Is server version newer than 1.8? 1.9+
	 * @param neww  Is server version newer than 1.13? 1.14+
	 * @param id    int id of Material
	 * @param data  int data of Material
	 * @return long ChunkKey
	 */
	public static synchronized void set(Position pos, boolean palet, boolean neww, Object cr) { // 1.8 - 1.16
		Object c = pos.getNMSChunk();
		int y = pos.getBlockY();
		Object sc = ((Object[]) Ref.invoke(c, get))[y >> 4];
		if (sc == null) {
			if (neww)
				sc = Ref.newInstance(aw, y >> 4 << 4);
			else
				sc = Ref.newInstance(aw, y >> 4 << 4, true);
			((Object[]) Ref.invoke(c, get))[y >> 4] = sc;
		}
		if (palet)
			Ref.invoke(sc, a, pos.getBlockX() & 0xF, y & 0xF, pos.getBlockZ() & 0xF, cr, false);
		else
			Ref.invoke(sc, a, pos.getBlockX() & 0xF, y & 0xF, pos.getBlockZ() & 0xF, cr);
	}

	public Position clone() {
		return new Position(w, x, y, z, yaw, pitch);
	}
}
