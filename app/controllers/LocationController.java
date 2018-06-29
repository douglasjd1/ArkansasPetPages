package controllers;

import models.*;
import models.BreederDetail;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Result;
import java.util.List;


import javax.inject.Inject;


public class LocationController extends ApplicationController
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
            String breederSql = "SELECT NEW models.BreederDetail(l.locationId, l.locationName, l.address, " +
                                "l.city, s.stateName, l.zipCode, b.breedName, b.breedId, l.phoneNumber, l.emailAddress, l.websiteURL) " +
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
            String shelterSql = "SELECT NEW models.ShelterDetail(l.locationId, l.locationName, l.city, " +
                                "l.address, s.stateName, l.zipCode, l.phoneNumber, l.websiteURL, l.emailAddress) " +
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

    @Transactional(readOnly = true)
    public Result getCreateShelterAccount(String status)
    {
        String stateSql = "SELECT s FROM State s";

        List<State> states = jpaApi.em().createQuery(stateSql, State.class).getResultList();

        return ok(views.html.createshelteraccount.render(states, ""));
    }

    @Transactional
    public Result postCreateShelterAccount()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String locationName = form.get("locationName");
        String city = form.get("city");
        String address = form.get("streetAddress");
        String stateName = form.get("state");
        String zipCode = form.get("zipCode");
        String phoneNumber = form.get("phoneNumber");
        String emailAddress = form.get("emailAddress");
        String URL = form.get("URL");
        String password = form.get("locationPassword");

        if(!isNewEmail(emailAddress, jpaApi))
        {
            return redirect(routes.LocationController.getCreateShelterAccount("That email is already in use"));
        }

        String stateSql = "SELECT s FROM State s WHERE s.stateName = :stateName";

        State state = jpaApi.em().createQuery(stateSql, State.class).setParameter("stateName", stateName).getSingleResult();

        Location location = new Location();

        location.setLocationName(locationName);
        location.setCity(city);
        location.setAddress(address);
        location.setZipCode(zipCode);
        location.setPhoneNumber(phoneNumber);
        location.setEmailAddress(emailAddress);
        location.setWebsiteURL(URL);
        location.setStateId(state.getStateId());

        try
        {
            byte[] salt = Password.getNewSalt();
            byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

            location.setSalt(salt);
            location.setLocationPassword(hashedPassword);
        }

        catch(Exception e)
        {
            e.printStackTrace();

            String statesSql = "SELECT s FROM State s";
            List<State> states = jpaApi.em().createQuery(statesSql, State.class).getResultList();

            return ok(views.html.createshelteraccount.render(states, "Error creating account"));
        }

        jpaApi.em().persist(location);

        logInLocation(location.getEmailAddress());

        return redirect(routes.LocationController.getShelterPage("Account successfully created"));
    }

    @Transactional(readOnly = true)
    public Result getShelterPage(String status)
    {
        if(isLoggedIn())
        {
            String emailAddress = session().get("loggedIn");
            String sql = "SELECT NEW models.ShelterDetail(l.locationId, l.locationName, l.city, l.address, s.stateName, " +
                    "l.zipCode, l.phoneNumber, l.websiteURL, l.emailAddress) " +
                    "FROM Location l " +
                    "JOIN State s ON s.stateId = l.stateId " +
                    "WHERE l.emailAddress = :emailAddress";

            ShelterDetail location = jpaApi.em().createQuery(sql, ShelterDetail.class).
                                      setParameter("emailAddress", emailAddress).getSingleResult();

            int locationId = location.getLocationId();

            String dogSql = "SELECT d FROM Dog d WHERE d.locationId = :locationId";

            List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("locationId", locationId).getResultList();
            return ok(views.html.shelterpage.render(location, dogs, ""));
        }
        return redirect(routes.UserController.getLogIn("Log in to access this page."));
    }

    @Transactional(readOnly = true)
    public Result getEditShelter()
    {
        if(isLoggedIn())
        {
            String emailAddress = session().get("loggedIn");
            String sql = "SELECT NEW models.ShelterDetail(l.locationId, l.locationName, l.address, l.city, s.stateName, " +
                    "l.zipCode, l.phoneNumber, l.websiteURL, l.emailAddress) " +
                    "FROM Location l " +
                    "JOIN State s ON s.stateId = l.stateId " +
                    "WHERE l.emailAddress = :emailAddress";

            ShelterDetail location = jpaApi.em().createQuery(sql, ShelterDetail.class).
                    setParameter("emailAddress", emailAddress).getSingleResult();

            String stateSql = "SELECT s FROM State s";

            List<State> states = jpaApi.em().createQuery(stateSql, State.class).getResultList();

            return ok(views.html.editshelter.render(location, states, ""));
        }
        else
        {
            return redirect(routes.UserController.getLogIn("Please log in to view this page"));
        }
    }

    @Transactional
    public Result postEditShelter()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String loggedInEmail = session().get("loggedIn");
        String breederSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        Location breeder = jpaApi.em().createQuery(breederSql, Location.class).
                setParameter("emailAddress", loggedInEmail).getSingleResult();

        String locationName = form.get("locationName");
        String address = form.get("streetAddress");
        String city = form.get("city");
        String stateName = form.get("state");
        String zipCode = form.get("zipCode");
        String phoneNumber = form.get("phoneNumber");
        String websiteUrl = form.get("URL");
        String emailAddress = form.get("emailAddress");
        String password = form.get("locationPassword");

        byte salt[] = breeder.getSalt();
        byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

        String stateSql = "SElECT s FROM State s WHERE s.stateName = :stateName";

        State state = jpaApi.em().createQuery(stateSql, State.class).
                      setParameter("stateName", stateName).getSingleResult();

        breeder.setLocationName(locationName);
        breeder.setAddress(address);
        breeder.setCity(city);
        breeder.setStateId(state.getStateId());
        breeder.setZipCode(zipCode);
        breeder.setPhoneNumber(phoneNumber);
        breeder.setWebsiteURL(websiteUrl);
        breeder.setEmailAddress(emailAddress);
        breeder.setLocationPassword(hashedPassword);

        return redirect(routes.LocationController.getShelterPage("Changes successfully saved"));
    }

    @Transactional
    public Result getDeleteShelterAccount()
    {
        String emailAddress = session().get("loggedIn");

        String locationSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        Location location = jpaApi.em().createQuery(locationSql, Location.class).
                            setParameter("emailAddress", emailAddress).getSingleResult();

        int locationId = location.getLocationId();

        String dogSql = "SELECT d FROM Dog d WHERE d.locationId = :locationId";

        List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).
                                    setParameter("locationId", locationId).getResultList();

        for(Dog dog : dogs)
        {
            jpaApi.em().remove(dog);
        }

        jpaApi.em().remove(location);

        return redirect(routes.UserController.getLogIn("Account successfully deleted"));
    }

    @Transactional
    public Result getBreeders()
    {
        String sql = "SELECT l FROM Location l WHERE l.breedId IS NOT NULL";

        List<Location> breeders = jpaApi.em().createQuery(sql, Location.class).getResultList();

        return ok(views.html.breeders.render(breeders));
    }

    @Transactional(readOnly = true)
    public Result getCreateBreederAccount(String status)
    {
        String stateSql = "SELECT s FROM State s";

        List<State> states = jpaApi.em().createQuery(stateSql, State.class).getResultList();

        String breedSql = "SELECT b FROM Breed b";

        List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

        return ok(views.html.createbreederaccount.render(breeds, states, status));
    }

    @Transactional
    public Result postCreateBreederAccount()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String locationName = form.get("locationName");
        String city = form.get("city");
        String breedName = form.get("breed");
        String address = form.get("streetAddress");
        String stateName = form.get("state");
        String zipCode = form.get("zipCode");
        String phoneNumber = form.get("phoneNumber");
        String emailAddress = form.get("emailAddress");
        String URL = form.get("URL");
        String password = form.get("locationPassword");

        if(!isNewEmail(emailAddress, jpaApi))
        {
            return redirect(routes.LocationController.getCreateBreederAccount("That email is already in use"));
        }

        String stateSql = "SELECT s FROM State s WHERE s.stateName = :stateName";
        String breedSql = "SELECT b FROM Breed b WHERE b.breedName = :breedName";

        State state = jpaApi.em().createQuery(stateSql, State.class).setParameter("stateName", stateName).getSingleResult();
        Breed breed = jpaApi.em().createQuery(breedSql, Breed.class).setParameter("breedName", breedName).getSingleResult();

        Location location = new Location();

        location.setLocationName(locationName);
        location.setCity(city);
        location.setAddress(address);
        location.setZipCode(zipCode);
        location.setPhoneNumber(phoneNumber);
        location.setEmailAddress(emailAddress);
        location.setWebsiteURL(URL);
        location.setStateId(state.getStateId());
        location.setBreedId(breed.getBreedId());

        try
        {
            byte[] salt = Password.getNewSalt();
            byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

            location.setSalt(salt);
            location.setLocationPassword(hashedPassword);
        }

        catch(Exception e)
        {
            e.printStackTrace();

            String statesSql = "SELECT s FROM State s";
            List<State> states = jpaApi.em().createQuery(statesSql, State.class).getResultList();
            String breedsSql = "SELECT b FROM Breed b";
            List<Breed> breeds = jpaApi.em().createQuery(breedsSql, Breed.class).getResultList();

            return ok(views.html.createbreederaccount.render(breeds, states, "Error creating account"));
        }

        jpaApi.em().persist(location);

        logInLocation(location.getEmailAddress());
        System.out.println(session().get("loggedIn"));
        return redirect(routes.LocationController.getBreederPage("Account successfully created"));
    }

    @Transactional(readOnly = true)
    public Result getBreederPage(String status)
    {
        if(isLoggedIn())
        {
            String emailAddress = session().get("loggedIn");
            String sql = "SELECT NEW models.BreederDetail(l.locationId, l.locationName, l.address, l.city, s.stateName, " +
                    "l.zipCode, b.breedName, b.breedId, " +
                    "l.phoneNumber, l.emailAddress, l.websiteURL) " +
                    "FROM Location l " +
                    "JOIN Breed b ON b.breedId = l.breedId " +
                    "JOIN State s ON s.stateId = l.stateId " +
                    "WHERE l.emailAddress = :emailAddress";

            BreederDetail location = jpaApi.em().createQuery(sql, BreederDetail.class).
                    setParameter("emailAddress", emailAddress).getSingleResult();

            return ok(views.html.breederpage.render(location));
        }
        return redirect(routes.UserController.getLogIn("Log in to access this page."));
    }

    @Transactional(readOnly = true)
    public Result getEditBreeder()
    {

        if(isLoggedIn())
        {
            String emailAddress = session().get("loggedIn");
            String sql = "SELECT NEW models.BreederDetail(l.locationId, l.locationName, l.address, l.city, s.stateName, " +
                    "l.zipCode, b.breedName, b.breedId, " +
                    "l.phoneNumber, l.emailAddress, l.websiteURL) " +
                    "FROM Location l " +
                    "JOIN Breed b ON b.breedId = l.breedId " +
                    "JOIN State s ON s.stateId = l.stateId " +
                    "WHERE l.emailAddress = :emailAddress";

            BreederDetail location = jpaApi.em().createQuery(sql, BreederDetail.class).
                    setParameter("emailAddress", emailAddress).getSingleResult();
            String stateSql = "SELECT s FROM State s";

            List<State> states = jpaApi.em().createQuery(stateSql, State.class).getResultList();

            String breedSql = "SELECT b FROM Breed b";

            List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

            return ok(views.html.editbreeder.render(location, breeds, states, ""));
        }
        else
        {
            return redirect(routes.UserController.getLogIn("Please log in to view this page"));
        }
    }

    @Transactional
    public Result postEditBreeder()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String loggedInEmail = session().get("loggedIn");
        String breederSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        Location breeder = jpaApi.em().createQuery(breederSql, Location.class).
                setParameter("emailAddress", loggedInEmail).getSingleResult();

        String locationName = form.get("locationName");
        String address = form.get("streetAddress");
        String city = form.get("city");
        String stateName = form.get("state");
        String zipCode = form.get("zipCode");
        String breedName = form.get("breed");
        String phoneNumber = form.get("phoneNumber");
        String websiteUrl = form.get("URL");
        String emailAddress = form.get("emailAddress");
        String password = form.get("locationPassword");

        byte salt[] = breeder.getSalt();
        byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

        String stateSql = "SElECT s FROM State s WHERE s.stateName = :stateName";
        String breedSql = "SELECT b FROM Breed b WHERE b.breedName = :breedName";

        State state = jpaApi.em().createQuery(stateSql, State.class).setParameter("stateName", stateName).getSingleResult();
        Breed breed = jpaApi.em().createQuery(breedSql, Breed.class).setParameter("breedName", breedName).getSingleResult();

        breeder.setLocationName(locationName);
        breeder.setAddress(address);
        breeder.setCity(city);
        breeder.setStateId(state.getStateId());
        breeder.setZipCode(zipCode);
        breeder.setBreedId(breed.getBreedId());
        breeder.setPhoneNumber(phoneNumber);
        breeder.setWebsiteURL(websiteUrl);
        breeder.setEmailAddress(emailAddress);
        breeder.setLocationPassword(hashedPassword);

        return redirect(routes.LocationController.getBreederPage("Changes successfully saved"));
    }

    @Transactional
    public Result getDeleteBreederAccount()
    {
        String emailAddress = session().get("loggedIn");

        String locationSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        Location location = jpaApi.em().createQuery(locationSql, Location.class).
                setParameter("emailAddress", emailAddress).getSingleResult();

        jpaApi.em().remove(location);

        return redirect(routes.UserController.getLogIn("Account successfully deleted"));
    }
}
