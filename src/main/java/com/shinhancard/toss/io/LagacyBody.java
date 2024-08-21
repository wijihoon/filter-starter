package com.shinhancard.toss.io;

import lombok.Getter;

@Getter
public class LagacyBody {
	private String content;

	public LagacyBody(String content) {
		this.content = content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "LagacyBody{content='" + content + "'}";
	}
}
