import java.util.Random;

public class Monitoramento {

    private static final String[] PREVISOES = {
        "Ensolarado", "Parcialmente Nublado", "Chuvas Esparsas",
        "Tempestade", "Seco e Quente", "Ventos Fortes", "Névoa"
    };

    private final Fazenda fazenda;
    private final double temperatura;  // °C
    private final int umidade;         // %
    private final int chuvaMm;         // mm/dia simulado
    private final String previsao;
    private final NivelRisco nivelRisco;

    public Monitoramento(Fazenda fazenda) {
        this.fazenda = fazenda;
        Random rand = new Random();
        this.temperatura = 15 + rand.nextDouble() * 25;
        this.umidade     = 20 + rand.nextInt(71);
        this.chuvaMm     = rand.nextInt(101);
        this.previsao    = PREVISOES[rand.nextInt(PREVISOES.length)];
        this.nivelRisco  = calcularRisco();
    }

    // Seca: umidade baixa + temperatura alta + pouca chuva → CRÍTICO
    // Excesso de chuva: acima de 80mm → CRÍTICO
    private NivelRisco calcularRisco() {
        boolean seco    = umidade < 35 && temperatura > 32 && chuvaMm < 10;
        boolean critico = chuvaMm > 80 || temperatura > 38;

        if (seco || critico) return NivelRisco.CRITICO;
        if (umidade < 50 || chuvaMm > 55 || temperatura > 34) return NivelRisco.ATENCAO;
        return NivelRisco.SEGURO;
    }

    public String getRecomendacao() {
        Cultura.StatusPlantacao status = fazenda.getCultura().getStatus();

        if (nivelRisco == NivelRisco.CRITICO && umidade < 35)
            return Ansi.VERMELHO + "IRRIGAÇÃO DE EMERGÊNCIA recomendada! Solo com déficit hídrico crítico." + Ansi.RESET;
        if (nivelRisco == NivelRisco.CRITICO && chuvaMm > 80)
            return Ansi.VERMELHO + "DRENAGEM URGENTE! Excesso de chuva pode provocar alagamento e perda da safra." + Ansi.RESET;
        if (nivelRisco == NivelRisco.SEGURO && status == Cultura.StatusPlantacao.PRONTA_PARA_COLHEITA)
            return Ansi.VERDE + "CONDIÇÕES IDEAIS PARA COLHEITA. Aproveite a janela climática favorável!" + Ansi.RESET;
        if (nivelRisco == NivelRisco.ATENCAO)
            return Ansi.AMARELO + "MEDIDAS DE PREVENÇÃO recomendadas. Monitore a lavoura nas próximas 24h." + Ansi.RESET;

        return Ansi.VERDE + "Condições estáveis. Mantenha o monitoramento regular da propriedade." + Ansi.RESET;
    }

    public NivelRisco getNivelRisco() { return nivelRisco; }
    public Fazenda getFazenda()       { return fazenda; }

    @Override
    public String toString() {
        return String.format(
            "  Temperatura : %.1f °C%n" +
            "  Umidade     : %d %%%n" +
            "  Chuva (sim.): %d mm/dia%n" +
            "  Previsão    : %s%n" +
            "  Risco       : %s",
            temperatura, umidade, chuvaMm, previsao, nivelRisco.formatado()
        );
    }
}
