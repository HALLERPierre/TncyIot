package eu.telecomnancy.tncyiot.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Florian on 30/01/2017.
 * describe a room in the TNCY building's map
 */

public class Room {
    private String roomName;
    private int posX;
    private int posY;

    public Room(String roomName, int posX, int posY) {
        this.roomName = roomName;
        this.posX = posX;
        this.posY = posY;
    }


    public static Map<String,Room> loadMoteInRooms(){
        Map<String,Room> map = new HashMap<>();
        map.put("153.111",new Room("2-06",491,90));
        map.put("81.77",new Room("2-07",587,90));
        map.put("9.138",new Room("2-07",600,90));
        map.put("53.105",new Room("2-06",491,90));
        map.put("77.106",new Room("2-05",410,90));

        return map;

    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
