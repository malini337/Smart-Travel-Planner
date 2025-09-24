import java.io.*;
import java.util.*;

/* Smart Travel Planner
   SDG Goal: SDG 11 - Sustainable Cities
   Java Concepts: Inheritance, ArrayList, Exception Handling
*/

/////////////////////// DATA CLASSES ///////////////////////
class Destination {
    private String name;
    private double costPerDay;
    public Destination(String name, double costPerDay) { this.name = name; this.costPerDay = costPerDay; }
    public String getName() { return name; }
    public double getCostPerDay() { return costPerDay; }
    public String toString() { return name + " (Rs" + costPerDay + "/day)"; }
}

class Cuisine {
    private String name; private double costPerMeal;
    public Cuisine(String name, double costPerMeal) { this.name=name; this.costPerMeal=costPerMeal; }
    public double getCostPerMeal() { return costPerMeal; }
    public String toString() { return name + " (Rs" + costPerMeal + "/meal)"; }
}

class Hotel {
    private String name, category; private double costPerNight; private int availableRooms;
    public Hotel(String name, String category, double costPerNight, int availableRooms){
        this.name=name; this.category=category; this.costPerNight=costPerNight; this.availableRooms=availableRooms;
    }
    public String getName(){return name;}
    public String getCategory(){return category;}
    public double getCostPerNight(){return costPerNight;}
    public int getAvailableRooms(){return availableRooms;}
    public boolean reserveRooms(int rooms){
        if(rooms<=availableRooms){availableRooms-=rooms; return true;} return false;
    }
    public String toString(){return name+" ("+category+") Rs"+costPerNight+"/night, Rooms left: "+availableRooms;}
}

class TravelClass {
    private String name; private double costPerPerson;
    public TravelClass(String name,double costPerPerson){this.name=name; this.costPerPerson=costPerPerson;}
    public String getName(){return name;}
    public double getCostPerPerson(){return costPerPerson;}
    public String toString(){return name+" (Rs"+costPerPerson+"/person)";}
}

/////////////////////// TRAVEL PLAN ///////////////////////
class TravelPlan {
    protected ArrayList<Destination> destinations=new ArrayList<>();
    protected Cuisine cuisine=null;
    protected Hotel hotel=null; protected int hotelRoomsReserved=0;
    protected TravelClass travelClass=null; protected int travelers=1;

    public void addDestination(Destination d){destinations.add(d);}
    public void setCuisine(Cuisine c){cuisine=c;}
    public void setHotel(Hotel h,int rooms){hotel=h; hotelRoomsReserved=rooms;}
    public void setTravelClass(TravelClass t,int travelers){travelClass=t; this.travelers=travelers;}
    public ArrayList<Destination> getDestinations(){return destinations;}
    public Cuisine getCuisine(){return cuisine;}
    public Hotel getHotel(){return hotel;}
    public int getHotelRoomsReserved(){return hotelRoomsReserved;}
    public TravelClass getTravelClass(){return travelClass;}
    public int getTravelers(){return travelers;}

    public void showPlan(){
        System.out.println("\n----- YOUR TRAVEL PLAN SUMMARY -----");
        System.out.println("Destinations:");
        for(Destination d:destinations) System.out.println("  - "+d);
        System.out.println("Cuisine chosen: "+(cuisine!=null? cuisine:"(none)"));
        if(hotel!=null) System.out.println("Hotel: "+hotel.getName()+" ("+hotel.getCategory()+") Rooms: "+hotelRoomsReserved+" Rs"+hotel.getCostPerNight()+"/night");
        if(travelClass!=null) System.out.println("Travel class: "+travelClass.getName()+" Travelers: "+travelers+" Rs"+travelClass.getCostPerPerson()+"/person");
    }
}

class CityPlan extends TravelPlan{
    private String cityName;
    public CityPlan(String cityName){this.cityName=cityName;}
    public String getCityName(){return cityName;}
    @Override public void showPlan(){System.out.println("\n=== City Plan for: "+cityName+" ==="); super.showPlan();}
}

class TourPlan extends CityPlan{
    public TourPlan(String city){super(city);}
    public void suggestEcoTip(){System.out.println("\nEco Tip: Use public transport, prefer local vendors, reduce single-use plastics.");}
}

/////////////////////// COST MANAGER ///////////////////////
class CostBreakdown{
    double destinationsCost=0, foodCost=0, hotelCost=0, travelCost=0, taxesAndFees=0, total=0;
}
class CostManager{
    public CostBreakdown estimateCost(TravelPlan plan,int days,int rooms,int travelers) throws Exception{
        if(plan.getDestinations().isEmpty()) throw new Exception("No destinations chosen!");
        if(plan.getHotel()==null) throw new Exception("No hotel selected!");
        if(plan.getCuisine()==null) throw new Exception("No cuisine selected!");
        if(plan.getTravelClass()==null) throw new Exception("No travel class selected!");
        CostBreakdown cb=new CostBreakdown();
        for(Destination d:plan.getDestinations()) cb.destinationsCost+=d.getCostPerDay()*days;
        cb.foodCost=plan.getCuisine().getCostPerMeal()*3*days*travelers;
        cb.hotelCost=plan.getHotel().getCostPerNight()*days*rooms;
        cb.travelCost=plan.getTravelClass().getCostPerPerson()*travelers;
        double subtotal=cb.destinationsCost+cb.foodCost+cb.hotelCost+cb.travelCost;
        cb.taxesAndFees=subtotal*0.05;
        cb.total=subtotal+cb.taxesAndFees;
        return cb;
    }
}

