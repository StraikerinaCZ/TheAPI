package me.Straiker123;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflections {

	public static Object cast(Class<?> object, Object toCast) {
		try {
			return object.cast(toCast);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethod(Class<?> main, String name, Class<?> parameter) {
		try {
			return main.getMethod(name, parameter);
		} catch (Exception e) {
			try {
				return main.getDeclaredMethod(name, parameter);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	public static Method getMethod(Class<?> main, String name, Class<?>... bricks) {
		try {
			return main.getMethod(name, bricks);
		} catch (Exception e) {
			try {
				return main.getDeclaredMethod(name, bricks);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	public static Method getMethod(Class<?> main, String name) {
		try {
			return main.getMethod(name);
		} catch (Exception e) {
			try {
				return main.getDeclaredMethod(name);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	public static Object c(Constructor<?> c, Object... items) {
		try {
			return c.newInstance(items);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Constructor<?> getConstructor(Class<?> main, Class<?>... bricks) {
		try {
			return main.getConstructor(bricks);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void processMethod(Object main, String method, Object value, Object value2, Object value3,
			Object value4) {
		try {
			main.getClass().getMethod(method, value.getClass(), value2.getClass(), value3.getClass(), value4.getClass())
					.invoke(main, value, value2, value3, value4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processMethod(Object main, String method, Object value, Object value2, Object value3) {
		try {
			main.getClass().getMethod(method, value.getClass(), value2.getClass(), value3.getClass()).invoke(main,
					value, value2, value3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processMethod(Object main, String method, Object value, Object value2) {
		try {
			main.getClass().getMethod(method, value.getClass(), value2.getClass()).invoke(main, value, value2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processMethod(Object main, String method, Object value) {
		try {
			main.getClass().getMethod(method, value.getClass()).invoke(main, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setField(Object main, String field, Object value) {
		Field f = getField(main.getClass(), field);
		try {
			f.set(main, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Field getField(Class<?> main, String name) {
		try {
			Field f = main.getField(name);
			f.setAccessible(true);
			return f;
		} catch (Exception e) {
			try {
				Field f = main.getDeclaredField(name);
				f.setAccessible(true);
				return f;
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}

	public static Object get(Object main, String field) {
		try {
			Field f = getField(main.getClass(), field);
			if (f != null) {
				f.setAccessible(true);
				return f.get(main);
			} else {
				Method m = main.getClass().getMethod(field);
				if (m == null)
					m = main.getClass().getDeclaredMethod(field);
				if (m != null)
					return m.invoke(main);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getNMSClass(String name) {
		return getClass("net.minecraft.server." + TheAPI.getServerVersion() + "." + name);
	}

	public static Class<?> getBukkitClass(String name) {
		return getClass("org.bukkit.craftbukkit." + TheAPI.getServerVersion() + "." + name);
	}

	public static Class<?> getClass(String name) {
		if (existsClass(name))
			try {
				return Class.forName(name);
			} catch (ClassNotFoundException e) {
			}
		return null;
	}

	public static boolean existNMSClass(String name) {
		try {
			Class.forName("net.minecraft.server." + TheAPI.getServerVersion() + "." + name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean existBukkitClass(String name) {
		try {
			Class.forName("org.bukkit.craftbukkit." + TheAPI.getServerVersion() + "." + name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean existsClass(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Object invoke(Object object, Method method, Object... items) {
		try {
			return method.invoke(object, items);
		} catch (Exception error) {
			return null;
		}
	}
}
