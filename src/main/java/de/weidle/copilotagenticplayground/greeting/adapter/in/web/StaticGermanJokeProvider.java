package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class StaticGermanJokeProvider implements JokeProvider {

    static final List<String> JOKES =
            List.of(
                    "Warum können Geister so schlecht lügen? Weil man durch sie hindurchsehen kann.",
                    "Was ist ein Keks unter einem Baum? Ein schattiges Plätzchen.",
                    "Warum hat der Mathematiker den Teppich aufgerollt? Er wollte ihn ausrechnen.",
                    "Was macht ein Pirat am Computer? Er drückt die Eingabe-Taste.",
                    "Warum nimmt der Pilz immer an Partys teil? Er ist ein echter Spaßpilz!",
                    "Was sagt ein Elektriker, wenn er ein neues Buch kauft? Ich muss es erst überfliegen – oder überstrom.",
                    "Warum hat der Scarecrow einen Preis gewonnen? Weil er auf seinem Gebiet herausragend war.",
                    "Was bekommt man, wenn man eine Kuh und ein Erdbeben kreuzt? Ein Milchshake.",
                    "Warum können Fahrräder nicht alleine stehen? Weil sie zwei-müde sind.",
                    "Was sagt ein Informatiker, wenn er Hunger hat? Ich brauche mehr Bytes.",
                    "Warum hat der Kalender so viele Freunde? Weil seine Tage gezählt sind.",
                    "Was ist der Unterschied zwischen einem Vogel und einer Fliege? Ein Vogel kann fliegen, aber eine Fliege kann nicht vogeln.");

    public StaticGermanJokeProvider() {
        if (JOKES.isEmpty()) {
            throw new IllegalStateException("Joke pool must not be empty");
        }
    }

    @Override
    public String randomJoke() {
        return JOKES.get(ThreadLocalRandom.current().nextInt(JOKES.size()));
    }
}
