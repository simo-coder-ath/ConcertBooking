import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { token, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="navbar">
      <Link to="/" className="navbar__brand">
        Concert<span>Booking</span>
      </Link>
      <nav className="navbar__links">
        {token ? (
          <button
            className="navbar__link navbar__link--ghost"
            onClick={() => {
              logout();
              navigate("/login");
            }}
          >
            Se déconnecter
          </button>
        ) : (
          <>
            <Link className="navbar__link" to="/login">
              Se connecter
            </Link>
            <Link className="navbar__link navbar__link--accent" to="/register">
              Créer un compte
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}