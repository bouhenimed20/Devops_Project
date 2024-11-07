package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.*;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;
import tn.esprit.spring.services.reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceWithMocksTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdateWithMocks() {
        // Préparation des données de test
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");
        reservation.setEstValide(true); // Ensure 'estValide' is set here.

        // Simulation du comportement des mocks
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Appel de la méthode
        Reservation result = reservationService.addOrUpdate(reservation);

        // Vérifications
        assertNotNull(result);
        assertEquals("2023/2024-A-1-123456789", result.getIdReservation());
        assertTrue(result.isEstValide()); // Ensure the value is checked
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void testFindAllWithMocks() {
        // Préparation des données de test
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());
        reservations.add(new Reservation());

        // Simulation du comportement des mocks
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Appel de la méthode
        List<Reservation> result = reservationService.findAll();

        // Vérifications
        assertEquals(2, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdWithMocks() {
        // Préparation des données de test
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");
        reservation.setEstValide(true); // Ensure 'estValide' is set here.

        // Simulation du comportement des mocks
        when(reservationRepository.findById("2023/2024-A-1-123456789")).thenReturn(Optional.of(reservation));

        // Appel de la méthode
        Reservation result = reservationService.findById("2023/2024-A-1-123456789");

        // Vérifications
        assertNotNull(result);
        assertEquals("2023/2024-A-1-123456789", result.getIdReservation());
        assertTrue(result.isEstValide()); // Ensure the value is checked
        verify(reservationRepository, times(1)).findById("2023/2024-A-1-123456789");
    }

    @Test
    void testDeleteByIdWithMocks() {
        // Appel de la méthode
        reservationService.deleteById("2023/2024-A-1-123456789");

        // Vérification que la méthode deleteById a été appelée
        verify(reservationRepository, times(1)).deleteById("2023/2024-A-1-123456789");
    }

    @Test
    void testDeleteReservationWithMocks() {
        // Préparation des données de test
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");

        // Appel de la méthode
        reservationService.delete(reservation);

        // Vérification que la méthode delete a été appelée sur le repository
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiantWithMocks() {
        // Préparation des données de test
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");
        reservation.setEstValide(true); // Ensure 'estValide' is set here.

        // Création de l'objet Bloc
        Bloc bloc = new Bloc();
        bloc.setNomBloc("Bloc A");

        // Création de l'objet Chambre et assignation du Bloc
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(1L);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setBloc(bloc); // Assigner le Bloc à la Chambre

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(123456789L);

        // Simulation du comportement des mocks
        when(chambreRepository.findByNumeroChambre(1L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(123456789L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(anyLong(), any(), any())).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Appel de la méthode
        Reservation result = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(1L, 123456789L);

        // Vérifications
        assertNotNull(result);
        assertEquals("2023/2024-A-1-123456789", result.getIdReservation());
        assertTrue(result.isEstValide()); // Ensure the value is checked
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testAnnulerReservationWithMocks() {
        // Préparation des données de test
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");
        reservation.setEstValide(true);

        // Simulation du comportement des mocks
        when(reservationRepository.findByEtudiantsCinAndEstValide(123456789L, true)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation(reservation.getIdReservation())).thenReturn(new Chambre());

        // Appel de la méthode
        String message = reservationService.annulerReservation(123456789L);

        // Vérifications
        assertEquals("La réservation 2023/2024-A-1-123456789 est annulée avec succés", message);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    void testAnnulerReservationsWithMocks() {
        // Préparation des données de test
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation1 = new Reservation();
        reservation1.setEstValide(true);
        reservations.add(reservation1);

        Reservation reservation2 = new Reservation();
        reservation2.setEstValide(true);
        reservations.add(reservation2);

        // Simulation du comportement des mocks
        when(reservationRepository.findByEstValideAndAnneeUniversitaireBetween(anyBoolean(), any(LocalDate.class), any(LocalDate.class))).thenReturn(reservations);

        // Appel de la méthode
        reservationService.annulerReservations();

        // Vérification que la méthode save a été appelée pour chaque réservation
        verify(reservationRepository, times(2)).save(any(Reservation.class));
    }

    @Test
    void testAffectReservationAChambreWithMocks() {
        // Préparation des données de test
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");
        reservation.setEstValide(true); // Ensure 'estValide' is set here.

        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);

        // Simulation du comportement des mocks
        when(reservationRepository.findById("2023/2024-A-1-123456789")).thenReturn(Optional.of(reservation));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        // Appel de la méthode
        reservationService.affectReservationAChambre("2023/2024-A-1-123456789", 1L);

        // Vérification que la méthode save a été appelée pour la chambre
        verify(chambreRepository, times(1)).save(chambre);
    }
}
