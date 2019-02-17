package com.abc.docs.domain;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
//@Entity
//@Table(name = "jhi_category")
public class Category {
	
//	@Column(name = "id_category")
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcategory;
	
	private String name;
	
	public Category(String name){	
		this.name=name;
	}
	
	public int getIdcategory() {
		return idcategory;
	}
	public void setIdcategory(int idcategory) {
		this.idcategory = idcategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Category [idcategory=" + idcategory + ", name=" + name
				+ "]";
	}
	

}
