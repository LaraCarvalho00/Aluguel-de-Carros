import { useState, FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { FiLogIn, FiUser, FiLock } from "react-icons/fi";
import toast from "react-hot-toast";
import { useAuth } from "../contexts/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [cpf, setCpf] = useState("");
  const [senha, setSenha] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await login(cpf, senha);
      toast.success("Login realizado com sucesso!");
      navigate("/");
    } catch {
      toast.error("CPF ou senha inválidos");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-image-panel">
        <div className="login-image-overlay">
          <h2>McQueen Car</h2>
          <p>Sua jornada começa aqui.</p>
        </div>
      </div>

      <div className="login-form-panel">
        <div className="login-card">
          <div className="login-header">
            <span className="login-logo">🏎️⚡</span>
            <h1>McQueen Car</h1>
            <p>Faça login para continuar</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="cpf">CPF</label>
              <div className="input-icon">
                <FiUser />
                <input
                  id="cpf"
                  type="text"
                  placeholder="000.000.000-00"
                  value={cpf}
                  onChange={(e) => setCpf(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="senha">Senha</label>
              <div className="input-icon">
                <FiLock />
                <input
                  id="senha"
                  type="password"
                  placeholder="Sua senha"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  required
                />
              </div>
            </div>

            <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
              <FiLogIn />
              {loading ? "Entrando..." : "Entrar"}
            </button>
          </form>

          <p className="login-footer">
            PUC Minas — Laboratório de Desenvolvimento de Software
          </p>
        </div>
      </div>
    </div>
  );
}
