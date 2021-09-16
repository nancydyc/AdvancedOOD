package AdvancedOOD;

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
            releaseLocker()d
 */
 
public class LockerStation {
    private final String id;
    private static final int ENVELOPE_NUM = 3;
    private static final int STANDARD_NUM = 3;
    private static final int COOLER_NUM = 3;
    public List<Locker> lockers;
    private int count;
    
    public LockerStation(String id) {
        this.id = id;
        lockers = new ArrayList<>();
        count = 0;
        for (int i = 0; i < ENVELOPE_NUM + STANDARD_NUM + COOLER_NUM; i++) {
            if (i < ENVELOPE_NUM) {
                lockers.add(new Envelope(Integer.toString(i)));
            } else if (i < ENVELOPE_NUM + STANDARD_NUM) {
                lockers.add(new Standard(Integer.toString(i)));
            } else {
                lockers.add(new Cooler(Integer.toString(i)));
            }
            count++;
        }
    }

    // AMZ delivery person search for available locker and deliver package
    public String getAvailableLocker(UserPkg pkg) {
        for (Locker locker : this.lockers) {
            if (locker.isEmpty() && locker.canFit(pkg)) {
                locker.openLocker();
                locker.confirmDelivery(pkg);
                locker.closeLocker();
                return locker.id;
            }
        }
        throw new RuntimeException("No available locker");
        //TODO: If no available matching size locker, search next bigger locker
    }

    // User come to the station to find their package
    public UserPkg findPackageLocker(String lockerId) {
        int idx = Integer.parseInt(lockerId);
        Locker locker = this.lockers.get(idx);
        locker.openLocker();
        locker.sendPickedUpNotification();
        UserPkg pack = locker.retrievePackage();
        this.lockers.set(idx, locker);
        locker.closeLocker();
        return pack;   
    }
    
    public static void main(String[] args) {
        User user1 = new User.UserBuilder("CC", "aba").setEmail("ccg@mail.ccsf.edu").build();
        System.out.println(user1);
        
        Order order1 = new Order("0", user1);
        System.out.println(order1.toString());
        
        LockerStation station1 = new LockerStation("1");
        System.out.println("Station " + station1.id + " has " + station1.count + " lockers in total.");
        System.out.println();
        
        UserPkg pack1 = new UserPkg("0", PkgType.SMALL, order1);
        System.out.println("This is a " + pack1.getSize() + " package. ID: " + pack1.getId());
        System.out.println();
        
        String deliveredPackId = station1.getAvailableLocker(pack1);
        System.out.println();
        
        if (deliveredPackId != null) {
            station1.findPackageLocker(deliveredPackId);
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
        System.out.println("Order " + this.id + " status: " + this.status);
    }
        
    public void setOnthewayStatus() {
    	this.status = Status.ON_THE_WAY;
        System.out.println("Order " + this.id + " status: " + this.status);
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
        System.out.println("Order is " + pkg.getOrder().status + " for package " + pkg.getId()); // send notification
    }

    public UserPkg retrievePackage() {
    	UserPkg ret = this.pkg;
    	System.out.println("Package ID " + pkg.getId() + " retrieved.");
    	this.pkg = null;
    	return ret;
    }
    
    public String closeLocker() {
    	System.out.println("Closed Locker " + this.id);
    	return this.id;
    }

    public String sendPickedUpNotification() {
        String msg = pkg.getOrder().toString() + ". Package ID: " + pkg.getId() + " is picked up at." + new Date();
        System.out.println(msg);
        return msg;
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
