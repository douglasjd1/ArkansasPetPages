package controllers;

import com.google.common.io.Files;
import models.Dog;
import models.DogDetail;
import models.Password;
import models.PetPagesUser;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
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

    public Result getCreateAccount()
    {
        return ok(views.html.createaccount.render(""));
    }

    @Transactional
    public Result postCreateAccount()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        String emailAddress = form.get("emailAddress");
        String password = form.get("userPassword");
        String bio = form.get("bio");

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
            return ok(views.html.createaccount.render("Error creating account"));
        }

        System.out.println(petPagesUser.getFirstName());
        jpaApi.em().persist(petPagesUser);

        int petPagesUserId = petPagesUser.getUserId();

        String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :petPagesUserId";

        List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("petPagesUserId", petPagesUserId).getResultList();

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

        String sql = "SELECT ppu FROM PetPagesUser ppu WHERE emailAddress = :emailAddress";

        List<PetPagesUser> users = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("emailAddress", emailAddress).getResultList();

        if(users.size() == 1)
        {
            PetPagesUser loggedInUser = users.get(0);

            byte salt[] = loggedInUser.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

            if(Arrays.equals(hashedPassword, loggedInUser.getUserPassword()))
            {
                logIn(loggedInUser.getUserId());
                return redirect(routes.UserController.getUserPage(""));
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

            }
        }

        return ok(views.html.login.render("Invalid username or password"));
    }

    @Transactional(readOnly = true)
    public Result getUserEdit(Integer userId)
    {
        if(isLoggedIn() && userId == Integer.parseInt(session().get("loggedIn")))
        {
            String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

            PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

            return ok(views.html.useredit.render(petPagesUser));
        }

        return ok(views.html.login.render("Please log in."));
    }

    @Transactional
    public Result postUserEdit(Integer userId)
    {
        if(isLoggedIn() && userId == Integer.parseInt(session().get("loggedIn")))
        {
            DynamicForm form = formFactory.form().bindFromRequest();

            String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

            PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

            String password = form.get("password");

            Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = formData.getFile("userProfilePhoto");
            File file = filePart.getFile();

            petPagesUser.setFirstName(form.get("firstName"));
            petPagesUser.setLastName(form.get("lastName"));
            petPagesUser.setEmailAddress(form.get("emailAddress"));
            petPagesUser.setBio(form.get("Bio"));

            if(file != null)
            {
                try
                {
                    petPagesUser.setProfilePhoto(Files.toByteArray(file));
                }
                catch(Exception e)
                {

                }
            }

            byte salt[] = petPagesUser.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);

            if(Arrays.equals(hashedPassword, petPagesUser.getUserPassword()))
            {
                return redirect("/userPage/" + petPagesUser.getUserId());
            }

            String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :petPagesUserId";

            List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("petPagesUserId", userId).getResultList();

            return ok(views.html.userpage.render(petPagesUser, dogs, "User edit successfully saved"));
        }
        return ok(views.html.login.render("Please log in to access this page."));
    }

    @Transactional(readOnly = true)
    public Result getUserPage(String status)
    {
        if(isLoggedIn())
        {
            int userId = Integer.parseInt(session().get("loggedIn"));

            String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

            PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

            String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :petPagesUserId";

            List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("petPagesUserId", userId).getResultList();

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
}
