
CREATE TABLE State (
                StateId VARCHAR(2) NOT NULL,
                StateName VARCHAR(30) NOT NULL,
                PRIMARY KEY (StateId)
);


CREATE TABLE Personality (
                Personalityd INT AUTO_INCREMENT NOT NULL,
                PersonalityName VARCHAR(50) NOT NULL,
                PRIMARY KEY (Personalityd)
);


CREATE TABLE HairLength (
                HairLengthId INT AUTO_INCREMENT NOT NULL,
                HairLengthName VARCHAR(50) NOT NULL,
                SortOrder INT NOT NULL,
                PRIMARY KEY (HairLengthId)
);


CREATE TABLE Color (
                ColorId INT AUTO_INCREMENT NOT NULL,
                ColorName VARCHAR(50) NOT NULL,
                PRIMARY KEY (ColorId)
);


CREATE TABLE Breed (
                BreedId INT AUTO_INCREMENT NOT NULL,
                BreedName VARCHAR(40) NOT NULL,
                WeightMin INT NOT NULL,
                WeightMax INT NOT NULL,
                HeightMin INT NOT NULL,
                LifeSpanMax INT NOT NULL,
                CostFromBreeder DECIMAL(10,2) NOT NULL,
                HairLengthId INT NOT NULL,
                LifeSpanMin INT NOT NULL,
                HeightMax INT NOT NULL,
                photo1 VARCHAR(50) NOT NULL,
                photo2 VARCHAR(50) NOT NULL,
                photo3 VARCHAR(50) NOT NULL,
                PRIMARY KEY (BreedId)
);


CREATE TABLE BreedColor (
                BreedColorId INT AUTO_INCREMENT NOT NULL,
                ColorId INT NOT NULL,
                BreedId INT NOT NULL,
                PRIMARY KEY (BreedColorId)
);


CREATE TABLE BreedPersonality (
                BreedPersonalityId INT AUTO_INCREMENT NOT NULL,
                Personalityd INT NOT NULL,
                BreedId INT NOT NULL,
                PRIMARY KEY (BreedPersonalityId)
);


CREATE TABLE Location (
                LocationId INT AUTO_INCREMENT NOT NULL,
                LocationName VARCHAR(50) NOT NULL,
                City VARCHAR(50) NOT NULL,
                BreedId INT,
                Address VARCHAR(50) NOT NULL,
                StateId VARCHAR(2) NOT NULL,
                ZipCode VARCHAR(5) NOT NULL,
                PhoneNumber VARCHAR(10) NOT NULL,
                PRIMARY KEY (LocationId)
);


CREATE TABLE Dog (
                DogId INT AUTO_INCREMENT NOT NULL,
                DogName VARCHAR(50) NOT NULL,
                DogAge INT NOT NULL,
                DogReffNum VARCHAR(50) NOT NULL,
                Weight INT NOT NULL,
                HairLengthId INT NOT NULL,
                LocationId INT NOT NULL,
                DogPhoto VARCHAR NOT NULL,
                PRIMARY KEY (DogId)
);


CREATE TABLE DogBreed (
                DogBreedId INT AUTO_INCREMENT NOT NULL,
                BreedId INT NOT NULL,
                DogId INT NOT NULL,
                PRIMARY KEY (DogBreedId)
);


CREATE TABLE DogPhoto (
                DogPhotoId INT NOT NULL,
                DogPhotoName VARCHAR(50) NOT NULL,
                DogPhotoData LONGBLOB NOT NULL,
                DogId INT NOT NULL,
                PRIMARY KEY (DogPhotoId)
);


CREATE TABLE DogColor (
                DogColorId INT AUTO_INCREMENT NOT NULL,
                DogId INT NOT NULL,
                ColorId INT NOT NULL,
                PRIMARY KEY (DogColorId)
);


CREATE TABLE DogPersonality (
                DogPersonalityId INT AUTO_INCREMENT NOT NULL,
                DogId INT NOT NULL,
                Personalityd INT NOT NULL,
                PRIMARY KEY (DogPersonalityId)
);


ALTER TABLE Location ADD CONSTRAINT state_location_fk
FOREIGN KEY (StateId)
REFERENCES State (StateId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogPersonality ADD CONSTRAINT personality_dogpersonality_fk
FOREIGN KEY (Personalityd)
REFERENCES Personality (Personalityd)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BreedPersonality ADD CONSTRAINT personality_breedpersonality_fk
FOREIGN KEY (Personalityd)
REFERENCES Personality (Personalityd)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Dog ADD CONSTRAINT hairlength_dog_fk
FOREIGN KEY (HairLengthId)
REFERENCES HairLength (HairLengthId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Breed ADD CONSTRAINT hairlength_breed_fk
FOREIGN KEY (HairLengthId)
REFERENCES HairLength (HairLengthId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BreedColor ADD CONSTRAINT color_breedcolor_fk
FOREIGN KEY (ColorId)
REFERENCES Color (ColorId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogColor ADD CONSTRAINT color_dogcolor_fk
FOREIGN KEY (ColorId)
REFERENCES Color (ColorId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Location ADD CONSTRAINT breed_location_fk
FOREIGN KEY (BreedId)
REFERENCES Breed (BreedId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BreedPersonality ADD CONSTRAINT breed_breedpersonality_fk
FOREIGN KEY (BreedId)
REFERENCES Breed (BreedId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BreedColor ADD CONSTRAINT breed_breedcolor_fk
FOREIGN KEY (BreedId)
REFERENCES Breed (BreedId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogBreed ADD CONSTRAINT breed_dogbreed_fk
FOREIGN KEY (BreedId)
REFERENCES Breed (BreedId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Dog ADD CONSTRAINT location_dog_fk
FOREIGN KEY (LocationId)
REFERENCES Location (LocationId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogPersonality ADD CONSTRAINT dog_dogpersonality_fk
FOREIGN KEY (DogId)
REFERENCES Dog (DogId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogColor ADD CONSTRAINT dog_dogcolor_fk
FOREIGN KEY (DogId)
REFERENCES Dog (DogId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogPhoto ADD CONSTRAINT dog_dogphoto_fk
FOREIGN KEY (DogId)
REFERENCES Dog (DogId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE DogBreed ADD CONSTRAINT dog_dogbreed_fk
FOREIGN KEY (DogId)
REFERENCES Dog (DogId)
ON DELETE NO ACTION
ON UPDATE NO ACTION;