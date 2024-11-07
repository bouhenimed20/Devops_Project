package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.services.etudiant.EtudiantService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // Fix for the ArrayList issue

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EtudiantServiceImplMockTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        // Arrange
        Etudiant etudiant = new Etudiant();
        when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        // Act
        Etudiant result = etudiantService.addOrUpdate(etudiant);

        // Assert
        assertNotNull(result);
        assertEquals(etudiant, result);
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void testFindAll() {
        // Arrange
        Etudiant etudiant1 = new Etudiant();
        Etudiant etudiant2 = new Etudiant();
        List<Etudiant> etudiants = Arrays.asList(etudiant1, etudiant2);
        when(etudiantRepository.findAll()).thenReturn(etudiants);

        // Act
        List<Etudiant> result = etudiantService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Arrange
        Etudiant etudiant = new Etudiant();
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        // Act
        Etudiant result = etudiantService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(etudiant, result);
        verify(etudiantRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        // Act
        etudiantService.deleteById(1L);

        // Assert
        verify(etudiantRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        // Arrange
        Etudiant etudiant = new Etudiant();

        // Act
        etudiantService.delete(etudiant);

        // Assert
        verify(etudiantRepository, times(1)).delete(etudiant);
    }

    @Test
    void testEtudiantEntity() {
        // Arrange
        Etudiant etudiant = new Etudiant();
        etudiant.setNomEt("John");
        etudiant.setPrenomEt("Doe");
        etudiant.setCin(12345678);
        etudiant.setEcole("EColeX");
        etudiant.setDateNaissance(LocalDate.of(1995, 5, 20));

        // Act
        String nom = etudiant.getNomEt();
        String prenom = etudiant.getPrenomEt();
        long cin = etudiant.getCin();
        String ecole = etudiant.getEcole();
        LocalDate dateNaissance = etudiant.getDateNaissance();

        // Assert
        assertEquals("John", nom);
        assertEquals("Doe", prenom);
        assertEquals(12345678, cin);
        assertEquals("EColeX", ecole);
        assertEquals(LocalDate.of(1995, 5, 20), dateNaissance);
    }

    @Test
    void testEtudiantWithReservations() {
        // Arrange
        Etudiant etudiant = new Etudiant();
        etudiant.setNomEt("Alice");
        etudiant.setPrenomEt("Smith");
        etudiant.setCin(98765432);
        etudiant.setEcole("SchoolY");
        etudiant.setDateNaissance(LocalDate.of(2000, 8, 15));

        Reservation reservation1 = new Reservation(); // Assuming Reservation has appropriate fields
        // reservation1.setId(1L); // Removed this line as setId is not required
        Reservation reservation2 = new Reservation();
        // reservation2.setId(2L); // Removed this line as well

        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        etudiant.setReservations(reservations);

        // Act
        List<Reservation> resultReservations = etudiant.getReservations();

        // Assert
        assertNotNull(resultReservations);
        assertEquals(2, resultReservations.size());
        assertTrue(resultReservations.contains(reservation1));
        assertTrue(resultReservations.contains(reservation2));
    }

    @Test
    void testEtudiantBuilder() {
        // Arrange & Act
        Etudiant etudiant = Etudiant.builder()
                .nomEt("Maria")
                .prenomEt("Jones")
                .cin(23456789)
                .ecole("AcademyZ")
                .dateNaissance(LocalDate.of(1997, 10, 30))
                .build();

        // Assert
        assertEquals("Maria", etudiant.getNomEt());
        assertEquals("Jones", etudiant.getPrenomEt());
        assertEquals(23456789, etudiant.getCin());
        assertEquals("AcademyZ", etudiant.getEcole());
        assertEquals(LocalDate.of(1997, 10, 30), etudiant.getDateNaissance());
    }

    @Test
    void testEtudiantNoArgsConstructor() {
        // Arrange
        Etudiant etudiant = new Etudiant();

        // Act
        etudiant.setNomEt("James");
        etudiant.setPrenomEt("Brown");

        // Assert
        assertEquals("James", etudiant.getNomEt());
        assertEquals("Brown", etudiant.getPrenomEt());
    }

    @Test
    void testEtudiantAllArgsConstructor() {
        // Arrange
        Etudiant etudiant = new Etudiant(1L, "John", "Doe", 12345678L, "EColeX", LocalDate.of(1995, 5, 20), new ArrayList<>());

        // Assert
        assertEquals("John", etudiant.getNomEt());
        assertEquals("Doe", etudiant.getPrenomEt());
        assertEquals(12345678L, etudiant.getCin());
        assertEquals("EColeX", etudiant.getEcole());
        assertEquals(LocalDate.of(1995, 5, 20), etudiant.getDateNaissance());
    }
}
