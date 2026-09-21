package dev.iurygabriel.vagamatch.jobs.domain;

/** Nível de senioridade da vaga (ordenado do menor para o maior). */
public enum Seniority {
    INTERN,
    JUNIOR,
    MID,
    SENIOR,
    LEAD;

    /** Inferência heurística de senioridade a partir do título da vaga. */
    public static Seniority fromTitle(String title) {
        if (title == null || title.isBlank()) {
            return MID;
        }
        String t = title.toLowerCase();
        if (containsAny(t, "intern", "estag", "estági", "trainee")) {
            return INTERN;
        }
        if (containsAny(t, "tech lead", "team lead", "principal", "staff", " lead", "lead ")) {
            return LEAD;
        }
        if (containsAny(t, "senior", "sênior", "sr.", "sr ", " sr", "specialist", "especialista")) {
            return SENIOR;
        }
        if (containsAny(t, "junior", "júnior", "jr.", "jr ", " jr", "entry")) {
            return JUNIOR;
        }
        if (containsAny(t, "pleno", "mid", "intermediate", "middle")) {
            return MID;
        }
        return MID;
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
