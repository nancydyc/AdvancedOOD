package AdvancedOOD;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  APIs
        put:
            scanQRcode()
            confirmPkgSize()
            assignToAnotherLocker()
            openLocker()
            confirmDelivery()
            sendNotification()
            closeLocker()
        get:
            inputOrderId()
            openLocker()
            closeLocker()
            sendNotification()
            releaseLocker()
        
    TODO:
        May have some problems when we want to use a bigger type of locker to store a smaller package:
        Solution: assign value to enum locker type.
                An ArrayList is better than HashMap when we want to sort the locker types by some value.

 */

 
public class LockerStation {
    private final String id;
    // private static final int EACH_CAPACITY = 5; // In one station, each type of lockers has five lockers
    // public List<Locker> lockers;
    // private int count;
    List<Locker> envLockers;
    List<Locker> stdLockers;
    List<Locker> cLockers;
    int capacity;
    HashMap<PkgType, List<Locker>> lockers;
    
    // public LockerStation(String id) {
    public LockerStation(String id, int envelopeNum, int standardNum, int coolerNum) {
        this.id = id;
        // lockers = new ArrayList<>();
        // count = 0;
        // for (int i = 0; i < EACH_CAPACITY; i++) {
        //     lockers.add(new Envelope("E " + i));
        //     count++;
        // }
        // for (int i = 0; i < EACH_CAPACITY; i++) {
        //     lockers.add(new Standard("S " + i));
        //     count++;
        // }
        // for (int i = 0; i < EACH_CAPACITY; i++) {
        //     lockers.add(new Cooler("C " + i));
            // count++;
        // }
        this.lockers = new HashMap<PkgType, List<Locker>>();
        List<Locker> envLockers = new ArrayList<>();
        for (int i = 0; i < envelopeNum; i++) {
            envLockers.add(new Envelope(String.valueOf(i) + " E"));
            capacity++;
        }
        this.lockers.put(PkgType.SMALL, envLockers);

        List<Locker> stdLockers = new ArrayList<>();
        for (int i = 0; i < standardNum; i++) {
            stdLockers.add(new Standard(String.valueOf(i) + " S"));
            capacity++;
        }
        this.lockers.put(PkgType.STANDARD, stdLockers);

        List<Locker> cLockers = new ArrayList<>();
        for (int i = 0; i < coolerNum; i++) {
            cLockers.add(new Cooler(String.valueOf(i) + " C"));
            capacity++;
        }
        this.lockers.put(PkgType.FRESH, cLockers);
    }

    public String getAvailableLocker(UserPkg pkg) {// and deliver
    	// find the matched locker type
        List<Locker> matchLockers = this.lockers.getOrDefault(pkg.getSize(), null); // TODO: pkg.getSize().getVal() => arraylist
        if (matchLockers == null) {
            throw new RuntimeException("No such type of locker");
        }
        // search for the exact locker
        for (int i = 0; i < matchLockers.size(); i++) {
            if (matchLockers.get(i).pkg == null) {
                // place package into the locker
                Locker locker = matchLockers.get(i);
                locker.openLocker();
                locker.confirmDelivery(pkg);
                matchLockers.set(i, locker);
                return locker.id;
            }
        }
        throw new RuntimeException("No available locker");
        //TODO: If no available matching size locker, search next bigger locker
    }

    public UserPkg findPackageLocker(String lockerId) { // how to match pick up locker id with locker id
        String lockerTypeKey = lockerId.split(" ")[1];
//        System.out.println("Locker type is " + lockerTypeKey);
        int num = Integer.parseInt(lockerId.split(" ")[0]);
        PkgType locationKey = null;
        if (lockerTypeKey.equals("E")) locationKey = PkgType.SMALL;
        if (lockerTypeKey.equals("S")) locationKey = PkgType.STANDARD;
        if (lockerTypeKey.equals("C")) locationKey = PkgType.FRESH;
        
//        System.out.println("Lockers at station" + this.id + ": " + this.lockers);
        List<Locker> matchLockers = this.lockers.getOrDefault(locationKey, null); // what the default value should be? line 67?
        if (matchLockers == null) {
            throw new RuntimeException("Invalid Locker ID");
        }
        Locker locker = matchLockers.get(num);
        locker.openLocker();
        UserPkg pack = locker.retrievePackage();
        matchLockers.set(num, locker);
        locker.closeLocker();
        return pack;   
    }
    
