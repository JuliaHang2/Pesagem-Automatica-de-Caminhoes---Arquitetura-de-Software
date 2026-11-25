import { useEffect, useState } from 'react';
import { api, hora, kg } from '../api.js';

export default function Expedicao() {
  const [lista, setLista] = useState([]);
  const carregar = () => api('/expedicao').then(setLista).catch(() => {});
  useEffect(() => { carregar(); const i = setInterval(carregar, 5000); return () => clearInterval(i); }, []);
  return (
    <section className="cartao">
      <h2>Caminhões aptos a carregar (tara registrada)</h2>
      <table>
        <thead><tr><th>Ticket</th><th>Placa</th><th>Cliente</th><th>Produto</th><th>Previsto</th><th>Tara</th><th>Entrada</th></tr></thead>
        <tbody>
          {lista.map((t) => (
            <tr key={t.id}><td>{t.codigo}</td><td>{t.placa}</td><td>{t.cliente}</td><td>{t.produto}</td>
              <td>{kg(t.pesoPrevistoKg)}</td><td>{kg(t.taraKg)}</td><td>{hora(t.entradaEm)}</td></tr>
          ))}
          {lista.length === 0 && <tr><td colSpan="7">Nenhum caminhão aguardando carregamento.</td></tr>}
        </tbody>
      </table>
    </section>
  );
}
