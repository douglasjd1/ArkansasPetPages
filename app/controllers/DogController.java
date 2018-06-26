package controllers;

import com.google.common.io.Files;
import models.*;
import models.Color;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Int;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DogController extends ApplicationController
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
    public Result getNewUserDog(Integer userId)
    {
        String colorSql = "SELECT c FROM Color c ORDER BY c.colorName";
        String personalitySql = "SELECT p FROM Personality p ORDER BY p.personalityName";

        List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();
        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();

        return ok(views.html.newuserdog.render(colors, personalities));
    }

    @Transactional
    public Result postNewUserDog(Integer userId)
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        Dog dog = new Dog();

        String dogName = form.get("dogName");
        int dogAge = Integer.parseInt(form.get("dogAge"));
        int weight = Integer.parseInt(form.get("dogWeight"));
        int height = Integer.parseInt(form.get("dogHeight"));
        String hairLengthName = form.get("hairLength");
        String colorName = form.get("colorName");

        String hairLengthSql = "SELECT hl FROM HairLength hl WHERE hairLengthName = :hairLengthName";

        HairLength hairLength = jpaApi.em().
                                createQuery(hairLengthSql, HairLength.class).
                                setParameter("hairLengthName", hairLengthName).
                                getSingleResult();

        String colorSql = "SELECT c FROM Color c WHERE colorName = :colorName";

        Color color = jpaApi.em().
                createQuery(colorSql, Color.class).
                setParameter("colorName", colorName).
                getSingleResult();

        dog.setDogName(dogName);
        dog.setDogAge(dogAge);
        dog.setWeight(weight);
        dog.setHeight(height);
        dog.setHairLengthId(hairLength.getHairLengthId());
        dog.setColorId(color.getColorId());
        dog.setPetPagesUserId(userId);

        jpaApi.em().persist(dog);

        String personalitySql = "SELECT p FROM Personality p";

        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();

        for(Personality personality : personalities)
        {
            String personalityName = personality.getPersonalityName();
            if(form.get(personalityName) != null)
            {
                DogPersonality dogPersonality = new DogPersonality();
                dogPersonality.setDogId(dog.getDogId());
                dogPersonality.setPersonalityId(personality.getPersonalityId());

                jpaApi.em().persist(dogPersonality);
            }
        }

        String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

        PetPagesUser user = jpaApi.em().createQuery(userSql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :userId";

        List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("userId", userId).getResultList();

        return ok(views.html.userpage.render(user, dogs, "Dog successfully added"));
    }

    public Result getNewDogPhoto()
    {
        return ok(views.html.newdogphoto.render());
    }

    @Transactional
    public Result postNewDogPhoto()
    {
        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("dogphoto");
        File file = filePart.getFile();

        DogPhoto dogPhoto = new DogPhoto();

        DynamicForm form = formFactory.form().bindFromRequest();

        int dogId = Integer.parseInt(form.get("dogid"));

        dogPhoto.setDogId(dogId);
        if(file != null)
        {
            try
            {
                dogPhoto.setDogPhotoData(Files.toByteArray(file));
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        jpaApi.em().persist(dogPhoto);

        return ok(views.html.newdogphoto.render());
    }

    @Transactional(readOnly = true)
    public Result getViewUserDog(Integer dogId)
    {
        String dogSql = "SELECT NEW models.DogDetail(d.dogId, d.dogName, d.weight, d.height, hl.hairLengthName, c.colorName, d.dogAge) " +
                "FROM Dog d " +
                "JOIN HairLength hl ON d.hairLengthId = hl.hairLengthId " +
                "JOIN Color c ON c.colorId = d.colorId " +
                "WHERE d.dogId = :dogId " +
                "ORDER BY d.dogName";

        DogDetail dog = jpaApi.em().createQuery(dogSql, DogDetail.class).setParameter("dogId", dogId).getSingleResult();

        String personalitySql = "SELECT p " +
                "FROM Personality p " +
                "JOIN DogPersonality dp ON p.personalityId = dp.personalityId " +
                "JOIN Dog d ON dp.dogId = d.dogId " +
                "WHERE d.dogId = :dogId " +
                "ORDER BY p.personalityName";

        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).setParameter("dogId", dogId).getResultList();

        String photoSql ="SELECT dp FROM DogPhoto dp WHERE dp.dogId = :dogId";

        List<DogPhoto> photos = jpaApi.em().createQuery(photoSql, DogPhoto.class).setParameter("dogId", dogId).getResultList();
        DogPhoto firstPhoto;
        if(photos.size() > 0)
        {
            firstPhoto = photos.get(0);
            photos.remove(0);
        }
        else
        {
            firstPhoto = new DogPhoto();
        }

        return ok(views.html.viewuserdog.render(dog, personalities, photos, firstPhoto));
    }

    @Transactional(readOnly = true)
    public Result getDogDefaultPhoto(Integer dogId)
    {
        String sql = "SELECT d FROM Dog d WHERE dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(sql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        return ok(dog.getDefaultPhotoData()).as("image/jpg");
    }

    @Transactional(readOnly = true)
    public Result getNewDogDefaultPhoto(Integer dogId)
    {
        String sql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";
        Dog dog = jpaApi.em().createQuery(sql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        return ok(views.html.newdogdefaultphoto.render(dog));
    }

    @Transactional
    public Result postNewDogDefaultPhoto(Integer dogId)
    {
        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("defaultphoto");
        File file = filePart.getFile();

        String sql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(sql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        if(file != null)
        {
            try
            {
                dog.setDefaultPhotoData(Files.toByteArray(file));
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        jpaApi.em().persist(dog);

        return ok(views.html.newdogdefaultphoto.render(dog));
    }

    @Transactional
    public Result getDogPhoto(Integer dogPhotoId)
    {
        String sql = "SELECT dp FROM DogPhoto dp WHERE dp.dogPhotoId = :dogPhotoId";

        DogPhoto dogPhoto = jpaApi.em().createQuery(sql, DogPhoto.class).setParameter("dogPhotoId", dogPhotoId).getSingleResult();

        return ok(dogPhoto.getDogPhotoData()).as("image/jpg");
    }


    @Transactional(readOnly = true)
    public Result getEditUserDog(int dogId)
    {
        String dogSql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        return ok(views.html.edituserdog.render(dog));
    }

    @Transactional
    public Result postEditUserDog(int dogId)
    {
        int userId = Integer.parseInt(session().get("loggedIn"));

        String sql = "SELECT ppu FROM PetPagesUser ppu WHERE userId = :userId";

        PetPagesUser petPagesUser = jpaApi.em().createQuery(sql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        String dogSql = "SELECT d FROM Dog d WHERE d.petPagesUserId = :petPagesUserId";

        List<Dog> dogs = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("petPagesUserId", userId).getResultList();

        return ok(views.html.userpage.render(petPagesUser, dogs, "Dog successfully saved"));
    }

}
