package controllers;

import com.google.common.io.Files;
import models.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class UserController extends ApplicationController
{
    private JPAApi jpaApi;
    private FormFactory formFactory;

    @Inject
    public UserController(JPAApi jpaApi, FormFactory formFactory)
    {
        this.jpaApi = jpaApi;
        this.formFactory = formFactory;
    }

    public Result getCreateUserAccount(String status)
    {
        return ok(views.html.createuseraccount.render(status));
    }

    @Transactional
    public Result postCreateUserAccount()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        String emailAddress = form.get("emailAddress");
        String password = form.get("userPassword");
        String bio = form.get("bio");

        if(!isNewEmail(emailAddress, jpaApi))
        {
            return redirect(routes.UserController.getCreateUserAccount("That email is already in use"));
        }

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("profilePhoto");
        File file = filePart.getFile();

        PetPagesUser petPagesUser = new PetPagesUser();

        petPagesUser.setFirstName(firstName);
        petPagesUser.setLastName(lastName);
        petPagesUser.setEmailAddress(emailAddress);
        petPagesUser.setBio(bio);

        if(file != null)
        {
            try
            {
                petPagesUser.setProfilePhoto(Files.toByteArray(file));
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        try
        {
            byte[] salt = Password.getNewSalt();
            byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

            petPagesUser.setSalt(salt);
            petPagesUser.setUserPassword(hashedPassword);
        }

        catch(Exception e)
        {
            e.printStackTrace();
            return ok(views.html.createuseraccount.render("Error creating account"));
        }

        jpaApi.em().persist(petPagesUser);

        int petPagesUserId = petPagesUser.getUserId();

        String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :petPagesUserId";

        List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("petPagesUserId", petPagesUserId).getResultList();

        logInUser(petPagesUser.getEmailAddress());

        return ok(views.html.userpage.render(petPagesUser, dogs, "Account successfully created"));
    }

    public Result getLogIn(String status)
    {
        return ok(views.html.login.render(status));
    }

    @Transactional(readOnly = true)
    public Result postLogIn()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String emailAddress = form.get("emailAddress");
        String password = form.get("password");

        String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE emailAddress = :emailAddress";
        String locationSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        List<PetPagesUser> users = jpaApi.em().createQuery(userSql, PetPagesUser.class).setParameter("emailAddress", emailAddress).getResultList();
        List<Location> locations = jpaApi.em().createQuery(locationSql, Location.class).setParameter("emailAddress", emailAddress).getResultList();

        if(users.size() == 1)
        {
            PetPagesUser loggedInUser = users.get(0);

            byte salt[] = loggedInUser.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

            if(Arrays.equals(hashedPassword, loggedInUser.getUserPassword()))
            {
                logInUser(loggedInUser.getEmailAddress());
                return redirect(routes.UserController.getUserPage(""));
            }
        }
        else if(locations.size() == 1 && locations.get(0).getBreedId() != null)
        {
            Location loggedInUser = locations.get(0);

            byte salt[] = loggedInUser.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

            if(Arrays.equals(hashedPassword, loggedInUser.getLocationPassword()))
            {
                logInLocation(loggedInUser.getEmailAddress());
                return redirect(routes.LocationController.getBreederPage(""));
            }
        }
        else if(locations.size() == 1 && locations.get(0).getBreedId() == null)
        {
            Location loggedInUser = locations.get(0);

            byte salt[] = loggedInUser.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

            if(Arrays.equals(hashedPassword, loggedInUser.getLocationPassword()))
            {
                logInLocation(loggedInUser.getEmailAddress());
                return redirect(routes.LocationController.getShelterPage(""));
            }
        }
        else
        {
            try
            {
                byte salt[] = Password.getNewSalt();
                Password.hashPassword(password.toCharArray(), salt);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        return ok(views.html.login.render("Invalid username or password"));
    }

    @Transactional(readOnly = true)
    public Result getUserEdit(Integer userId)
    {
        String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";
        PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        String emailAddress = petPagesUser.getEmailAddress();

        if(isLoggedIn() && emailAddress.equals(session().get("loggedIn")))
        {

            return ok(views.html.useredit.render(petPagesUser));
        }

        return ok(views.html.login.render("Please log in."));
    }

    @Transactional
    public Result postUserEdit(Integer userId)
    {
        String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

        PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        String emailAddress = petPagesUser.getEmailAddress();
        if(isLoggedIn() && emailAddress.equals(session().get("loggedIn")))
        {
            DynamicForm form = formFactory.form().bindFromRequest();


            Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = formData.getFile("userProfilePhoto");
            File file = filePart.getFile();

            petPagesUser.setFirstName(form.get("firstName"));
            petPagesUser.setLastName(form.get("lastName"));
            petPagesUser.setEmailAddress(form.get("emailAddress"));
            petPagesUser.setBio(form.get("bio"));



            if(file.length() != 0)
            {
                try
                {
                    petPagesUser.setProfilePhoto(Files.toByteArray(file));
                }
                catch(Exception e)
                {
                    //do nothing
                }
            }

            String password = form.get("userPassword");
            byte salt[] = petPagesUser.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);
            petPagesUser.setUserPassword(hashedPassword);

            if(Arrays.equals(hashedPassword, petPagesUser.getUserPassword()))
            {
                return redirect(routes.UserController.getUserPage("Account successfully saved"));
            }
        }
        return ok(views.html.login.render("Please log in to access this page."));
    }

    @Transactional(readOnly = true)
    public Result getUserPage(String status)
    {
        if(isLoggedIn())
        {
            String emailAddress = session().get("loggedIn");

            String sql = "SELECT ppu FROM PetPagesUser ppu WHERE emailAddress = :emailAddress";

            PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).
                                        setParameter("emailAddress", emailAddress).getSingleResult();

            String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :petPagesUserId";

            List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).
                             setParameter("petPagesUserId", petPagesUser.getUserId()).getResultList();

            return ok(views.html.userpage.render(petPagesUser, dogs, status));
        }

        else
        {
            return redirect(routes.UserController.getLogIn("Log in to view your account"));
        }
    }

    @Transactional(readOnly = true)
    public Result getUserProfilePhoto(Integer userId)
    {
        String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

        PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        if(petPagesUser.getProfilePhoto() != null)
        {
            return ok(petPagesUser.getProfilePhoto()).as("image/jpg");
        }
        else
        {
            return ok("");
        }
    }

    public Result getLogOut()
    {
        logOut();
        return redirect(routes.UserController.getLogIn("Successfully logged out"));
    }

    @Transactional(readOnly = true)
    public Result getViewAccount()
    {
        String emailAddress = session().get("loggedIn");
        String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.emailAddress = :emailAddress";
        String locationSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        List<PetPagesUser> users = jpaApi.em().createQuery(userSql, PetPagesUser.class).
                                   setParameter("emailAddress", emailAddress).getResultList();
        List<Location> locations = jpaApi.em().createQuery(locationSql, Location.class).
                                   setParameter("emailAddress", emailAddress).getResultList();

        if(isLoggedIn())
        {
            if(users.size() == 1)
            {
                return redirect(routes.UserController.getUserPage(""));
            }
            else if(locations.size() == 1 && locations.get(0).getBreedId() != null)
            {
                return redirect(routes.LocationController.getBreederPage(""));
            }
            else if(locations.size() == 1 && locations.get(0).getBreedId() == null)
            {
                return redirect(routes.LocationController.getShelterPage(""));
            }
        }
        return redirect(routes.UserController.getLogIn("Please log in to view this page"));
    }

    @Transactional
    public Result getDeleteUserAccount()
    {
        if(isLoggedIn())
        {
            String emailAddress = session().get("loggedIn");
            String sql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.emailAddress = :emailAddress";

            PetPagesUser user = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("emailAddress", emailAddress).getSingleResult();

            jpaApi.em().remove(user);
            return redirect(routes.UserController.getLogIn("Account successfully deleted."));
        }
        else
        {
            return redirect(routes.UserController.getLogIn("Please log in to access this page"));
        }
    }
}
