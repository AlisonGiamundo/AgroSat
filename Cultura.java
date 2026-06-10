public class Cultura {

    public enum Tipo {
        MILHO, SOJA, TRIGO, CAFE, CANA_DE_ACUCAR, ALGODAO, OUTRO
    }

    public enum StatusPlantacao {
        EM_CRESCIMENTO("Em Crescimento"),
        PRONTA_PARA_COLHEITA("Pronta para Colheita"),
        DANIFICADA("Danificada"),
        PLANTADA("Recém Plantada");

        private final String descricao;
        StatusPlantacao(String descricao) { this.descricao = descricao; }
        public String getDescricao() { return descricao; }
    }

    private Tipo tipo;
    private StatusPlantacao status;

    public Cultura(Tipo tipo, StatusPlantacao status) {
        this.tipo = tipo;
        this.status = status;
    }

    public Tipo getTipo()               { return tipo; }
    public StatusPlantacao getStatus()  { return status; }
    public void setStatus(StatusPlantacao status) { this.status = status; }

    @Override
    public String toString() {
        return tipo.name().replace("_", " ") + " (" + status.getDescricao() + ")";
    }
}
