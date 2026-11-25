import { useEffect, useState } from 'react';
import { api, hora } from '../api.js';

export default function Portaria() {
  const [placa, setPlaca] = useState('');
  const [ticket, setTicket] = useState(null);
  const [erro, setErro] = useState('');
  const [pedidos, setPedidos] = useState([]);

  const carregar = () => api('/pedidos?status=PROGRAMADO').then(setPedidos).catch(() => {});
  useEffect(() => { carregar(); }, []);

  async function emitir(e) {
    e.preventDefault();
    setErro(''); setTicket(null);
    try {
      setTicket(await api('/tickets', { method: 'POST', body: { placa, manual: true } }));
      setPlaca('');
      carregar();
    } catch (err) { setErro(err.message); }
  }

  return (
    <div className="grade">
      <section className="cartao">
        <h2>Entrada de veículo</h2>
        <form onSubmit={emitir}>
          <label>Placa do veículo (cadastro manual no MVP)
            <input value={placa} onChange={(e) => setPlaca(e.target.value.toUpperCase())} placeholder="ABC1D23" maxLength={8} />
          </label>
          <button className="primario">Emitir ticket</button>
        </form>
        {erro && <p className="erro">{erro}</p>}
        {ticket && (
          <div className="ticket">
            <h3>TICKET DE ENTRADA</h3>
            <p className="codigo">{ticket.codigo}</p>
            <p>Placa: <b>{ticket.placa}</b></p>
            <p>Cliente: {ticket.cliente} · {ticket.produto}</p>
            <p>Entrada: {hora(ticket.entradaEm)}</p>
            <button onClick={() => window.print()}>Imprimir</button>
          </div>
        )}
      </section>
      <section className="cartao">
        <h2>Carregamentos programados (ERP simulado)</h2>
        <table>
          <thead><tr><th>Placa</th><th>Cliente</th><th>Produto</th><th>Previsto</th></tr></thead>
          <tbody>
            {pedidos.map((p) => (
              <tr key={p.id}><td>{p.placaProgramada}</td><td>{p.cliente}</td><td>{p.produto}</td>
                <td>{Number(p.pesoPrevistoKg).toLocaleString('pt-BR')} kg</td></tr>
            ))}
            {pedidos.length === 0 && <tr><td colSpan="4">Nenhum carregamento programado.</td></tr>}
          </tbody>
        </table>
      </section>
    </div>
  );
}
