package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;

public class YamlLoader implements DataLoader {
	private static final Pattern pattern = Pattern.compile("(.*?)+:*");
	private MultiMap<String, String, DataHolder> data = new MultiMap<>();
	private boolean l;
	private List<String> header = new ArrayList<>(1), footer = new ArrayList<>(1);
	
	@Override
	public MultiMap<String, String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		data.clear();
		header.clear();
		footer.clear();
		List<Object> items = new ArrayList<>(1);
		List<String> lines = new ArrayList<>(1);
		String key = "";
		StringBuilder v = new StringBuilder();
		int last = 0, f=0, c = 0;
		for(String text : input.split(System.lineSeparator())) {
			if(text.trim().startsWith("#")||c==0 && text.trim().isEmpty()) {
				if(c!=0) {
					if(c==1) {
						set(key, Maker.objectFromJson(v.toString()), lines);
						v=new StringBuilder();
						}else
						if(c==2) {
							set(key, items, lines);
							items=new ArrayList<>(1);
						}
						c=0;
				}
				if(f==0)
					header.add(text.replaceFirst(cs(c(text),0), ""));
				else
				lines.add(text.replaceFirst(cs(c(text),0), ""));
				continue;
			}
			if(c!=0 && text.contains(":") && pattern.matcher(text).find()) {
				if(c==1) {
				set(key, Maker.objectFromJson(v.toString()), lines);
				v=new StringBuilder();
				}
				if(c==2) {
					set(key, items, lines);
					items=new ArrayList<>(1);
				}
				c=0;
			}
			if(c==2 || text.replaceFirst(cs(c(text),0), "").startsWith("- ") && !key.equals("")) {
				items.add(Maker.objectFromJson(c!=2?text.replaceFirst(text.split("- ")[0]+"- ", ""):text.replaceFirst(cs(c(text),0),"")));
			}else {
				if(!items.isEmpty()) {
					set(key, items, lines);
					items=new ArrayList<>(1);
				}
				if(c==1) {
					v.append(text.replaceFirst(cs(c(text),0),""));
					continue;
				}
				if(c(text.split(":")[0]) <= last) {
					if(!text.startsWith(" "))key="";
					if(c(text.split(":")[0]) == last) {
						String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
						int remove = key.length()-lastr.length();
						if(remove > 0)
						key=key.substring(0, remove);
					}else {
					for(int i = 0; i < Math.abs(last-c(text.split(":")[0])); ++i) {
					String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
					int remove = key.length()-lastr.length();
					if(remove < 0)break;
					key=key.substring(0, remove);
				}}}
				key+=(key.equals("")?"":".")+text.split(":")[0].trim();
				f=1;
				last=c(text.split(":")[0]);
				if(!text.replaceFirst(text.split(":")[0]+":", "").trim().isEmpty()) {
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|")) {
						c=1;
						continue;
					}
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|-")) {
						c=2;
						continue;
					}
					set(key, Maker.objectFromJson(text.replaceFirst(text.split(":")[0]+": ", "")), lines);
				}
			}
		}
		if(!items.isEmpty()||c==2) {
			set(key, items, lines);
		}else
		if(c==1) {
			set(key, Maker.objectFromJson(v.toString()), lines);
		}else
		if(!lines.isEmpty())
			footer=lines;
		l=true;
	}

	@Override
	public List<String> getHeader() {
		return header;
	}

	@Override
	public List<String> getFooter() {
		return footer;
	}

	//Other
	private final String cs(int s, int doubleSpace) {
		String i = "";
		String space = doubleSpace==1?"  ":" ";
		for(int c = 0; c < s; ++c)
			i+=space;
		return i;
	}
	
	private final int c(String s) {
		int i = 0;
		for(char c : s.toCharArray())
			if(c==' ')++i;
			else break;
		return i;
	}
	
	private final void set(String key, Object o, List<String> lines) {
		data.put(key.split("\\.")[0], key, new DataHolder(o, new ArrayList<>(lines)));
		lines.clear();
	}

	@Override
	public boolean loaded() {
		return l;
	}
}
