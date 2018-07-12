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
import java.util.ArrayList;
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
        PetPagesUser user = new PetPagesUser();
        List<String> statusList = new ArrayList<>();
        statusList.add(status);
        return ok(views.html.createuseraccount.render(statusList, user));
    }

    @Transactional
    public Result postCreateUserAccount()
    {
        boolean isValid = true;
        List<String> status = new ArrayList<>();

        DynamicForm form = formFactory.form().bindFromRequest();

        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        String emailAddress = form.get("emailAddress");
        String password = form.get("userPassword");
        String passwordCheck = form.get("userPasswordCheck");
        String bio = form.get("bio");
        int showEmail = Integer.parseInt(form.get("showEmail"));

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("profilePhoto");
        File file = filePart.getFile();

        PetPagesUser petPagesUser = new PetPagesUser();

        petPagesUser.setFirstName(firstName);
        petPagesUser.setLastName(lastName);
        petPagesUser.setEmailAddress(emailAddress);
        petPagesUser.setBio(bio);

        if(showEmail == 0)
        {
            petPagesUser.setEmailFlag(false);
        }
        else
        {
            petPagesUser.setEmailFlag(true);
        }

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

        try
        {
            byte[] salt = Password.getNewSalt();
            byte[] hashedPassword = Password.hashPassword(password.toCharArray(), salt);

            petPagesUser.setSalt(salt);
            petPagesUser.setUserPassword(hashedPassword);
        }

        catch(Exception e)
        {
            return ok(views.html.createuseraccount.render(status, petPagesUser));
        }

        if(!password.equals(passwordCheck))
        {
            isValid = false;
            status.add("Passwords must match");
        }
        if(!isNewEmail(emailAddress, jpaApi))
        {
            isValid = false;
            status.add("That email address is already in use. ");
        }

        if(emailAddress.equals(""))
        {
            isValid = false;
            status.add("Enter an email address.");
        }
        if(password.length() < 8)
        {
            isValid = false;
            status.add("Password must be at least 8 characters.");
        }

        if(firstName.equals(""))
        {
            isValid = false;
            status.add("Enter a first name.");
        }

        if(lastName.equals(""))
        {
            isValid = false;
            status.add("Enter a last name.");
        }
        if(!isValid)
        {
            return ok(views.html.createuseraccount.render(status, petPagesUser));
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
    public Result getUserEdit(Integer userId, String status)
    {
        String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";
        PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        List<String> statusList = new ArrayList<>();
        statusList.add(status);

        String emailAddress = petPagesUser.getEmailAddress();

        if(isLoggedIn() && emailAddress.equals(session().get("loggedIn")))
        {
            return ok(views.html.useredit.render(petPagesUser, statusList));
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
            boolean isValid = true;
            List<String> status = new ArrayList<>();

            DynamicForm form = formFactory.form().bindFromRequest();

            Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> filePart = formData.getFile("userProfilePhoto");
            File file = filePart.getFile();

            String firstName = form.get("firstName");
            String lastName = form.get("lastName");
            String newEmailAddress = form.get("emailAddress");
            String password = form.get("userPassword");
            String passwordCheck = form.get("userPasswordCheck");
            int showEmail = Integer.parseInt(form.get("showEmail"));

            if(firstName.equals(""))
            {
                isValid = false;
                status.add("Enter a first name.");
            }

            if(lastName.equals(""))
            {
                isValid = false;
                status.add("Enter a last name.");
            }

            if(emailAddress.equals(""))
            {
                isValid = false;
                status.add("Enter an email address.");
            }

            if(!isNewEmail(newEmailAddress, jpaApi) && !newEmailAddress.equals(emailAddress))
            {
                isValid = false;
                status.add("That email is already in use.");
            }

            if(password.length() < 8 && !password.equals(""))
            {
                isValid = false;
                status.add("Password must be at least 8 characters.");
            }

            if(!password.equals(passwordCheck))
            {
                isValid = false;
                status.add("Passwords must match");
            }

            if(!isValid)
            {
                return ok(views.html.useredit.render(petPagesUser, status));
            }

            petPagesUser.setFirstName(form.get("firstName"));
            petPagesUser.setLastName(form.get("lastName"));
            petPagesUser.setEmailAddress(form.get("emailAddress"));
            petPagesUser.setBio(form.get("bio"));

            if(showEmail == 0)
            {
                petPagesUser.setEmailFlag(false);
            }
            else
            {
                petPagesUser.setEmailFlag(true);
            }

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

            if(!password.equals(""))
            {
                byte salt[] = petPagesUser.getSalt();
                byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);
                petPagesUser.setUserPassword(hashedPassword);
            }

            return redirect(routes.UserController.getUserPage("Account successfully saved"));
        }

        return ok(views.html.login.render("Please log in to access this page."));
    }

    @Transactional(readOnly = true)
    public Result getUserPage(String status)
    {
        if(isLoggedIn())
        {
            try
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
            catch(Exception e)
            {
                return redirect(routes.UserController.getLogIn("Log in as a user to view your account"));
            }

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

    @Transactional(readOnly = true)
    public Result getUsers()
    {
        String userSql = "SELECT ppu FROM PetPagesUser ppu";

        List<PetPagesUser> users = jpaApi.em().createQuery(userSql, PetPagesUser.class).getResultList();

        return ok(views.html.users.render(users));
    }

    @Transactional(readOnly = true)
    public Result postUsers()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String search = form.get("search");

        String firstNameql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.firstName LIKE CONCAT('%', :search, '%')";

        List<PetPagesUser> firstNameUsers = jpaApi.em().createQuery(firstNameql, PetPagesUser.class).
                                   setParameter("search", search).getResultList();

        String lastNameSql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.lastName LIKE CONCAT('%', :search, '%')";

        List<PetPagesUser> lastNameUsers = jpaApi.em().createQuery(lastNameSql, PetPagesUser.class).
                setParameter("search", search).getResultList();

        List<PetPagesUser> users = new ArrayList<>();

        users.addAll(firstNameUsers);
        users.addAll(lastNameUsers);

        return ok(views.html.users.render(users));
    }


    @Transactional(readOnly = true)
    public Result getUser(Integer userId)
    {
        String userSql = "SELECT ppu FROM PetPagesUser ppu " +
                         "WHERE ppu.userId = :userId";

        PetPagesUser user = jpaApi.em().createQuery(userSql, PetPagesUser.class).
                            setParameter("userId", userId).getSingleResult();

        String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :userId";

        List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).
                         setParameter("userId", userId).getResultList();

        return ok(views.html.user.render(user, dogs));
    }
}
