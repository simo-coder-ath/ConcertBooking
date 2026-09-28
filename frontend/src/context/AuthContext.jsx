import { createContext, useContext, useState } from "react";
import client from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [utilisateurId, setUtilisateurId] = useState(
    localStorage.getItem("utilisateurId")
  );

  async function login(email, motDePasse) {
    const { data } = await client.post("/api/auth/login", {
      email,
      motDePasse,
    });

    localStorage.setItem("token", data.token);
    localStorage.setItem("utilisateurId", data.utilisateurId);
    setToken(data.token);
    setUtilisateurId(data.utilisateurId);
  }

  async function register(payload) {
    const { data } = await client.post("/api/utilisateurs", payload);
    return data;
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("utilisateurId");
    setToken(null);
    setUtilisateurId(null);
  }

  return (
    <AuthContext.Provider
      value={{ token, utilisateurId, login, register, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);