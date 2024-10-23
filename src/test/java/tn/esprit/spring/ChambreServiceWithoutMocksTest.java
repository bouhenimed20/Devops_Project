/*package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.Services.Chambre.ChambreService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest to load the full application context and all related beans.
@SpringBootTest
public class ChambreServiceWithoutMocksTest {

    @Autowired
    private ChambreService chambreService; // The service to be tested

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private BlocRepository blocRepository;

    private Bloc bloc;

    @BeforeEach
    public void setUp() {
        // Clear any existing data before each test
        chambreRepository.deleteAll();
        blocRepository.deleteAll();

        // Setting up test data
        bloc = new Bloc();
        bloc.setNomBloc("Bloc A");
        bloc.setCapaciteBloc(10);
        blocRepository.save(bloc);  // Save Bloc to be linked with Chambre

        Chambre chambre = new Chambre();
        chambre.setNumeroChambre(101);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setBloc(bloc);
        chambreRepository.save(chambre);  // Save Chambre
    }

    @Test
    public void testAddOrUpdateChambre() {
        // Arrange
        Bloc bloc = new Bloc(); // Initialize the bloc object appropriately
        bloc.setName("Bloc A"); // Assuming there's a name or other attributes to set

        Chambre chambre = new Chambre();
        chambre.setNumeroChambre(102);
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.setBloc(bloc);

        // Act
        Chambre savedChambre = chambreService.addOrUpdate(chambre);

        // Assert
        assertNotNull(savedChambre, "Saved chambre should not be null");
        assertEquals(102, savedChambre.getNumeroChambre(), "Chambre number should match");
        assertEquals(TypeChambre.DOUBLE, savedChambre.getTypeC(), "Chambre type should match");
        assertNotNull(savedChambre.getBloc(), "Bloc should not be null");
        assertEquals(bloc.getName(), savedChambre.getBloc().getName(), "Bloc name should match");
    }


    @Test
    public void testFindAllChambres() {
        List<Chambre> chambres = chambreService.findAll();
        assertFalse(chambres.isEmpty());
        assertEquals(1, chambres.size());
    }

    @Test
    public void testFindChambreById() {
        Chambre chambre = chambreRepository.findAll().get(0); // Fetch the first Chambre
        Chambre foundChambre = chambreService.findById(chambre.getIdChambre());
        assertNotNull(foundChambre);
        assertEquals(chambre.getNumeroChambre(), foundChambre.getNumeroChambre());
    }

    @Test
    public void testDeleteChambreById() {
        Chambre chambre = chambreRepository.findAll().get(0);
        chambreService.deleteById(chambre.getIdChambre());

        Optional<Chambre> deletedChambre = chambreRepository.findById(chambre.getIdChambre());
        assertFalse(deletedChambre.isPresent());
    }

    @Test
    public void testGetChambresParNomBloc() {
        List<Chambre> chambres = chambreService.getChambresParNomBloc("Bloc A");
        assertEquals(1, chambres.size());
        assertEquals("Bloc A", chambres.get(0).getBloc().getNomBloc());
    }

    @Test
    public void testNbChambreParTypeEtBloc() {
        long count = chambreService.nbChambreParTypeEtBloc(TypeChambre.SIMPLE, bloc.getIdBloc());
        assertEquals(1, count);
    }

    // Add more tests for other methods, as necessary
}
*/