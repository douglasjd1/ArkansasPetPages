package controllers;

import models.Breed;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.math.BigDecimal;


public class BreedController extends Controller
{
    private JPAApi jpaApi;
    private FormFactory formFactory;

    @Inject
    public BreedController(FormFactory formFactory, JPAApi jpaApi)
    {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    public Result getBreed()
    {
        return ok(views.html.breed.render());
    }

    public Result getNewBreed()
    {
        return ok(views.html.newbreed.render());
    }

    @Transactional
    public Result postNewBreed()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String breedName = form.get("breedName");
        int weightMin = Integer.parseInt(form.get("weightMin"));
        int weightMax = Integer.parseInt(form.get("weightMax"));
        int heightMin = Integer.parseInt(form.get("heightMin"));
        int heightMax = Integer.parseInt(form.get("heightMax"));
        int lifeSpanMin = Integer.parseInt(form.get("lifeSpanMin"));
        int lifeSpanMax = Integer.parseInt(form.get("lifeSpanMax"));
        int hairLengthId = Integer.parseInt(form.get("hairLengthId"));
        BigDecimal costFromBreeder = new BigDecimal(form.get("costFromBreeder"));

        Breed breed = new Breed();

        breed.setBreedName(breedName);
        breed.setWeightMin(weightMin);
        breed.setWeightMax(weightMax);
        breed.setHeightMin(heightMin);
        breed.setHeightMax(heightMax);
        breed.setLifeSpanMin(lifeSpanMin);
        breed.setLifeSpanMax(lifeSpanMax);
        breed.setHairLengthId(hairLengthId);
        breed.setCostFromBreeder(costFromBreeder);

        jpaApi.em().persist(breed);

        return ok(views.html.newbreed.render());
    }
}
