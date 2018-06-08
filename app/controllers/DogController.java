package controllers;

import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class DogController extends Controller
{
    private JPAApi jpaApi;
    private FormFactory formFactory;

    @Inject
    public DogController(FormFactory formFactory, JPAApi jpaApi)
    {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result postNewDogPersonality()
    {
        return ok(views.html.newdogpersonality.render());
    }

    @Transactional
    public Result postNewDogColor()
    {
        return ok(views.html.newdogcolor.render());
    }
    @Transactional
    public Result getNewDogPersonality()
    {
        return ok(views.html.newdogpersonality.render());
    }

    @Transactional
    public Result getNewDogColor()
    {
        return ok(views.html.newdogcolor.render());
    }
}
