public static long F(int n) {
    if (n == 0)
        return 0;
    long[] F = new long[n + 1];
    F[0] = 0;
    F[1] = 1;
    for (int i = 2; i <= n; i++)
        F[i] = F[i - 1] + F[i - 2];
    return F[n];
}

public static void main(String[] args) {
        int N = 7;
        long fibo = F(N);

        System.out.println("fibo of " + N + " is: " + fibo);

}