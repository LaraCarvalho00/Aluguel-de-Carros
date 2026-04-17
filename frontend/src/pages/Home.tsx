import { Link } from "react-router-dom";
import {
  FiFileText,
  FiPlusCircle,
  FiUsers,
  FiUserPlus,
  FiSearch,
  FiClipboard,
  FiCheckSquare,
} from "react-icons/fi";
import { MdDirectionsCar } from "react-icons/md";
import { useAuth } from "../contexts/AuthContext";

interface Card {
  to: string;
  icon: React.ReactNode;
  title: string;
  description: string;
}

export default function Home() {
  const { hasRole, user } = useAuth();
  const isCliente = hasRole("CLIENTE");
  const isAgente  = hasRole("AGENTE");
  const isAdmin   = hasRole("ADMIN");

  const clienteCards: Card[] = [
    {
      to: "/automoveis",
      icon: <MdDirectionsCar size={36} />,
      title: "Automóveis Disponíveis",
      description: "Veja os veículos disponíveis para alugar.",
    },
    {
      to: "/pedidos/novo",
      icon: <FiPlusCircle size={36} />,
      title: "Solicitar Aluguel",
      description: "Crie um novo pedido de aluguel.",
    },
    {
      to: "/pedidos",
      icon: <FiFileText size={36} />,
      title: "Meus Pedidos",
      description: "Acompanhe o status dos seus pedidos.",
    },
  ];

  const agenteCards: Card[] = [
    {
      to: "/clientes",
      icon: <FiUsers size={36} />,
      title: "Clientes",
      description: "Visualize e gerencie os clientes cadastrados.",
    },
    {
      to: "/pedidos",
      icon: <FiCheckSquare size={36} />,
      title: "Avaliar Pedidos",
      description: "Analise e emita parecer sobre os pedidos.",
    },
    {
      to: "/contratos/novo",
      icon: <FiClipboard size={36} />,
      title: "Executar Contrato",
      description: "Formalize contratos de pedidos aprovados.",
    },
    {
      to: "/contratos",
      icon: <FiFileText size={36} />,
      title: "Contratos",
      description: "Visualize todos os contratos firmados.",
    },
  ];

  const adminCards: Card[] = [
    {
      to: "/clientes",
      icon: <FiUsers size={36} />,
      title: "Clientes",
      description: "Gerencie todos os clientes do sistema.",
    },
    {
      to: "/clientes/novo",
      icon: <FiUserPlus size={36} />,
      title: "Cadastrar Cliente",
      description: "Registre um novo cliente.",
    },
    {
      to: "/clientes/buscar",
      icon: <FiSearch size={36} />,
      title: "Buscar por CPF",
      description: "Encontre um cliente pelo CPF.",
    },
    {
      to: "/automoveis",
      icon: <MdDirectionsCar size={36} />,
      title: "Frota de Automóveis",
      description: "Gerencie todos os veículos cadastrados.",
    },
    {
      to: "/automoveis/novo",
      icon: <FiPlusCircle size={36} />,
      title: "Novo Automóvel",
      description: "Cadastre um novo veículo na frota.",
    },
    {
      to: "/pedidos",
      icon: <FiFileText size={36} />,
      title: "Todos os Pedidos",
      description: "Visualize e gerencie todos os pedidos.",
    },
    {
      to: "/contratos",
      icon: <FiClipboard size={36} />,
      title: "Contratos",
      description: "Visualize todos os contratos formalizados.",
    },
  ];

  const cards = isAdmin ? adminCards : isAgente ? agenteCards : clienteCards;

  const perfilLabel: Record<string, string> = {
    CLIENTE: "Cliente",
    AGENTE:  "Agente",
    ADMIN:   "Administrador",
  };

  const perfilDesc: Record<string, string> = {
    CLIENTE: "Solicite e acompanhe seus pedidos de aluguel.",
    AGENTE:  "Analise pedidos, emita pareceres e formalize contratos.",
    ADMIN:   "Gerencie clientes, veículos, pedidos e contratos.",
  };

  const perfil = user?.perfil ?? "";
  const nomeExibicao =
    user?.nome?.trim() || user?.cpf || "";

  return (
    <div className="home">
      <div className="hero">
        <h1>Bem-vindo, {nomeExibicao}</h1>
        <p>
          <strong>{perfilLabel[perfil] ?? perfil}</strong>&nbsp;—&nbsp;
          {perfilDesc[perfil] ?? ""}
        </p>
      </div>

      <div className="home-cards">
        {cards.map(({ to, icon, title, description }) => (
          <Link key={to} to={to} className="home-card">
            {icon}
            <h2>{title}</h2>
            <p>{description}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
