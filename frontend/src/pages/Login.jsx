import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [erreur, setErreur] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setErreur(null);
    try {
      await login(email, motDePasse);
      navigate("/");
    } catch {
      setErreur("Email ou mot de passe incorrect.");
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Se connecter</h1>
        <p className="auth-card__subtitle">Accédez à vos réservations.</p>

        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Mot de passe
          <input
            type="password"
            value={motDePasse}
            onChange={(e) => setMotDePasse(e.target.value)}
            required
          />
        </label>

        {erreur && <p className="auth-card__error">{erreur}</p>}

        <button type="submit" className="btn btn--accent">
          Se connecter
        </button>

        <p className="auth-card__footer">
          Pas encore de compte ? <Link to="/register">S'inscrire</Link>
        </p>
      </form>
    </div>
  );
}