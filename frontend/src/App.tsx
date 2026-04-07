import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import Header from "./components/Header";
import Home from "./pages/Home";
import ClienteListPage from "./pages/ClienteListPage";
import ClienteCreatePage from "./pages/ClienteCreatePage";
import ClienteEditPage from "./pages/ClienteEditPage";
import ClienteSearchPage from "./pages/ClienteSearchPage";
import AutomovelListPage from "./pages/AutomovelListPage";
import AutomovelCreatePage from "./pages/AutomovelCreatePage";
import AutomovelEditPage from "./pages/AutomovelEditPage";
import PedidoListPage from "./pages/PedidoListPage";
import PedidoCreatePage from "./pages/PedidoCreatePage";

export default function App() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
      <Header />
      <main className="container">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/clientes" element={<ClienteListPage />} />
          <Route path="/clientes/novo" element={<ClienteCreatePage />} />
          <Route path="/clientes/editar/:id" element={<ClienteEditPage />} />
          <Route path="/clientes/buscar" element={<ClienteSearchPage />} />
          <Route path="/automoveis" element={<AutomovelListPage />} />
          <Route path="/automoveis/novo" element={<AutomovelCreatePage />} />
          <Route path="/automoveis/editar/:id" element={<AutomovelEditPage />} />
          <Route path="/pedidos" element={<PedidoListPage />} />
          <Route path="/pedidos/novo" element={<PedidoCreatePage />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}
