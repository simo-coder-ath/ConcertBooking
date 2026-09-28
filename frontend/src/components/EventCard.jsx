import { Link } from "react-router-dom";

export default function EventCard({ evenement }) {
  const date = new Date(evenement.dateEvenement).toLocaleDateString("fr-FR", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });

  return (
    <Link to={`/evenements/${evenement.id}`} className="ticket-card">
      <div className="ticket-card__main">
        <span className="ticket-card__date">{date}</span>
        <h3 className="ticket-card__title">{evenement.titre}</h3>
        <p className="ticket-card__desc">{evenement.description}</p>
      </div>
      <div className="ticket-card__stub">
        <span>Réserver</span>
        <span className="ticket-card__arrow">→</span>
      </div>
    </Link>
  );
}