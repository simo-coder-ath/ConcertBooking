import { useEffect, useState } from "react";
import client from "../api/client";
import EventCard from "../components/EventCard";

export default function Events() {
  const [evenements, setEvenements] = useState([]);
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState(null);

  useEffect(() => {
    client
      .get("/api/evenements")
      .then(({ data }) =>
        setEvenements(data.filter((e) => e.statut === "PUBLIE"))
      )
      .catch(() => setErreur("Impossible de charger les concerts."))
      .finally(() => setChargement(false));
  }, []);

  if (chargement) return <p className="state-message">Chargement des concerts…</p>;
  if (erreur) return <p className="state-message">{erreur}</p>;
  if (evenements.length === 0)
    return <p className="state-message">Aucun concert n'est en vente pour l'instant.</p>;

  return (
    <div>
      <h1 className="events-page__title">Prochains concerts</h1>
      <div className="events-grid">
        {evenements.map((ev) => (
          <EventCard key={ev.id} evenement={ev} />
        ))}
      </div>
    </div>
  );
}