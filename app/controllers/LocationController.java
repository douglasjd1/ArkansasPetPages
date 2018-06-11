package controllers;

import models.Location;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

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
            return ok(views.html.breeder.render(location));

        return ok(views.html.shelter.render(location));
    }
}
