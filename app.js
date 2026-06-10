// ── Dados iniciais de demonstração ──────────────────────────────────────────
const fazendas = [
  { nome: 'Fazenda Santa Fé',    local: 'Ribeirão Preto - SP', cultura: 'Soja',  status: 'Em Crescimento',     area: 340 },
  { nome: 'Sítio Boa Esperança', local: 'Uberaba - MG',        cultura: 'Milho', status: 'Pronta para Colheita', area: 180 },
];

const PREVISOES = ['Ensolarado', 'Parcialmente Nublado', 'Chuvas Esparsas', 'Tempestade', 'Seco e Quente', 'Ventos Fortes', 'Névoa'];

let leituraAtual = null;
let historicoTemp  = [];
let historicoUmid  = [];
let graficoInstance = null;

// ── Simulação de dados de satélite ──────────────────────────────────────────
function gerarLeitura() {
  const temp    = +(15 + Math.random() * 25).toFixed(1);
  const umidade = Math.floor(20 + Math.random() * 71);
  const chuva   = Math.floor(Math.random() * 101);
  const previsao = PREVISOES[Math.floor(Math.random() * PREVISOES.length)];
  const risco   = calcularRisco(temp, umidade, chuva);
  return { temp, umidade, chuva, previsao, risco };
}

function calcularRisco(temp, umidade, chuva) {
  const seco    = umidade < 35 && temp > 32 && chuva < 10;
  const critico = chuva > 80 || temp > 38;
  if (seco || critico) return 'critico';
  if (umidade < 50 || chuva > 55 || temp > 34) return 'atencao';
  return 'seguro';
}

function recomendacao(risco, status) {
  if (risco === 'critico')
    return '🚨 SITUAÇÃO CRÍTICA — Irrigação de emergência recomendada imediatamente!';
  if (risco === 'atencao')
    return '⚠️ ATENÇÃO — Adote medidas de prevenção e monitore a lavoura nas próximas 24h.';
  if (status === 'Pronta para Colheita')
    return '✅ CONDIÇÕES IDEAIS — Momento perfeito para iniciar a colheita!';
  return '✅ SEGURO — Condições estáveis. Continue o monitoramento regular.';
}

// ── Relógio ──────────────────────────────────────────────────────────────────
function atualizarRelogio() {
  document.getElementById('clock').textContent =
    new Date().toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'medium' });
}
setInterval(atualizarRelogio, 1000);
atualizarRelogio();

// ── Select de fazendas ───────────────────────────────────────────────────────
function renderizarSelect() {
  const sel = document.getElementById('selectFazenda');
  sel.innerHTML = fazendas.map((f, i) =>
    `<option value="${i}">${f.nome} — ${f.local}</option>`
  ).join('');
}

// ── Dashboard principal ──────────────────────────────────────────────────────
function atualizarDashboard() {
  const idx     = +document.getElementById('selectFazenda').value;
  const fazenda = fazendas[idx];
  leituraAtual  = gerarLeitura();

  // Cards de clima
  document.getElementById('temperatura').textContent = leituraAtual.temp + ' °C';
  document.getElementById('umidade').textContent     = leituraAtual.umidade + ' %';
  document.getElementById('chuva').textContent       = leituraAtual.chuva + ' mm';
  document.getElementById('previsao').textContent    = leituraAtual.previsao;

  // Alerta banner
  const banner = document.getElementById('alerta-banner');
  banner.className = 'alerta-banner ' + leituraAtual.risco;
  banner.textContent = recomendacao(leituraAtual.risco, fazenda.status);

  // Lista de fazendas com status
  renderizarListaFazendas();

  // Mapa
  document.getElementById('mapa-fazenda-nome').textContent = fazenda.nome;
  document.getElementById('mapa-cultura').textContent      = fazenda.cultura;
  document.getElementById('mapa-status').textContent       = fazenda.status;
  document.getElementById('mapa-area').textContent         = fazenda.area + ' ha';

  const riscoLabel = { seguro: '🟢 Seguro', atencao: '🟡 Atenção', critico: '🔴 Crítico' };
  document.getElementById('mapa-risco').textContent = riscoLabel[leituraAtual.risco];

  const cores = { seguro: '#22c55e', atencao: '#eab308', critico: '#ef4444' };
  document.getElementById('mapa-risco-overlay').style.background = cores[leituraAtual.risco];

  // Mover marcador aleatoriamente dentro do mapa (simula posição da fazenda)
  const mx = 80 + Math.floor(Math.random() * 140);
  const my = 60 + Math.floor(Math.random() * 80);
  document.getElementById('mapa-marker').setAttribute('cx', mx);
  document.getElementById('mapa-marker').setAttribute('cy', my);
  document.getElementById('mapa-marker-pulse').setAttribute('cx', mx);
  document.getElementById('mapa-marker-pulse').setAttribute('cy', my);

  // Gráfico
  atualizarGrafico(leituraAtual.temp, leituraAtual.umidade);
}

