package de.weidle.copilotagenticplayground.greeting.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    ENGLISH("Hello", "🇬🇧", "English"),
    GERMAN("Hallo", "🇩🇪", "Deutsch"),
    FRENCH("Bonjour", "🇫🇷", "Français"),
    KOREAN("안녕하세요", "🇰🇷", "한국어"),
    ITALIAN("Ciao", "🇮🇹", "Italiano"),
    SWABIAN("Grüaß di", "bw", "Schwäbisch");

    private final String greeting;
    private final String flag;
    private final String displayName;
}
