import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import client from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function EventDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { token, utilisateurId } = useAuth();

  const [evenement, setEvenement] = useState(null);
  const [placeId, setPlaceId] = useState("");
  const [erreur, setErreur] = useState(null);
  const [envoi, setEnvoi] = useState(false);

  useEffect(() => {
    client
      .get(`/api/evenements/${id}`)
      .then(({ data }) => setEvenement(data))
      .catch(() => setErreur("Concert introuvable."));
  }, [id]);

  async function handleReserver(e) {
    e.preventDefault();

    if (!token) {
      navigate("/login");
      return;
    }

    setErreur(null);
    setEnvoi(true);

    try {
      const { data } = await client.post("/api/reservations", {
        utilisateurId: Number(utilisateurId),
        evenementId: Number(id),
        placeIds: [Number(placeId)],
      });

      navigate(`/checkout/${data.commandeId}`, {
        state: { reservation: data },
      });
    } catch (err) {
      setErreur(
        err.response?.data?.message || "Cette place n'a pas pu être réservée."
      );
    } finally {
      setEnvoi(false);
    }
  }

  if (!evenement) {
    return (
      <p className="state-message">{erreur || "Chargement du concert…"}</p>
    );
  }

  const date = new Date(evenement.dateEvenement).toLocaleDateString("fr-FR", {
    weekday: "long",
    day: "2-digit",
    month: "long",
    year: "numeric",
  });

  return (
    <div className="detail-page">
      <div className="detail-page__poster">
        <span className="detail-page__date">{date}</span>
        <h1>{evenement.titre}</h1>
        <p>{evenement.description}</p>
      </div>

      <form className="detail-page__form" onSubmit={handleReserver}>
        <h2>Réserver une place</h2>
        <p className="detail-page__hint">
          La place est bloquée pendant 10 minutes, le temps de payer.
        </p>

        <label>
          Numéro de la place
          <input
            type="number"
            min="1"
            value={placeId}
            onChange={(e) => setPlaceId(e.target.value)}
            required
          />
        </label>

        {erreur && <p className="auth-card__error">{erreur}</p>}

        <button type="submit" className="btn btn--accent" disabled={envoi}>
          {envoi ? "Réservation en cours…" : "Réserver cette place"}
        </button>
      </form>
    </div>
  );
}