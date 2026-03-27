import { Link, useLocation } from "react-router-dom";
import { FiUsers, FiHome, FiUserPlus, FiSearch } from "react-icons/fi";

export default function Header() {
  const { pathname } = useLocation();

  const navItems = [
    { to: "/", label: "Início", icon: <FiHome /> },
    { to: "/clientes", label: "Clientes", icon: <FiUsers /> },
    { to: "/clientes/novo", label: "Cadastrar", icon: <FiUserPlus /> },
    { to: "/clientes/buscar", label: "Buscar CPF", icon: <FiSearch /> },
  ];

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="logo">
          <span className="logo-icon">🚗</span>
          <span className="logo-text">Aluguel de Carros</span>
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
      </div>
    </header>
  );
}
