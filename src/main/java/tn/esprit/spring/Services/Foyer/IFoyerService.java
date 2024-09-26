package tn.esprit.spring.Services.Foyer;

import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.Universite;

import java.util.List;

public interface IFoyerService {
    Foyer addOrUpdate(Foyer f);

    List<Foyer> findAll();

    Foyer findById(long id);

    void deleteById(long id);

    void delete(Foyer f);

    Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite);

    Universite desaffecterFoyerAUniversite(long idUniversite);

    Foyer ajouterFoyerEtAffecterAUniversite (Foyer foyer, long idUniversite); // Universite: Parent / Foyer:Child
    Foyer ajoutFoyerEtBlocs(Foyer foyer);


}
