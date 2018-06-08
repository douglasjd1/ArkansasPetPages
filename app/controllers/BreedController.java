package controllers;

import models.Breed;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import models.Color;
import java.math.BigDecimal;
import java.util.List;


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

    @Transactional(readOnly = true)
    public Result getBreed(Integer breedId)
    {
        String sql = "SELECT b FROM Breed b WHERE breedId = :breedId";

        Breed breed = jpaApi.em().createQuery(sql, Breed.class).setParameter("breedId", breedId).getSingleResult();
        return ok(views.html.breed.render(breed));
    }

    @Transactional(readOnly = true)
    public Result getBreeds()
    {
        String sql = "SELECT b FROM Breed b ORDER BY breedName";

        List<Breed> breeds = jpaApi.em().createQuery(sql, Breed.class).getResultList();

        return ok(views.html.breeds.render(breeds));
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

    @Transactional
    public Result postNewPersonality()
    {
        return ok(views.html.newpersonality.render());
    }

    @Transactional
    public Result postNewColor()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String colorName = form.get("colorName");

        Color color = new Color();
        color.setColorName(colorName);
        jpaApi.em().persist(color);

        return ok(views.html.newcolor.render());
    }

    @Transactional
    public Result postNewBreedPersonality()
    {
        return ok(views.html.newpersonality.render());
    }

    @Transactional
    public Result postNewBreedColor()
    {
        return ok(views.html.newpersonality.render());
    }

    @Transactional
    public Result getNewPersonality()
    {
        return ok(views.html.newpersonality.render());
    }

    @Transactional
    public Result getNewColor()
    {
        return ok(views.html.newcolor.render());
    }

    @Transactional
    public Result getNewBreedPersonality()
    {
        return ok(views.html.newbreedpersonality.render());
    }

    @Transactional
    public Result getNewBreedColor()
    {
        return ok(views.html.newbreedcolor.render());
    }
}
