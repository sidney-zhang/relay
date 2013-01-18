package com.exttech.security.domain;

import javax.persistence.Column;

public class Authority extends AbstractDomain {

	@Column(name = "authority_name")
	private String name;

	@Column(name = "authority_desc")
	private String desc;

	private boolean enabled;

	@Column(name = "issys")
	private boolean isSys;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSys() {
		return isSys;
	}

	public void setSys(boolean isSys) {
		this.isSys = isSys;
	}
}
