package com.sorasoft.dicecam.view.watermark;

public enum WaterMarkType {

	BUNDLE_WATERMARK("WAT001", "Designed by sorasoft"),
	PRIVATE_WATERMARK("WAT002", "Self Designed Watermark"),
	LICENSING_WATERMARK("WAT003", "Licensed Watermark like Hello kitty.");

	private String code;
	private String desc;

	WaterMarkType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}
}
