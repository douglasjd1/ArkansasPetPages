package controllers;

import models.Location;
import models.Password;
import models.PetPagesUser;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.*;

import services.Counter;
import services.Email;
import views.html.*;

import javax.inject.Inject;
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends ApplicationController
{

    private JPAApi jpaApi;
    private FormFactory formFactory;

    @Inject
    public HomeController(JPAApi jpaApi, FormFactory formFactory)
    {
        this.jpaApi = jpaApi;
        this.formFactory = formFactory;
    }

    public Result index()
    {
        return ok(index.render("Your new application is ready."));
    }

    public Result getPractice()
    {
        return ok(views.html.practice.render());
    }

    public Result getHome()
    {
        return ok(views.html.home.render());
    }

    public Result getCreateAccount()
    {
        return ok(views.html.createaccount.render());
    }

    public Result getForgotPassword()
    {
        return ok(views.html.forgotpassword.render(""));
    }

    @Transactional()
    public Result postForgotPassword()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        StringBuilder stringBuilder = new StringBuilder();
        scala.collection.mutable.StringBuilder scalaStringBuilder = new scala.collection.mutable.StringBuilder(stringBuilder);

        String userEmailSql = "SELECT ppu.emailAddress FROM PetPagesUser ppu";
        List<String> userEmails = jpaApi.em().createQuery(userEmailSql, String.class).getResultList();

        String locationEmailSql = "SELECT l.emailAddress from Location l";
        List<String> locationEmails = jpaApi.em().createQuery(locationEmailSql, String.class).getResultList();

        String emailAddress = form.get("emailAddress");

        if (!userEmails.contains(emailAddress) && !locationEmails.contains(emailAddress))
        {
            return ok(views.html.forgotpassword.render("That email is not associated with an account"));
        }

        if (userEmails.contains(emailAddress))
        {
            String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.emailAddress = :emailAddress";
            PetPagesUser user = jpaApi.em().createQuery(userSql, PetPagesUser.class).
                                setParameter("emailAddress", emailAddress).getSingleResult();

            String password = Email.generateRandomPassword();

            views.html.forgotpasswordemailuser.render(user, password).buildString(scalaStringBuilder);

            Email.sendForgotPasswordEmail(stringBuilder.toString(), emailAddress);

            byte salt[] = user.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);
            user.setUserPassword(hashedPassword);

            jpaApi.em().persist(user);

            return redirect(routes.UserController.getLogIn("Check your email. We sent you a temporary password."));
        }

        if(locationEmails.contains(emailAddress))
        {
            String locationSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

            Location location = jpaApi.em().createQuery(locationSql, Location.class).
                                setParameter("emailAddress", emailAddress).getSingleResult();

            String password = Email.generateRandomPassword();

            views.html.forgotpasswordemaillocation.render(location, password).buildString(scalaStringBuilder);

            Email.sendForgotPasswordEmail(stringBuilder.toString(), emailAddress);

            byte salt[] = location.getSalt();
            byte hashedPassword[] = Password.hashPassword(password.toCharArray(), salt);
            location.setLocationPassword(hashedPassword);

            jpaApi.em().persist(location);

            return redirect(routes.UserController.getLogIn("Check your email. We sent you a temporary password."));
        }

        return redirect(routes.UserController.getLogIn("Error occured. Please try again"));
    }
}
