import { useState } from 'react';
import { api, getSessao, hora, kg } from '../api.js';

export default function Balanca() {
  const [codigo, setCodigo] = useState('');
  const [ticket, setTicket] = useState(null);
  const [peso, setPeso] = useState('');
  const [estavel, setEstavel] = useState(true);
  const [placaLida, setPlacaLida] = useState('');
  const [erro, setErro] = useState('');
  const [msg, setMsg] = useState('');
  const [just, setJust] = useState('');
  const supervisor = getSessao()?.perfil === 'SUPERVISOR';

  const limpar = () => { setErro(''); setMsg(''); };

  async function buscar(e) {
    e.preventDefault(); limpar(); setTicket(null);
    try { setTicket(await api('/tickets/' + codigo.trim().toUpperCase())); } catch (err) { setErro(err.message); }
  }
  async function simular(divergente = false) {
    limpar();
    try {
      const r = await api(`/balanca/simular?codigo=${ticket.codigo}&divergente=${divergente}`);
      setPeso(r.pesoKg); setEstavel(r.estabilizado);
    } catch (err) { setErro(err.message); }
  }
  async function pesar() {
    limpar();
    try {
      const t = await api('/pesagens', { method: 'POST', body: { codigoTicket: ticket.codigo, pesoKg: peso, estabilizado: estavel, placaLida } });
      setTicket(t); setPeso('');
      setMsg(t.status === 'ENCERRADO' ? 'Pesagem final concluída. Veículo LIBERADO.'
        : t.status === 'AGUARDANDO_AUTORIZACAO' ? 'Peso fora da tolerância: aguardando autorização do supervisor.'
        : 'Tara registrada. Encaminhe o veículo à doca de expedição.');
    } catch (err) { setErro(err.message); }
  }
  async function autorizar() {
    limpar();
    try { setTicket(await api(`/tickets/${ticket.id}/autorizar`, { method: 'POST', body: { justificativa: just } })); setMsg('Exceção autorizada. Veículo LIBERADO.'); }
    catch (err) { setErro(err.message); }
  }

  const proximo = ticket && (ticket.status === 'EMITIDO' ? 'TARA (veículo vazio)'
    : ticket.status === 'TARA_REGISTRADA' ? 'PESO BRUTO (veículo carregado)' : null);

  return (
    <div className="grade">
      <section className="cartao">
        <h2>Leitura do ticket</h2>
        <form onSubmit={buscar}>
          <label>Código do ticket (leitor simulado)
            <input value={codigo} onChange={(e) => setCodigo(e.target.value)} placeholder="TK-XXXXXXXX" />
          </label>
          <button className="primario">Ler ticket</button>
        </form>
        {ticket && (
          <dl className="detalhes">
            <dt>Placa</dt><dd>{ticket.placa}</dd>
            <dt>Situação</dt><dd>{ticket.status}</dd>
            <dt>Pedido</dt><dd>{ticket.cliente} · {ticket.produto} · previsto {kg(ticket.pesoPrevistoKg)}</dd>
            <dt>Tara</dt><dd>{kg(ticket.taraKg)}</dd>
            <dt>Peso bruto</dt><dd>{kg(ticket.brutoKg)}</dd>
            <dt>Peso líquido</dt><dd><b>{kg(ticket.pesoLiquidoKg)}</b></dd>
          </dl>
        )}
        {erro && <p className="erro">{erro}</p>}
        {msg && <p className="ok">{msg}</p>}
      </section>

      {ticket && proximo && (
        <section className="cartao">
          <h2>Pesagem: {proximo}</h2>
          <label>Peso lido (kg)<input type="number" value={peso} onChange={(e) => setPeso(e.target.value)} /></label>
          <label className="linha"><input type="checkbox" checked={estavel} onChange={(e) => setEstavel(e.target.checked)} /> Leitura estabilizada</label>
          <label>Placa lida pela câmera (opcional, p/ conferência)
            <input value={placaLida} onChange={(e) => setPlacaLida(e.target.value.toUpperCase())} />
          </label>
          <div className="botoes">
            <button onClick={() => simular(false)}>Simular leitura</button>
            {ticket.status === 'TARA_REGISTRADA' && <button onClick={() => simular(true)}>Simular peso divergente</button>}
            <button className="primario" onClick={pesar} disabled={!peso}>Registrar pesagem</button>
          </div>
        </section>
      )}

      {ticket && ticket.status === 'AGUARDANDO_AUTORIZACAO' && (
        <section className="cartao">
          <h2>Divergência de peso</h2>
          <p>O peso líquido ultrapassou a tolerância do pedido. {supervisor ? 'Informe a justificativa para liberar.' : 'Chame um supervisor.'}</p>
          {supervisor && (<>
            <label>Justificativa<input value={just} onChange={(e) => setJust(e.target.value)} /></label>
            <button className="primario" onClick={autorizar}>Autorizar liberação</button>
          </>)}
        </section>
      )}

      {ticket && ticket.status === 'ENCERRADO' && (
        <section className="cartao comprovante">
          <h2>Comprovante de pesagem</h2>
          <p>Ticket <b>{ticket.codigo}</b> · Placa <b>{ticket.placa}</b></p>
          <p>Tara: {kg(ticket.taraKg)} · Bruto: {kg(ticket.brutoKg)} · <b>Líquido: {kg(ticket.pesoLiquidoKg)}</b></p>
          <p>Entrada: {hora(ticket.entradaEm)} · Saída: {hora(ticket.saidaEm)}</p>
          {ticket.excecaoAutorizada && <p>Liberado por exceção autorizada.</p>}
          <button onClick={() => window.print()}>Imprimir comprovante</button>
        </section>
      )}
    </div>
  );
}
