package controllers;

import play.mvc.*;

import services.Email;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends ApplicationController
{

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
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

    public Result getSendEmail()
    {
        return ok(views.html.sendemail.render());
    }

    public Result postSendEmail()
    {
        Email.sendEmail("I am not happy with my service", "douglasjd09@gmail.com");
        return ok(views.html.sendemail.render());
    }

}
