import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import { AuthProvider } from "./contexts/AuthContext";
import Header from "./components/Header";
import PrivateRoute from "./components/PrivateRoute";
import LoginPage from "./pages/LoginPage";
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
import ContratoListPage from "./pages/ContratoListPage";
import ContratoCreatePage from "./pages/ContratoCreatePage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/*"
            element={
              <PrivateRoute>
                <>
                  <Header />
                  <main className="container">
                    <Routes>
                      <Route path="/" element={<Home />} />
                      <Route
                        path="/clientes"
                        element={
                          <PrivateRoute roles={["AGENTE", "ADMIN"]}>
                            <ClienteListPage />
                          </PrivateRoute>
                        }
                      />
                      <Route
                        path="/clientes/novo"
                        element={
                          <PrivateRoute roles={["AGENTE", "ADMIN"]}>
                            <ClienteCreatePage />
                          </PrivateRoute>
                        }
                      />
                      <Route
                        path="/clientes/editar/:id"
                        element={
                          <PrivateRoute roles={["AGENTE", "ADMIN"]}>
                            <ClienteEditPage />
                          </PrivateRoute>
                        }
                      />
                      <Route
                        path="/clientes/buscar"
                        element={
                          <PrivateRoute roles={["AGENTE", "ADMIN"]}>
                            <ClienteSearchPage />
                          </PrivateRoute>
                        }
                      />
                      <Route path="/automoveis" element={<AutomovelListPage />} />
                      <Route
                        path="/automoveis/novo"
                        element={
                          <PrivateRoute roles={["ADMIN"]}>
                            <AutomovelCreatePage />
                          </PrivateRoute>
                        }
                      />
                      <Route
                        path="/automoveis/editar/:id"
                        element={
                          <PrivateRoute roles={["ADMIN"]}>
                            <AutomovelEditPage />
                          </PrivateRoute>
                        }
                      />
                      <Route path="/pedidos" element={<PedidoListPage />} />
                      <Route
                        path="/pedidos/novo"
                        element={
                          <PrivateRoute roles={["CLIENTE", "ADMIN"]}>
                            <PedidoCreatePage />
                          </PrivateRoute>
                        }
                      />
                      <Route
                        path="/contratos"
                        element={
                          <PrivateRoute roles={["AGENTE", "ADMIN"]}>
                            <ContratoListPage />
                          </PrivateRoute>
                        }
                      />
                      <Route
                        path="/contratos/novo"
                        element={
                          <PrivateRoute roles={["AGENTE", "ADMIN"]}>
                            <ContratoCreatePage />
                          </PrivateRoute>
                        }
                      />
                    </Routes>
                  </main>
                </>
              </PrivateRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
