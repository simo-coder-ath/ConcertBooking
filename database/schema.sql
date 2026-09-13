-- Enums métier
CREATE TYPE role_utilisateur AS ENUM ('CLIENT', 'ORGANISATEUR', 'ADMIN');
CREATE TYPE statut_evenement AS ENUM ('BROUILLON', 'PUBLIE', 'ANNULE', 'TERMINE');
CREATE TYPE statut_commande AS ENUM ('EN_ATTENTE_DE_PAIEMENT', 'PAYEE', 'ECHOUEE', 'ANNULEE_TIMEOUT', 'REMBOURSEE');
CREATE TYPE statut_place AS ENUM ('DISPONIBLE', 'VERROUILLEE', 'RESERVEE', 'VENDUE');











CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(30),
    role role_utilisateur DEFAULT 'CLIENT',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);














CREATE TABLE salles (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    adresse TEXT NOT NULL,
    ville VARCHAR(100) NOT NULL,
    code_postal VARCHAR(20) NOT NULL,
    capacite INT NOT NULL
);










CREATE TABLE sieges (
    id BIGSERIAL PRIMARY KEY,
    salle_id BIGINT NOT NULL REFERENCES salles(id) ON DELETE CASCADE,
    rang VARCHAR(10) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    zone VARCHAR(50), 
    CONSTRAINT uq_salle_siege UNIQUE (salle_id, rang, numero)
);








CREATE TABLE evenements (
    id BIGSERIAL PRIMARY KEY,
    salle_id BIGINT NOT NULL REFERENCES salles(id),
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    image_banner_url VARCHAR(500),
    date_ouverture_ventes TIMESTAMP WITH TIME ZONE,
    date_evenement TIMESTAMP WITH TIME ZONE NOT NULL,
    statut statut_evenement DEFAULT 'BROUILLON',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);







CREATE TABLE categories_prix (
    id BIGSERIAL PRIMARY KEY,
    evenement_id BIGINT NOT NULL REFERENCES evenements(id) ON DELETE CASCADE,
    nom VARCHAR(50) NOT NULL, -- Ex: "Carré Or", "VIP", "Tarif Reduit"
    prix DECIMAL(10, 2) NOT NULL,
    CONSTRAINT chk_prix_positif CHECK (prix >= 0)
);











CREATE TABLE places (
    id BIGSERIAL PRIMARY KEY,
    evenement_id BIGINT NOT NULL REFERENCES evenements(id) ON DELETE CASCADE,
    siege_id BIGINT NOT NULL REFERENCES sieges(id),
    categorie_prix_id BIGINT NOT NULL REFERENCES categories_prix(id),
    statut statut_place DEFAULT 'DISPONIBLE',
    version INT DEFAULT 0 NOT NULL, -- Champ critique pour le Verrouillage Optimiste (Concurrency Control)
    CONSTRAINT uq_evenement_siege UNIQUE (evenement_id, siege_id)
);









CREATE TABLE commandes (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL, 
    utilisateur_id BIGINT NOT NULL REFERENCES utilisateurs(id),
    statut statut_commande DEFAULT 'EN_ATTENTE_DE_PAIEMENT',
    montant_total DECIMAL(10, 2) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL, 
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);















CREATE TABLE lignes_commande (
    id BIGSERIAL PRIMARY KEY,
    commande_id BIGINT NOT NULL REFERENCES commandes(id) ON DELETE CASCADE,
    place_id BIGINT UNIQUE NOT NULL REFERENCES places(id), -- Empêche qu'une place soit attribuée à 2 commandes
    prix_unitaire DECIMAL(10, 2) NOT NULL
);







CREATE TABLE factures (
    id BIGSERIAL PRIMARY KEY,
    commande_id BIGINT UNIQUE NOT NULL REFERENCES commandes(id),
    montant_regle DECIMAL(10, 2) NOT NULL,
    payment_provider VARCHAR(50) NOT NULL, 
    transaction_id VARCHAR(255) NOT NULL, 
    pdf_ticket_url VARCHAR(500), 
    statut_paiement VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);





CREATE INDEX idx_places_evenement_statut 
ON places(evenement_id, statut) 
INCLUDE (categorie_prix_id);

















CREATE INDEX idx_commandes_timeout 
ON commandes(statut, expires_at) 
WHERE statut = 'EN_ATTENTE_DE_PAIEMENT';









CREATE INDEX idx_commandes_utilisateur 
ON commandes(utilisateur_id, created_at DESC);

