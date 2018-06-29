package controllers;

import com.google.common.io.Files;
import models.*;
import models.Color;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
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
    public Result getNewUserDog()
    {
        String colorSql = "SELECT c FROM Color c ORDER BY c.colorName";
        String personalitySql = "SELECT p FROM Personality p ORDER BY p.personalityName";
        String breedSql = "SELECT b FROM Breed b";

        List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();
        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();
        List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

        return ok(views.html.newuserdog.render(colors, personalities, breeds));
    }

    @Transactional(readOnly = true)
    public Result getNewLocationDog()
    {
        String colorSql = "SELECT c FROM Color c ORDER BY c.colorName";
        String personalitySql = "SELECT p FROM Personality p ORDER BY p.personalityName";
        String breedSql = "SELECT b FROM Breed b ORDER BY b.breedName";

        List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();
        List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();
        List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

        return ok(views.html.newlocationdog.render(colors, personalities, breeds));
    }

    @Transactional
    public Result postNewUserDog()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        Dog dog = new Dog();

        String dogName = form.get("dogName");
        int dogAge = Integer.parseInt(form.get("dogAge"));
        int weight = Integer.parseInt(form.get("dogWeight"));
        int height = Integer.parseInt(form.get("dogHeight"));
        String hairLengthName = form.get("hairLength");
        String colorName = form.get("colorName");

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("dogPhoto");
        File file = filePart.getFile();

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

        String emailAddress = session().get("loggedIn");
        String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.emailAddress = :emailAddress";

        PetPagesUser petPagesUser = jpaApi.em().createQuery(userSql, PetPagesUser.class).
                                    setParameter("emailAddress", emailAddress).getSingleResult();

        dog.setDogName(dogName);
        dog.setDogAge(dogAge);
        dog.setWeight(weight);
        dog.setHeight(height);
        dog.setHairLengthId(hairLength.getHairLengthId());
        dog.setColorId(color.getColorId());
        dog.setPetPagesUserId(petPagesUser.getUserId());

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

        DogPhoto dogPhoto = new DogPhoto();

        dogPhoto.setDogId(dog.getDogId());

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

        String breed1 = form.get("breed1");
        String breed2 = form.get("breed2");
        String breed3 = form.get("breed3");

        if(!breed1.equals("Select breed"))
        {
            newDogBreed(breed1, dog);
        }
        if(!breed2.equals("Select breed"))
        {
            newDogBreed(breed2, dog);
        }
        if(!breed3.equals("Select breed"))
        {
            newDogBreed(breed3, dog);
        }

        return redirect(routes.UserController.getUserPage("Dog successfully added."));
    }

    @Transactional(readOnly = true)
    private void newDogBreed(String breedName, Dog dog)
    {
        String breed1Sql = "SELECT b FROM Breed b WHERE b.breedName = :breedName";
        Breed breed = jpaApi.em().createQuery(breed1Sql, Breed.class).
                setParameter("breedName", breedName).getSingleResult();

        DogBreed dogBreed = new DogBreed();
        dogBreed.setBreedId(breed.getBreedId());
        dogBreed.setDogId(dog.getDogId());

        jpaApi.em().persist(dogBreed);
    }

    @Transactional
    public Result postNewLocationDog()
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        Dog dog = new Dog();

        String dogName = form.get("dogName");
        String refId = form.get("refId");
        int dogAge = Integer.parseInt(form.get("dogAge"));
        int weight = Integer.parseInt(form.get("dogWeight"));
        int height = Integer.parseInt(form.get("dogHeight"));
        String hairLengthName = form.get("hairLength");
        String colorName = form.get("colorName");

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> filePart = formData.getFile("dogPhoto");
        File file = filePart.getFile();

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

        String emailAddress = session().get("loggedIn");

        String locationSql = "SELECT l FROM Location l WHERE l.emailAddress = :emailAddress";

        Location location = jpaApi.em().createQuery(locationSql, Location.class).
                            setParameter("emailAddress", emailAddress).getSingleResult();

        dog.setDogName(dogName);
        dog.setDogReffNum(refId);
        dog.setDogAge(dogAge);
        dog.setWeight(weight);
        dog.setHeight(height);
        dog.setHairLengthId(hairLength.getHairLengthId());
        dog.setColorId(color.getColorId());
        dog.setLocationId(location.getLocationId());

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

        DogPhoto dogPhoto = new DogPhoto();

        dogPhoto.setDogId(dog.getDogId());

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

        String breed1 = form.get("breed1");
        String breed2 = form.get("breed2");
        String breed3 = form.get("breed3");

        if(!breed1.equals("Select breed"))
        {
            newDogBreed(breed1, dog);
        }
        if(!breed2.equals("Select breed"))
        {
            newDogBreed(breed2, dog);
        }
        if(!breed3.equals("Select breed"))
        {
            newDogBreed(breed3, dog);
        }

        return redirect(routes.LocationController.getShelterPage("Dog successfully added."));
    }
    @Transactional(readOnly = true)
    public Result getViewUserDog(int userId, int dogId)
    {
        String dogSql = "SELECT NEW models.DogDetail(d.dogId, d.dogName, d.dogReffNum, d.weight, d.height, hl.hairLengthName, c.colorName, d.dogAge) " +
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

        String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.userId = :userId";
        PetPagesUser user = jpaApi.em().createQuery(userSql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        return ok(views.html.viewuserdog.render(user, dog, personalities, photos, firstPhoto));
    }

    @Transactional
    public Result getDogPhoto(Integer dogPhotoId)
    {
        String sql = "SELECT dp FROM DogPhoto dp WHERE dp.dogPhotoId = :dogPhotoId";

        DogPhoto dogPhoto = jpaApi.em().createQuery(sql, DogPhoto.class).
                            setParameter("dogPhotoId", dogPhotoId).getSingleResult();

        return ok(dogPhoto.getDogPhotoData()).as("image/jpg");
    }

    @Transactional
    public Result getDogDefaultPhoto(Integer dogId)
    {
        String sql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(sql, Dog.class).
                setParameter("dogId", dogId).getSingleResult();

        return ok(dog.getDefaultPhotoData()).as("image/jpg");
    }


    @Transactional(readOnly = true)
    public Result getEditUserDog(int userId, int dogId)
    {
        String userSql = "SELECT ppu FROM PetPagesUser ppu WHERE ppu.userId = :userId";
        PetPagesUser user = jpaApi.em().createQuery(userSql, PetPagesUser.class).setParameter("userId", userId).getSingleResult();

        String emailAddress = user.getEmailAddress();
        if(isLoggedIn() && emailAddress.equals(session().get("loggedIn")))
        {
            String dogSql = "SELECT NEW models.DogDetail(d.dogId, d.dogName, d.dogReffNum, d.weight, d.height, hl.hairLengthName, c.colorName, d.dogAge) " +
                    "FROM Dog d " +
                    "JOIN HairLength hl ON d.hairLengthId = hl.hairLengthId " +
                    "JOIN Color c ON c.colorId = d.colorId " +
                    "WHERE d.dogId = :dogId " +
                    "ORDER BY d.dogName";

            DogDetail dog = jpaApi.em().createQuery(dogSql, DogDetail.class).setParameter("dogId", dogId).getSingleResult();

            String dogColorSql = "SELECT c FROM Color c " +
                    "JOIN Dog d ON d.colorId = c.colorId " +
                    "WHERE d.dogId = :dogId";

            Color color = jpaApi.em().createQuery(dogColorSql, Color.class).setParameter("dogId", dogId).getSingleResult();

            String dogPersonalitySql = "SELECT p FROM Personality p " +
                    "JOIN DogPersonality dp ON dp.personalityId = p.personalityId " +
                    "JOIN Dog d ON dp.dogId = d.dogId " +
                    "WHERE d.dogId = :dogId";

            List<Personality> dogPersonalities = jpaApi.em().createQuery(dogPersonalitySql, Personality.class).setParameter("dogId", dogId).getResultList();
            List<Integer> dogPersonalityIds = new ArrayList<>();

            for(Personality personality: dogPersonalities)
            {
                dogPersonalityIds.add(personality.getPersonalityId());
            }

            String colorSql = "SELECT c FROM Color c ORDER BY c.colorName";
            String personalitySql = "SELECT p FROM Personality p ORDER BY p.personalityName";
            String breedSql = "SELECT b FROM Breed b ORDER BY b.breedName";

            List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();
            List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();
            List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

            String dogBreedSql = "SELECT b FROM Breed b " +
                                 "JOIN DogBreed db ON db.breedId = b.breedId " +
                                 "WHERE db.dogId = :dogId";

            List<Breed> dogBreeds = jpaApi.em().createQuery(dogBreedSql, Breed.class).setParameter("dogId", dog.getDogId()).getResultList();

            String breed1 = "";
            String breed2 = "";
            String breed3 = "";

            if(dogBreeds.size() == 0)
            {
                breed1 = "Select breed";
                breed2 = "Select breed";
                breed3 = "Select breed";
            }
            if(dogBreeds.size() == 1)
            {
                breed1 = dogBreeds.get(0).getBreedName();
                breed2 = "Select breed";
                breed3 = "Select breed";
            }
            if(dogBreeds.size() == 2)
            {
                breed1 = dogBreeds.get(0).getBreedName();
                breed2 = dogBreeds.get(1).getBreedName();
                breed3 = "Select breed";
            }
            if(dogBreeds.size() == 3)
            {
                breed1 = dogBreeds.get(0).getBreedName();
                breed2 = dogBreeds.get(1).getBreedName();
                breed3 = dogBreeds.get(2).getBreedName();
            }

            return ok(views.html.edituserdog.render(user, dog, color, colors, dogPersonalities, personalities, dogPersonalityIds,
                                                    breeds, breed1, breed2, breed3));
        }
        else
        {
            return redirect(routes.UserController.getLogIn("Please log in to access this page."));
        }
    }

    @Transactional
    public Result postEditUserDog(int userId, int dogId)
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String dogSql = "SELECT d FROM Dog d WHERE dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        String dogName = form.get("dogName");
        int dogAge = Integer.parseInt(form.get("dogAge"));
        int weight = Integer.parseInt(form.get("dogWeight"));
        int height = Integer.parseInt(form.get("dogHeight"));
        String hairLengthName = form.get("hairLength");
        String colorName = form.get("colorName");

        String hairLengthSql = "SELECT hl FROM HairLength hl " +
                               "WHERE hl.hairLengthName = :hairLengthName";

        HairLength hairLength = jpaApi.em().createQuery(hairLengthSql, HairLength.class).
                                setParameter("hairLengthName", hairLengthName).getSingleResult();

        String colorSql = "SELECT c FROM Color c " +
                          "WHERE c.colorName = :colorName";

        Color color = jpaApi.em().createQuery(colorSql, Color.class).setParameter("colorName", colorName).getSingleResult();

        dog.setDogName(dogName);
        dog.setDogAge(dogAge);
        dog.setWeight(weight);
        dog.setHeight(height);
        dog.setHairLengthId(hairLength.getHairLengthId());
        dog.setColorId(color.getColorId());

        String deleteDogPersonalitiesSql = "SELECT dp FROM DogPersonality dp WHERE dp.dogId = :dogId";

        List<DogPersonality> deletedDogPersonalities = jpaApi.em().
                                                     createQuery(deleteDogPersonalitiesSql, DogPersonality.class).
                                                     setParameter("dogId", dogId).getResultList();

        for(DogPersonality dogPersonality : deletedDogPersonalities)
        {
            jpaApi.em().remove(dogPersonality);
        }

        String dogPersonalitiesSql = "SELECT p FROM Personality p";

        List<Personality> personalities = jpaApi.em().createQuery(dogPersonalitiesSql, Personality.class).getResultList();

        for(Personality personality : personalities)
        {
            if(form.get(String.valueOf(personality.getPersonalityId())) != null)
            {
                DogPersonality newDogPersonality = new DogPersonality();

                newDogPersonality.setPersonalityId(personality.getPersonalityId());
                newDogPersonality.setDogId(dog.getDogId());
                jpaApi.em().persist(newDogPersonality);
            }
        }

        jpaApi.em().persist(dog);

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();

        Http.MultipartFormData.FilePart<File> filePart1 = formData.getFile("dogPhoto1");
        File file1 = filePart1.getFile();

        Http.MultipartFormData.FilePart<File> filePart2 = formData.getFile("dogPhoto2");
        File file2 = filePart2.getFile();

        Http.MultipartFormData.FilePart<File> filePart3 = formData.getFile("dogPhoto3");
        File file3 = filePart3.getFile();

        if(file1 != null)
        {
            try
            {
                DogPhoto dogPhoto = new DogPhoto();
                dogPhoto.setDogId(dog.getDogId());
                dogPhoto.setDogPhotoData(Files.toByteArray(file1));
                jpaApi.em().persist(dogPhoto);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        if(file2 != null)
        {
            try
            {
                DogPhoto dogPhoto = new DogPhoto();
                dogPhoto.setDogId(dog.getDogId());
                dogPhoto.setDogPhotoData(Files.toByteArray(file2));
                jpaApi.em().persist(dogPhoto);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        if(file3 != null)
        {
            try
            {
                DogPhoto dogPhoto = new DogPhoto();
                dogPhoto.setDogId(dog.getDogId());
                dogPhoto.setDogPhotoData(Files.toByteArray(file3));
                jpaApi.em().persist(dogPhoto);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }
        String dogBreedSql = "SELECT db FROM DogBreed db " +
                "WHERE db.dogId = :dogId";

        List<DogBreed> dogBreeds = jpaApi.em().createQuery(dogBreedSql, DogBreed.class).setParameter("dogId", dog.getDogId()).getResultList();

        String breed1 = form.get("breed1");
        String breed2 = form.get("breed2");
        String breed3 = form.get("breed3");

        if(dogBreeds.size() == 1)
        {
            jpaApi.em().remove(dogBreeds.get(0));
        }
        if(dogBreeds.size() == 2)
        {
            jpaApi.em().remove(dogBreeds.get(0));
            jpaApi.em().remove(dogBreeds.get(1));
        }
        if(dogBreeds.size() == 3)
        {
            jpaApi.em().remove(dogBreeds.get(0));
            jpaApi.em().remove(dogBreeds.get(1));
            jpaApi.em().remove(dogBreeds.get(2));
        }

        if(!breed1.equals("Select breed"))
        {
            newDogBreed(breed1, dog);
        }
        if(!breed2.equals("Select breed"))
        {
            newDogBreed(breed2, dog);
        }
        if(!breed3.equals("Select breed"))
        {
            newDogBreed(breed3, dog);
        }

        return redirect(routes.UserController.getUserPage("Dog successfully saved."));
    }

    @Transactional
    public Result getDeleteUserDog(int dogId)
    {
        String dogSql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";
        Dog dog = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        String dogBreedSql = "SELECT db FROM DogBreed db WHERE db.dogId = :dogId";
        List<DogBreed> breeds = jpaApi.em().createQuery(dogBreedSql, DogBreed.class).
                                setParameter("dogId", dogId).getResultList();

        for(DogBreed dogBreed : breeds)
        {
            jpaApi.em().remove(dogBreed);
        }

        String dogPersonalitySql = "SELECT dp FROM DogPersonality dp WHERE dp.dogId = :dogId";
        List<DogPersonality> personalities = jpaApi.em().createQuery(dogPersonalitySql, DogPersonality.class).
                                             setParameter("dogId", dogId).getResultList();

        for(DogPersonality dogPersonality : personalities)
        {
            jpaApi.em().remove(dogPersonality);
        }

        String dogPhotoSql = "SELECT dph FROM DogPhoto dph WHERE dph.dogId = :dogId";
        List<DogPhoto> photos = jpaApi.em().createQuery(dogPhotoSql, DogPhoto.class).
                setParameter("dogId", dogId).getResultList();

        for(DogPhoto photo : photos)
        {
            jpaApi.em().remove(photo);
        }

        jpaApi.em().remove(dog);

        return redirect(routes.UserController.getUserPage("Dog successfully deleted"));
    }

    @Transactional(readOnly = true)
    public Result getViewLocationDog(int dogId)
    {
        String dogSql = "SELECT NEW models.DogDetail(d.dogId, d.dogName, d.dogReffNum, d.weight, d.height, hl.hairLengthName, c.colorName, d.dogAge) " +
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

        return ok(views.html.viewlocationdog.render(dog, personalities, photos, firstPhoto));
    }

    @Transactional(readOnly = true)
    public Result getEditLocationDog(int dogId)
    {
        String emailAddress = session().get("loggedIn");

        if(isLoggedIn() && emailAddress.equals(session().get("loggedIn")))
        {
            String dogSql = "SELECT NEW models.DogDetail(d.dogId, d.dogName, d.dogReffNum, d.weight, d.height, hl.hairLengthName, c.colorName, d.dogAge) " +
                    "FROM Dog d " +
                    "JOIN HairLength hl ON d.hairLengthId = hl.hairLengthId " +
                    "JOIN Color c ON c.colorId = d.colorId " +
                    "WHERE d.dogId = :dogId " +
                    "ORDER BY d.dogName";

            DogDetail dog = jpaApi.em().createQuery(dogSql, DogDetail.class).setParameter("dogId", dogId).getSingleResult();

            String dogColorSql = "SELECT c FROM Color c " +
                    "JOIN Dog d ON d.colorId = c.colorId " +
                    "WHERE d.dogId = :dogId";

            Color color = jpaApi.em().createQuery(dogColorSql, Color.class).setParameter("dogId", dogId).getSingleResult();

            String dogPersonalitySql = "SELECT p FROM Personality p " +
                    "JOIN DogPersonality dp ON dp.personalityId = p.personalityId " +
                    "JOIN Dog d ON dp.dogId = d.dogId " +
                    "WHERE d.dogId = :dogId";

            List<Personality> dogPersonalities = jpaApi.em().createQuery(dogPersonalitySql, Personality.class).setParameter("dogId", dogId).getResultList();
            List<Integer> dogPersonalityIds = new ArrayList<>();

            for(Personality personality: dogPersonalities)
            {
                dogPersonalityIds.add(personality.getPersonalityId());
            }

            String colorSql = "SELECT c FROM Color c ORDER BY c.colorName";
            String personalitySql = "SELECT p FROM Personality p ORDER BY p.personalityName";

            List<Color> colors = jpaApi.em().createQuery(colorSql, Color.class).getResultList();
            List<Personality> personalities = jpaApi.em().createQuery(personalitySql, Personality.class).getResultList();

            String dogBreedSql = "SELECT b FROM Breed b " +
                    "JOIN DogBreed db ON db.breedId = b.breedId " +
                    "WHERE db.dogId = :dogId";

            List<Breed> dogBreeds = jpaApi.em().createQuery(dogBreedSql, Breed.class).setParameter("dogId", dog.getDogId()).getResultList();

            String breedSql = "SELECT b FROM Breed b ORDER BY b.breedName";

            List<Breed> breeds = jpaApi.em().createQuery(breedSql, Breed.class).getResultList();

            String breed1 = "";
            String breed2 = "";
            String breed3 = "";

            if(dogBreeds.size() == 0)
            {
                breed1 = "Select breed";
                breed2 = "Select breed";
                breed3 = "Select breed";
            }
            if(dogBreeds.size() == 1)
            {
                breed1 = dogBreeds.get(0).getBreedName();
                breed2 = "Select breed";
                breed3 = "Select breed";
            }
            if(dogBreeds.size() == 2)
            {
                breed1 = dogBreeds.get(0).getBreedName();
                breed2 = dogBreeds.get(1).getBreedName();
                breed3 = "Select breed";
            }
            if(dogBreeds.size() == 3)
            {
                breed1 = dogBreeds.get(0).getBreedName();
                breed2 = dogBreeds.get(1).getBreedName();
                breed3 = dogBreeds.get(2).getBreedName();
            }

            return ok(views.html.editlocationdog.render(dog, color, colors, dogPersonalities, personalities, dogPersonalityIds,
                                                        breeds, breed1, breed2, breed3));
        }
        else
        {
            return redirect(routes.UserController.getLogIn("Please log in to access this page."));
        }
    }

    @Transactional
    public Result postEditLocationDog(int dogId)
    {
        DynamicForm form = formFactory.form().bindFromRequest();

        String dogSql = "SELECT d FROM Dog d WHERE dogId = :dogId";

        Dog dog = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        String dogName = form.get("dogName");
        String dogReffNum = form.get("refId");
        int dogAge = Integer.parseInt(form.get("dogAge"));
        int weight = Integer.parseInt(form.get("dogWeight"));
        int height = Integer.parseInt(form.get("dogHeight"));
        String hairLengthName = form.get("hairLength");
        String colorName = form.get("colorName");

        String hairLengthSql = "SELECT hl FROM HairLength hl " +
                "WHERE hl.hairLengthName = :hairLengthName";

        HairLength hairLength = jpaApi.em().createQuery(hairLengthSql, HairLength.class).setParameter("hairLengthName", hairLengthName).getSingleResult();

        String colorSql = "SELECT c FROM Color c " +
                "WHERE c.colorName = :colorName";

        Color color = jpaApi.em().createQuery(colorSql, Color.class).setParameter("colorName", colorName).getSingleResult();

        dog.setDogName(dogName);
        dog.setDogReffNum(dogReffNum);
        dog.setDogAge(dogAge);
        dog.setWeight(weight);
        dog.setHeight(height);
        dog.setHairLengthId(hairLength.getHairLengthId());
        dog.setColorId(color.getColorId());

        String deleteDogPersonalitiesSql = "SELECT dp FROM DogPersonality dp WHERE dp.dogId = :dogId";

        List<DogPersonality> deletedDogPersonalities = jpaApi.em().
                createQuery(deleteDogPersonalitiesSql, DogPersonality.class).
                setParameter("dogId", dogId).getResultList();

        for(DogPersonality dogPersonality : deletedDogPersonalities)
        {
            jpaApi.em().remove(dogPersonality);
        }

        String dogPersonalitiesSql = "SELECT p FROM Personality p";

        List<Personality> personalities = jpaApi.em().createQuery(dogPersonalitiesSql, Personality.class).getResultList();

        for(Personality personality : personalities)
        {
            if(form.get(String.valueOf(personality.getPersonalityId())) != null)
            {
                DogPersonality newDogPersonality = new DogPersonality();

                newDogPersonality.setPersonalityId(personality.getPersonalityId());
                newDogPersonality.setDogId(dog.getDogId());
                jpaApi.em().persist(newDogPersonality);
            }
        }

        jpaApi.em().persist(dog);

        Http.MultipartFormData<File> formData = request().body().asMultipartFormData();

        Http.MultipartFormData.FilePart<File> filePart1 = formData.getFile("dogPhoto1");
        File file1 = filePart1.getFile();

        Http.MultipartFormData.FilePart<File> filePart2 = formData.getFile("dogPhoto2");
        File file2 = filePart2.getFile();

        Http.MultipartFormData.FilePart<File> filePart3 = formData.getFile("dogPhoto3");
        File file3 = filePart3.getFile();

        if(file1 != null)
        {
            try
            {
                DogPhoto dogPhoto = new DogPhoto();
                dogPhoto.setDogId(dog.getDogId());
                dogPhoto.setDogPhotoData(Files.toByteArray(file1));
                jpaApi.em().persist(dogPhoto);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        if(file2 != null)
        {
            try
            {
                DogPhoto dogPhoto = new DogPhoto();
                dogPhoto.setDogId(dog.getDogId());
                dogPhoto.setDogPhotoData(Files.toByteArray(file2));
                jpaApi.em().persist(dogPhoto);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        if(file3 != null)
        {
            try
            {
                DogPhoto dogPhoto = new DogPhoto();
                dogPhoto.setDogId(dog.getDogId());
                dogPhoto.setDogPhotoData(Files.toByteArray(file3));
                jpaApi.em().persist(dogPhoto);
            }
            catch(Exception e)
            {
                //do nothing
            }
        }

        String dogBreedSql = "SELECT db FROM DogBreed db " +
                "WHERE db.dogId = :dogId";

        List<DogBreed> dogBreeds = jpaApi.em().createQuery(dogBreedSql, DogBreed.class).setParameter("dogId", dog.getDogId()).getResultList();

        String breed1 = form.get("breed1");
        String breed2 = form.get("breed2");
        String breed3 = form.get("breed3");

        if(dogBreeds.size() == 1)
        {
            jpaApi.em().remove(dogBreeds.get(0));
        }
        if(dogBreeds.size() == 2)
        {
            jpaApi.em().remove(dogBreeds.get(0));
            jpaApi.em().remove(dogBreeds.get(1));
        }
        if(dogBreeds.size() == 3)
        {
            jpaApi.em().remove(dogBreeds.get(0));
            jpaApi.em().remove(dogBreeds.get(1));
            jpaApi.em().remove(dogBreeds.get(2));
        }

        if(!breed1.equals("Select breed"))
        {
            newDogBreed(breed1, dog);
        }
        if(!breed2.equals("Select breed"))
        {
            newDogBreed(breed2, dog);
        }
        if(!breed3.equals("Select breed"))
        {
            newDogBreed(breed3, dog);
        }

        return redirect(routes.LocationController.getShelterPage("Edit dog successful"));
    }

    @Transactional
    public Result getDeleteLocationDog(int dogId)
    {
        String dogSql = "SELECT d FROM Dog d WHERE d.dogId = :dogId";
        Dog dog = jpaApi.em().createQuery(dogSql, Dog.class).setParameter("dogId", dogId).getSingleResult();

        String dogBreedSql = "SELECT db FROM DogBreed db WHERE db.dogId = :dogId";
        List<DogBreed> breeds = jpaApi.em().createQuery(dogBreedSql, DogBreed.class).
                setParameter("dogId", dogId).getResultList();

        for(DogBreed dogBreed : breeds)
        {
            jpaApi.em().remove(dogBreed);
        }

        String dogPersonalitySql = "SELECT dp FROM DogPersonality dp WHERE dp.dogId = :dogId";
        List<DogPersonality> personalities = jpaApi.em().createQuery(dogPersonalitySql, DogPersonality.class).
                setParameter("dogId", dogId).getResultList();

        for(DogPersonality dogPersonality : personalities)
        {
            jpaApi.em().remove(dogPersonality);
        }

        String dogPhotoSql = "SELECT dph FROM DogPhoto dph WHERE dph.dogId = :dogId";
        List<DogPhoto> photos = jpaApi.em().createQuery(dogPhotoSql, DogPhoto.class).
                setParameter("dogId", dogId).getResultList();

        for(DogPhoto photo : photos)
        {
            jpaApi.em().remove(photo);
        }

        jpaApi.em().remove(dog);

        return redirect(routes.LocationController.getShelterPage("Dog successfully deleted"));
    }

    @Transactional(readOnly = true)
    public Result getViewDog(int dogId)
    {
        String dogSql = "SELECT NEW models.DogDetail(d.dogId, d.dogName, d.dogReffNum, d.weight, d.height, hl.hairLengthName, c.colorName, d.dogAge) " +
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

        String locationSql = "SELECT l FROM Location l " +
                             "JOIN Dog d ON d.locationId = l.locationId " +
                             "WHERE d.dogId = :dogId";

        Location shelter = jpaApi.em().createQuery(locationSql, Location.class).
                           setParameter("dogId", dogId).getSingleResult();

        return ok(views.html.viewdog.render(dog, personalities, photos, firstPhoto, shelter));
    }

    @Transactional(readOnly = true)
    public Result getViewDogs()
    {
        String dogsSql = "SELECT d FROM Dog d WHERE d.locationId != null";

        List<Dog> dogs = jpaApi.em().createQuery(dogsSql, Dog.class).getResultList();

        return ok(views.html.viewdogs.render(dogs));
    }
}
