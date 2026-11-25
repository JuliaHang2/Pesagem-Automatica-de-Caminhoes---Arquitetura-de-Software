import { useEffect, useState } from 'react';
import { api, hora, kg } from '../api.js';

export default function Dashboard() {
  const [d, setD] = useState(null);
  const [alertas, setAlertas] = useState([]);
  const [log, setLog] = useState([]);

  useEffect(() => {
    const carregar = () => {
      api('/dashboard').then(setD).catch(() => {});
      api('/alertas').then(setAlertas).catch(() => {});
      api('/auditoria').then(setLog).catch(() => {});
    };
    carregar();
    const i = setInterval(carregar, 10000);
    return () => clearInterval(i);
  }, []);

  return (
    <>
      <div className="indicadores">
        <div className="cartao"><span>Caminhões atendidos</span><strong>{d?.caminhoesAtendidos ?? '—'}</strong></div>
        <div className="cartao"><span>Tickets em aberto</span><strong>{d?.ticketsAbertos ?? '—'}</strong></div>
        <div className="cartao"><span>Tempo médio de permanência</span><strong>{d ? d.tempoMedioMin + ' min' : '—'}</strong></div>
        <div className="cartao"><span>Volume total carregado</span><strong>{d ? kg(d.volumeTotalKg) : '—'}</strong></div>
      </div>
      <section className="cartao">
        <h2>Alertas de permanência excedida</h2>
        {alertas.length === 0 ? <p>Nenhum alerta.</p> : (
          <ul>{alertas.map((a) => <li key={a.codigo}>{a.placa} ({a.codigo}) está há {a.minutos} min no pátio</li>)}</ul>
        )}
      </section>
      <section className="cartao">
        <h2>Últimas operações (auditoria)</h2>
        <table>
          <thead><tr><th>Data/hora</th><th>Usuário</th><th>Operação</th><th>Detalhe</th></tr></thead>
          <tbody>{log.slice(0, 20).map((a) => <tr key={a.id}><td>{hora(a.dataHora)}</td><td>{a.usuario}</td><td>{a.operacao}</td><td>{a.detalhe}</td></tr>)}</tbody>
        </table>
      </section>
    </>
  );
}
