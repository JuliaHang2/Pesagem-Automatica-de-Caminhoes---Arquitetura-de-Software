import { useEffect, useState } from 'react';
import { api } from '../api.js';

const CAMPOS = [
  ['TOLERANCIA_PERCENTUAL', 'Tolerância de peso (%)', 'number'],
  ['TEMPO_MAX_PERMANENCIA_MIN', 'Tempo máximo de permanência (min)', 'number'],
  ['CALIBRACAO_VALIDADE', 'Validade da calibração da balança', 'date'],
];

export default function Parametros() {
  const [p, setP] = useState({});
  const [msg, setMsg] = useState('');
  const [erro, setErro] = useState('');
  useEffect(() => { api('/parametros').then(setP).catch((e) => setErro(e.message)); }, []);

  async function salvar(e) {
    e.preventDefault(); setMsg(''); setErro('');
    try { setP(await api('/parametros', { method: 'PUT', body: p })); setMsg('Parâmetros salvos.'); }
    catch (err) { setErro(err.message); }
  }

  return (
    <section className="cartao estreito">
      <h2>Parâmetros do sistema</h2>
      <form onSubmit={salvar}>
        {CAMPOS.map(([chave, rotulo, tipo]) => (
          <label key={chave}>{rotulo}
            <input type={tipo} value={p[chave] ?? ''} onChange={(e) => setP({ ...p, [chave]: e.target.value })} />
          </label>
        ))}
        <button className="primario">Salvar</button>
      </form>
      {msg && <p className="ok">{msg}</p>}
      {erro && <p className="erro">{erro}</p>}
    </section>
  );
}
