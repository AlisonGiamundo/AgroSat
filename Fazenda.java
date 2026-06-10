public class Fazenda {

    private static int contadorId = 1;

    private final int id;
    private String nome;
    private String localizacao;
    private Cultura cultura;

    public Fazenda(String nome, String localizacao, Cultura cultura) {
        this.id = contadorId++;
        this.nome = nome;
        this.localizacao = localizacao;
        this.cultura = cultura;
    }

    public int getId()             { return id; }
    public String getNome()        { return nome; }
    public String getLocalizacao() { return localizacao; }
    public Cultura getCultura()    { return cultura; }

    public void setNome(String nome)               { this.nome = nome; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public void setCultura(Cultura cultura)        { this.cultura = cultura; }

    @Override
    public String toString() {
        return String.format("[ID:%d] %s | %s | Cultura: %s", id, nome, localizacao, cultura);
    }
}
