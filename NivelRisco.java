public enum NivelRisco {
    SEGURO ("SEGURO",  Ansi.VERDE),
    ATENCAO("ATENÇÃO", Ansi.AMARELO),
    CRITICO("CRÍTICO", Ansi.VERMELHO);

    private final String descricao;
    private final String cor;

    NivelRisco(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() { return descricao; }

    public String formatado() {
        return cor + "[" + descricao + "]" + Ansi.RESET;
    }
}
