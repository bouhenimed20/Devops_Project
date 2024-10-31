package tn.esprit.spring.services.chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }


    @Override
    public Chambre findById(long id) {
        Optional<Chambre> optionalChambre = repo.findById(id);

        if (optionalChambre.isPresent()) {
            return optionalChambre.get();
        } else {
            // Handle the case where the Chambre is not found, e.g., throw an exception or return null
            throw new NoSuchElementException("Chambre not found with id: " + id);
        }
    }


    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        return repo.countByTypeCAndBlocIdBloc(type, idBloc);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        LocalDate[] anneeUniversitaireDates = getAnneeUniversitaireDates();
        List<Chambre> listChambreDispo = new ArrayList<>();

        for (Chambre c : repo.findAll()) {
            if (isChambreEligible(c, nomFoyer, type)) {
                int numReservation = countReservationsInCurrentYear(c, anneeUniversitaireDates);
                if (isChambreAvailable(c, numReservation)) {
                    listChambreDispo.add(c);
                }
            }
        }
        return listChambreDispo;
    }

    private LocalDate[] getAnneeUniversitaireDates() {
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
        return new LocalDate[]{dateDebutAU, dateFinAU};
    }

    private boolean isChambreEligible(Chambre c, String nomFoyer, TypeChambre type) {
        return c.getTypeC().equals(type) && c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer);
    }

    private int countReservationsInCurrentYear(Chambre c, LocalDate[] dates) {
        int numReservation = 0;
        for (Reservation reservation : c.getReservations()) {
            if (reservation.getAnneeUniversitaire().isBefore(dates[1]) && reservation.getAnneeUniversitaire().isAfter(dates[0])) {
                numReservation++;
            }
        }
        return numReservation;
    }

    private boolean isChambreAvailable(Chambre c, int numReservation) {
        switch (c.getTypeC()) {
            case SIMPLE:
                return numReservation == 0;
            case DOUBLE:
                return numReservation < 2;
            case TRIPLE:
                return numReservation < 3;
            default:
                return false;
        }
    }

    @Override
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() + " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {  // Check if the list of chambres is not empty
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() + " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }


    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100.0) / totalChambre;  // Cast to double
        double pDouble = (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100.0) / totalChambre;  // Cast to double
        double pTriple = (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100.0) / totalChambre;  // Cast to double

        log.info("Nombre total des chambres: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égal à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égal à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égal à " + pTriple);
    }


    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        LocalDate dateDebutAU = getDebutAnneeUniversitaire();
        LocalDate dateFinAU = getFinAnneeUniversitaire();

        for (Chambre c : repo.findAll()) {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                    c.getIdChambre(), true, dateDebutAU, dateFinAU);
            logAvailablePlaces(c, nbReservation);
        }
    }

    private LocalDate getDebutAnneeUniversitaire() {
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            return LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
        } else {
            return LocalDate.of(Integer.parseInt("20" + year), 9, 15);
        }
    }

    private LocalDate getFinAnneeUniversitaire() {
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            return LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            return LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
    }

    private void logAvailablePlaces(Chambre c, long nbReservation) {
        int placesTotal = getTotalPlaces(c);
        if (nbReservation < placesTotal) {
            log.info("Le nombre de place disponible pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (placesTotal - nbReservation));
        } else {
            log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complète");
        }
    }

    private int getTotalPlaces(Chambre c) {
        switch (c.getTypeC()) {
            case SIMPLE:
                return 1;
            case DOUBLE:
                return 2;
            case TRIPLE:
                return 3;
            default:
                return 0; // or throw an exception if type is not recognized
        }
    }
}
