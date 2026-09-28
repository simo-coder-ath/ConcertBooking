import { useState } from "react";
import { Link, useParams, useLocation } from "react-router-dom";
import client from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function Checkout() {
  const { commandeId } = useParams();
  const { state } = useLocation();
  const { utilisateurId } = useAuth();

  const reservation = state?.reservation;
  const [statut, setStatut] = useState("idle"); // idle | envoi | succes
  const [resultat, setResultat] = useState(null);
  const [erreur, setErreur] = useState(null);

  async function handlePayer() {
    setStatut("envoi");
    setErreur(null);

    try {
      const { data } = await client.post("/api/paiements", {
        commandeId: Number(commandeId),
        utilisateurId: Number(utilisateurId),
        montant: reservation.montantTotal,
        paymentToken: "tok_test_visa",
      });
      setResultat(data);
      setStatut("succes");
    } catch (err) {
      setErreur(err.response?.data?.message || "Le paiement a échoué.");
      setStatut("idle");
    }
  }

  if (!reservation) {
    return (
      <p className="state-message">
        Aucune commande en cours. <Link to="/">Retour aux concerts</Link>
      </p>
    );
  }

  const expiration = new Date(reservation.expiresAt).toLocaleTimeString(
    "fr-FR",
    { hour: "2-digit", minute: "2-digit" }
  );

  return (
    <div className="checkout-page">
      <div className="stub-ticket">
        <div className="stub-ticket__main">
          <span className="stub-ticket__label">Commande</span>
          <h1>{reservation.reference}</h1>
          <p className="stub-ticket__amount">
            {Number(reservation.montantTotal).toFixed(2)} €
          </p>
          <p className="stub-ticket__meta">
            {statut === "succes"
              ? "Commande payée"
              : `À payer avant ${expiration}`}
          </p>
        </div>

        <div className="stub-ticket__perforation" />

        <div className="stub-ticket__action">
          {statut !== "succes" ? (
            <>
              <button
                className="btn btn--accent"
                onClick={handlePayer}
                disabled={statut === "envoi"}
              >
                {statut === "envoi" ? "Paiement en cours…" : "Payer maintenant"}
              </button>
              {erreur && <p className="auth-card__error">{erreur}</p>}
            </>
          ) : (
            <>
              <p className="stub-ticket__success">Paiement confirmé</p>
              <p className="stub-ticket__transaction">
                Transaction {resultat.transactionId}
              </p>
              <Link to="/" className="btn">
                Retour aux concerts
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}