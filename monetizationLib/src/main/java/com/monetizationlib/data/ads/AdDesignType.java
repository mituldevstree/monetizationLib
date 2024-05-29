package com.monetizationlib.data.ads;

public enum AdDesignType {
    LARGE(AdType.Native), MEDIUM(AdType.Native), SMALL(AdType.Native), EXTRA_SMALL(AdType.Native);

    private AdType[] types;

    AdDesignType(AdType... types) {
        this.types = types;
    }

    public AdType[] getTypes() {
        return types;
    }
}
