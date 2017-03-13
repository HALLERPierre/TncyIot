package eu.telecomnancy.tncyiot.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.telecomnancy.tncyiot.Entity.Light;

/**
 * Created by kromer1u on 26/01/17.
 *
 */
public class MailManager {
    private static final java.lang.String CONFIG_PROPERTIES = "prop.properties";
    private static Date lastmailsenddate ;

    /**
     * method called to send email
     * @param context
     */
    public static void sendMailSilent(Context context, Light light){

        //init the last mail send date 1 minute ago
        if (lastmailsenddate == null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -1);
            lastmailsenddate = cal.getTime();

        }
        if (getDateDiff(lastmailsenddate,new Date(),TimeUnit.SECONDS)<300){
            Log.e("Mail","sendMailSilent called but ignored");
            return;
        }

        //load properties gmail account is not versionned
        final Properties props = loadProperties(context);

        //Get SP & add listener to it
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String emailDest = prefs.getString("email", "f.kromer54@gmail.com");
        String username = prefs.getString("username", "noname");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(props.getProperty("username"), props.getProperty("password"));
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("TncyIot@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailDest));
            message.setSubject("Tncy IOT light detected");
            message.setText("Dear " + username +" , "
                    + "Light : " + light.getMote() + " switched on.");

            Transport.send(message);
            lastmailsenddate = new Date();

            Log.d("Mail","sendMailSilent called");


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties loadProperties(Context context) {
        Properties props = new Properties();
        /**
         * getAssets() Return an AssetManager instance for your
         * application's package. AssetManager Provides access to an
         * application's raw asset files;
         */
        AssetManager assetManager = context.getAssets();
        /**
         * Open an asset using ACCESS_STREAMING mode. This
         */
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(CONFIG_PROPERTIES);
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * Loads properties from the specified InputStream,
         */

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return props;

    }

    /**
     * Open mail app, dont use
     * @param context
     */
    @Deprecated
    public static  void sendMailIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, "f.kromer54@gmail.com");
        intent.setData(Uri.parse("mailto:" + "f.kromer54@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "azert");
        intent.putExtra(Intent.EXTRA_TEXT, "azertyuuyfcryxxu");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        try {

            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Email error:", e.toString());
        }
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
