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
        // Preparation of test data
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");

        // Simulating the behavior of the mocks
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Calling the method
        Reservation result = reservationService.addOrUpdate(reservation);

        // Verifications
        assertNotNull(result);
        assertEquals("2023/2024-A-1-123456789", result.getIdReservation());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void testFindAllWithMocks() {
        // Preparation of test data
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());
        reservations.add(new Reservation());

        // Simulating the behavior of the mocks
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Calling the method
        List<Reservation> result = reservationService.findAll();

        // Verifications
        assertEquals(2, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdWithMocks() {
        // Preparation of test data
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");

        // Simulating the behavior of the mocks
        when(reservationRepository.findById("2023/2024-A-1-123456789")).thenReturn(Optional.of(reservation));

        // Calling the method
        Reservation result = reservationService.findById("2023/2024-A-1-123456789");

        // Verifications
        assertNotNull(result);
        assertEquals("2023/2024-A-1-123456789", result.getIdReservation());
        verify(reservationRepository, times(1)).findById("2023/2024-A-1-123456789");
    }

    @Test
    void testDeleteByIdWithMocks() {
        // Calling the method
        reservationService.deleteById("2023/2024-A-1-123456789");

        // Verifying that deleteById was called
        verify(reservationRepository, times(1)).deleteById("2023/2024-A-1-123456789");
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiantWithMocks() {
        // Preparation of test data
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");

        // Creating the Bloc object
        Bloc bloc = new Bloc();
        bloc.setNomBloc("Bloc A");

        // Creating the Chambre object and assigning the Bloc
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(1L);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setBloc(bloc); // Assigning Bloc to Chambre

        Etudiant etudiant = new Etudiant();
        etudiant.setCin(123456789L);

        // Simulating the behavior of the mocks
        when(chambreRepository.findByNumeroChambre(1L)).thenReturn(chambre);
        when(etudiantRepository.findByCin(123456789L)).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(anyLong(), any(), any())).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Calling the method
        Reservation result = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(1L, 123456789L);

        // Verifications
        assertNotNull(result);
        assertEquals("2023/2024-A-1-123456789", result.getIdReservation());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testAnnulerReservationWithMocks() {
        // Preparation of test data
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");
        reservation.setEstValide(true);

        // Simulating the behavior of the mocks
        when(reservationRepository.findByEtudiantsCinAndEstValide(123456789L, true)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation(reservation.getIdReservation())).thenReturn(new Chambre());

        // Calling the method
        String message = reservationService.annulerReservation(123456789L);

        // Verifications
        assertEquals("La réservation 2023/2024-A-1-123456789 est annulée avec succés", message);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    void testAnnulerReservationsWithMocks() {
        // Preparation of test data
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation1 = new Reservation();
        reservation1.setEstValide(true);
        reservations.add(reservation1);

        Reservation reservation2 = new Reservation();
        reservation2.setEstValide(true);
        reservations.add(reservation2);

        // Simulating the behavior of the mocks
        when(reservationRepository.findByEstValideAndAnneeUniversitaireBetween(anyBoolean(), any(LocalDate.class), any(LocalDate.class))).thenReturn(reservations);

        // Calling the method
        reservationService.annulerReservations();

        // Verifying that save was called for each reservation
        verify(reservationRepository, times(2)).save(any(Reservation.class));
    }

    @Test
    void testAffectReservationAChambreWithMocks() {
        // Preparation of test data
        Reservation reservation = new Reservation();
        reservation.setIdReservation("2023/2024-A-1-123456789");

        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);

        // Simulating the behavior of the mocks
        when(reservationRepository.findById("2023/2024-A-1-123456789")).thenReturn(Optional.of(reservation));
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        // Calling the method
        reservationService.affectReservationAChambre("2023/2024-A-1-123456789", 1L);

        // Verifying that save was called for the chambre
        verify(chambreRepository, times(1)).save(chambre);
    }
}
