public final class Ansi {
    private Ansi() {}

    private static final String E = String.valueOf((char) 27);

    public static final String RESET    = E + "[0m";
    public static final String NEGRITO  = E + "[1m";
    public static final String CIANO    = E + "[36m";
    public static final String VERDE    = E + "[32m";
    public static final String AMARELO  = E + "[33m";
    public static final String VERMELHO = E + "[31m";
}
