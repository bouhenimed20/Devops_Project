package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.services.etudiant.EtudiantService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
}