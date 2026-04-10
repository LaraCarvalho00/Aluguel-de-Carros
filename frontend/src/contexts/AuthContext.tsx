import { createContext, useContext, useState, useEffect, ReactNode } from "react";

interface AuthUser {
  cpf: string;
  perfil: string;
}

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  login: (cpf: string, senha: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  hasRole: (role: string) => boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

function decodeJwt(token: string): { sub: string; roles: string[] } | null {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return { sub: payload.sub, roles: payload.roles ?? [] };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem("token")
  );
  const [user, setUser] = useState<AuthUser | null>(() => {
    const t = localStorage.getItem("token");
    if (!t) return null;
    const decoded = decodeJwt(t);
    if (!decoded) return null;
    return { cpf: decoded.sub, perfil: decoded.roles[0] ?? "" };
  });

  useEffect(() => {
    if (token) {
      localStorage.setItem("token", token);
    } else {
      localStorage.removeItem("token");
    }
  }, [token]);

  async function login(cpf: string, senha: string) {
    const response = await fetch("/api/v1/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: cpf, password: senha }),
    });

    if (!response.ok) {
      throw new Error("CPF ou senha inválidos");
    }

    const data = await response.json();
    const accessToken: string = data.access_token;
    const decoded = decodeJwt(accessToken);

    if (!decoded) throw new Error("Token inválido");

    setToken(accessToken);
    setUser({ cpf: decoded.sub, perfil: decoded.roles[0] ?? "" });
  }

  function logout() {
    setToken(null);
    setUser(null);
  }

  function hasRole(role: string) {
    return user?.perfil === role;
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        logout,
        isAuthenticated: !!token,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de AuthProvider");
  return ctx;
}
