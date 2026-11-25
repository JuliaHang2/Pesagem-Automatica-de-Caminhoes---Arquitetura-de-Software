import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, setSessao } from '../api.js';

export default function Login() {
  const [login, setLogin] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const nav = useNavigate();

  async function entrar(e) {
    e.preventDefault();
    try {
      const r = await api('/auth/login', { method: 'POST', body: { login, senha } });
      setSessao(r);
      nav(r.perfil === 'FISCAL' ? '/historico' : '/portaria');
    } catch (err) { setErro(err.message); }
  }

  return (
    <div className="login">
      <form className="cartao" onSubmit={entrar}>
        <h2>Pesagem Automatizada de Caminhões</h2>
        <label>Login<input value={login} onChange={(e) => setLogin(e.target.value)} autoFocus /></label>
        <label>Senha<input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} /></label>
        {erro && <p className="erro">{erro}</p>}
        <button className="primario">Entrar</button>
        <small>Demo: operador/operador123 · supervisor/supervisor123 · fiscal/fiscal123 · admin/admin123</small>
      </form>
    </div>
  );
}
