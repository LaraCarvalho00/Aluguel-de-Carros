import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  FiHome,
  FiUsers,
  FiFileText,
  FiLogOut,
  FiUser,
  FiClipboard,
} from "react-icons/fi";
import { MdDirectionsCar } from "react-icons/md";
import toast from "react-hot-toast";
import { useAuth } from "../contexts/AuthContext";

const PERFIL_LABEL: Record<string, string> = {
  CLIENTE: "Cliente",
  AGENTE: "Agente",
  ADMIN: "Admin",
};

export default function Header() {
  const { pathname } = useLocation();
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();

  const navItems = [
    { to: "/", label: "Início", icon: <FiHome />, roles: null },
    { to: "/clientes", label: "Clientes", icon: <FiUsers />, roles: ["AGENTE", "ADMIN"] },
    { to: "/automoveis", label: "Automóveis", icon: <MdDirectionsCar />, roles: null },
    { to: "/pedidos", label: "Pedidos", icon: <FiFileText />, roles: null },
    { to: "/contratos", label: "Contratos", icon: <FiClipboard />, roles: ["AGENTE", "ADMIN"] },
  ].filter(({ roles }) => {
    if (!roles) return true;
    return roles.some((r: string) => hasRole(r));
  });

  function handleLogout() {
    logout();
    toast.success("Até logo!");
    navigate("/login");
  }

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          <span className="logo-icon">🏎️⚡</span>
          <span className="logo-text">McQueen Car</span>
        </Link>
        <nav className="nav">
          {navItems.map(({ to, label, icon }) => (
            <Link
              key={to}
              to={to}
              className={`nav-link ${pathname === to ? "active" : ""}`}
            >
              {icon}
              <span>{label}</span>
            </Link>
          ))}
        </nav>
        {user && (
          <div className="header-user">
            <FiUser />
            <span className="header-user-info">
              <span className="header-user-cpf">
                {user.nome?.trim() || user.cpf}
              </span>
              <span className="header-user-perfil">
                {PERFIL_LABEL[user.perfil] ?? user.perfil}
              </span>
            </span>
            <button
              className="btn-logout"
              onClick={handleLogout}
              title="Sair"
            >
              <FiLogOut />
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
