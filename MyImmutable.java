package AdvancedOOD;

public final class MyImmutable {
    private final int[] arr;
    private static final int LUCKYNUM = 9;
    final int number;

    public MyImmutable() {
        arr = new int[10];
        // LUCKYNUM = 9;
        number = 7; // can also be initialized above
    }

    public int getLuckyNumber() {
        return LUCKYNUM;
    }

    public void notMyLuckNumber() {
        System.out.println(number + " is not my lucky number");
    }

    public int getNumber() {
        return number;
    }

    public static void main(String[] args) {
        MyImmutable test1 = new MyImmutable();
        int firstEle = test1.arr[0];
        System.out.println(firstEle); // original 0
        test1.arr[0] = 1;
        System.out.println(firstEle); // not changed 0
        System.out.println(test1.arr[0]); // changed 1
        firstEle = 2;
        System.out.println(firstEle); // 2
        System.out.println(test1.arr[0]); // 1

        System.out.println(test1.LUCKYNUM); // not a static way to access
        System.out.println(test1.getLuckyNumber()); // a static way

        test1.notMyLuckNumber();
        System.out.println(test1.getNumber());
        // test1.number = 3; // final field cannot be assigned
        // test1.notMyLuckNumber();
    }
}

// class LuckNumber extends MyImmutable {
class LuckNumber {
    // private int number; // changed the field of parent class

    // public LuckNumber() {
    //     // super();
    //     number = 0;
    // }

    public int notMyNumber() {
        MyImmutable numObj = new MyImmutable();
        return numObj.number;
    }

    public static void main(String[] args) {
        LuckNumber test1 = new LuckNumber();
        int num = test1.notMyNumber();
        System.out.println(num);
    }
}