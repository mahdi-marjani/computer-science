public static int factorial(int n) {
    if (n == 0)
        return 1;
    else
        return n * factorial(n - 1);
}

public static void main(String[] args) {
    int num = 3;
    int fact = factorial(num);
    System.out.println("Factorial of " + num + " is: " + fact);
}