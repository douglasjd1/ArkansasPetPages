package controllers;

import models.Location;
import models.PetPagesUser;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public boolean isNewEmail(String emailAddress, JPAApi jpaApi)
    {
        boolean result = true;

        String userSql = "SELECT ppu FROM PetPagesUser ppu";
        String locationSql = "SELECT l FROM Location l";
        System.out.println(userSql);

        List<PetPagesUser> users = jpaApi.em().createQuery(userSql, PetPagesUser.class).getResultList();
        List<Location> locations = jpaApi.em().createQuery(locationSql, Location.class).getResultList();

        List<String> usedEmailAdresses = new ArrayList<>();

        for(PetPagesUser user : users)
        {
            usedEmailAdresses.add(user.getEmailAddress());
        }

        for(Location location : locations)
        {
            usedEmailAdresses.add(location.getEmailAddress());
        }

        if(usedEmailAdresses.contains(emailAddress))
        {
            result = false;
        }
        return result;
    }

    @Transactional
    public boolean isUserLoggedIn(JPAApi jpaApi)
    {
        boolean isUser = false;
        String sql = "SELECT ppu.emailAddress FROM PetPagesUser ppu";

        List<String> userEmails = jpaApi.em().createQuery(sql, String.class).getResultList();

        if(isLoggedIn() && userEmails.contains(session().get("loggedIn")))
        {
            isUser = true;
        }

        return isUser;
    }
}
