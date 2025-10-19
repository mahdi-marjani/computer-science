public class TowersOfHanoi {
    public static void moves(int N, int from, int to, int help) {
        if (N == 1)
            System.out.printf("%d --> %d\n", from, to);
        else {
            moves(N - 1, from, help, to);
            System.out.printf("%d --> %d\n", from, to);
            moves(N - 1, help, to, from);
        }
    }

    public static void main(String[] args) {
        int N = 3;
        moves(N, 1, 3, 2);
    }
}