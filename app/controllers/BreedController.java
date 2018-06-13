package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
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
        String sql = "SELECT NEW models.BreedDetail(b.breedId, b.breedName, c.colorName, b.weightMin, b.weightMax, b.heightMin, b.heightMax, " +
                     "b.lifeSpanMin, b.lifeSpanMax, hl.hairLengthName, b.costFromBreeder, b.photo1, b.photo2, b.photo3) " +
                     "FROM Breed b " +
                     "JOIN HairLength hl ON hl.hairLengthId = b.hairLengthId " +
                     "JOIN BreedColor bc ON bc.breedId = b.breedId " +
                     "JOIN Color c ON bc.colorId = c.colorId " +
                     "WHERE b.breedId = :breedId " +
                     "GROUP BY b.breedId";

        BreedDetail breedDetail = jpaApi.em().createQuery(sql, BreedDetail.class).setParameter("breedId", breedId).getSingleResult();

        String personalitySql = "SELECT NEW models.PersonalityByBreed(bp.breedPersonalityId, p.personalityName) " +
                                "FROM Personality p " +
                                "JOIN BreedPersonality bp ON bp.personalityId = p.personalityId " +
                                "JOIN Breed b ON bp.breedId = b.breedId " +
                                "WHERE b.breedId = :breedId";

        List<PersonalityByBreed> personalities = jpaApi.em().createQuery(personalitySql, PersonalityByBreed.class).setParameter("breedId", breedId).getResultList();

        String colorSql = "SELECT NEW models.ColorByBreed(bc.breedColorId, c.colorName) " +
                          "FROM Color c " +
                          "JOIN BreedColor bc ON bc.colorId = c.colorId " +
                          "JOIN Breed b ON bc.breedId = b.breedId " +
                          "WHERE b.breedId = :breedId";

        List<ColorByBreed> colors = jpaApi.em().createQuery(colorSql, ColorByBreed.class).setParameter("breedId", breedId).getResultList();

        return ok(views.html.breed.render(breedDetail, personalities, colors));
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
        DynamicForm form = formFactory.form().bindFromRequest();

        String personalityName = form.get("personalityName");

        Personality personality = new Personality();

        jpaApi.em().persist(personality);

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
        DynamicForm form = formFactory.form().bindFromRequest();
        BreedColor breedColor = new BreedColor();

        int breedId = Integer.parseInt(form.get(("breedId")));
        int colorId = Integer.parseInt(form.get(("colorId")));

        breedColor.setBreedId(breedId);
        breedColor.setColorId(colorId);

        jpaApi.em().persist(breedColor);

        return ok(views.html.newbreedcolor.render());
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

    @Transactional
    public Result getColors()
    {
        String sql = "SELECT c FROM Color c ORDER BY ColorName";

        List<Color> colors = jpaApi.em().createQuery(sql, Color.class).getResultList();

        return ok(views.html.colors.render(colors));
    }

    @Transactional
    public Result getPersonalities()
    {
        String sql = "SELECT p FROM Personality p ORDER BY PersonalityName";

        List<Personality> personalities = jpaApi.em().createQuery(sql, Personality.class).getResultList();

        return ok(views.html.personalities.render(personalities));
    }
}
