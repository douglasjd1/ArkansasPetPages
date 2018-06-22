package controllers;

import models.*;
import models.BreederDetail;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import java.util.List;


import javax.inject.Inject;


public class LocationController extends Controller
{
    private JPAApi jpaApi;
    private FormFactory formFactory;

    @Inject
    public LocationController(JPAApi jpaApi, FormFactory formFactory)
    {
        this.jpaApi = jpaApi;
        this.formFactory = formFactory;
    }

    @Transactional
    public Result getNewLocation()
    {
        return ok(views.html.newlocation.render());
    }

    @Transactional
    public Result postNewLocation()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String locationName = form.get("locationName");
        String city = form.get("city");
        String breedId = form.get("breedId");
        String address = form.get("address");
        String stateId = form.get("stateId");
        String zipCode = form.get("zipCode");
        String phoneNumber = form.get("phoneNumber");

        Location location = new Location();

        location.setLocationName(locationName);
        location.setCity(city);
        if(!breedId.equals(""))
            location.setBreedId(Integer.parseInt(breedId));
        location.setAddress(address);
        location.setStateId(stateId);
        location.setZipCode(zipCode);
        location.setPhoneNumber(phoneNumber);

        jpaApi.em().persist(location);

        return ok(views.html.newlocation.render());
    }

    @Transactional
    public Result getLocation(Integer locationId)
    {
        String sql = "SELECT l FROM Location l " +
                "WHERE locationId = :locationId";

        Location location = jpaApi.em().
                createQuery(sql, Location.class).
                setParameter("locationId", locationId).
                getSingleResult();

        if(location.getBreedId() != null)
        {
            String breederSql = "SELECT NEW models.BreederDetail(l.locationId, l.locationName, l.city, b.breedName, l.address, s.stateName, l.zipCode, l.phoneNumber, l.websiteURL) " +
                         "FROM Location l " +
                         "JOIN Breed b ON b.breedId = l.breedId " +
                         "JOIN State s ON s.stateId = l.stateId " +
                         "WHERE locationId = :locationId";

            BreederDetail breeder = jpaApi.em().
                    createQuery(breederSql, BreederDetail.class).
                    setParameter("locationId", locationId).
                    getSingleResult();


            return ok(views.html.breeder.render(breeder));
        }

        else
        {
            String shelterSql = "SELECT NEW models.ShelterDetail(l.locationId, l.locationName, l.city, l.address, s.stateName, l.zipCode, l.phoneNumber, l.websiteURL) " +
                    "FROM Location l " +
                    "JOIN State s ON s.stateId = l.stateId " +
                    "WHERE locationId = :locationId";

            ShelterDetail shelter = jpaApi.em().
                    createQuery(shelterSql, ShelterDetail.class).
                    setParameter("locationId", locationId).
                    getSingleResult();

            String dogSql = "SELECT d FROM Dog d  WHERE d.locationId = :locationId";

            List<Dog> dogs = jpaApi.em().
                    createQuery(dogSql, Dog.class).
                    setParameter("locationId", locationId).
                    getResultList();
            return ok(views.html.shelter.render(shelter, dogs));
        }

    }

    @Transactional
    public Result getShelters()
    {
        String sql = "SELECT l FROM Location l WHERE l.breedId IS NULL";

        List<Location> shelters = jpaApi.em().createQuery(sql, Location.class).getResultList();

        return ok(views.html.shelters.render(shelters));
    }

    @Transactional
    public Result getBreeders()
    {
        String sql = "SELECT l FROM Location l WHERE l.breedId IS NOT NULL";

        List<Location> breeders = jpaApi.em().createQuery(sql, Location.class).getResultList();

        return ok(views.html.breeders.render(breeders));
    }
}
