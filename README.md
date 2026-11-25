# Pesagem Automatizada de Caminhões

UTFPR-DV (Engenharia de Software).
Equipe: Beatriz Dourado de Lima, Júlia Hang e Luis Fernando de Souza Perim.

Sistema que automatiza a pesagem de caminhões em um frigorífico, no estilo das cancelas com ticket:
emissão do ticket na entrada, registro de tara e peso bruto, cálculo do peso líquido, validação de
tolerância contra o pedido, comprovante, painel de liberação, histórico e dashboard.

## Escopo do MVP
Incluído: cadastro manual da placa, emissão de ticket, tara e peso bruto com balança simulada, peso líquido,
validação de tolerância, login por perfil (JWT), comprovante, painel virtual, histórico/auditoria e dashboard.

Fora do MVP (evoluções futuras): OCR de placas, equipamentos físicos (câmera, totem, cancela, painel de LED),
integração real com ERP e emissão de NF-e/SEFAZ. No MVP a liberação ocorre direto após a pesagem válida.

## Tecnologias
React + Vite · Java 17 + Spring Boot 3 · PostgreSQL · Spring Security + JWT · JUnit + Mockito · Docker · GitHub Projects

## Como executar

### Com Docker
```bash
docker compose up --build
```
Aplicação em http://localhost:5173 · API em http://localhost:8080

### Sem Docker
```bash
# banco: PostgreSQL com database/usuário/senha "pesagem"
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev      # http://localhost:5173
```
Testes: `cd backend && mvn test`

## Usuários de demonstração
| Login | Senha | Perfil |
|---|---|---|
| operador | operador123 | OPERADOR |
| supervisor | supervisor123 | SUPERVISOR |
| fiscal | fiscal123 | FISCAL |
| admin | admin123 | ADMINISTRADOR |

Placas com carregamento programado (ERP simulado): `ABC1D23`, `EFG4H56`, `IJK7L89`.

## Fluxo para testar
1. Login como `operador` → **Portaria** → emitir ticket para `ABC1D23`.
2. **Balança** → ler o ticket → *Simular leitura* → *Registrar pesagem* (tara).
3. **Expedição** mostra o caminhão apto a carregar.
4. **Balança** → mesmo ticket → simular leitura (bruto) → registrar. Se dentro da tolerância, o caminhão é liberado.
   Use *Simular peso divergente* para testar a exceção (RN11) e autorize como `supervisor`.
5. **Painel virtual** (`/painel`) mostra LIBERADO; **Histórico** e **Dashboard** mostram o registro.

## Estrutura
```
backend/   API Spring Boot (domain, repository, service, web, security, config, exception, dto)
frontend/  Telas React (pages: Login, Portaria, Balanca, Expedicao, Historico, Dashboard, Parametros, Painel)
docker-compose.yml
```

## Regras de negócio implementadas
RN01–RN04 (ticket e placa), RN05–RN07 e RN09–RN11 (pesagem), RN08 e RN18 (imutabilidade/auditoria),
RN15 (comprovante), RN16 (ticket encerrado), RN17 (alerta de permanência), RN19 (perfis), RN20 (calibração).
RN12–RN14 (NF-e/SEFAZ) ficam para a etapa futura.
