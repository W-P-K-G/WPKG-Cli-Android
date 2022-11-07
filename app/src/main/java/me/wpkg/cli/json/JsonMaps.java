package me.wpkg.cli.json;

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

    public static class AddressJSON {
        public uAddresses[] uAddresses;
        public tAddresses[] tAddresses;

        public static class uAddresses {
            public String ip;
            public int port;
        }

        public static class tAddresses {
            public String ip;
            public int port;
        }
    }
}
