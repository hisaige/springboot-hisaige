package com.hisaige.core.test.pojo;

import java.io.Serializable;

public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String address;
	private String sex;
	private Integer age;
	
	
	public Person(Integer id, String name, String address, String sex, Integer age) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.sex = sex;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Person [id=" + id + ", userName=" + name + ", address=" + address + ", sex=" + sex + ", age=" + age + "]";
	}
}
