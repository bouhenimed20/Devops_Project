package tn.esprit.spring.Services.Bloc;

import tn.esprit.spring.DAO.Entities.Bloc;

import java.util.List;

public interface IBlocService {
    Bloc addOrUpdate(Bloc b);
    Bloc addOrUpdate2(Bloc b);

    List<Bloc> findAll();

    Bloc findById(long id);

    void deleteById(long id);

    void delete(Bloc b);

    Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc);
    Bloc affecterBlocAFoyer( String nomBloc,  String nomFoyer) ;


}
