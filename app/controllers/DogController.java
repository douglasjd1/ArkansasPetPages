package controllers;

import models.Dog;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Int;

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

    @Transactional(readOnly = true)
    public Result getNewDog()
    {
        return ok(views.html.newdog.render());
    }

    @Transactional
    public Result postNewDog()
    {
        DynamicForm form = formFactory.form().bindFromRequest();
        Dog dog = new Dog();

        String dogName = form.get("dogName");
        int dogAge = Integer.parseInt(form.get("dogAge"));
        int dogReffNum = Integer.parseInt(form.get("dogReffNum"));
        int weight = Integer.parseInt(form.get("weight"));
        int hairLengthId = Integer.parseInt(form.get("hairLengthId"));
        int locationId = Integer.parseInt(form.get("locationId"));
        int colorId = Integer.parseInt(form.get("colorId"));

        dog.setDogName(dogName);
        dog.setDogAge(dogAge);
        dog.setDogReffNum(dogReffNum);
        dog.setWeight(weight);
        dog.setHairLengthId(hairLengthId);
        dog.setLocationId(locationId);
        dog.setColorId(colorId);

        jpaApi.em().persist(dog);

        return ok(views.html.newdog.render());
    }
}
