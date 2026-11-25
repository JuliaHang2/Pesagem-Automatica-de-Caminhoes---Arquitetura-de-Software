const BASE = '/api';

export function getSessao() {
  try { return JSON.parse(localStorage.getItem('sessao')); } catch { return null; }
}
export function setSessao(s) {
  if (s) localStorage.setItem('sessao', JSON.stringify(s));
  else localStorage.removeItem('sessao');
}

export async function api(path, { method = 'GET', body } = {}) {
  const s = getSessao();
  const r = await fetch(BASE + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(s ? { Authorization: 'Bearer ' + s.token } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (r.status === 401 && !path.startsWith('/auth') && path !== '/painel') {
    setSessao(null);
    window.location.href = '/login';
  }
  const dados = await r.json().catch(() => null);
  if (!r.ok) throw new Error(dados?.erro || 'Erro ' + r.status);
  return dados;
}

export const kg = (v) => (v == null ? '—' : Number(v).toLocaleString('pt-BR', { maximumFractionDigits: 0 }) + ' kg');
export const hora = (v) => (v ? new Date(v).toLocaleString('pt-BR') : '—');