/////////////////////// FILE LOADER ///////////////////////
class PlacesFileLoader{
    public static Map<String,ArrayList<Destination>> loadFromFile(String filename) throws IOException{
        Map<String,ArrayList<Destination>> db=new HashMap<>();
        File f=new File(filename);
        if(!f.exists()) throw new FileNotFoundException("File not found: "+filename);
        BufferedReader br=new BufferedReader(new FileReader(f));
        String line;
        while((line=br.readLine())!=null){
            line=line.trim();
            if(line.isEmpty()||!line.contains("|")) continue;
            String[] parts=line.split("\\|",2);
            String city=parts[0].trim(); String list=parts[1].trim();
            String[] entries=list.split(",");
            ArrayList<Destination> places=new ArrayList<>();
            for(String e:entries){
                if(!e.contains(":")) continue;
                String[] p=e.split(":",2);
                String placeName=p[0].trim(); double cost=0;
                try{cost=Double.parseDouble(p[1].trim());}catch(Exception ex){cost=500;}
                places.add(new Destination(placeName,cost));
            }
            if(!places.isEmpty()) db.put(city,places);
        }
        br.close();
        return db;
    }
}

/////////////////////// MAIN ///////////////////////
public class SmartTravelPlanner{
    private static Map<String, ArrayList<Destination>> buildStaticCityDatabase(){
        Map<String, ArrayList<Destination>> db=new LinkedHashMap<>();
        db.put("Paris", new ArrayList<>(Arrays.asList(new Destination("Eiffel Tower",1200), new Destination("Louvre Museum",900), new Destination("Notre-Dame Cathedral",500), new Destination("Montmartre",400), new Destination("Seine River Cruise",600))));
        db.put("London", new ArrayList<>(Arrays.asList(new Destination("British Museum",600), new Destination("Tower of London",800), new Destination("London Eye",700), new Destination("Buckingham Palace",300), new Destination("Camden Market",200))));
        db.put("Tokyo", new ArrayList<>(Arrays.asList(new Destination("Senso-ji Temple",400), new Destination("Shibuya Crossing",200), new Destination("Tokyo Skytree",900), new Destination("Meiji Shrine",300), new Destination("Tsukiji Market",350))));
        db.put("New York", new ArrayList<>(Arrays.asList(new Destination("Statue of Liberty",800), new Destination("Central Park",300), new Destination("Metropolitan Museum",950), new Destination("Times Square",150), new Destination("Brooklyn Bridge",250))));
        db.put("Sydney", new ArrayList<>(Arrays.asList(new Destination("Sydney Opera House",900), new Destination("Harbour Bridge",300), new Destination("Bondi Beach",200), new Destination("Royal Botanic Garden",250), new Destination("Taronga Zoo",600))));
        return db;
    }

    private static ArrayList<Cuisine> buildCuisines(){
        return new ArrayList<>(Arrays.asList(new Cuisine("Local Specialties",350),new Cuisine("Continental",500),new Cuisine("Asian Fusion",450),new Cuisine("Street Food",200),new Cuisine("Vegetarian/Vegan",300),new Cuisine("Seafood",550),new Cuisine("Fine Dining",1200)));
    }

    private static ArrayList<Hotel> buildHotels(){
        return new ArrayList<>(Arrays.asList(new Hotel("City Budget Inn","1-Star Budget",3000,15),new Hotel("Comfort Stay","2-Star",4500,10),new Hotel("Holiday Comfort","3-Star",7000,8),new Hotel("Grand Royale","4-Star",12000,6),new Hotel("Platinum Suites","5-Star",18000,4),new Hotel("Eco Lodge","Eco-Friendly 3-Star",6500,5),new Hotel("Boutique Heritage","Luxury Boutique",15000,3)));
    }

    private static ArrayList<TravelClass> buildTravelClasses(){
        return new ArrayList<>(Arrays.asList(new TravelClass("Economy",25000),new TravelClass("Premium Economy",40000),new TravelClass("Business",90000),new TravelClass("First Class",180000)));
    }

    private static void displayAvailableCities(Map<String, ArrayList<Destination>> db){
        System.out.println("\nAvailable Destinations (Cities):");
        int idx=1;
        for(String city:db.keySet()){System.out.println(" "+idx+". "+city); idx++;}
    }

