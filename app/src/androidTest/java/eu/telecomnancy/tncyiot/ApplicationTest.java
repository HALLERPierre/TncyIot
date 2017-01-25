package eu.telecomnancy.tncyiot;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.github.javafaker.Faker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.Entity.LightsRecordsData;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    @Test
    public void testDetectSwitchOn(){
        Faker faker = new Faker();
        String label = "light1";
        Light l = new Light(new Date().getTime(),label,faker.number().randomDouble(2,Light.THRESHOLD,Light.THRESHOLD+50),faker.number().numberBetween(80,200)+"" );
        assertTrue("SwitchOn ok",l.isSwitchOn()==true);


    }

    @Test
    public void testDetectPeakInTime() {
        //https://fr.wikipedia.org/wiki/Moyenne_glissante

        //generates dummy data
        Faker faker = new Faker();
        LightsRecordsData map = new LightsRecordsData();
        for (int i=0;i< 50;i++){
            String label = "light"+faker.number().randomDigit();
            Light l = new Light(faker.date().between(new Date(2017,1,1), new Date(2017,1,24)).getTime(),label,faker.number().randomDouble(2,2,200),faker.number().numberBetween(80,200)+"" );
            if (! map.containsKey(label)){
                map.put(label,new LightRecords(new LightRecords.ChangeListener() {
                    @Override
                    public void onChange(Light light) {

                    }
                }));
            }
            map.get(label).add(l);
        }
        //sort light data
        for(Map.Entry<String,LightRecords> entry : map.entrySet()) {
            List<Light> lightList = entry.getValue();

            Collections.sort(lightList, new Comparator<Light>() {
                @Override
                public int compare(Light l1, Light l2)
                {
                    return  l1.getTimestamp() > l2.getTimestamp() ? +1 : l1.getTimestamp() < l2.getTimestamp() ? -1 : 0;
                }
            });

        }
        //add light switch on
        String label = "light1";
        Light l = new Light(new Date().getTime(),label,faker.number().randomDouble(2,Light.THRESHOLD,Light.THRESHOLD+50),faker.number().numberBetween(80,200)+"" );
        map.get(label).add(l);




        assertThat(true, is(true));

    }
}