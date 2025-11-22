package com._oormthonUNIV.newnew.user.entity.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserGeneration {
    SILENT(1928, 1945),
    BABY_BOOMER(1946, 1964),
    GEN_X(1965, 1980),
    MILLENNIAL(1981, 1996),   // MZ 중 M
    GEN_Z(1997, 2012),        // Z세대
    GEN_ALPHA(2013, 2025);    // 알파세대 (현재 진행 중)

    private final int startYear;
    private final int endYear;

    public boolean includes(int birthYear) {
        return birthYear >= startYear && birthYear <= endYear;
    }

    public static UserGeneration fromBirthYear(int birthYear) {
        for (UserGeneration g : values()) {
            if (g.includes(birthYear)) return g;
        }
        throw new IllegalArgumentException("Unknown generation for " + birthYear);
    }
}