    private static int readIntSafe(Scanner sc,int min,int max){
        int val=-1;
        while(true){
            String s=sc.nextLine().trim();
            try{val=Integer.parseInt(s);
                if(val<min||val>max){System.out.print("Enter number between "+min+"-"+max+": "); continue;}
                break;
            }catch(NumberFormatException e){System.out.print("Invalid. Enter number between "+min+"-"+max+": ");}
        }
        return val;
    }

    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        Map<String,ArrayList<Destination>> cityDatabase;

        System.out.println("=== Smart Travel Planner (SDG 11 - Sustainable Cities) ===");

        try{
            System.out.print("Load places.txt file? (y/n): ");
            String useFile=sc.nextLine().trim().toLowerCase();
            if(useFile.equals("y")){
                System.out.print("Enter filename (default: places.txt): ");
                String filename=sc.nextLine().trim(); if(filename.isEmpty()) filename="places.txt";
                try{
                    cityDatabase=PlacesFileLoader.loadFromFile(filename);
                    if(cityDatabase.isEmpty()){
                        System.out.println("File empty. Using static data."); cityDatabase=buildStaticCityDatabase();
                    }else{System.out.println("Loaded data from "+filename);}
                }catch(Exception fe){System.out.println("Failed. Using static data."); cityDatabase=buildStaticCityDatabase();}
            }else cityDatabase=buildStaticCityDatabase();

            ArrayList<Cuisine> cuisines=buildCuisines();
            ArrayList<Hotel> hotels=buildHotels();
            ArrayList<TravelClass> travelClasses=buildTravelClasses();

            displayAvailableCities(cityDatabase);
            System.out.print("\nEnter city name to visit: ");
            String cityChoice=sc.nextLine().trim();
            if(!cityDatabase.containsKey(cityChoice)) throw new Exception("City not found.");

            TourPlan plan=new TourPlan(cityChoice);
            ArrayList<Destination> places=cityDatabase.get(cityChoice);
            System.out.println("\nPlaces available in "+cityChoice+":");
            for(int i=0;i<places.size();i++) System.out.println(" "+(i+1)+". "+places.get(i));
            System.out.print("Enter comma-separated place numbers: ");
            String[] tokens=sc.nextLine().trim().split(",");
            for(String t:tokens){try{int p=Integer.parseInt(t.trim())-1; if(p>=0&&p<places.size()) plan.addDestination(places.get(p));}catch(Exception e){continue;}}
            if(plan.getDestinations().isEmpty()) for(int i=0;i<Math.min(2,places.size());i++) plan.addDestination(places.get(i));

            System.out.println("\nCuisine Options:");
            for(int i=0;i<cuisines.size();i++) System.out.println(" "+(i+1)+". "+cuisines.get(i));
            System.out.print("Choose cuisine number: ");
            int cChoice=readIntSafe(sc,1,cuisines.size()); plan.setCuisine(cuisines.get(cChoice-1));

            System.out.println("\nHotel Options:");
            for(int i=0;i<hotels.size();i++) System.out.println(" "+(i+1)+". "+hotels.get(i));
            System.out.print("Choose hotel number: ");
            int hChoice=readIntSafe(sc,1,hotels.size()); Hotel chosenHotel=hotels.get(hChoice-1);
            System.out.print("Rooms to reserve (available "+chosenHotel.getAvailableRooms()+"): ");
            int rooms=readIntSafe(sc,1,chosenHotel.getAvailableRooms());
            chosenHotel.reserveRooms(rooms); plan.setHotel(chosenHotel,rooms);

            System.out.println("\nTravel Class Options:");
            for(int i=0;i<travelClasses.size();i++) System.out.println(" "+(i+1)+". "+travelClasses.get(i));
            System.out.print("Choose travel class number: ");
            int tChoice=readIntSafe(sc,1,travelClasses.size());
            TravelClass chosenTravel=travelClasses.get(tChoice-1);
            System.out.print("Number of travelers: ");
            int travelers=readIntSafe(sc,1,10); plan.setTravelClass(chosenTravel,travelers);

            System.out.print("\nNumber of days for the trip: ");
            int days=readIntSafe(sc,1,365);

            CostManager cm=new CostManager(); CostBreakdown cb=cm.estimateCost(plan,days,rooms,travelers);

            plan.showPlan(); plan.suggestEcoTip();
            System.out.println("\n----- COST BREAKDOWN -----");
            System.out.printf("Destinations cost: Rs%.2f\n",cb.destinationsCost);
            System.out.printf("Food cost: Rs%.2f\n",cb.foodCost);
            System.out.printf("Hotel cost: Rs%.2f\n",cb.hotelCost);
            System.out.printf("Travel cost: Rs%.2f\n",cb.travelCost);
            System.out.printf("Taxes & Fees: Rs%.2f\n",cb.taxesAndFees);
            System.out.printf("TOTAL ESTIMATED COST: Rs%.2f\n",cb.total);
            System.out.println("\nThank you for using Smart Travel Planner!");
        }catch(Exception e){System.out.println("\n[Error] "+e.getMessage());}
        finally{sc.close();}
    }
}
