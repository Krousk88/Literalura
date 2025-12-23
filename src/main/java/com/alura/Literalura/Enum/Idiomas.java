package com.alura.Literalura.Enum;

public enum Idiomas {
    ESPAÑOL("es"),
    INGLES("en"),
    FRANCES("fr"),
    ALEMAN("de"),
    ITALIANO("it"),
    PORTUGUES("pt"),
    UNKNOWN("und");

    private final String codigo;
    Idiomas(String codigo) {
        this.codigo = codigo;
    }
    public String getCodigo() {
        return codigo;
    }

    public static Idiomas obtenerPorNombre(String codigo) {
        for (Idiomas idioma : Idiomas.values()) {
            if (idioma.name().equalsIgnoreCase(codigo) || idioma.getCodigo().equalsIgnoreCase(codigo)) {
                return idioma;
            }
        }
        return UNKNOWN;
    }
}
