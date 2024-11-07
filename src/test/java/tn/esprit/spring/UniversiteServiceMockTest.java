package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.dao.repositories.UniversiteRepository;
import tn.esprit.spring.services.universite.UniversiteService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class UniversiteServiceMockTest {
    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteService universiteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Universite universite = new Universite();
        universite.setNomUniversite("Test University");
        when(universiteRepository.save(universite)).thenReturn(universite);

        universiteService.addOrUpdate(universite);


        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testFindAll() {
        List<Universite> universities = new ArrayList<>();
        universities.add(new Universite());
        when(universiteRepository.findAll()).thenReturn(universities);

        List<Universite> result = universiteService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Universite universite = new Universite();
        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

        Universite result = universiteService.findById(1L);

        assertNotNull(result);
        verify(universiteRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        universiteService.deleteById(1L);
        verify(universiteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete() {
        Universite universite = new Universite();
        universiteService.delete(universite);
        verify(universiteRepository, times(1)).delete(universite);
    }
}

