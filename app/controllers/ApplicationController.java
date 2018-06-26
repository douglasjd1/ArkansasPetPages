package controllers;

import play.mvc.Controller;

public class ApplicationController extends Controller
{
    public void logIn(int userId)
    {
        session().put("loggedIn", "" + userId);
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
