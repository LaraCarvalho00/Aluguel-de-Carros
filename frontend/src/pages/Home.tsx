import { Link } from "react-router-dom";
import { FiUsers, FiUserPlus, FiSearch } from "react-icons/fi";

export default function Home() {
  return (
    <div className="home">
      <div className="hero">
        <h1>Sistema de Aluguel de Carros</h1>
        <p>Gerencie seus clientes de forma rápida e eficiente.</p>
      </div>
      <div className="home-cards">
        <Link to="/clientes" className="home-card">
          <FiUsers size={40} />
          <h2>Ver Clientes</h2>
          <p>Visualize todos os clientes cadastrados no sistema.</p>
        </Link>
        <Link to="/clientes/novo" className="home-card">
          <FiUserPlus size={40} />
          <h2>Cadastrar Cliente</h2>
          <p>Registre um novo cliente com seus dados pessoais.</p>
        </Link>
        <Link to="/clientes/buscar" className="home-card">
          <FiSearch size={40} />
          <h2>Buscar por CPF</h2>
          <p>Encontre rapidamente um cliente pelo número do CPF.</p>
        </Link>
      </div>
    </div>
  );
}
