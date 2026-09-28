import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: "",
    motDePasse: "",
    nom: "",
    prenom: "",
    telephone: "",
  });
  const [erreur, setErreur] = useState(null);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErreur(null);
    try {
      await register(form);
      navigate("/login");
    } catch (err) {
      setErreur(
        err.response?.data?.message || "Impossible de créer le compte."
      );
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Créer un compte</h1>
        <p className="auth-card__subtitle">
          Réservez vos places en quelques clics.
        </p>

        <label>
          Prénom
          <input name="prenom" value={form.prenom} onChange={handleChange} required />
        </label>
        <label>
          Nom
          <input name="nom" value={form.nom} onChange={handleChange} required />
        </label>
        <label>
          Email
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Mot de passe (8 caractères minimum)
          <input
            type="password"
            name="motDePasse"
            value={form.motDePasse}
            onChange={handleChange}
            required
            minLength={8}
          />
        </label>
        <label>
          Téléphone
          <input name="telephone" value={form.telephone} onChange={handleChange} />
        </label>

        {erreur && <p className="auth-card__error">{erreur}</p>}

        <button type="submit" className="btn btn--accent">
          Créer mon compte
        </button>

        <p className="auth-card__footer">
          Déjà inscrit ? <Link to="/login">Se connecter</Link>
        </p>
      </form>
    </div>
  );
}