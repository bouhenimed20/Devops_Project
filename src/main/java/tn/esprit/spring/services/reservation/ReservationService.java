package tn.esprit.spring.services.reservation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService implements IReservationService
{
    ReservationRepository repo;
    ChambreRepository chambreRepository;
    EtudiantRepository etudiantRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    @Override
    public Reservation addOrUpdate(Reservation r) {
        return repo.save(r);
    }


    @Override
    public List<Reservation> findAll() {
        return repo.findAll();
    }

    @Override
    public Reservation findById(String id) {
        Optional<Reservation> optionalReservation = repo.findById(id);

        if (optionalReservation.isPresent()) {
            return optionalReservation.get();
        } else {
            throw new NoSuchElementException("Aucune réservation trouvée pour l'ID : " + id);
        }
    }


    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Reservation r) {
        repo.delete(r);
    }

    @Override
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(Long numChambre, long cin) {
        // Pour l’ajout de Réservation, l’id est un String et c’est la concaténation de "numeroChambre",
        // "nomBloc" et "cin". Aussi, l’ajout ne se fait que si la capacite maximale de la chambre est encore non atteinte.

        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        Reservation res = new Reservation();
        Chambre c = chambreRepository.findByNumeroChambre(numChambre);
        Etudiant e = etudiantRepository.findByCin(cin);
        boolean ajout = false;
        int numRes = chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(c.getIdChambre(), dateDebutAU, dateFinAU);
        logger.error("Erreur lors du traitement de la réservation : {}", numRes);
        switch (c.getTypeC()) {
            case SIMPLE:
                if (numRes < 1) {
                    ajout = true;
                } else {
                    log.info("Chambre simple remplie !");
                }
                break;
            case DOUBLE:
                if (numRes < 2) {
                    ajout = true;
                } else {
                    log.info("Chambre double remplie !");
                }
                break;
            case TRIPLE:
                if (numRes < 3) {
                    ajout = true;
                } else {
                    log.info("Chambre triple remplie !");
                }
                break;
        }
        if (ajout) {
            res.setEstValide(false);
            res.setAnneeUniversitaire(LocalDate.now());
            // AU-BLOC-NumChambre-CIN --> Exemple: 2023/2024-Bloc A-1-123456789
            res.setIdReservation(dateDebutAU.getYear() + "/" + dateFinAU.getYear() + "-" + c.getBloc().getNomBloc() + "-" + c.getNumeroChambre() + "-" + e.getCin());
            res.getEtudiants().add(e);
            res.setEstValide(true);
            res = repo.save(res);
            c.getReservations().add(res);
            chambreRepository.save(c);
        }
        return res;
    }

    @Override
    public long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee) {
        return repo.countByAnneeUniversitaireBetween(debutAnnee, finAnnee);
    }

    @Override
    public String annulerReservation(long cinEtudiant) {
        Reservation r = repo.findByEtudiantsCinAndEstValide(cinEtudiant, true);
        Chambre c = chambreRepository.findByReservationsIdReservation(r.getIdReservation());
        c.getReservations().remove(r);
        chambreRepository.save(c);
        repo.delete(r);
        return "La réservation " + r.getIdReservation() + " est annulée avec succés";
    }

    @Override
    public void affectReservationAChambre(String idRes, long idChambre) {
        Optional<Reservation> optionalReservation = repo.findById(idRes);
        Optional<Chambre> optionalChambre = chambreRepository.findById(idChambre);

        if (optionalReservation.isPresent() && optionalChambre.isPresent()) {
            Reservation r = optionalReservation.get();
            Chambre c = optionalChambre.get();
            c.getReservations().add(r);
            chambreRepository.save(c);
        } else {
            throw new NoSuchElementException("Réservation ou chambre non trouvée pour les IDs fournis.");
        }
    }

    @Override
    public void annulerReservations() {
        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        for (Reservation reservation : repo.findByEstValideAndAnneeUniversitaireBetween(true, dateDebutAU, dateFinAU)) {
            reservation.setEstValide(false);
            repo.save(reservation);
            log.info("La reservation "+ reservation.getIdReservation()+" est annulée automatiquement");
        }
    }

}
