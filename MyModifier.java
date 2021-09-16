package AdvancedOOD;

public class MyModifier {
    private int n1;
    int n2;
    protected int n3;
    public int n4;

    public MyModifier(int n1, int n2, int n3, int n4) {
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
    }

    public void printField() {
        System.out.print(this.n1);
        System.out.print(this.n2);
        System.out.print(this.n3);
        System.out.print(this.n4);
    }

    public static void main(String[] args) {
        MyModifier test1 = new MyModifier(1, 2, 3, 4);
        test1.printField();
    }
}


class TestSamePackage {
    public static void main(String[] args) {
        MyModifier test1 = new MyModifier(1, 2, 3, 4);
        test1.printField();
        System.out.println("\n" + test1.n4);
        System.out.println(test1.n3);
        System.out.println(test1.n2);
        // System.out.println(test1.n1); // unvisible
    }
}