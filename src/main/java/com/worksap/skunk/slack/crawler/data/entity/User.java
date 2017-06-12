package com.worksap.skunk.slack.crawler.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class User {
	@Id
	private String id;
	
	@Column(nullable = false)
	private String name;
	
	private String realName;
}
