public class TestMainInJar {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(new TransClass().getNumber());
        int count = 0;
        while (true) {
            Thread.sleep(1000);
            int number = new TransClass().getNumber();
            System.out.println(number);
        }
    }

}
