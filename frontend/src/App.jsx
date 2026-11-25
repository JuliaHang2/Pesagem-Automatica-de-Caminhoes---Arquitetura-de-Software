import { Navigate, NavLink, Route, Routes, useNavigate } from 'react-router-dom';
import { getSessao, setSessao } from './api.js';
import Login from './pages/Login.jsx';
import Portaria from './pages/Portaria.jsx';
import Balanca from './pages/Balanca.jsx';
import Expedicao from './pages/Expedicao.jsx';
import Historico from './pages/Historico.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Parametros from './pages/Parametros.jsx';
import Painel from './pages/Painel.jsx';

const OP = ['OPERADOR', 'SUPERVISOR', 'ADMINISTRADOR'];
const GESTAO = ['SUPERVISOR', 'ADMINISTRADOR'];
const MENU = [
  { to: '/portaria', texto: 'Portaria', perfis: OP, el: <Portaria /> },
  { to: '/balanca', texto: 'Balança', perfis: OP, el: <Balanca /> },
  { to: '/expedicao', texto: 'Expedição', perfis: OP, el: <Expedicao /> },
  { to: '/historico', texto: 'Histórico', perfis: null, el: <Historico /> },
  { to: '/dashboard', texto: 'Dashboard', perfis: GESTAO, el: <Dashboard /> },
  { to: '/parametros', texto: 'Parâmetros', perfis: ['ADMINISTRADOR'], el: <Parametros /> },
];

function Layout({ sessao, children }) {
  const nav = useNavigate();
  const itens = MENU.filter((m) => !m.perfis || m.perfis.includes(sessao.perfil));
  const sair = () => { setSessao(null); nav('/login'); };
  return (
    <>
      <header className="topo">
        <strong>Pesagem Automatizada</strong>
        <nav>
          {itens.map((m) => <NavLink key={m.to} to={m.to}>{m.texto}</NavLink>)}
          <a href="/painel" target="_blank" rel="noreferrer">Painel virtual</a>
        </nav>
        <span className="usuario">{sessao.nome} ({sessao.perfil}) <button onClick={sair}>Sair</button></span>
      </header>
      <main>{children}</main>
    </>
  );
}

export default function App() {
  const sessao = getSessao();
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/painel" element={<Painel />} />
      {MENU.map((m) => (
        <Route key={m.to} path={m.to} element={
          !sessao ? <Navigate to="/login" /> :
          m.perfis && !m.perfis.includes(sessao.perfil) ? <Navigate to="/historico" /> :
          <Layout sessao={sessao}>{m.el}</Layout>} />
      ))}
      <Route path="*" element={<Navigate to={sessao ? '/historico' : '/login'} />} />
    </Routes>
  );
}
