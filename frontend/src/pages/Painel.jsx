import { useEffect, useState } from 'react';
import { api, kg } from '../api.js';

export default function Painel() {
  const [d, setD] = useState({ mensagem: 'AGUARDANDO VEÍCULO', placa: '', pesoKg: null });
  useEffect(() => {
    const carregar = () => api('/painel').then(setD).catch(() => {});
    carregar();
    const i = setInterval(carregar, 2000);
    return () => clearInterval(i);
  }, []);
  const liberado = d.mensagem === 'LIBERADO';
  return (
    <div className={'painel ' + (liberado ? 'verde' : d.mensagem.startsWith('AGUARDE') ? 'vermelho' : 'neutro')}>
      <div className="msg">{d.mensagem}</div>
      {d.placa && <div className="placa">{d.placa}</div>}
      {d.pesoKg != null && <div className="peso">{kg(d.pesoKg)}</div>}
    </div>
  );
}