    public static void main(String[] args) {
        User user1 = new User.UserBuilder("CC", "aba").setEmail("ccg@mail.ccsf.edu").build();
        System.out.println(user1);
        
        Order order1 = new Order("0", user1);
        System.out.println(order1.toString());
        
        LockerStation station1 = new LockerStation("1", 5, 6, 7);
        System.out.println("Station " + station1.id + " has " + station1.capacity + " lockers in total.");
        System.out.println();
        
        UserPkg pack1 = new UserPkg("0", PkgType.SMALL, order1);
        System.out.println("This is a " + pack1.getSize() + " package. ID: " + pack1.getId());
        System.out.println();
        
        String deliveredPackId = station1.getAvailableLocker(pack1);
        System.out.println();
        
        if (deliveredPackId != null) {
            UserPkg userPackage = station1.findPackageLocker(deliveredPackId);
            System.out.println(userPackage.getOrder().toString() + ". Package: " + userPackage.getId() + " delivery completed.");	
        }

    }
}


class Order {
    private final String id;
    Status status;
    User user;
    Date createAt;

    public Order(String id, User user) {
        this.id = id;
        this.user = user;
        createAt = new Date();
        status = Status.SHIPPED;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String displayUserName() {
        User user = getUser();
        if (user == null) {
            throw new RuntimeException("No user matched with this order");
        }
        return user.getName();
    }
    
    public void setDeliveredStatus() {
    	this.status = Status.DELIVERED;
    }
        
    public void setOnthewayStatus() {
    	this.status = Status.ON_THE_WAY;
    }
    
    @Override
    public String toString() {
    	return "This is " + this.getUser().getName() + "'s order " + this.getId() + " , created at " + this.createAt;
    }
}


class User {
    // private final String id;
    private String name;
    private String password;
    private String email;
    private String phone;

    private User(UserBuilder builder) {
        // this.id = id;
        this.name = builder.name;
        this.password = builder.password;
        this.email = builder.email;
        this.phone = builder.phone;
    }
    
    @Override
    public String toString() {
        return "User data: " + name + " " + email + " " + phone;
    }

    public static class UserBuilder {
        // private final String id;
        private String name;
        private String password;
        private String email;
        private String phone;

        public UserBuilder(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    // getter
    public String getName() {
        return this.name;
    }
}

enum Status {
    SHIPPED,
    ON_THE_WAY,
    DELIVERED
}

enum PkgType {
    SMALL(0),
    STANDARD(1),
    XL(2),
    FRESH(3),
    INSURED(4); // expected ";"

    private int val;

    private PkgType(int val) { // only private allowed for enum constructor
        this.val = val;
    }

    public int getVal() {
        return this.val;
    }
}

class UserPkg {
    private final String id;
    private final PkgType size;
    private final Order order; // is it necessary?

    public UserPkg(String id, PkgType size, Order order) {
        this.size = size;
        this.id = id;
        this.order = order;
        this.order.setOnthewayStatus();
    }

    public Order getOrder() {
        return order;
    }

    public PkgType getSize() {
        return size;
    }

    public String getId() {
        return id;
    }
}

enum LockerType {
    SMALL,
    STANDARD,
    XL,
    COOLER
}

abstract class Locker {
    String id;
    LockerType size;
    UserPkg pkg;

    public Locker(String id) {
        this.id = id;
    }

    public abstract boolean canFit(UserPkg pkg);
    
    public boolean isEmpty() {
        return pkg == null;
    }
    
    public String openLocker() {
    	System.out.println("Opened Locker " + this.id);
    	return this.id;
    }

    public void confirmDelivery(UserPkg pkg) {
        this.pkg = pkg;
        pkg.getOrder().setDeliveredStatus();
        System.out.println("Order is " + pkg.getOrder().status + ". Closed Locker " + id); 
    }
    
    public UserPkg retrievePackage() {
    	UserPkg ret = this.pkg;
    	System.out.println(pkg.getId() + " retrieved.");
    	this.pkg = null;
    	return ret;
    }
    
    public String closeLocker() {
    	System.out.println("Closed Locker " + this.id);
    	return this.id;
    }
}

class Envelope extends Locker {
    public Envelope(String id) {
        super(id);
    }
    @Override
    public boolean canFit(UserPkg pkg) {
        return pkg.getSize() == PkgType.SMALL;
    }
}

class Standard extends Locker {
    public Standard(String id) {
        super(id);
    }
    @Override
    public boolean canFit(UserPkg pkg) {
        return pkg.getSize() == PkgType.STANDARD || pkg.getSize() == PkgType.SMALL; // To optimize: use a comparator class for different sizes
    }
}

class Cooler extends Locker {
    public Cooler(String id) {
        super(id);
    }
    @Override
    public boolean canFit(UserPkg pkg) {
        return pkg.getSize() == PkgType.FRESH;
    }
}
