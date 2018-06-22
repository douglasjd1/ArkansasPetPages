package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

public class Email
{
    public static void sendEmail(String contents, String destinationEmail)
    {
        String sender = "douglasjd09@gmail.com";
        String subject = "Test Email";

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
}
