package net.mixednutz.app.server.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="Emoji")
public class Emoji  {
	
	private static Pattern pattern = Pattern.compile("(U\\+)?([0-9A-F]+)");
	
	private String id;
	private Integer sortId;
	private String description;
	private EmojiSubCategory subCategory;
	
	
	@Column(name="emoji_id")
	@GeneratedValue(generator="system-native")
	@GenericGenerator(name="system-native", strategy = "native")
	public Integer getSortId() {
		return sortId;
	}
	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}
	@Id
	@Column(name="htmlEntity")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@ManyToOne()
	public EmojiSubCategory getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(EmojiSubCategory subCategory) {
		this.subCategory = subCategory;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String name) {
		this.description = name;
	}
	@Transient
	public String getHtmlCode() {
		return getHtmlCode(getId());
	}
	@Transient
	public String getText() {
		return getText(getId());
	}
	
	public static String getHtmlCode(String code) {
		if (code!=null) {
			StringBuffer buffer = new StringBuffer();
			for (String c: code.split(" ")) {
				Matcher matcher = pattern.matcher(c);
				if (matcher.matches()) {
					String group2 = matcher.group(2);
					buffer.append("&#x")
						.append(group2)
						.append(";");
				}
			}
			return buffer.toString();
		}
		return null;
	}
	
	public static String getText(String code) {
		if (code!=null) {
			String[] split = code.split(" ");
			int[] codepoints = new int[split.length];
			int i =0;
			for (String c: split) {
				Matcher matcher = pattern.matcher(c);
				if (matcher.matches()) {
					String group2 = matcher.group(2);
					int hex = Integer.parseInt(group2, 16);
					codepoints[i] = hex;
				}
				i++;
			}
			return new String(codepoints, 0, codepoints.length);
		}
		return null;
	}
	
}
