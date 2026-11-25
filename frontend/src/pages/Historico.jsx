import { Fragment, useEffect, useState } from 'react';
import { api, getSessao, hora, kg } from '../api.js';

export default function Historico() {
  const [placa, setPlaca] = useState('');
  const [lista, setLista] = useState([]);
  const [aberto, setAberto] = useState(null);
  const [pesagens, setPesagens] = useState([]);
  const [erro, setErro] = useState('');
  const supervisor = getSessao()?.perfil === 'SUPERVISOR';

  const carregar = () => api('/tickets?placa=' + encodeURIComponent(placa)).then(setLista).catch((e) => setErro(e.message));
  useEffect(() => { carregar(); }, []);

  async function detalhar(t) {
    setErro('');
    if (aberto === t.codigo) { setAberto(null); return; }
    setAberto(t.codigo);
    setPesagens(await api(`/tickets/${t.codigo}/pesagens`));
  }
  async function corrigir(p, t) {
    const novo = window.prompt('Novo peso (kg):');
    const justificativa = novo && window.prompt('Justificativa da correção:');
    if (!novo || !justificativa) return;
    try {
      await api(`/pesagens/${p.id}/corrigir`, { method: 'POST', body: { novoPesoKg: novo, justificativa } });
      setPesagens(await api(`/tickets/${t.codigo}/pesagens`));
      carregar();
    } catch (err) { setErro(err.message); }
  }

  return (
    <section className="cartao">
      <h2>Histórico e rastreabilidade</h2>
      <form className="linha" onSubmit={(e) => { e.preventDefault(); carregar(); }}>
        <input value={placa} onChange={(e) => setPlaca(e.target.value)} placeholder="Filtrar por placa" />
        <button className="primario">Buscar</button>
      </form>
      {erro && <p className="erro">{erro}</p>}
      <table>
        <thead><tr><th>Ticket</th><th>Placa</th><th>Cliente</th><th>Situação</th><th>Líquido</th><th>Entrada</th><th>Saída</th></tr></thead>
        <tbody>
          {lista.map((t) => (
            <Fragment key={t.id}>
              <tr className="clicavel" onClick={() => detalhar(t)}>
                <td>{t.codigo}</td><td>{t.placa}</td><td>{t.cliente}</td><td>{t.status}</td>
                <td>{kg(t.pesoLiquidoKg)}</td><td>{hora(t.entradaEm)}</td><td>{hora(t.saidaEm)}</td>
              </tr>
              {aberto === t.codigo && (
                <tr><td colSpan="7">
                  <table className="interna">
                    <thead><tr><th>Tipo</th><th>Peso</th><th>Data/hora</th><th>Usuário</th><th>Vigente</th><th>Justificativa</th><th></th></tr></thead>
                    <tbody>
                      {pesagens.map((p) => (
                        <tr key={p.id} className={p.vigente ? '' : 'riscado'}>
                          <td>{p.tipo}</td><td>{kg(p.pesoKg)}</td><td>{hora(p.dataHora)}</td><td>{p.usuario}</td>
                          <td>{p.vigente ? 'sim' : 'substituída'}</td><td>{p.justificativa || ''}</td>
                          <td>{supervisor && p.vigente && <button onClick={() => corrigir(p, t)}>Corrigir</button>}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </td></tr>
              )}
            </Fragment>
          ))}
          {lista.length === 0 && <tr><td colSpan="7">Nenhum registro.</td></tr>}
        </tbody>
      </table>
    </section>
  );
}
