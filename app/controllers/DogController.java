package controllers;

import com.google.common.io.Files;
import models.Dog;
import models.Color;
import models.DogPhoto;
import models.Location;
import models.Personality;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Int;
import views.html.dog;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.util.List;

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
    public Result getNewUserDog()
    {
        String colorSql = "SELECT c FROM Color c";
        String personalitySql = "SELECT p FROM Personality p";
        String locationSql = "SELECT l FROM Location l";

        List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();
        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();
        List<Location> locations = jpaApi.em().createQuery(locationSql, Location.class).getResultList();

        return ok(views.html.newuserdog.render(colors, personalities));
    }

    @Transactional
    public Result postNewUserDog()
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

        return ok("Dog saved");
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
    public Result getDog(Integer dogId)
    {
        String sql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(sql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        String photoSql ="SELECT dp FROM DogPhoto dp WHERE dp.dogId = :dogId";

        List<DogPhoto> photos = jpaApi.em().createQuery(photoSql, DogPhoto.class).setParameter("dogId", dogId).getResultList();

        DogPhoto firstPhoto = photos.get(0);
        photos.remove(0);
        return ok(views.html.dog.render(dog, photos, firstPhoto));
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
}
