package controllers;

import play.mvc.Controller;

public class ApplicationController extends Controller
{
    public void logInUser(String emailAddress)
    {
        session().put("loggedIn", emailAddress);
    }

    public void logInLocation(String emailAddress)
    {
        session().put("loggedIn", emailAddress);
    }

    public void logOut()
    {
        session().clear();
    }

    public boolean isLoggedIn()
    {
        boolean loggedIn = false;

        if((session().get("loggedIn") != null))
        {
            loggedIn = true;
        }

        return loggedIn;
    }
}
