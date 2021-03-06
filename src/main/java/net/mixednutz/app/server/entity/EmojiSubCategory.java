package net.mixednutz.app.server.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Emoji_Subcategory")
public class EmojiSubCategory {

	private Integer id;
	private String name;
	private EmojiCategory parentCategory;
	private List<Emoji> emoji;
	
	@Id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonIgnore
	@ManyToOne
	public EmojiCategory getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(EmojiCategory parentCategory) {
		this.parentCategory = parentCategory;
	}
	@JsonIgnore
	@OneToMany(mappedBy="subCategory", cascade=CascadeType.ALL, orphanRemoval=true)
	public List<Emoji> getEmoji() {
		return emoji;
	}
	public void setEmoji(List<Emoji> emoji) {
		this.emoji = emoji;
	}
	
}
