package com.monetizationlib.data.ads;

import androidx.annotation.NonNull;

public enum FacebookCPMType {

    MAX {
        @NonNull
        @Override
        String getAdUnit(@NonNull AdDesignType adDesignType) {
            return calculateAdUnit(HIGH, adDesignType);
        }

        @Override
        String getTag() {
            return "max";
        }
    },
    HIGH {
        @NonNull
        @Override
        String getAdUnit(@NonNull AdDesignType adDesignType) {
            return calculateAdUnit(HIGH, adDesignType);
        }

        @Override
        String getTag() {
            return "high";
        }
    },
    MEDIUM {
        @NonNull
        @Override
        String getAdUnit(@NonNull AdDesignType adDesignType) {
            return calculateAdUnit(MEDIUM, adDesignType);
        }

        @Override
        String getTag() {
            return "medium";
        }
    },
    LOW_MEDIUM {
        @NonNull
        @Override
        String getAdUnit(@NonNull AdDesignType adDesignType) {
            return calculateAdUnit(MEDIUM, adDesignType);
        }

        @Override
        String getTag() {
            return "low-medium";
        }
    },
    LOW {
        @NonNull
        @Override
        String getAdUnit(@NonNull AdDesignType adDesignType) {
            return calculateAdUnit(LOW, adDesignType);
        }

        @Override
        String getTag() {
            return "low";
        }
    };

    @NonNull
    private static String calculateAdUnit(@NonNull FacebookCPMType cpmType, @NonNull AdDesignType adDesignType) {
        return "";
    }

    @NonNull
    abstract String getAdUnit(@NonNull AdDesignType adDesignType);

    abstract String getTag();
}
