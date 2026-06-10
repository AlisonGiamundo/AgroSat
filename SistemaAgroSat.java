import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemaAgroSat {

    private final List<Fazenda> fazendas = new ArrayList<>();
    private final List<String> tiposCulturaExtras = new ArrayList<>();
    private final Scanner scanner;

    public SistemaAgroSat(Scanner scanner) {
        this.scanner = scanner;
        carregarDadosDemostracao();
    }

    private void carregarDadosDemostracao() {
        fazendas.add(new Fazenda("Fazenda Santa Fé",    "Ribeirão Preto - SP",
                new Cultura(Cultura.Tipo.SOJA,  Cultura.StatusPlantacao.EM_CRESCIMENTO)));
        fazendas.add(new Fazenda("Sítio Boa Esperança", "Uberaba - MG",
                new Cultura(Cultura.Tipo.MILHO, Cultura.StatusPlantacao.PRONTA_PARA_COLHEITA)));
    }

    public void executar() {
        int opcao = -1;
        do {
            exibirMenuPrincipal();
            opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> cadastrarFazenda();
                case 2 -> cadastrarCultura();
                case 3 -> consultarClima();
                case 4 -> exibirAlertas();
                case 5 -> exibirRecomendacoes();
                case 6 -> listarFazendas();
                case 7 -> atualizarFazenda();
                case 0 -> System.out.println(Ansi.VERDE + "\nEncerrando o AgroSat. Boas colheitas!" + Ansi.RESET);
                default -> System.out.println(Ansi.AMARELO + "Opção inválida." + Ansi.RESET);
            }
        } while (opcao != 0);
    }

    // Borda interna: 38 chars. Textos centralizados manualmente (sem emoji para evitar desalinhamento).
    private void exibirMenuPrincipal() {
        System.out.println();
        System.out.println(Ansi.CIANO + Ansi.NEGRITO + "╔══════════════════════════════════════╗" + Ansi.RESET);
        System.out.println(Ansi.CIANO + Ansi.NEGRITO + "║            A G R O S A T             ║" + Ansi.RESET);
        System.out.println(Ansi.CIANO + Ansi.NEGRITO + "║  Monitoramento Agrícola por Satélite ║" + Ansi.RESET);
        System.out.println(Ansi.CIANO + Ansi.NEGRITO + "╚══════════════════════════════════════╝" + Ansi.RESET);
        System.out.println("  [1] Cadastrar Fazenda");
        System.out.println("  [2] Cadastrar Cultura");
        System.out.println("  [3] Consulta Climática");
        System.out.println("  [4] Alertas de Seca / Chuva");
        System.out.println("  [5] Recomendações (IA Simulada)");
        System.out.println("  [6] Listar Propriedades");
        System.out.println("  [7] Atualizar Dados de Fazenda");
        System.out.println("  [0] Sair");
        System.out.println("─".repeat(40));
    }

    private void cadastrarFazenda() {
        cabecalho("CADASTRO DE FAZENDA");
        System.out.print("Nome da fazenda   : ");
        String nome = scanner.nextLine().trim();
        System.out.print("Localização       : ");
        String local = scanner.nextLine().trim();

        Cultura cultura = selecionarCultura();
        Cultura.StatusPlantacao status = selecionarStatusPlantacao();
        cultura = new Cultura(cultura.getTipo(), status);

        Fazenda nova = new Fazenda(nome, local, cultura);
        fazendas.add(nova);
        System.out.println(Ansi.VERDE + "\n✔ Fazenda cadastrada com sucesso! " + nova + Ansi.RESET);
    }

    private void cadastrarCultura() {
        cabecalho("CADASTRO DE CULTURA");
        System.out.print("Nome da nova cultura (ex: Girassol): ");
        String nome = scanner.nextLine().trim();
        if (nome.isBlank()) {
            System.out.println(Ansi.AMARELO + "Nome inválido." + Ansi.RESET);
            return;
        }
        tiposCulturaExtras.add(nome.toUpperCase());
        System.out.println(Ansi.VERDE + "✔ Cultura \"" + nome + "\" registrada para uso futuro." + Ansi.RESET);
        System.out.println("  Culturas extras disponíveis: " + tiposCulturaExtras);
    }

    private void consultarClima() {
        cabecalho("CONSULTA CLIMÁTICA (DADOS DE SATÉLITE)");
        Fazenda fazenda = selecionarFazenda();
        if (fazenda == null) return;

        Monitoramento m = new Monitoramento(fazenda);
        System.out.println("\n  Fazenda    : " + fazenda.getNome());
        System.out.println("  Localização: " + fazenda.getLocalizacao());
        System.out.println(m);
    }

    private void exibirAlertas() {
        cabecalho("ALERTAS DE SECA / CHUVA");
        Fazenda fazenda = selecionarFazenda();
        if (fazenda == null) return;

        Monitoramento m = new Monitoramento(fazenda);
        System.out.println(m);

        NivelRisco risco = m.getNivelRisco();
        System.out.println("\n" + "─".repeat(40));
        System.out.println("  ALERTA: " + risco.formatado());
        switch (risco) {
            case SEGURO  -> System.out.println(Ansi.VERDE    + "  Condições climáticas favoráveis para a lavoura." + Ansi.RESET);
            case ATENCAO -> System.out.println(Ansi.AMARELO  + "  Variações climáticas detectadas. Fique alerta!"  + Ansi.RESET);
            case CRITICO -> System.out.println(Ansi.VERMELHO + "  SITUAÇÃO CRÍTICA! Intervenção imediata necessária." + Ansi.RESET);
        }
    }

    private void exibirRecomendacoes() {
        cabecalho("IA SIMULADA — RECOMENDAÇÕES");
        Fazenda fazenda = selecionarFazenda();
        if (fazenda == null) return;

        Monitoramento m = new Monitoramento(fazenda);
        System.out.println(m);
        System.out.println("\n" + "─".repeat(40));
        System.out.println("  Risco      : " + m.getNivelRisco().formatado());
        System.out.println("  Cultura    : " + fazenda.getCultura());
        System.out.println("\n  " + Ansi.NEGRITO + "Recomendação:" + Ansi.RESET);
        System.out.println("  → " + m.getRecomendacao());
    }

    private void listarFazendas() {
        cabecalho("PROPRIEDADES CADASTRADAS");
        if (fazendas.isEmpty()) {
            System.out.println(Ansi.AMARELO + "Nenhuma fazenda cadastrada ainda." + Ansi.RESET);
            return;
        }
        for (Fazenda f : fazendas) {
            System.out.println("  " + f);
        }
        System.out.println("\n  Total: " + fazendas.size() + " propriedade(s).");
    }

    private void atualizarFazenda() {
        cabecalho("ATUALIZAÇÃO DE DADOS");
        Fazenda fazenda = selecionarFazenda();
        if (fazenda == null) return;

        System.out.println("\n  Dados atuais: " + fazenda);
        System.out.println("  (Pressione ENTER para manter o valor atual)");

        System.out.print("Novo nome [" + fazenda.getNome() + "]: ");
        String nome = scanner.nextLine().trim();
        if (!nome.isBlank()) fazenda.setNome(nome);

        System.out.print("Nova localização [" + fazenda.getLocalizacao() + "]: ");
        String local = scanner.nextLine().trim();
        if (!local.isBlank()) fazenda.setLocalizacao(local);

        System.out.print("Alterar cultura? (s/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            Cultura novaCultura = selecionarCultura();
            Cultura.StatusPlantacao novoStatus = selecionarStatusPlantacao();
            fazenda.setCultura(new Cultura(novaCultura.getTipo(), novoStatus));
        }

        System.out.println(Ansi.VERDE + "\n✔ Fazenda atualizada: " + fazenda + Ansi.RESET);
    }

    private Fazenda selecionarFazenda() {
        if (fazendas.isEmpty()) {
            System.out.println(Ansi.AMARELO + "Nenhuma fazenda cadastrada. Use a opção [1] primeiro." + Ansi.RESET);
            return null;
        }
        System.out.println("\n  Selecione a fazenda:");
        for (int i = 0; i < fazendas.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, fazendas.get(i).getNome());
        }
        int idx = lerInteiro("Opção: ") - 1;
        if (idx < 0 || idx >= fazendas.size()) {
            System.out.println(Ansi.AMARELO + "Seleção inválida." + Ansi.RESET);
            return null;
        }
        return fazendas.get(idx);
    }

    private Cultura selecionarCultura() {
        Cultura.Tipo[] tipos = Cultura.Tipo.values();
        System.out.println("\n  Tipos de cultura:");
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, tipos[i].name().replace("_", " "));
        }
        if (!tiposCulturaExtras.isEmpty()) {
            System.out.println("  (Culturas extras: " + tiposCulturaExtras + " — use OUTRO)");
        }
        int idx = lerInteiro("Opção: ") - 1;
        Cultura.Tipo tipo = (idx >= 0 && idx < tipos.length) ? tipos[idx] : Cultura.Tipo.OUTRO;
        return new Cultura(tipo, Cultura.StatusPlantacao.PLANTADA);
    }

    private Cultura.StatusPlantacao selecionarStatusPlantacao() {
        Cultura.StatusPlantacao[] statuses = Cultura.StatusPlantacao.values();
        System.out.println("\n  Status da plantação:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, statuses[i].getDescricao());
        }
        int idx = lerInteiro("Opção: ") - 1;
        return (idx >= 0 && idx < statuses.length) ? statuses[idx] : Cultura.StatusPlantacao.PLANTADA;
    }

    private void cabecalho(String titulo) {
        System.out.println("\n" + Ansi.CIANO + Ansi.NEGRITO + "── " + titulo + " ──" + Ansi.RESET);
    }

    private int lerInteiro(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
