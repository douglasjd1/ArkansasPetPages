package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

import java.security.SecureRandom;
import java.util.Random;

public class Email
{
    public static void sendForgotPasswordEmail(String contents, String destinationEmail)
    {
        String sender = "douglasjd09@gmail.com";
        String subject = "Arkansas Pet Pages - Password Reset";

        try
        {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                    .standard()
                    .withRegion(Regions.US_WEST_2)
                    //.withRegion(Regions.US_EAST_1)
                    .build();

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(destinationEmail))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(contents)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)))
                    .withSource(sender);

            client.sendEmail(request);
            System.out.println("Sent email!");

        }
        catch (Exception e)
        {
            System.out.println("Unable to send email " + e.getMessage());
        }
    }

    public static void sendWelcomeEmail(String contents, String destinationEmail)
    {
        String sender = "douglasjd09@gmail.com";
        String subject = "Arkansas Pet Pages - Welcome!";

        try
        {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                    .standard()
                    .withRegion(Regions.US_WEST_2)
                    //.withRegion(Regions.US_EAST_1)
                    .build();

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(destinationEmail))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(contents)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(subject)))
                    .withSource(sender);

            client.sendEmail(request);
            System.out.println("Sent email!");

        }
        catch (Exception e)
        {
            System.out.println("Unable to send email " + e.getMessage());
        }
    }

    public static String generateRandomPassword()
    {
        Random random = new SecureRandom();

        String charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                         "0123456789!@#$%^&*_=+-/";

        String password = "";

        for(int i = 0; i < 8; i++)
        {
            password += charSet.charAt(random.nextInt(74));
        }

        return password;
    }
}
