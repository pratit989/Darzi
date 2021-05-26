package com.infinityandriod.darzi;

import androidx.annotation.NonNull;

public class CommonMethods {
    @NonNull
    public static String toTitleCase(@NonNull String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