function gerarNovaLeitura() {
  atualizarDashboard();
}

// ── Lista de fazendas com risco ──────────────────────────────────────────────
function renderizarListaFazendas() {
  const container = document.getElementById('lista-fazendas');
  container.innerHTML = fazendas.map(f => {
    const leitura = gerarLeitura();
    const label   = { seguro: 'Seguro', atencao: 'Atenção', critico: 'Crítico' };
    return `
      <div class="fazenda-item ${leitura.risco}">
        <div>
          <div class="fazenda-item-nome">${f.nome}</div>
          <div class="fazenda-item-sub">${f.cultura} · ${f.status} · ${f.area} ha</div>
        </div>
        <span class="badge ${leitura.risco}">${label[leitura.risco]}</span>
      </div>`;
  }).join('');
}

// ── Gráfico ──────────────────────────────────────────────────────────────────
function atualizarGrafico(temp, umid) {
  const agora = new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' });

  historicoTemp.push(temp);
  historicoUmid.push(umid);

  if (historicoTemp.length > 7) { historicoTemp.shift(); historicoUmid.shift(); }

  const labels = historicoTemp.map((_, i) =>
    i === historicoTemp.length - 1 ? agora : `L${i + 1}`
  );

  if (graficoInstance) {
    graficoInstance.data.labels              = labels;
    graficoInstance.data.datasets[0].data   = [...historicoTemp];
    graficoInstance.data.datasets[1].data   = [...historicoUmid];
    graficoInstance.update();
    return;
  }

  const ctx = document.getElementById('grafico').getContext('2d');
  graficoInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [
        {
          label: 'Temperatura (°C)',
          data: [...historicoTemp],
          borderColor: '#ef4444',
          backgroundColor: 'rgba(239,68,68,0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 4,
        },
        {
          label: 'Umidade (%)',
          data: [...historicoUmid],
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59,130,246,0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 4,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { labels: { color: '#e2e8f0', font: { size: 12 } } },
      },
      scales: {
        x: { ticks: { color: '#64748b' }, grid: { color: '#2a2d3e' } },
        y: { ticks: { color: '#64748b' }, grid: { color: '#2a2d3e' }, min: 0, max: 100 },
      },
    },
  });
}

// ── Modal cadastro ───────────────────────────────────────────────────────────
function abrirModal()  { document.getElementById('modal-overlay').classList.remove('hidden'); }
function fecharModal() { document.getElementById('modal-overlay').classList.add('hidden'); }
function fecharModalFora(e) { if (e.target.id === 'modal-overlay') fecharModal(); }

function cadastrarFazenda(e) {
  e.preventDefault();
  fazendas.push({
    nome:    document.getElementById('f-nome').value.trim(),
    local:   document.getElementById('f-local').value.trim(),
    cultura: document.getElementById('f-cultura').value,
    status:  document.getElementById('f-status').value,
    area:    +document.getElementById('f-area').value,
  });
  fecharModal();
  e.target.reset();
  renderizarSelect();
  document.getElementById('selectFazenda').value = fazendas.length - 1;
  atualizarDashboard();
}

// ── Inicialização ────────────────────────────────────────────────────────────
renderizarSelect();
atualizarDashboard();
