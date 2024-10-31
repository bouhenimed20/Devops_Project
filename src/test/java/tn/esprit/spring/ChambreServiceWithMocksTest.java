package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.dao.entities.*;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.services.chambre.ChambreService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChambreServiceWithMocksTest {

   @Mock
   private ChambreRepository chambreRepository;

   @Mock
   private BlocRepository blocRepository;

   @InjectMocks
   private ChambreService chambreService;

   @BeforeEach
   public void setUp() {
      // Mock initialization is handled by MockitoExtension
   }

   @Test
   void testAddOrUpdate() {
      Chambre chambre = new Chambre();
      when(chambreRepository.save(chambre)).thenReturn(chambre);

      Chambre result = chambreService.addOrUpdate(chambre);

      assertEquals(chambre, result);
      verify(chambreRepository, times(1)).save(chambre);
   }

   @Test
   void testFindAll() {
      List<Chambre> chambres = Arrays.asList(new Chambre(), new Chambre());
      when(chambreRepository.findAll()).thenReturn(chambres);

      List<Chambre> result = chambreService.findAll();

      assertEquals(chambres, result);
      verify(chambreRepository, times(1)).findAll();
   }

   @Test
   void testFindById() {
      Chambre chambre = new Chambre();
      when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

      Chambre result = chambreService.findById(1L);

      assertEquals(chambre, result);
      verify(chambreRepository, times(1)).findById(1L);
   }

   @Test
   void testDeleteById() {
      doNothing().when(chambreRepository).deleteById(1L);

      chambreService.deleteById(1L);

      verify(chambreRepository, times(1)).deleteById(1L);
   }

   @Test
   void testDelete() {
      Chambre chambre = new Chambre();
      doNothing().when(chambreRepository).delete(chambre);

      chambreService.delete(chambre);

      verify(chambreRepository, times(1)).delete(chambre);
   }

   // Other tests remain unchanged...



@Test
     void testGetChambresParNomBloc() {
        List<Chambre> chambres = Arrays.asList(new Chambre(), new Chambre());
        when(chambreRepository.findByBlocNomBloc("Bloc A")).thenReturn(chambres);

        List<Chambre> result = chambreService.getChambresParNomBloc("Bloc A");

        assertEquals(chambres, result);
        verify(chambreRepository, times(1)).findByBlocNomBloc("Bloc A");
    }

    @Test
     void testNbChambreParTypeEtBloc() {
        when(chambreRepository.countByTypeCAndBlocIdBloc(TypeChambre.SIMPLE, 1L)).thenReturn(Math.toIntExact(5L));

        long result = chambreService.nbChambreParTypeEtBloc(TypeChambre.SIMPLE, 1L);

        assertEquals(5L, result);
        verify(chambreRepository, times(1)).countByTypeCAndBlocIdBloc(TypeChambre.SIMPLE, 1L);
    }

    @Test
     void testGetChambresNonReserveParNomFoyerEtTypeChambre() {
        // Setup dates for the test
          LocalDate.of(2023, 9, 15);
         LocalDate.of(2024, 6, 30);

        // Create a Foyer object
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Foyer A"); // Set the name of the Foyer

        // Create a Bloc object and associate it with the Foyer
        Bloc bloc = new Bloc();
        bloc.setNomBloc("Bloc A");
        bloc.setFoyer(foyer); // Associate Foyer with the Bloc

        // Create a Chambre object and associate it with the Bloc
        Chambre chambre = new Chambre();
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setBloc(bloc); // Set the Bloc
        chambre.setReservations(Collections.emptyList()); // No reservations

        // Mock the chambreRepository to return the Chambre object
        when(chambreRepository.findAll()).thenReturn(Collections.singletonList(chambre));

        // Call the method under test
        List<Chambre> result = chambreService.getChambresNonReserveParNomFoyerEtTypeChambre("Foyer A", TypeChambre.SIMPLE);

        // Validate the results
        assertEquals(1, result.size());
        verify(chambreRepository, times(1)).findAll();
    }


    @Test
     void testPourcentageChambreParTypeChambre() {
        when(chambreRepository.count()).thenReturn(10L);
        when(chambreRepository.countChambreByTypeC(TypeChambre.SIMPLE)).thenReturn(4L);
        when(chambreRepository.countChambreByTypeC(TypeChambre.DOUBLE)).thenReturn(3L);
        when(chambreRepository.countChambreByTypeC(TypeChambre.TRIPLE)).thenReturn(3L);

        chambreService.pourcentageChambreParTypeChambre();

        verify(chambreRepository, times(1)).count();
        verify(chambreRepository, times(1)).countChambreByTypeC(TypeChambre.SIMPLE);
        verify(chambreRepository, times(1)).countChambreByTypeC(TypeChambre.DOUBLE);
        verify(chambreRepository, times(1)).countChambreByTypeC(TypeChambre.TRIPLE);
    }

    @Test
     void testNbPlacesDisponibleParChambreAnneeEnCours() {
        // Adjust dates as necessary, based on the expected behavior of your service method
        LocalDate dateDebutAU = LocalDate.of(2024, 9, 15);  // This might be calculated in the service
        LocalDate dateFinAU = LocalDate.of(2025, 6, 30); // This might be calculated in the service

        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setNumeroChambre(101);

        when(chambreRepository.findAll()).thenReturn(Collections.singletonList(chambre));
        when(chambreRepository.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                1L, true, dateDebutAU, dateFinAU)).thenReturn(0L);

        chambreService.nbPlacesDisponibleParChambreAnneeEnCours();

        verify(chambreRepository, times(1)).findAll();
        verify(chambreRepository, times(1)).countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(
                1L, true, dateDebutAU, dateFinAU);
    }

}
