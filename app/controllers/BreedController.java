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
import java.math.RoundingMode;
import java.util.*;


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

        personality.setPersonalityName(personalityName);

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

    @Transactional(readOnly=true)
    public Result getDogulator()
    {
        String personalitySql = "SELECT p FROM Personality p ORDER BY PersonalityName";

        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();

        String colorSql = "SELECT c FROM Color c ORDER BY ColorName";

        List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();

        return ok(views.html.dogulator.render(personalities, colors));
    }

    @Transactional(readOnly=true)
    public Result postDogulator()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String breedSql = "SELECT b FROM Breed b";
        List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

        String breedPersonalitySql = "SELECT bp FROM BreedPersonality bp";
        List<BreedPersonality> breedPersonalities = jpaApi.em().createQuery(breedPersonalitySql, BreedPersonality.class).getResultList();

        String personalitySql = "SELECT p FROM Personality p";
        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();

        String breedColorSql = "SELECT bc FROM BreedColor bc";
        List<BreedColor> breedColors = jpaApi.em().createQuery(breedColorSql, BreedColor.class).getResultList();

        String colorSql = "SELECT c FROM Color c";
        List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();

        List<Integer> selectedPersonalities = new ArrayList<>();

        for(Personality personality : personalities)
        {
            String personalityCheckbox = "personality" + personality.getPersonalityId();
            if(form.get(personalityCheckbox) != null)
            {
                selectedPersonalities.add(personality.getPersonalityId());
            }
        }

        List<Integer> selectedColors = new ArrayList<>();

        for(Color color : colors)
        {
            String colorCheckbox = "color" + color.getColorId();
            if(form.get(colorCheckbox) != null)
            {
                selectedColors.add(color.getColorId());
            }
        }

        List<DogulatorResult> results = new ArrayList<>();

        for(Breed breed : breeds)
        {
            Integer score = 0;
            int totalScore = 0;

            int breedAvgWeight = (breed.getWeightMin() + breed.getWeightMax()) / 2;
            int breedAvgHeight = (breed.getHeightMin() + breed.getHeightMax()) / 2;

            //Weight check boxes
            if(form.get("weightVerySmall") != null && (breedAvgWeight >= 0 && breedAvgWeight <= 25))
            {
                score += 4;
            }
            if(form.get("weightSmall") != null && (breedAvgWeight >= 26 && breedAvgWeight <= 50))
            {
                score += 4;
            }
            if(form.get("weightMedium") != null && (breedAvgWeight >= 51 && breedAvgWeight <= 75))
            {
                score += 4;
            }
            if(form.get("weightLarge") != null && (breedAvgWeight >= 76 && breedAvgWeight <= 100))
            {
                score += 4;
            }
            if(form.get("weightVeryLarge") != null && (breedAvgWeight >= 101))
            {
                score += 4;
            }

            totalScore += 4;

            //Height check boxes
            if(form.get("heightVeryShort") != null && (breedAvgHeight >= 0 && breedAvgHeight <= 15))
            {
                score += 4;
            }
            if(form.get("heightShort") != null && (breedAvgHeight >= 16 && breedAvgHeight <= 20))
            {
                score += 4;
            }
            if(form.get("heightMedium") != null && (breedAvgHeight >= 21 && breedAvgHeight <= 25))
            {
                score += 4;
            }
            if(form.get("heightTall") != null && (breedAvgHeight >= 26 && breedAvgHeight <= 30))
            {
                score += 4;
            }
            if(form.get("heightVeryTall") != null && (breedAvgHeight >= 31))
            {
                score += 4;
            }

            totalScore += 4;

            //Lifespan check boxes
            if(form.get("lifeSpanShort") != null && (breed.getLifeSpanMin() >= 5 && breed.getLifeSpanMin() <= 9))
            {
                score += 4;
            }
            if(form.get("lifeSpanMedium") != null && (breed.getLifeSpanMin() >= 10 && breed.getLifeSpanMin() <= 13))
            {
                score += 4;
            }
            if(form.get("lifeSpanLong") != null && (breed.getLifeSpanMin() >= 14))
            {
                score += 4;
            }

            totalScore += 4;

            //Hair length check boxes
            if(form.get("hairHairless") != null && (breed.getHairLengthId() == 4))
            {
                score += 4;
            }
            if(form.get("hairShort") != null && (breed.getHairLengthId() == 1))
            {
                score += 4;
            }
            if(form.get("hairMedium") != null && (breed.getHairLengthId() == 2))
            {
                score += 4;
            }
            if(form.get("hairLong") != null && (breed.getHairLengthId() == 3))
            {
                score += 4;
            }
            if(form.get("hairVeryLong") != null && (breed.getHairLengthId() == 6))
            {
                score += 4;
            }

            totalScore += 4;

            //Personality check boxes
            for(BreedPersonality breedPersonality : breedPersonalities)
            {
                if(breedPersonality.getBreedId() == breed.getBreedId())
                {
                    if(selectedPersonalities.contains(breedPersonality.getPersonalityId()))
                    {
                        score++;
                    }

                    totalScore++;
                }
            }

            //Color check boxes
            for(BreedColor breedColor : breedColors)
            {
                if(breedColor.getBreedId() == breed.getBreedId())
                {
                    if(selectedColors.contains(breedColor.getColorId()))
                    {
                        score++;
                    }

                    totalScore++;
                }
            }

            //Cost check boxes
            if(form.get("cheap") != null && ((breed.getCostFromBreeder().compareTo(new BigDecimal(0)) >= 0 && (breed.getCostFromBreeder().compareTo(new BigDecimal(500)) <= 0))))
            {
                score += 4;
            }
            if(form.get("cheap") != null && ((breed.getCostFromBreeder().compareTo(new BigDecimal(501)) >= 0 && (breed.getCostFromBreeder().compareTo(new BigDecimal(1000)) <= 0))))
            {
                score += 4;
            }
            if(form.get("cheap") != null && ((breed.getCostFromBreeder().compareTo(new BigDecimal(1001)) >= 0 && (breed.getCostFromBreeder().compareTo(new BigDecimal(1500)) <= 0))))
            {
                score += 4;
            }
            if(form.get("cheap") != null && ((breed.getCostFromBreeder().compareTo(new BigDecimal(1501)) >= 0)))
            {
                score += 4;
            }

            totalScore += 4;

            //Calculate percentage
            double percentage = 100 * (double)score / (double)totalScore;

            BigDecimal bd = new BigDecimal(percentage);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            percentage = bd.doubleValue();

            results.add(new DogulatorResult(breed.getBreedName(), percentage, breed.getPhoto1(), breed.getBreedId()));
        }

        Collections.sort(results);
        Collections.reverse(results);

        List<DogulatorResult> resultsLimited = new ArrayList<>();

        for(int i = 0; i < 10; i++)
        {
            resultsLimited.add(results.get(i));
        }

        return ok(views.html.dogulatorresults.render(results));
    }
}
