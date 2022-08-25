package com.wpkg.cli.json;

public class JsonMaps
{
    public static class ClientObject
    {
        public String name;
        public int id;

        public boolean joined;

        public String version;

        public ClientObject(int id,String name,boolean joined,String version)
        {
            this.id = id;
            this.name = name;
            this.joined = joined;
            this.version = version;
        }

        public ClientObject()
        {

        }
    }

    public static class ClientMap
    {
        public ClientObject[] clients;

        public ClientMap()
        {

        }
    }
}
